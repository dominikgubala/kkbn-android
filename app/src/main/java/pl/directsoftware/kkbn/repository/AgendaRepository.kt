package pl.directsoftware.kkbn.repository

import pl.directsoftware.kkbn.api.data.EventDay
import pl.directsoftware.kkbn.repository.base.AbstractRepository
import pl.directsoftware.kkbn.utils.Logger

object AgendaRepository : AbstractRepository() {

    private val logger = Logger(AgendaRepository::class.java.simpleName)

    suspend fun getEvents(): List<EventDay>? {
        val events = service.getAgenda()
        if (!events.success) {
            logger.methodError("getEvents", events.errors?.firstOrNull() ?: "unknown")
            return null
        }

        return events.data
    }
}