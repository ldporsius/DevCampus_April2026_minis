package nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation

sealed interface ReadyToTypeAction {
    data class OnPinDigitEntered(val digit: Int) : ReadyToTypeAction
    data object OnDeletePress : ReadyToTypeAction
    data class OnCellClicked(val index: Int) : ReadyToTypeAction
    data object OnClearPin : ReadyToTypeAction
    data object OnConfirmPin : ReadyToTypeAction
}