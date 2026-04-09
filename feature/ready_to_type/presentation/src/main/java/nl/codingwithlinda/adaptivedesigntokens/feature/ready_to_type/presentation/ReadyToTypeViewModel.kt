package nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.domain.PinValidator

private const val PIN_STATUS_RESET_DELAY = 700L

class ReadyToTypeViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val pinValidator: PinValidator,
) : ViewModel() {

    private val _state = MutableStateFlow(
        ReadyToTypeState(
            pin1 = savedStateHandle["pin1"],
            pin2 = savedStateHandle["pin2"],
            pin3 = savedStateHandle["pin3"],
            pin4 = savedStateHandle["pin4"],
            activeCell = savedStateHandle["activeCell"] ?: 0,
        )
    )
    val state = _state.asStateFlow()

    private var resetStatusJob: Job? = null

    fun onAction(action: ReadyToTypeAction) {
        when (action) {
            is ReadyToTypeAction.OnPinDigitEntered -> {
                resetStatusJob?.cancel()
                val cell = _state.value.activeCell
                val next = (cell + 1).coerceAtMost(3)
                savedStateHandle["pin${cell + 1}"] = action.digit
                savedStateHandle["activeCell"] = next
                _state.update {
                    it.withPinAt(cell, action.digit)
                        .copy(activeCell = next, pinStatus = PinStatus.Idle)
                }
            }
            ReadyToTypeAction.OnDeletePress -> {
                resetStatusJob?.cancel()
                val cell = _state.value.activeCell
                val digit = _state.value.pinAt(cell)
                if (digit != null) {
                    savedStateHandle["pin${cell + 1}"] = null
                    _state.update { it.withPinAt(cell, null).copy(pinStatus = PinStatus.Idle) }
                } else {
                    val prev = (cell - 1).coerceAtLeast(0)
                    savedStateHandle["activeCell"] = prev
                    _state.update { it.copy(activeCell = prev) }
                }
            }
            is ReadyToTypeAction.OnCellClicked -> {
                savedStateHandle["activeCell"] = action.index
                _state.update { it.copy(activeCell = action.index) }
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
        }
    }
}