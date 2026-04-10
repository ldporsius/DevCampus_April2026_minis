package nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.domain.PinValidator
import org.junit.Rule
import org.junit.Test

class ReadyToTypeScreenViewModelTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val robot by lazy { ReadyToTypeRobot(composeTestRule) }

    private fun buildViewModel() = ReadyToTypeViewModel(
        savedStateHandle = SavedStateHandle(),
        pinValidator = PinValidator(),
    )

    // region Correct PIN flow

    @Test
    fun entering_correct_pin_and_confirming_shows_unlocked() {
        robot
            .setContentWithViewModel(buildViewModel())
            .enterDigitInCell(0, 2)
            .enterDigitInCell(1, 5)
            .enterDigitInCell(2, 8)
            .enterDigitInCell(3, 0)
            .clickEnterPin()
            .assertUnlockedDisplayed()
    }

    // endregion

    // region Wrong PIN flow

    @Test
    fun entering_wrong_pin_and_confirming_shows_wrong_pin_message() {
        robot
            .setContentWithViewModel(buildViewModel())
            .enterDigitInCell(0, 1)
            .enterDigitInCell(1, 2)
            .enterDigitInCell(2, 3)
            .enterDigitInCell(3, 4)
            .clickEnterPin()
            .assertWrongPinDisplayed()
    }

    @Test
    fun entering_wrong_pin_hides_enter_pin_button() {
        robot
            .setContentWithViewModel(buildViewModel())
            .enterDigitInCell(0, 9)
            .enterDigitInCell(1, 9)
            .enterDigitInCell(2, 9)
            .enterDigitInCell(3, 9)
            .clickEnterPin()
            .assertEnterPinButtonNotDisplayed()
    }

    // endregion

    @Test
    fun pressingBack_on_last_cell_clears_pin_moves_focus_to_previous_cell(): Unit = runBlocking{
        robot
            .setContentWithViewModel(buildViewModel())
            .enterDigitInCell(0, 9)
            .enterDigitInCell(1, 9)
            .enterDigitInCell(2, 9)
            .enterDigitInCell(3, 9)
            .pressBack()
            .assertCellHasValue(0, "9")
            .assertCellHasValue(1, "9")
            .assertCellHasValue(2, "9")
            .assertCellHasNoValue(3)
    }

    @Test
    fun pressingBack_on_all_cells_clears_all(): Unit = runBlocking{
        robot
            .setContentWithViewModel(buildViewModel())
            .enterDigitInCell(0, 9)
            .enterDigitInCell(1, 9)
            .enterDigitInCell(2, 9)
            .enterDigitInCell(3, 9)
            .pressBack()
            .assertCellHasNoValue(3)
            .pressBack()
            .pressBack()

            delay(1000)
        robot
            .assertCellHasNoValue(2)
            .pressBack()
            .pressBack()
            .assertCellHasNoValue(1)
            .pressBack()
            .pressBack()
            .assertCellHasNoValue(0)


    }

    @Test
    fun pressingBack_andEnterNewValue_newValueCorrect(): Unit = runBlocking{
        robot
            .setContentWithViewModel(buildViewModel())
            .enterDigitInCell(0, 9)
            .pressBack()
            .assertCellHasNoValue(1)
            .enterDigitInCell(0, 1)
            .assertCellHasValue(0, "1")


    }
}