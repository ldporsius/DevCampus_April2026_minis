package nl.codingwithlinda.cloud_photo_upload.data

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters

class PhotoBackupWorkerFactory : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? = when (workerClassName) {
        PhotoBackupWorker::class.java.name ->
            PhotoBackupWorker(appContext, workerParameters)
        else -> null
    }
}