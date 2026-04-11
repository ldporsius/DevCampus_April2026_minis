package nl.codingwithlinda.cloud_photo_upload.data

import nl.codingwithlinda.cloud_photo_upload.domain.PhotoRepository

class StubPhotoRepository : PhotoRepository {
    override fun getPhotoCount(): Int = 200
}