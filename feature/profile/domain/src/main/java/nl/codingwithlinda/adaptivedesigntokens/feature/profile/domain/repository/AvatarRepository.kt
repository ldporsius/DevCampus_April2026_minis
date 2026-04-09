package nl.codingwithlinda.adaptivedesigntokens.feature.profile.domain.repository

interface AvatarRepository {
    suspend fun loadAvatar(fileName: String): ByteArray
}