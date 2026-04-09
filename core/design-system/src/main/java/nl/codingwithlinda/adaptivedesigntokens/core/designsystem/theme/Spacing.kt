package nl.codingwithlinda.adaptivedesigntokens.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nl.codingwithlinda.adaptivedesigntokens.core.designsystem.UiMode

@Immutable
data class Spacing(
    val none: Dp = 0.dp,
    val extraSmall: Dp,
    val small: Dp,
    val medium: Dp,
    val large: Dp,
    val extraLarge: Dp,
    val huge: Dp,
)

fun compactSpacing() = Spacing(
    extraSmall = 2.dp,
    small = 4.dp,
    medium = 8.dp,
    large = 16.dp,
    extraLarge = 24.dp,
    huge = 32.dp,
)

fun comfortableSpacing() = Spacing(
    extraSmall = 4.dp,
    small = 8.dp,
    medium = 16.dp,
    large = 24.dp,
    extraLarge = 32.dp,
    huge = 48.dp,
)

fun expandedSpacing() = Spacing(
    extraSmall = 8.dp,
    small = 16.dp,
    medium = 24.dp,
    large = 32.dp,
    extraLarge = 48.dp,
    huge = 64.dp,
)

fun UiMode.toSpacing(): Spacing = when (this) {
    UiMode.Compact     -> compactSpacing()
    UiMode.Comfortable -> comfortableSpacing()
    UiMode.Expanded    -> expandedSpacing()
}

val LocalSpacing = staticCompositionLocalOf { comfortableSpacing() }

val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current