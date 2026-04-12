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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import nl.codingwithlinda.adaptivedesigntokens.core.designsystem.theme.AdaptiveDesignTokensTheme
import nl.codingwithlinda.adaptivedesigntokens.feature.editing_status.presentation.NoteRoot
import nl.codingwithlinda.adaptivedesigntokens.feature.editing_status.presentation.theme.EditingStatusTheme
import nl.codingwithlinda.adaptivedesigntokens.feature.profile.presentation.ProfileRoot
import nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation.ReadyToTypeRoot
import nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation.theme.ReadyToTypeTheme
import nl.codingwithlinda.cloud_photo_upload.presentation.PhotoBackupRoot
import nl.codingwithlinda.guided_tour.presentation.TaskManagerRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var selectedTab by remember { mutableIntStateOf(0) }

            Column(modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
            ) {
                AdaptiveDesignTokensTheme {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Profile") },
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Ready to Type") },
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("Note") },
                        )
                        Tab(
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            text = { Text("Photo") },
                        )
                        Tab(
                            selected = selectedTab == 4,
                            onClick = { selectedTab = 4 },
                            text = { Text("Tour") },
                        )
                    }
                }

                Box(modifier = androidx.compose.ui.Modifier.weight(1f)) {
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