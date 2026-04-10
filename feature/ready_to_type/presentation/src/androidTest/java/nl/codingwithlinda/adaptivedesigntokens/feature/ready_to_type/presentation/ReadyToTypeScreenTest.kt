package nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class ReadyToTypeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val robot by lazy { ReadyToTypeRobot(composeTestRule) }

    // region Static content

    @Test
    fun displays_title() {
        robot
            .setContent(ReadyToTypeState())
            .assertTitleDisplayed()
    }

    @Test
    fun displays_subtitle() {
        robot
            .setContent(ReadyToTypeState())
            .assertSubtitleDisplayed()
    }

    @Test
    fun displays_all_four_pin_cells() {
        robot
            .setContent(ReadyToTypeState())
            .assertAllCellsDisplayed()
    }

    // endregion

    // region PinStatus-driven UI

    @Test
    fun shows_enter_pin_button_when_status_is_idle() {
        robot
            .setContent(ReadyToTypeState(pinStatus = PinStatus.Idle))
            .assertEnterPinButtonDisplayed()
    }

    @Test
    fun shows_unlocked_text_when_status_is_unlocked() {
        robot
            .setContent(ReadyToTypeState(pinStatus = PinStatus.Unlocked))
            .assertUnlockedDisplayed()
            .assertEnterPinButtonNotDisplayed()
    }

    @Test
    fun shows_wrong_pin_text_when_status_is_wrong_pin() {
        robot
            .setContent(ReadyToTypeState(pinStatus = PinStatus.WrongPin))
            .assertWrongPinDisplayed()
            .assertEnterPinButtonNotDisplayed()
    }

    // endregion

    // region Actions

    @Test
    fun clicking_enter_pin_triggers_confirm_action() {
        var actionReceived: ReadyToTypeAction? = null

        robot
            .setContent(
                state = ReadyToTypeState(pinStatus = PinStatus.Idle),
                onAction = { actionReceived = it },
            )
            .clickEnterPin()

        assert(actionReceived == ReadyToTypeAction.OnConfirmPin)
    }

    @Test
    fun clicking_a_cell_triggers_cell_clicked_action_with_correct_index() {
        val actionsReceived = mutableListOf<ReadyToTypeAction>()

        robot
            .setContent(
                state = ReadyToTypeState(),
                onAction = { action ->
                    println("--- onAction: $action")
                    actionsReceived.add(action) },
            )
            .clickCell(2)

        assert(ReadyToTypeAction.OnCellClicked(2) in actionsReceived) {
            "Expected OnCellClicked(2) but received: $actionsReceived"
        }
    }

    // endregion
}