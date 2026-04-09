package nl.codingwithlinda.adaptivedesigntokens.feature.profile.data.repository

import android.content.res.AssetManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nl.codingwithlinda.adaptivedesigntokens.feature.profile.domain.repository.AvatarRepository

class AssetAvatarRepository(
    private val assets: AssetManager
) : AvatarRepository {

    override suspend fun loadAvatar(fileName: String): ByteArray =
        withContext(Dispatchers.IO) {
            assets.open(fileName).use { it.readBytes() }
        }
}