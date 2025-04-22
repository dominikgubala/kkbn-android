package pl.directsoftware.kkbn.repository

import kotlinx.coroutines.runBlocking
import pl.directsoftware.kkbn.api.data.Place
import pl.directsoftware.kkbn.repository.base.AbstractRepository
import pl.directsoftware.kkbn.utils.Logger

object PlaceRepository : AbstractRepository() {

    private val logger = Logger(PlaceRepository::class.java.simpleName)

    fun getPlace(): Place? {
        val place = runBlocking { service.getPlace() }
        if (!place.success) {
            logger.methodError("getPlace", place.errors?.firstOrNull() ?: "unknown")
            return null
        }

        return place.data
    }
}