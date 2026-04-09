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
        )
    )
    val state = _state.asStateFlow()

    private var resetStatusJob: Job? = null

    fun onAction(action: ReadyToTypeAction) {
        when (action) {
            is ReadyToTypeAction.OnPinDigitEntered -> {
                resetStatusJob?.cancel()
                val digit = action.digit
                val key = "pin${action.position}"
                savedStateHandle[key] = digit
                _state.update {
                    when (action.position) {
                        1 -> it.copy(pin1 = digit, pinStatus = PinStatus.Idle)
                        2 -> it.copy(pin2 = digit, pinStatus = PinStatus.Idle)
                        3 -> it.copy(pin3 = digit, pinStatus = PinStatus.Idle)
                        4 -> it.copy(pin4 = digit, pinStatus = PinStatus.Idle)
                        else -> it
                    }
                }
            }
            ReadyToTypeAction.OnClearPin -> {
                resetStatusJob?.cancel()
                listOf("pin1", "pin2", "pin3", "pin4").forEach { savedStateHandle[it] = null }
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
            _state.update { ReadyToTypeState() }
        }
    }
}