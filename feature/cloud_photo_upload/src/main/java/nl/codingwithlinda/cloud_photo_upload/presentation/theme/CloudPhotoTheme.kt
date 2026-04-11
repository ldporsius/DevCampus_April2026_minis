package nl.codingwithlinda.cloud_photo_upload.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import nl.codingwithlinda.adaptivedesigntokens.core.designsystem.theme.Typography

private val CloudPhotoColorScheme = lightColorScheme(
    primary             = CpPrimary,
    onPrimary           = CpOnPrimary,
    primaryContainer    = CpPrimaryContainer,
    onPrimaryContainer  = CpOnPrimaryContainer,
    inversePrimary      = CpInversePrimary,
    secondary           = CpSecondary,
    onSecondary         = CpOnSecondary,
    secondaryContainer  = CpSecondaryContainer,
    onSecondaryContainer = CpOnSecondaryContainer,
    tertiary            = CpTertiary,
    onTertiary          = CpOnTertiary,
    tertiaryContainer   = CpTertiaryContainer,
    onTertiaryContainer = CpOnTertiaryContainer,
    error               = CpError,
    onError             = CpOnError,
    errorContainer      = CpErrorContainer,
    onErrorContainer    = CpOnErrorContainer,
    background          = CpBg,
    onBackground        = CpOnBg,
    surface             = CpSurface,
    onSurface           = CpOnSurface,
    surfaceVariant      = CpSurfaceVariant,
    onSurfaceVariant    = CpOnSurfaceVariant,
    outline             = CpOutline,
    outlineVariant      = CpOutlineVariant,
    scrim               = CpScrim,
    inverseSurface      = CpInverseSurface,
    inverseOnSurface    = CpInverseOnSurface,
)

@Composable
fun CloudPhotoTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = CloudPhotoColorScheme,
        typography = Typography,
        content = content,
    )
}