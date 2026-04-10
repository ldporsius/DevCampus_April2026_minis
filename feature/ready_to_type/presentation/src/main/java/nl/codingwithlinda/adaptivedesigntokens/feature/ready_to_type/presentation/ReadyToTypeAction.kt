package nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation

sealed interface ReadyToTypeAction {
    data class OnPinDigitEntered(val index: Int, val digit: String) : ReadyToTypeAction
    data class OnDeletePress(val index: Int) : ReadyToTypeAction
    data class OnCellClicked(val index: Int) : ReadyToTypeAction
    data object OnClearPin : ReadyToTypeAction
    data object OnConfirmPin : ReadyToTypeAction
}