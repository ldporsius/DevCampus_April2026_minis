package nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.domain.PinValidator
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReadyToTypeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: ReadyToTypeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ReadyToTypeViewModel(
            savedStateHandle = SavedStateHandle(),
            pinValidator = PinValidator(),
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // region Behavior 1 — entering a digit updates only that cell

    @Test
    fun `entering a digit sets it only in the active cell`() {
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(0, "5"))

        val state = viewModel.state.value
        assertEquals("5", state.pin1)
        assertEquals("", state.pin2)
        assertEquals("", state.pin3)
        assertEquals("", state.pin4)
    }

    @Test
    fun `entering a digit in cell 2 does not affect other cells`() {
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(0, "2"))
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(1, "5"))

        val state = viewModel.state.value
        assertEquals("2", state.pin1)
        assertEquals("5", state.pin2)
        assertEquals("", state.pin3)
        assertEquals("", state.pin4)
    }

    // endregion

    // region Behavior 2 — entering a digit moves focus to the next cell

    @Test
    fun `entering a digit advances active cell by one`() {
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(0, "5"))

        assertEquals(1, viewModel.state.value.activeCell)
    }

    @Test
    fun `entering a digit in the last cell does not advance beyond it`() {
        repeat(4) { viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(it, it.toString())) }

        assertEquals(3, viewModel.state.value.activeCell)
    }

    // endregion

    // region Behavior 3 — backspace on a filled cell clears it and stays
    // Note: OnDeletePress only moves the cursor; digit clearing is handled by the UI's
    // onValueChange callback (the IME removes the character from the BasicTextField).

    @Test
    fun `backspace on filled cell keeps focus on the same cell`() {
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(0, "5")) // pin1, activeCell=1
        viewModel.onAction(ReadyToTypeAction.OnCellClicked(0))           // focus cell 0
        viewModel.onAction(ReadyToTypeAction.OnDeletePress(-1))

        assertEquals(0, viewModel.state.value.activeCell)
    }

    // endregion

    // region Behavior 4 — backspace on an empty cell moves focus to the previous cell

    @Test
    fun `backspace on empty cell moves focus to previous cell`() {
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(0, "5")) // activeCell=1
        viewModel.onAction(ReadyToTypeAction.OnDeletePress(-1))          // cell 1 is empty

        assertEquals(0, viewModel.state.value.activeCell)
    }

    @Test
    fun `backspace on empty cell does not clear the digit in the previous cell`() {
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(0, "5")) // pin1, activeCell=1
        viewModel.onAction(ReadyToTypeAction.OnDeletePress(-1))          // cell 1 is empty

        assertEquals("5", viewModel.state.value.pin1)
    }

    // endregion

    // region Behavior 5 — backspace on the first empty cell does nothing

    @Test
    fun `backspace on first cell when empty keeps active cell at 0`() {
        viewModel.onAction(ReadyToTypeAction.OnDeletePress(-1))

        assertEquals(0, viewModel.state.value.activeCell)
    }

    @Test
    fun `backspace on first cell when empty does not change any digit`() {
        viewModel.onAction(ReadyToTypeAction.OnDeletePress(-1))

        val state = viewModel.state.value
        assertEquals("", state.pin1)
        assertEquals("", state.pin2)
        assertEquals("", state.pin3)
        assertEquals("", state.pin4)
    }

    // endregion

    // region Behavior 6 — clicking a cell focuses it without changing digits

    @Test
    fun `clicking a cell updates the active cell`() {
        viewModel.onAction(ReadyToTypeAction.OnCellClicked(2))

        assertEquals(2, viewModel.state.value.activeCell)
    }

    @Test
    fun `clicking a filled cell does not change its digit`() {
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(0, "2"))
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(1, "5"))
        viewModel.onAction(ReadyToTypeAction.OnCellClicked(0))

        assertEquals("2", viewModel.state.value.pin1)
        assertEquals("5", viewModel.state.value.pin2)
    }

    @Test
    fun `clicking a cell does not affect any digits`() {
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(0, "2"))
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(1, "5"))
        viewModel.onAction(ReadyToTypeAction.OnCellClicked(0))

        val state = viewModel.state.value
        assertEquals("2", state.pin1)
        assertEquals("5", state.pin2)
        assertEquals("", state.pin3)
        assertEquals("", state.pin4)
    }

    // endregion

    // region Behavior 7 — active cell advances to action.index + 1, not currentActiveCell + 1
    //
    // Bug: if _activeCell was out of sync with action.index (e.g. a spurious onValueChange
    // fires on focus-in for a filled cell after backpress navigation), the old code advanced
    // from the current _activeCell value instead of from action.index, overshooting by one.

    @Test
    fun `re-entering a digit in cell 0 advances focus to cell 1, not cell 2`() {
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(0, "1")) // activeCell → 1
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(0, "5")) // re-enter at index 0

        assertEquals(1, viewModel.state.value.activeCell)
    }

    // endregion
}