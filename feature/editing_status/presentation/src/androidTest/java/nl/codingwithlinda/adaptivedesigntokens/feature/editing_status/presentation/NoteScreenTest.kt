package nl.codingwithlinda.adaptivedesigntokens.feature.editing_status.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import nl.codingwithlinda.adaptivedesigntokens.feature.editing_status.presentation.NoteRobot

class NoteScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>() as AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, *>


    private val robot by lazy { NoteRobot(composeTestRule) }

    @Test
    fun chip_remains_visible_when_soft_keyboard_is_open() : Unit = runBlocking{
        robot
            .setContent(NoteState())
            .clickTextField()
            .assertChipIsDisplayed()
        robot
            .enterText("Hello")
            .assertTextVisible("Hello")
            .assertStateEditing()
            .assertStateSaved()

    }
}