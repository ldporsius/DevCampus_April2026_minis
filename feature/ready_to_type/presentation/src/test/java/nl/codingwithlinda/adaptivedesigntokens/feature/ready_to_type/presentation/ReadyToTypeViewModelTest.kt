package nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.domain.PinValidator
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

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

    // The ViewModel stores pin values as "_$digit" (sentinel + digit).
    private fun pin(digit: Int) = "_$digit"

    // region Behavior 1 — entering a digit updates only that cell

    @Test
    fun `entering a digit sets it only in the active cell`() {
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(0, "5"))

        val state = viewModel.state.value
        assertEquals(pin(5), state.pin1)
        assertNull(state.pin2)
        assertNull(state.pin3)
        assertNull(state.pin4)
    }

    @Test
    fun `entering a digit in cell 2 does not affect other cells`() {
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(0, "2")) // cell 0 → activeCell becomes 1
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(1, "5")) // cell 1 → activeCell becomes 2

        val state = viewModel.state.value
        assertEquals(pin(2), state.pin1)
        assertEquals(pin(5), state.pin2)
        assertNull(state.pin3)
        assertNull(state.pin4)
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
    // TODO: Re-enable when delete refactor is complete.
    //       OnDeletePress currently only sets navigation direction; digit clearing is not yet implemented.

    // @Test fun `backspace on filled cell clears the digit`() { ... }

    @Test
    fun `backspace on filled cell keeps focus on the same cell`() {
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(0, "5")) // pin1, activeCell=1
        viewModel.onAction(ReadyToTypeAction.OnCellClicked(0))           // focus cell 0
        viewModel.onAction(ReadyToTypeAction.OnDeletePress(-1))

        assertEquals(0, viewModel.state.value.activeCell)
    }

    // @Test fun `backspace on last filled cell sets pin to blank`() { ... }

    // @Test fun `backspace on filled cell does not affect other cells`() { ... }

    // endregion

    // region Behavior 4 — backspace on an empty cell moves focus to the previous cell
    // TODO: Re-enable when delete refactor is complete.

    // @Test fun `backspace on empty cell moves focus to previous cell`() { ... }

    @Test
    fun `backspace on empty cell does not clear the digit in the previous cell`() {
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(0, "5")) // pin1, activeCell=1
        viewModel.onAction(ReadyToTypeAction.OnDeletePress(-1))          // cell 1 is empty

        assertEquals(pin(5), viewModel.state.value.pin1)
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
        assertNull(state.pin1)
        assertNull(state.pin2)
        assertNull(state.pin3)
        assertNull(state.pin4)
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
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(0, "2")) // pin1
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(1, "5")) // pin2
        viewModel.onAction(ReadyToTypeAction.OnCellClicked(0))

        assertEquals(pin(2), viewModel.state.value.pin1)
        assertEquals(pin(5), viewModel.state.value.pin2)
    }

    @Test
    fun `clicking a cell does not affect any digits`() {
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(0, "2"))
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(1, "5"))
        viewModel.onAction(ReadyToTypeAction.OnCellClicked(0))

        val state = viewModel.state.value
        assertEquals(pin(2), state.pin1)
        assertEquals(pin(5), state.pin2)
        assertNull(state.pin3)
        assertNull(state.pin4)
    }

    // endregion
}