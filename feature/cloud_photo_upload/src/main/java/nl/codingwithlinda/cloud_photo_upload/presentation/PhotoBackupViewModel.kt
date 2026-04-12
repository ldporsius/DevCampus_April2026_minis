package nl.codingwithlinda.cloud_photo_upload.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.codingwithlinda.cloud_photo_upload.data.photoUploadWorkRequest
import nl.codingwithlinda.cloud_photo_upload.domain.NetworkObserver
import nl.codingwithlinda.cloud_photo_upload.domain.NetworkStatus
import nl.codingwithlinda.cloud_photo_upload.domain.PhotoRepository
import nl.codingwithlinda.cloud_photo_upload.presentation.interaction.PhotoAction
import nl.codingwithlinda.cloud_photo_upload.presentation.interaction.PhotoBackupState
import nl.codingwithlinda.cloud_photo_upload.presentation.interaction.PhotoBackupUiState

class PhotoBackupViewModel(
    private val workManager: WorkManager,
    photoRepository: PhotoRepository,
    private val networkObserver: NetworkObserver,
) : ViewModel() {

    private val photos = photoRepository.getPhotos()

    private val _uiState = MutableStateFlow(PhotoBackupUiState(total = photos.size))
    val uiState = _uiState.stateIn(viewModelScope, SharingStarted.Eagerly, PhotoBackupUiState())

    init {
        viewModelScope.launch {
            combine(
                workManager.getWorkInfosForUniqueWorkFlow(UNIQUE_WORK_NAME),
                networkObserver.observe()
            ) { workInfos, networkStatus ->
                if (workInfos.isEmpty()) return@combine null

                val succeeded = workInfos.count { it.state == WorkInfo.State.SUCCEEDED }
                val hasRunning = workInfos.any { it.state == WorkInfo.State.RUNNING }
                val isEnqueued = workInfos.any { it.state == WorkInfo.State.ENQUEUED }
                val allFinished = workInfos.all { it.state.isFinished }
                val isProgressing = hasRunning || (isEnqueued && succeeded > 0)

                val backupState = when {
                    networkStatus == NetworkStatus.Unavailable
                        && (hasRunning || isEnqueued) -> PhotoBackupState.PAUSED
                    isProgressing   -> PhotoBackupState.RUNNING
                    isEnqueued      -> PhotoBackupState.STARTED   // waiting for constraints, nothing uploaded yet
                    allFinished && succeeded == workInfos.size -> PhotoBackupState.FINISHED
                    allFinished     -> PhotoBackupState.PAUSED    // some workers failed
                    else            -> PhotoBackupState.IDLE
                }

                PhotoBackupUiState(state = backupState, numberUploaded = succeeded, total = workInfos.size)
            }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { newState -> _uiState.update { newState } }
        }
    }

    fun onAction(action: PhotoAction) {
        when (action) {
            PhotoAction.BackupCompleted -> {
                workManager.pruneWork()
                _uiState.update { PhotoBackupUiState(total = photos.size) }
            }
            PhotoAction.StartBackup -> {
                val requests = photos.map { uri -> photoUploadWorkRequest(uri) }

                var continuation = workManager.beginUniqueWork(
                    UNIQUE_WORK_NAME,
                    ExistingWorkPolicy.KEEP,
                    requests.first()
                )
                requests.drop(1).forEach { request ->
                    continuation = continuation.then(request)
                }
                continuation.enqueue()
            }
        }
    }

    companion object {
        const val UNIQUE_WORK_NAME = "photo_backup"
    }
}