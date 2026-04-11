package nl.codingwithlinda.cloud_photo_upload.presentation.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.font.FontWeight

data class CardTextStyle(
    val descriptionWeight: FontWeight,
    val statusWeight: FontWeight,
)

val LocalCardTextStyle = compositionLocalOf {
    CardTextStyle(
        descriptionWeight = FontWeight.Normal,
        statusWeight = FontWeight.Bold,
    )
}