package pl.directsoftware.kkbn.repository

import kotlinx.coroutines.runBlocking
import pl.directsoftware.kkbn.api.data.Event
import pl.directsoftware.kkbn.repository.base.AbstractRepository
import pl.directsoftware.kkbn.utils.Logger

object EventRepository : AbstractRepository() {

    private val logger = Logger(EventRepository::class.java.simpleName)

    fun getEvents(): List<Event>? {
        val events = runBlocking { privateService.getEvents() }
        if (!events.success) {
            logger.methodError("getEvents", events.errors?.firstOrNull() ?: "unknown")
            return null
        }

        return events.data
    }

}