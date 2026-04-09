package nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation

sealed interface PinStatus {
    data object Idle : PinStatus
    data object Unlocked : PinStatus
    data object WrongPin : PinStatus
}