package nl.codingwithlinda.adaptivedesigntokens.feature.editing_status.presentation

sealed interface NoteAction {
    data class OnNoteChanged(val text: String) : NoteAction
}