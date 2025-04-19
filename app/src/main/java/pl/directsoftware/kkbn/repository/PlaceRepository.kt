package pl.directsoftware.kkbn.repository

import kotlinx.coroutines.runBlocking
import pl.directsoftware.kkbn.api.data.Place
import pl.directsoftware.kkbn.repository.base.AbstractRepository
import pl.directsoftware.kkbn.utils.Logger

class PlaceRepository : AbstractRepository() {

    fun getPlace(): Place? {
        val place = runBlocking { service.getPlace() }
        if (!place.success) {
            logger.methodError("getPlace", place.errors?.firstOrNull() ?: "unknown")
            return null
        }

        return place.data
    }

    companion object {

        private val logger = Logger(PlaceRepository::class.java.simpleName)
    }
}