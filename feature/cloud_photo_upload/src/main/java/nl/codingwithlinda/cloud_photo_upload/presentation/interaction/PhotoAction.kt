package nl.codingwithlinda.cloud_photo_upload.presentation.interaction

sealed interface PhotoAction {
    data object StartBackup : PhotoAction
    data object BackupCompleted : PhotoAction
}