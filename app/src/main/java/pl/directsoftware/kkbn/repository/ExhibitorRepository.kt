package pl.directsoftware.kkbn.repository

import kotlinx.coroutines.runBlocking
import pl.directsoftware.kkbn.api.data.Exhibitor
import pl.directsoftware.kkbn.repository.base.AbstractRepository
import pl.directsoftware.kkbn.utils.Logger

class ExhibitorRepository : AbstractRepository() {

    fun getExhibitors(): List<Exhibitor>? {
        val exhibitors = runBlocking { service.getExhibitors() }
        if (!exhibitors.success) {
            logger.methodError("getExhibitors", exhibitors.errors?.firstOrNull() ?: "unknown")
            return null
        }

        return exhibitors.data
    }

    companion object {

        private val logger = Logger(ExhibitorRepository::class.java.simpleName)
    }
}