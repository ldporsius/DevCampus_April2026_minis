package nl.codingwithlinda.guided_tour.presentation

enum class TourStep(
    val stepNumber: Int,
    val description: String,
    val isLast: Boolean,
) {
    SEARCH(1, "Use search to quickly find your tasks", false),
    FILTERS(2, "Filter your tasks by status", false),
    TASK_LIST(3, "Your tasks will appear here", false),
    ADD_BUTTON(4, "Tap here to create a new task", true);

    val total: Int get() = entries.size
}