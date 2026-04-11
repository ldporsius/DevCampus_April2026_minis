package nl.codingwithlinda.adaptivedesigntokens.feature.editing_status.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

data class NoteState(
    val noteText: String = "",
    val status: EditingStatus = EditingStatus.Idle,
)

enum class EditingStatus {
    Idle,
    Editing,
    Saved,
}

@Composable
fun EditingStatus.statusText(): String {
    return when (this) {
        EditingStatus.Idle -> ""
        EditingStatus.Editing -> stringResource(R.string.editing_status_editing)
        EditingStatus.Saved -> stringResource(R.string.editing_status_saved)
    }
}