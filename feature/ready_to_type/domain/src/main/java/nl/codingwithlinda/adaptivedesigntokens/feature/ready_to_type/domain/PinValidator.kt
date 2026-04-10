package nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.domain

class PinValidator {

    fun validate(pin1: String?, pin2: String?, pin3: String?, pin4: String?): Boolean{
        val p1 = pin1?.toIntOrNull()
        val p2 = pin2?.toIntOrNull()
        val p3 = pin3?.toIntOrNull()
        val p4 = pin4?.toIntOrNull()
        return validate(p1, p2, p3, p4)
    }
    fun validate(pin1: Int?, pin2: Int?, pin3: Int?, pin4: Int?): Boolean =
        pin1 == 2 && pin2 == 5 && pin3 == 8 && pin4 == 0
}