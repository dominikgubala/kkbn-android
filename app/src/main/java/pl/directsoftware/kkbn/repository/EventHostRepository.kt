package pl.directsoftware.kkbn.repository

import kotlinx.coroutines.runBlocking
import pl.directsoftware.kkbn.api.data.EventHost
import pl.directsoftware.kkbn.repository.base.AbstractRepository
import pl.directsoftware.kkbn.utils.Logger

object EventHostRepository : AbstractRepository() {

    private val logger = Logger(EventHostRepository::class.java.simpleName)

    fun getEventHosts(): List<EventHost>? {
        val events = runBlocking { privateService.getEventHosts() }
        if (!events.success) {
            logger.methodError("getEvents", events.errors?.firstOrNull() ?: "unknown")
            return null
        }

        return events.data
    }

    fun getEventParticipants(): List<EventHost>? {
        val events = runBlocking { privateService.getEventParticipants() }
        if (!events.success) {
            logger.methodError("getEventParticipants", events.errors?.firstOrNull() ?: "unknown")
            return null
        }

        return events.data
    }

}