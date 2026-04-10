package nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation

import nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation.ReadyToTypeViewModel.Companion.SENTINEL

data class ReadyToTypeState(
    val pin1: String = "",
    val pin2: String = "",
    val pin3: String = "",
    val pin4: String = "",
    val activeCell: Int = 0,
    val pinStatus: PinStatus = PinStatus.Idle,
) {
    fun pinAt(index: Int): String = when (index) {
        0 -> pin1
        1 -> pin2
        2 -> pin3
        3 -> pin4
        else -> ""
    }

    fun withPinAt(index: Int, digit: String): ReadyToTypeState = when (index) {
        0 -> copy(pin1 = digit)
        1 -> copy(pin2 = digit)
        2 -> copy(pin3 = digit)
        3 -> copy(pin4 = digit)
        else -> this
    }

    fun listPins() = listOf(pin1, pin2, pin3, pin4)

}