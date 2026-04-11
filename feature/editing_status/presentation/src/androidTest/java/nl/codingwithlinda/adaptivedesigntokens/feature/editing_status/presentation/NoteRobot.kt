package nl.codingwithlinda.adaptivedesigntokens.feature.editing_status.presentation

import nl.codingwithlinda.adaptivedesigntokens.feature.editing_status.presentation.theme.EditingStatusTheme

class NoteRobot(
    private val composeTestRule: AndroidComposeTestRule<*, *>,
) {

    private val context = composeTestRule.activity.applicationContext

    private val stateEditingText = context.getString(R.string.editing_status_editing)
    private val stateSavedText = context.getString(R.string.editing_status_saved)

    fun setContent(
        state: NoteState,
        onAction: (NoteAction) -> Unit = {},
    ) = apply {
        composeTestRule.setContent {
            EditingStatusTheme {
                NoteScreen(state = state, onAction = onAction)
            }
        }
    }

    fun clickTextField() = apply {
        composeTestRule.onNodeWithTag("note_text_field").performClick()
        composeTestRule.waitForIdle()
    }

    fun assertChipIsDisplayed() = apply {
        composeTestRule.onNodeWithTag("editing_status_chip").assertIsDisplayed()
    }

    fun enterText(text: String) = apply {
        composeTestRule.onNodeWithTag("note_text_field")
            .assertIsDisplayed()
            .performTextInput(text)
    }

    fun assertTextVisible(text: String) = apply {
        composeTestRule.onNodeWithText(text, substring = true).assertIsDisplayed()
    }


    @OptIn(ExperimentalTestApi::class)
    fun assertStateEditing() = apply {
        composeTestRule.waitUntilExactlyOneExists(
            hasText(stateEditingText),
            timeoutMillis = 1_500
        )
    }
    @OptIn(ExperimentalTestApi::class)
    fun assertStateSaved() = apply {
        composeTestRule.waitUntilExactlyOneExists(
            hasText(stateSavedText),
            timeoutMillis = 2_500
        )
    }
}