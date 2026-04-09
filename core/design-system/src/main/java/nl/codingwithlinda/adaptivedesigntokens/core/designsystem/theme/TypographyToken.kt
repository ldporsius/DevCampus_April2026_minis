package nl.codingwithlinda.adaptivedesigntokens.core.designsystem.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.TextStyle
import nl.codingwithlinda.adaptivedesigntokens.core.designsystem.UiMode

data class TypographyToken(
    val captionTextStyle: TextStyle,
    val titleTextStyle: TextStyle,
    val bodyTextStyle: TextStyle,
    val labelTextStyle: TextStyle
)


val compactTypographyToken = TypographyToken(
    captionTextStyle = Typography.headlineSmall,
    titleTextStyle = Typography.titleSmall,
    bodyTextStyle = Typography.bodySmall,
    labelTextStyle = Typography.labelSmall
)

val comfortableTypographyToken = TypographyToken(
    captionTextStyle = Typography.headlineMedium,
    titleTextStyle = Typography.titleMedium,
    bodyTextStyle = Typography.bodyMedium,
    labelTextStyle = Typography.labelMedium
)

val expandedTypographyToken = TypographyToken(
    captionTextStyle = Typography.headlineLarge,
    titleTextStyle = Typography.titleLarge,
    bodyTextStyle = Typography.bodyLarge,
    labelTextStyle = Typography.labelLarge
)


val LocalTypography = compositionLocalOf {
    compactTypographyToken }


fun UiMode.toLocalTypography() = when (this) {
    UiMode.Compact -> compactTypographyToken
    UiMode.Comfortable -> comfortableTypographyToken
    UiMode.Expanded -> expandedTypographyToken
}