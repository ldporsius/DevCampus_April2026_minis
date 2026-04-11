package nl.codingwithlinda.adaptivedesigntokens.feature.editing_status.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import nl.codingwithlinda.adaptivedesigntokens.core.designsystem.theme.Typography

private val EditingStatusColorScheme = lightColorScheme(
    primary = EsPrimary,
    onPrimary = EsOnPrimary,
    primaryContainer = EsPrimaryContainer,
    onPrimaryContainer = EsOnPrimaryContainer,
    secondary = EsSecondary,
    onSecondary = EsOnSecondary,
    secondaryContainer = EsSecondaryContainer,
    onSecondaryContainer = EsOnSecondaryContainer,
    tertiary = EsTertiary,
    onTertiary = EsOnTertiary,
    tertiaryContainer = EsTertiaryContainer,
    onTertiaryContainer = EsOnTertiaryContainer,
    error = EsError,
    onError = EsOnError,
    errorContainer = EsErrorContainer,
    onErrorContainer = EsOnErrorContainer,
    background = EsBg,
    onBackground = EsOnBg,
    surface = EsSurface,
    onSurface = EsOnSurface,
    surfaceVariant = EsSurfaceVariant,
    onSurfaceVariant = EsOnSurfaceVariant,
    outline = EsOutline,
    outlineVariant = EsOutlineVariant,
    scrim = EsScrim,
    inverseSurface = EsInverseSurface,
    inverseOnSurface = EsInverseOnSurface,
)

@Composable
fun EditingStatusTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = EditingStatusColorScheme,
        typography = Typography,
        content = content,
    )
}