package nl.codingwithlinda.guided_tour.presentation

data class TourState(
    val showDialog: Boolean = false,
    val currentStep: TourStep? = null,
)