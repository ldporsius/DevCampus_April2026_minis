package nl.codingwithlinda.cloud_photo_upload.di

import androidx.work.WorkManager
import androidx.work.WorkerFactory
import nl.codingwithlinda.cloud_photo_upload.data.AndroidNetworkObserver
import nl.codingwithlinda.cloud_photo_upload.data.PhotoBackupWorkerFactory
import nl.codingwithlinda.cloud_photo_upload.data.StubPhotoRepository
import nl.codingwithlinda.cloud_photo_upload.domain.NetworkObserver
import nl.codingwithlinda.cloud_photo_upload.domain.PhotoRepository
import nl.codingwithlinda.cloud_photo_upload.presentation.PhotoBackupViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val cloudPhotoModule = module {
    single<WorkerFactory> { PhotoBackupWorkerFactory() }
    single { WorkManager.getInstance(androidContext()) }
    single<PhotoRepository> { StubPhotoRepository() }
    single<NetworkObserver> { AndroidNetworkObserver(androidContext()) }
    viewModelOf(::PhotoBackupViewModel)
}