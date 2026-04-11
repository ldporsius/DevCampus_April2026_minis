package nl.codingwithlinda.adaptivedesigntokens.feature.editing_status.presentation

import androidx.activity.compose.setContent
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.runBlocking
import nl.codingwithlinda.adaptivedesigntokens.MainActivity
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class NoteScreenTest2{

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()


    @Test
    fun testNoteScreen(): Unit = runBlocking {
        val viewModel = EditingStatusViewModel(
            SavedStateHandle()
        )

        composeTestRule.runOnUiThread {
            composeTestRule.activity.setContent {
                NoteScreen(
                    state = viewModel.state.collectAsStateWithLifecycle().value,
                    onAction = viewModel::onAction,
                )
            }
        }

        composeTestRule.onNodeWithTag("note_text_field").performTextInput("Hello")
        composeTestRule.onNodeWithTag("note_text_field").assertTextContains("Hello", substring = false)

        composeTestRule.waitUntilExactlyOneExists(
            hasText("Editing", substring = true),
            timeoutMillis = 500
        )

        composeTestRule.waitUntilExactlyOneExists(
            hasText("Saved", substring = true),
            timeoutMillis = 1_500
        )

        composeTestRule.waitUntilDoesNotExist(
            hasText("Saved", substring = true),
            timeoutMillis = 2001
        )




    }


}