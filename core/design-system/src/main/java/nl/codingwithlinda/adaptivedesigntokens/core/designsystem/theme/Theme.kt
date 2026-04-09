package nl.codingwithlinda.adaptivedesigntokens.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import nl.codingwithlinda.adaptivedesigntokens.core.designsystem.UiMode

private val LightColorScheme = lightColorScheme(
    // Primary
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    inversePrimary = InversePrimary,
    // Secondary
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    // Tertiary
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    // Error
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    // Background
    background = Bg,
    onBackground = TextPrimary,
    // Surface
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceLower,
    onSurfaceVariant = TextSecondary,
    surfaceTint = Primary,
    inverseSurface = InverseSurface,
    inverseOnSurface = InverseOnSurface,
    // Outline
    outline = Outline,
    outlineVariant = OutlineVariant,
    // Scrim
    scrim = Overlay,
)

@Composable
fun AdaptiveDesignTokensTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )

}