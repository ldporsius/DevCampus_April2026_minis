package nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation

data class ReadyToTypeState(
    val pin1: Int? = null,
    val pin2: Int? = null,
    val pin3: Int? = null,
    val pin4: Int? = null,
    val pinStatus: PinStatus = PinStatus.Idle,
)