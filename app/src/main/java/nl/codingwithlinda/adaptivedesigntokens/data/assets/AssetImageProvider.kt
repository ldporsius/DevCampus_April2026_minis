package nl.codingwithlinda.adaptivedesigntokens.data.assets

import androidx.compose.ui.graphics.ImageBitmap

abstract class AssetImageProvider {
    abstract suspend fun loadImage(fileName: String): ImageBitmap
}