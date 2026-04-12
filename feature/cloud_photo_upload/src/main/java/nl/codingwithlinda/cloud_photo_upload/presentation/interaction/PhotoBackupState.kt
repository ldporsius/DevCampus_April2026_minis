package nl.codingwithlinda.cloud_photo_upload.presentation.interaction

import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import nl.codingwithlinda.cloud_photo_upload.R
import nl.codingwithlinda.cloud_photo_upload.presentation.theme.CardTextStyle
import nl.codingwithlinda.cloud_photo_upload.presentation.theme.CpGrassGreen
import nl.codingwithlinda.cloud_photo_upload.presentation.theme.CpOnGrassGreen
import nl.codingwithlinda.cloud_photo_upload.presentation.theme.CpOnSkyBlue
import nl.codingwithlinda.cloud_photo_upload.presentation.theme.CpSkyBlue

enum class PhotoBackupState {
    IDLE,
    STARTED,
    RUNNING,
    PAUSED,
    FINISHED
}

@Composable
fun PhotoBackupState.toDescription() = when(this){
    PhotoBackupState.IDLE -> ""
    PhotoBackupState.STARTED -> stringResource(R.string.backup_started)
    PhotoBackupState.RUNNING -> stringResource( R.string.backup_running)
    PhotoBackupState.PAUSED -> stringResource(R.string.backup_paused)
    PhotoBackupState.FINISHED -> stringResource(R.string.backup_finished)
}
@Composable
fun PhotoBackupState.toStatusText(progress: Int, numPhotos: Int) = when(this){
    PhotoBackupState.IDLE -> stringResource(R.string.ready_to_backup, numPhotos)
    PhotoBackupState.STARTED -> ""
    PhotoBackupState.RUNNING -> stringResource(R.string.backup_progress, progress, numPhotos)
    PhotoBackupState.PAUSED -> stringResource(R.string.waiting_for_connectivity)
    PhotoBackupState.FINISHED -> stringResource(R.string.number_of_photos_uploaded, numPhotos)
}

@Composable
fun PhotoBackupState.toButtonText() = when (this) {
    PhotoBackupState.IDLE -> stringResource(R.string.button_start_backup)
    PhotoBackupState.STARTED -> ""
    PhotoBackupState.RUNNING -> stringResource(R.string.button_in_progress)
    PhotoBackupState.PAUSED -> stringResource(R.string.button_in_progress)
    PhotoBackupState.FINISHED -> stringResource(R.string.button_completed)
}

fun PhotoBackupState.toCardTextStyle() = when (this) {
    PhotoBackupState.FINISHED -> CardTextStyle(
        descriptionWeight = FontWeight.Bold,
        statusWeight = FontWeight.Normal,
    )
    else -> CardTextStyle(
        descriptionWeight = FontWeight.Normal,
        statusWeight = FontWeight.Bold,
    )
}

@Composable
fun PhotoBackupState.toButtonColors() = when (this) {
    PhotoBackupState.IDLE -> ButtonDefaults.buttonColors(
        containerColor = CpSkyBlue,
        contentColor = CpOnSkyBlue,
    )
    PhotoBackupState.FINISHED -> ButtonDefaults.buttonColors(
        containerColor = CpGrassGreen,
        contentColor = CpOnGrassGreen,
    )
    else -> ButtonDefaults.buttonColors()
}