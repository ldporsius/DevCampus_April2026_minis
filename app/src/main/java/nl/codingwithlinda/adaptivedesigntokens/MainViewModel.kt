package nl.codingwithlinda.adaptivedesigntokens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

private const val KEY_TAB = "selected_tab"

class MainViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val selectedTab = savedStateHandle.getStateFlow(KEY_TAB, 0)

    fun selectTab(index: Int) {
        savedStateHandle[KEY_TAB] = index
    }
}