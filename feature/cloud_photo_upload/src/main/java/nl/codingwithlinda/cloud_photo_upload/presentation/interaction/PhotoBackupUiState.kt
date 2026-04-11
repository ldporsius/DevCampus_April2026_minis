package nl.codingwithlinda.cloud_photo_upload.presentation.interaction

data class PhotoBackupUiState(
    val state: PhotoBackupState = PhotoBackupState.IDLE,
    val total : Int = 1,
    val numberUploaded: Int = 0
){
    fun isButtonEnabled() = state == PhotoBackupState.IDLE || state == PhotoBackupState.FINISHED
    fun progress() = (numberUploaded.toFloat() / total.toFloat()).coerceIn(0f, 1f)
}
