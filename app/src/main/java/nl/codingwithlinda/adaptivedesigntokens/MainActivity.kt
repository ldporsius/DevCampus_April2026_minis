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
import nl.codingwithlinda.adaptivedesigntokens.feature.profile.presentation.ProfileRoot
import nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation.ReadyToTypeRoot
import nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation.theme.ReadyToTypeTheme

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
                    }
                }

                Box(modifier = androidx.compose.ui.Modifier.weight(1f)) {
                    when (selectedTab) {
                        0 -> AdaptiveDesignTokensTheme { ProfileRoot() }
                        1 -> ReadyToTypeTheme { ReadyToTypeRoot() }
                    }
                }
            }
        }
    }
}