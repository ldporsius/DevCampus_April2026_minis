package nl.codingwithlinda.guided_tour.presentation

sealed interface TourAction {
    data object Skip : TourAction
    data object StartTour : TourAction
    data object NextStep : TourAction
    data object Finish : TourAction
}