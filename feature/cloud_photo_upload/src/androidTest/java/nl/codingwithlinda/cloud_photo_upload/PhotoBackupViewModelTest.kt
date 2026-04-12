package nl.codingwithlinda.cloud_photo_upload

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import nl.codingwithlinda.cloud_photo_upload.data.StubPhotoRepository
import nl.codingwithlinda.cloud_photo_upload.domain.NetworkObserver
import nl.codingwithlinda.cloud_photo_upload.domain.NetworkStatus
import nl.codingwithlinda.cloud_photo_upload.presentation.PhotoBackupViewModel
import nl.codingwithlinda.cloud_photo_upload.presentation.interaction.PhotoAction
import nl.codingwithlinda.cloud_photo_upload.presentation.interaction.PhotoBackupState
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class PhotoBackupViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var workManager: WorkManager

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        WorkManagerTestInitHelper.initializeTestWorkManager(context)
        workManager = WorkManager.getInstance(context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isIdle() = runTest {
        val vm = buildViewModel(NetworkStatus.Available)

        val state = vm.uiState.first()

        assertEquals(PhotoBackupState.IDLE, state.state)
        assertEquals(200, state.total) // StubPhotoRepository returns 200
    }

    @Test
    fun startBackup_enqueuedWork_stateBecomesRunning() = runTest {
        val vm = buildViewModel(NetworkStatus.Available)
        val testDriver = WorkManagerTestInitHelper.getTestDriver(
            InstrumentationRegistry.getInstrumentation().targetContext
        )!!

        vm.onAction(PhotoAction.StartBackup)

        // Satisfy the CONNECTED network constraint so WorkManager moves to RUNNING
        val workInfo = workManager
            .getWorkInfosForUniqueWork(PhotoBackupViewModel.UNIQUE_WORK_NAME)
            .get()
            .firstOrNull()

        assertEquals(WorkInfo.State.ENQUEUED, workInfo?.state)

        testDriver.setAllConstraintsMet(workInfo!!.id)

        val updatedInfo = workManager
            .getWorkInfosForUniqueWork(PhotoBackupViewModel.UNIQUE_WORK_NAME)
            .get()
            .firstOrNull()

        assertEquals(WorkInfo.State.RUNNING, updatedInfo?.state)
    }

    @Test
    fun networkUnavailable_whileEnqueued_stateIsPaused() = runTest {
        val vm = buildViewModel(NetworkStatus.Unavailable)

        vm.onAction(PhotoAction.StartBackup)

        // Work is enqueued but network is unavailable → ViewModel maps to PAUSED
        val state = vm.uiState.first { it.state != PhotoBackupState.IDLE }

        assertEquals(PhotoBackupState.PAUSED, state.state)
    }

    // --- helpers ---

    private fun buildViewModel(networkStatus: NetworkStatus): PhotoBackupViewModel {
        return PhotoBackupViewModel(
            workManager = workManager,
            photoRepository = StubPhotoRepository(),
            networkObserver = FakeNetworkObserver(networkStatus),
        )
    }
}

private class FakeNetworkObserver(private val status: NetworkStatus) : NetworkObserver {
    override fun observe(): Flow<NetworkStatus> = flowOf(status)
}