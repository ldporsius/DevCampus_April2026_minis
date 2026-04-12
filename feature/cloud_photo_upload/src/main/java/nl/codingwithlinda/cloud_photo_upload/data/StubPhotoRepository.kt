package nl.codingwithlinda.cloud_photo_upload.data

import nl.codingwithlinda.cloud_photo_upload.domain.PhotoRepository

class StubPhotoRepository : PhotoRepository {
    override fun getPhotos(): List<String> =
        List(200) { "content://media/external/images/media/$it" }
}