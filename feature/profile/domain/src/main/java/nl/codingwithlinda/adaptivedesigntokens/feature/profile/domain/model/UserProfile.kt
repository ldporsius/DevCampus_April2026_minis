package nl.codingwithlinda.adaptivedesigntokens.feature.profile.domain.model

data class UserProfile(
    val name: String,
    val role: String,
    val followersCount: String,
    val postsCount: String,
    val avatarAsset: String,
)