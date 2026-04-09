package nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.domain

class PinValidator {

    fun validate(pin1: Int?, pin2: Int?, pin3: Int?, pin4: Int?): Boolean =
        pin1 == 2 && pin2 == 5 && pin3 == 8 && pin4 == 0
}