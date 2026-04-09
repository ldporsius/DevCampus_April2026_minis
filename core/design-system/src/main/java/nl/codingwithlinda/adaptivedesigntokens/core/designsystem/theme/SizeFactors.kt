package nl.codingwithlinda.adaptivedesigntokens.core.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nl.codingwithlinda.adaptivedesigntokens.core.designsystem.UiMode

data class LocalSize(
    val small: Dp,
    val medium: Dp,
    val large: Dp
)

val avatarSize = LocalSize(
    small = 56.dp,
    medium = 64.dp,
    large = 72.dp
)

fun UiMode.toAvatarSize() = when(
    this
){
    UiMode.Compact -> avatarSize.small
    UiMode.Comfortable -> avatarSize.medium
    UiMode.Expanded -> avatarSize.large
}

val LocalSizeProvider = staticCompositionLocalOf { avatarSize }