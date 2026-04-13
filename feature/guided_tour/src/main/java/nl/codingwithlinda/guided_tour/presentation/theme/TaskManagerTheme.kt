package nl.codingwithlinda.guided_tour.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import nl.codingwithlinda.adaptivedesigntokens.core.designsystem.theme.Typography

private val TaskManagerColorScheme = lightColorScheme(
    primary             = TmPrimary,
    onPrimary           = TmOnPrimary,
    primaryContainer    = TmPrimaryContainer,
    onPrimaryContainer  = TmOnPrimaryContainer,
    inversePrimary      = TmInversePrimary,
    secondary           = TmSecondary,
    onSecondary         = TmOnSecondary,
    secondaryContainer  = TmSecondaryContainer,
    onSecondaryContainer = TmOnSecondaryContainer,
    tertiary            = TmTertiary,
    onTertiary          = TmOnTertiary,
    tertiaryContainer   = TmTertiaryContainer,
    onTertiaryContainer = TmOnTertiaryContainer,
    error               = TmError,
    onError             = TmOnError,
    errorContainer      = TmErrorContainer,
    onErrorContainer    = TmOnErrorContainer,
    background          = TmBg,
    onBackground        = TmOnBg,
    surface             = TmSurface,
    onSurface           = TmOnSurface,
    surfaceVariant      = TmSurfaceVariant,
    onSurfaceVariant    = TmOnSurfaceVariant,
    outline             = TmOutline,
    outlineVariant      = TmOutlineVariant,
    scrim               = TmScrim,
    inverseSurface      = TmInverseSurface,
    inverseOnSurface    = TmInverseOnSurface,
)

@Composable
fun TaskManagerTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = TaskManagerColorScheme,
        typography = Typography,
        content = content,
    )
}