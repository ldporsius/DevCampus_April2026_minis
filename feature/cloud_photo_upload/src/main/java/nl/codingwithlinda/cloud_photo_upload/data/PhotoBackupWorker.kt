package nl.codingwithlinda.cloud_photo_upload.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay
import java.util.UUID

class PhotoBackupWorker(
    context: Context,
    private val workerParams: WorkerParameters): CoroutineWorker(context, workerParams) {


    override suspend fun doWork(): Result {

        val total = workerParams.inputData.getInt(KEY_TOTAL, 1)
        setProgress(
            workDataOf(KEY_TOTAL to total, KEY_PROGRESS to 0)
        )
        val photos = List(total){
            "photo $it"
        }
        println("--- PhotoBackupWorker --- total: $total")
        var numUploaded = 0
        do {
            delay(200)
            numUploaded += 1
            setProgress(
                workDataOf(KEY_PROGRESS to numUploaded )
            )
        }while (numUploaded < photos.size)
        return Result.success(
            workDataOf(
                KEY_PROGRESS to numUploaded,
                KEY_TOTAL to total)
        )

    }

    companion object{

        const val KEY_PROGRESS = "progress"
        const val KEY_TOTAL = "total"
    }
}

fun backupWorkRequest(total: Int) = OneTimeWorkRequestBuilder<PhotoBackupWorker>()
    .setInputData(workDataOf(PhotoBackupWorker.KEY_TOTAL to total))
    .setConstraints(
        Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    )
    .build()