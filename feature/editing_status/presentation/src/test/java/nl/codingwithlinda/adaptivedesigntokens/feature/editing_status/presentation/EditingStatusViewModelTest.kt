package nl.codingwithlinda.adaptivedesigntokens.feature.editing_status.presentation

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditingStatusViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: EditingStatusViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = EditingStatusViewModel(savedStateHandle = SavedStateHandle())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // region Behavior 1 — typing sets status to Editing

    @Test
    fun `typing text sets status to Editing`() {
        viewModel.onAction(NoteAction.OnNoteChanged("Hello"))

        assertEquals(EditingStatus.Editing, viewModel.state.value.status)
    }

    @Test
    fun `typed text is reflected in state`() {
        viewModel.onAction(NoteAction.OnNoteChanged("Hello"))

        assertEquals("Hello", viewModel.state.value.noteText)
    }

    // endregion

    // region Behavior 2 — auto-save after 2 seconds of inactivity

    @Test
    fun `status is set to Saved after 2 seconds of inactivity`() = runTest(testDispatcher) {
        viewModel.onAction(NoteAction.OnNoteChanged("Hello"))
        assertEquals(EditingStatus.Editing, viewModel.state.value.status)

        advanceTimeBy(2_001)

        assertEquals(EditingStatus.Saved, viewModel.state.value.status)
    }

    @Test
    fun `status stays Editing before 2 seconds have passed`() = runTest(testDispatcher) {
        viewModel.onAction(NoteAction.OnNoteChanged("Hello"))

        advanceTimeBy(1_999)

        assertEquals(EditingStatus.Editing, viewModel.state.value.status)
    }

    @Test
    fun `typing again before 2 seconds resets the auto-save timer`() = runTest(testDispatcher) {
        viewModel.onAction(NoteAction.OnNoteChanged("Hello"))
        advanceTimeBy(1_500)

        viewModel.onAction(NoteAction.OnNoteChanged("Hello!")) // resets the 2-second window
        advanceTimeBy(1_500) // only 1.5s since the last keystroke

        assertEquals(EditingStatus.Editing, viewModel.state.value.status)
    }

    // endregion
}