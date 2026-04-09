package nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation

sealed interface ReadyToTypeAction {
    data class OnPinDigitEntered(val position: Int, val digit: Int?) : ReadyToTypeAction
    data object OnClearPin : ReadyToTypeAction
    data object OnConfirmPin : ReadyToTypeAction
}