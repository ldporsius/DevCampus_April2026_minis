package nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val ReadyToTypeColorScheme = darkColorScheme(
    primary = RttPrimary,
    onPrimary = RttOnPrimary,
    primaryContainer = RttPrimaryContainer,
    onPrimaryContainer = RttOnPrimaryContainer,
    secondary = RttSecondary,
    onSecondary = RttOnSecondary,
    secondaryContainer = RttSecondaryContainer,
    onSecondaryContainer = RttOnSecondaryContainer,
    tertiary = RttTertiary,
    onTertiary = RttOnTertiary,
    tertiaryContainer = RttTertiaryContainer,
    onTertiaryContainer = RttOnTertiaryContainer,
    error = RttError,
    onError = RttOnError,
    errorContainer = RttErrorContainer,
    onErrorContainer = RttOnErrorContainer,
    background = RttBg,
    onBackground = RttTextPrimary,
    surface = RttSurface,
    onSurface = RttTextPrimary,
    surfaceVariant = RttSurfaceLower,
    onSurfaceVariant = RttTextSecondary,
    inverseSurface = RttInverseSurface,
    inverseOnSurface = RttInverseOnSurface,
    outline = RttOutline,
    outlineVariant = RttOutlineVariant,
    scrim = RttOverlay,
)

private val ReadyToTypeTypography = Typography(
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = 0.sp,
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.5.sp,
    ),
)

@Composable
fun ReadyToTypeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ReadyToTypeColorScheme,
        typography = ReadyToTypeTypography,
        content = content,
    )
}