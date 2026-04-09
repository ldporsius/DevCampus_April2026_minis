package nl.codingwithlinda.adaptivedesigntokens.data.assets

import android.content.res.AssetManager
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidAssetImageProvider(
    private val assets: AssetManager
) : AssetImageProvider() {

    override suspend fun loadImage(fileName: String): ImageBitmap =
        withContext(Dispatchers.IO) {
            assets.open(fileName).use { stream ->
                BitmapFactory.decodeStream(stream).asImageBitmap()
            }
        }
}