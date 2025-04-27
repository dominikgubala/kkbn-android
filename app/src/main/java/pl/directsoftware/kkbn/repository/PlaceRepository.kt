package pl.directsoftware.kkbn.repository

import pl.directsoftware.kkbn.api.data.Place
import pl.directsoftware.kkbn.repository.base.AbstractRepository
import pl.directsoftware.kkbn.utils.Logger

object PlaceRepository : AbstractRepository() {

    private val logger = Logger(PlaceRepository::class.java.simpleName)

    suspend fun getPlace(): Place? {
        val place = service.getPlace()
        if (!place.success) {
            logger.methodError("getPlace", place.errors?.firstOrNull() ?: "unknown")
            return null
        }

        return place.data
    }
}