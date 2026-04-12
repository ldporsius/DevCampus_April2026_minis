package nl.codingwithlinda.cloud_photo_upload.domain

import kotlinx.coroutines.flow.Flow

enum class NetworkStatus {
    Available,
    Unavailable,
}

interface NetworkObserver {
    fun observe(): Flow<NetworkStatus>
}