package nl.codingwithlinda.guided_tour.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nl.codingwithlinda.guided_tour.data.TourPreferencesDataSource

private const val KEY_STEP = "tour_step_index"

class TourViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val dataSource: TourPreferencesDataSource,
    private val appScope: CoroutineScope,
) : ViewModel() {

    // stepIndex: -1 = not in tutorial, 0..3 = active step
    // Survives config changes via SavedStateHandle.
    // Cleared on process death via swipe-from-recents (task removed), so the
    // dialog re-appears on next cold start as required.

    private val stepFlow: StateFlow<Int> = savedStateHandle.getStateFlow(KEY_STEP, -1)

    val state: StateFlow<TourState> = combine(
        dataSource.tourDone,
        stepFlow,
    ) { tourDone, step ->
        TourState(
            showDialog = !tourDone && step == -1,
            currentStep = if (!tourDone && step >= 0) TourStep.entries[step] else null,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TourState(),
    )

    fun onAction(action: TourAction) {
        when (action) {
            TourAction.Skip -> viewModelScope.launch { dataSource.setTourDone(true) }
            TourAction.StartTour -> savedStateHandle[KEY_STEP] = 0
            TourAction.NextStep -> {
                val current = savedStateHandle.get<Int>(KEY_STEP) ?: return
                if (current < TourStep.entries.lastIndex) {
                    savedStateHandle[KEY_STEP] = current + 1
                }
            }
            TourAction.Finish -> {
                savedStateHandle[KEY_STEP] = -1
                viewModelScope.launch { dataSource.setTourDone(true) }
            }
            TourAction.Reset -> reset()
        }
    }

    fun reset() = appScope.launch{
        dataSource.setTourDone(false)
    }

}