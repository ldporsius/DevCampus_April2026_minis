package nl.codingwithlinda.adaptivedesigntokens.feature.editing_status.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val AUTO_SAVE_DELAY = 1_000L
private const val AUTO_DISMISS_DELAY = 2_000L

class EditingStatusViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _state = MutableStateFlow(
        NoteState(
            noteText = savedStateHandle["note_text"] ?: "",
        )
    )
    val state = _state.asStateFlow()

    private var autoSaveJob: Job? = null

    fun onAction(action: NoteAction) {
        when (action) {
            is NoteAction.OnNoteChanged -> {
                autoSaveJob?.cancel()
                savedStateHandle["note_text"] = action.text
                _state.update { it.copy(noteText = action.text, status = EditingStatus.Editing) }
                autoSaveJob = viewModelScope.launch {
                    delay(AUTO_SAVE_DELAY)
                    _state.update { it.copy(status = EditingStatus.Saved) }
                    delay(AUTO_DISMISS_DELAY)
                    _state.update { it.copy(status = EditingStatus.Idle) }
                }
            }

        }
    }
}