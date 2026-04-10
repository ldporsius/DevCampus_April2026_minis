package nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.domain.PinValidator

private const val PIN_STATUS_RESET_DELAY = 1000L

class ReadyToTypeViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val pinValidator: PinValidator,
) : ViewModel() {

    companion object{
        // Space sentinel ensures the IME always has a character to delete,
        // making soft-keyboard backspace fire onValueChange reliably.
        const val SENTINEL = "_"
    }

    private val _activeCell = MutableStateFlow(0)

    private val _state = MutableStateFlow(
        ReadyToTypeState(
            pin1 = savedStateHandle["pin1"] ?: "",
            pin2 = savedStateHandle["pin2"] ?: "",
            pin3 = savedStateHandle["pin3"] ?: "",
            pin4 = savedStateHandle["pin4"] ?: "",
            activeCell = savedStateHandle["activeCell"] ?: 0,
        )
    )
    val state = _state.combine(_activeCell) { state, activeCell ->
        state.copy(activeCell = activeCell)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    private var resetStatusJob: Job? = null

    enum class Direction(val position: Int){
        FORWARD(1), BACK(-1)
    }
    private val _direction = MutableStateFlow(Direction.FORWARD)



    private val mutex = Mutex()
    private val lock = Any()
    fun onAction(action: ReadyToTypeAction) {
        when (action) {
            is ReadyToTypeAction.OnPinDigitEntered -> viewModelScope.launch{
                resetStatusJob?.cancel()

                mutex.withLock(lock) {
                    val newvalue = action.digit.lastOrNull { it.isDigit() }?.toString() ?: ""

                    _state.update {
                        it.withPinAt(action.index, newvalue.toString())
                            .copy(pinStatus = PinStatus.Idle)
                    }
                    if (!newvalue.isEmpty()) {
                        _activeCell.update {
                            it.plus(1).coerceIn(0, 3)
                        }
                    }

                }
            }
            is ReadyToTypeAction.OnDeletePress -> {
                _activeCell.update {
                    it.minus(1).coerceIn(0,3)
                }
            }
            is ReadyToTypeAction.OnCellClicked -> {
                savedStateHandle["activeCell"] = action.index
                _activeCell.update { action.index }
            }
            ReadyToTypeAction.OnClearPin -> {
                resetStatusJob?.cancel()
                listOf("pin1", "pin2", "pin3", "pin4").forEach { savedStateHandle[it] = null }
                savedStateHandle["activeCell"] = 0
                _state.update { ReadyToTypeState() }
            }
            ReadyToTypeAction.OnConfirmPin -> {
                val s = _state.value
                val status = if (pinValidator.validate(s.pin1, s.pin2, s.pin3, s.pin4)) {
                    PinStatus.Unlocked
                } else {
                    PinStatus.WrongPin
                }
                _state.update { it.copy(pinStatus = status) }
                if (status == PinStatus.WrongPin) scheduleResetAfterInvalid()
            }
        }
    }

    private fun scheduleResetAfterInvalid() {
        resetStatusJob?.cancel()
        resetStatusJob = viewModelScope.launch {
            delay(PIN_STATUS_RESET_DELAY)
            listOf("pin1", "pin2", "pin3", "pin4").forEach { savedStateHandle[it] = null }
            savedStateHandle["activeCell"] = 0
            _state.update { ReadyToTypeState() }
            _activeCell.update { 0 }
        }
    }
}