package nl.codingwithlinda.adaptivedesigntokens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nl.codingwithlinda.adaptivedesigntokens.core.designsystem.theme.AdaptiveDesignTokensTheme
import nl.codingwithlinda.adaptivedesigntokens.feature.editing_status.presentation.NoteRoot
import nl.codingwithlinda.adaptivedesigntokens.feature.editing_status.presentation.theme.EditingStatusTheme
import nl.codingwithlinda.adaptivedesigntokens.feature.profile.presentation.ProfileRoot
import nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation.ReadyToTypeRoot
import nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation.theme.ReadyToTypeTheme
import nl.codingwithlinda.cloud_photo_upload.presentation.PhotoBackupRoot
import nl.codingwithlinda.guided_tour.presentation.TaskManagerRoot
import org.koin.androidx.compose.koinViewModel

private val tabTitles = listOf("Profile", "Ready to Type", "Note", "Photo", "Tour")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: MainViewModel = koinViewModel()
            val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeContentPadding()
            ) {
                AdaptiveDesignTokensTheme {
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { viewModel.selectTab(index) },
                                text = { AutoSizeTabText(text = title) },
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        0 -> AdaptiveDesignTokensTheme { ProfileRoot() }
                        1 -> ReadyToTypeTheme { ReadyToTypeRoot() }
                        2 -> EditingStatusTheme { NoteRoot() }
                        3 -> PhotoBackupRoot()
                        4 -> TaskManagerRoot()
                    }
                }
            }
        }
    }
}

@Composable
private fun AutoSizeTabText(
    text: String,
    maxFontSize: TextUnit = 24.sp,
) {
    var fontSize by remember(text) { mutableStateOf(maxFontSize) }

    Text(
        text = text,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Visible,
        style = TextStyle(fontSize = fontSize),
        onTextLayout = { result ->
            if (result.didOverflowWidth) {
                fontSize *= 0.9f
            }
        },
    )
}