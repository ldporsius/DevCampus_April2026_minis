package nl.codingwithlinda.cloud_photo_upload.domain

interface PhotoRepository {
    fun getPhotoCount(): Int
}