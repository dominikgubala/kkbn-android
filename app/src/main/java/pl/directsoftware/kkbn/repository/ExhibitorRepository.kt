package pl.directsoftware.kkbn.repository

import pl.directsoftware.kkbn.api.data.Exhibitor
import pl.directsoftware.kkbn.repository.base.AbstractRepository
import pl.directsoftware.kkbn.utils.Logger

object ExhibitorRepository : AbstractRepository() {

    private val logger = Logger(ExhibitorRepository::class.java.simpleName)

    suspend fun getExhibitors(): List<Exhibitor>? {
        val exhibitors = service.getExhibitors()
        if (!exhibitors.success) {
            logger.methodError("getExhibitors", exhibitors.errors?.firstOrNull() ?: "unknown")
            return null
        }

        return exhibitors.data
    }
}