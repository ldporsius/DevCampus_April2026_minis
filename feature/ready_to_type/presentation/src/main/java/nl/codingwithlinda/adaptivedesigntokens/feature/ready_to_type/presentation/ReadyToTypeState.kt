package nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation

data class ReadyToTypeState(
    val pin1: Int? = null,
    val pin2: Int? = null,
    val pin3: Int? = null,
    val pin4: Int? = null,
    val activeCell: Int = 0,
    val pinStatus: PinStatus = PinStatus.Idle,
) {
    fun pinAt(index: Int): Int? = when (index) {
        0 -> pin1
        1 -> pin2
        2 -> pin3
        3 -> pin4
        else -> null
    }

    fun withPinAt(index: Int, digit: Int?): ReadyToTypeState = when (index) {
        0 -> copy(pin1 = digit)
        1 -> copy(pin2 = digit)
        2 -> copy(pin3 = digit)
        3 -> copy(pin4 = digit)
        else -> this
    }
}