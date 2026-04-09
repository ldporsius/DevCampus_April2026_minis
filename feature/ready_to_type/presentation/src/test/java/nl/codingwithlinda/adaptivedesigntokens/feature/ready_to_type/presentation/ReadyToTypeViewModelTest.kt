package nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation

import androidx.lifecycle.SavedStateHandle
import nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.domain.PinValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class ReadyToTypeViewModelTest {

    private lateinit var viewModel: ReadyToTypeViewModel

    @Before
    fun setUp() {
        viewModel = ReadyToTypeViewModel(
            savedStateHandle = SavedStateHandle(),
            pinValidator = PinValidator(),
        )
    }

    // region Behavior 1 — entering a digit updates only that cell

    @Test
    fun `entering a digit sets it only in the active cell`() {
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(5))

        val state = viewModel.state.value
        assertEquals(5, state.pin1)
        assertNull(state.pin2)
        assertNull(state.pin3)
        assertNull(state.pin4)
    }

    @Test
    fun `entering a digit in cell 2 does not affect other cells`() {
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(2)) // cell 0 → activeCell becomes 1
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(5)) // cell 1 → activeCell becomes 2

        val state = viewModel.state.value
        assertEquals(2, state.pin1)
        assertEquals(5, state.pin2)
        assertNull(state.pin3)
        assertNull(state.pin4)
    }

    // endregion

    // region Behavior 2 — entering a digit moves focus to the next cell

    @Test
    fun `entering a digit advances active cell by one`() {
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(5))

        assertEquals(1, viewModel.state.value.activeCell)
    }

    @Test
    fun `entering a digit in the last cell does not advance beyond it`() {
        repeat(4) { viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(it)) }

        assertEquals(3, viewModel.state.value.activeCell)
    }

    // endregion

    // region Behavior 3 — backspace on a filled cell clears it and stays

    @Test
    fun `backspace on filled cell clears the digit`() {
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(5)) // pin1=5, activeCell=1
        viewModel.onAction(ReadyToTypeAction.OnCellClicked(0))     // focus cell 0
        viewModel.onAction(ReadyToTypeAction.OnDeletePress)

        assertNull(viewModel.state.value.pin1)
    }

    @Test
    fun `backspace on filled cell keeps focus on the same cell`() {
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(5)) // pin1=5, activeCell=1
        viewModel.onAction(ReadyToTypeAction.OnCellClicked(0))     // focus cell 0
        viewModel.onAction(ReadyToTypeAction.OnDeletePress)

        assertEquals(0, viewModel.state.value.activeCell)
    }

    @Test
    fun `backspace on filled cell does not affect other cells`() {
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(2)) // pin1=2, activeCell=1
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(5)) // pin2=5, activeCell=2
        viewModel.onAction(ReadyToTypeAction.OnCellClicked(0))
        viewModel.onAction(ReadyToTypeAction.OnDeletePress)

        assertNull(viewModel.state.value.pin1)
        assertEquals(5, viewModel.state.value.pin2)
    }

    // endregion

    // region Behavior 4 — backspace on an empty cell moves focus to the previous cell

    @Test
    fun `backspace on empty cell moves focus to previous cell`() {
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(5)) // pin1=5, activeCell=1
        viewModel.onAction(ReadyToTypeAction.OnDeletePress)        // cell 1 is empty → move back

        assertEquals(0, viewModel.state.value.activeCell)
    }

    @Test
    fun `backspace on empty cell does not clear the digit in the previous cell`() {
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(5)) // pin1=5, activeCell=1
        viewModel.onAction(ReadyToTypeAction.OnDeletePress)        // move to cell 0

        assertEquals(5, viewModel.state.value.pin1)
    }

    // endregion

    // region Behavior 5 — backspace on the first empty cell does nothing

    @Test
    fun `backspace on first cell when empty keeps active cell at 0`() {
        viewModel.onAction(ReadyToTypeAction.OnDeletePress)

        assertEquals(0, viewModel.state.value.activeCell)
    }

    @Test
    fun `backspace on first cell when empty does not change any digit`() {
        viewModel.onAction(ReadyToTypeAction.OnDeletePress)

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
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(2)) // pin1=2
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(5)) // pin2=5
        viewModel.onAction(ReadyToTypeAction.OnCellClicked(0))

        assertEquals(2, viewModel.state.value.pin1)
        assertEquals(5, viewModel.state.value.pin2)
    }

    @Test
    fun `clicking a cell does not affect any digits`() {
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(2))
        viewModel.onAction(ReadyToTypeAction.OnPinDigitEntered(5))
        viewModel.onAction(ReadyToTypeAction.OnCellClicked(0))

        val state = viewModel.state.value
        assertEquals(2, state.pin1)
        assertEquals(5, state.pin2)
        assertNull(state.pin3)
        assertNull(state.pin4)
    }

    // endregion
}