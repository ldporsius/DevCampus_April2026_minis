package nl.codingwithlinda.cloud_photo_upload.data

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay

class PhotoBackupWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if (inputData.getString(KEY_URI) == null) return Result.failure()

        // simulate upload of a single photo
        delay(100)

        return Result.success()
    }

    companion object {
        const val KEY_URI = "uri"
        const val UPLOAD_TAG = "photo_upload"
    }
}

fun photoUploadWorkRequest(uri: String) = OneTimeWorkRequestBuilder<PhotoBackupWorker>()
    .setInputData(workDataOf(PhotoBackupWorker.KEY_URI to uri))
    .addTag(PhotoBackupWorker.UPLOAD_TAG)
    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
    .setConstraints(
        Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    )
    .build()