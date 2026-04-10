package nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isFocused
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.click
import androidx.compose.ui.test.pressKey
import nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation.theme.ReadyToTypeTheme

class ReadyToTypeRobot(private val composeTestRule: ComposeContentTestRule) {

    fun setContent(
        state: ReadyToTypeState,
        onAction: (ReadyToTypeAction) -> Unit = {},
    ) = apply {
        composeTestRule.setContent {
            ReadyToTypeTheme {
                ReadyToTypeScreen(state = state, onAction = onAction)
            }
        }
    }

    fun setContentWithViewModel(viewModel: ReadyToTypeViewModel) = apply {
        composeTestRule.setContent {
            ReadyToTypeTheme {
                ReadyToTypeRoot(viewModel = viewModel)
            }
        }
    }

    fun enterDigitInCell(cellIndex: Int, digit: Int) = apply {
        composeTestRule.onNodeWithTag("pin_cell_$cellIndex").performTextInput("$digit")
    }

    fun assertTitleDisplayed() = apply {
        composeTestRule.onNodeWithText("Secure Vault").assertIsDisplayed()
    }

    fun assertSubtitleDisplayed() = apply {
        composeTestRule.onNodeWithText("Unlock your vault using your 4-digit PIN").assertIsDisplayed()
    }

    fun assertAllCellsDisplayed() = apply {
        repeat(4) { index ->
            composeTestRule.onNodeWithTag("pin_cell_$index").assertIsDisplayed()
        }
    }

    fun assertEnterPinButtonDisplayed() = apply {
        composeTestRule.onNodeWithText("Enter pin").assertIsDisplayed()
    }

    fun assertEnterPinButtonNotDisplayed() = apply {
        composeTestRule.onNodeWithText("Enter pin").assertIsNotDisplayed()
    }

    fun assertUnlockedDisplayed() = apply {
        composeTestRule.onNodeWithText("\u2713 Unlocked succesfully").assertIsDisplayed()
    }

    fun assertWrongPinDisplayed() = apply {
        composeTestRule.onNodeWithText("\u2717 Wrong PIN, try again").assertIsDisplayed()
    }

    fun clickEnterPin() = apply {
        composeTestRule.onNodeWithText("Enter pin").performClick()
    }

    fun clickCell(index: Int) = apply {
        composeTestRule.onNodeWithTag("pin_cell_$index").performTouchInput { click() }
    }

    @OptIn(ExperimentalTestApi::class)
    fun pressBack() = apply {
        composeTestRule.onNode(isFocused()).performKeyInput { pressKey(Key.Backspace) }
        composeTestRule.waitForIdle()
    }

    fun assertCellHasValue(cellIndex: Int, value: String) = apply {
        composeTestRule.onNodeWithTag("pin_cell_$cellIndex")
            .assertIsDisplayed()
            .assertTextContains(value, substring = true)
    }
    fun assertCellHasNoValue(cellIndex: Int) = apply {
        composeTestRule.onNodeWithTag("pin_cell_$cellIndex")
            .assertIsDisplayed()
            .assertTextEquals("")
    }
}