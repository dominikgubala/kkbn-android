package pl.directsoftware.kkbn.repository

import pl.directsoftware.kkbn.api.data.Profile
import pl.directsoftware.kkbn.repository.base.AbstractRepository
import pl.directsoftware.kkbn.utils.Logger

object ProfileRepository : AbstractRepository() {

    private val logger = Logger(ProfileRepository::class.java.simpleName)

    suspend fun getProfile(): Profile? {
        val place = privateService.getProfile()
        if (!place.success) {
            logger.methodError("getProfile", place.errors?.firstOrNull() ?: "unknown")
            return null
        }

        return place.data
    }
}