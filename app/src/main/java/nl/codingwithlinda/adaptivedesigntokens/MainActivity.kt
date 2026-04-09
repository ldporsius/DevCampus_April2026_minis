package nl.codingwithlinda.adaptivedesigntokens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import nl.codingwithlinda.adaptivedesigntokens.core.designsystem.theme.AdaptiveDesignTokensTheme
import nl.codingwithlinda.adaptivedesigntokens.feature.profile.presentation.ProfileRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdaptiveDesignTokensTheme {
                ProfileRoot()
            }
        }
    }
}