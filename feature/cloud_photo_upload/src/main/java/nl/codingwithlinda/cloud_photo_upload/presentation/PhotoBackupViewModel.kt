package nl.codingwithlinda.cloud_photo_upload.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.codingwithlinda.cloud_photo_upload.data.PhotoBackupWorker
import nl.codingwithlinda.cloud_photo_upload.data.PhotoBackupWorker.Companion.KEY_PROGRESS
import nl.codingwithlinda.cloud_photo_upload.data.PhotoBackupWorker.Companion.KEY_TOTAL
import nl.codingwithlinda.cloud_photo_upload.data.backupWorkRequest
import nl.codingwithlinda.cloud_photo_upload.domain.PhotoRepository
import nl.codingwithlinda.cloud_photo_upload.presentation.interaction.PhotoAction
import nl.codingwithlinda.cloud_photo_upload.presentation.interaction.PhotoBackupState
import nl.codingwithlinda.cloud_photo_upload.presentation.interaction.PhotoBackupUiState
import java.util.UUID

class PhotoBackupViewModel(
    private val workManager: WorkManager,
    private val photoRepository: PhotoRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(PhotoBackupUiState(total = photoRepository.getPhotoCount()))

    val uiState = _uiState.stateIn(viewModelScope, SharingStarted.Eagerly, PhotoBackupUiState())

    private val _requestId = MutableStateFlow<UUID?>(null)

    private var workJob: Job? = null

    init {

        _requestId.onEach { requestId ->
            workJob?.cancel()

            if (requestId == null) return@onEach

            workJob = viewModelScope.launch {
                workManager.getWorkInfoByIdFlow(requestId).collect{ workInfo ->

                    ensureActive()
                    println("--- PhotoBackupViewModel --- workInfo: $workInfo")
                    workInfo?.let { info ->
                        val progress = info.progress.getInt(KEY_PROGRESS, 0)
                        info.progress.getInt(KEY_TOTAL, -1).let { total ->
                            if (total == -1) return@let
                            _uiState.update {
                                it.copy(total = total)
                            }
                        }
                        val backupState = when (info.state) {
                            WorkInfo.State.RUNNING -> PhotoBackupState.RUNNING
                            WorkInfo.State.ENQUEUED -> PhotoBackupState.IDLE
                            WorkInfo.State.SUCCEEDED -> PhotoBackupState.FINISHED
                            WorkInfo.State.FAILED -> PhotoBackupState.PAUSED
                            WorkInfo.State.BLOCKED -> PhotoBackupState.PAUSED
                            WorkInfo.State.CANCELLED -> PhotoBackupState.IDLE
                        }

                        _uiState.update { it.copy(state = backupState, numberUploaded = progress) }
                    }
                }
            }
        }.launchIn(viewModelScope)

    }

    fun onAction(action: PhotoAction){
        when(action){
            PhotoAction.BackupCompleted -> {
                _uiState.update { PhotoBackupUiState(total = photoRepository.getPhotoCount()) }
            }
            PhotoAction.StartBackup -> {
                _requestId.value?.run {
                    workManager.cancelWorkById(this)
                }

                val request = backupWorkRequest(photoRepository.getPhotoCount())
                val id = request.id
                _requestId.update { id }
                workManager.enqueue(request)
            }

        }
    }
}