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

    fun updateEvent(event: Event): Event? {
        val events = runBlocking {
            try {
                logger.debug("updateEvent, id(${event.id}), title(${event.title}), dateTimeBegin(${event.dateTimeBegin}), dateTimeEnd(${event.dateTimeEnd}), host(${event.host?.id}), maxParticipants(${event.maxParticipants})")
                privateService.updateEvent(
                    event.id,
                    event.title,
                    event.dateTimeBegin,
                    event.dateTimeEnd,
                    event.host?.id,
                    event.maxParticipants
                )
            } catch (e: Exception) {
                logger.methodException("updateEvent(${event.id})", e)
                return@runBlocking null
            }
        }

        if (events == null) return null

        if (!events.success) {
            logger.methodError("updateEvent($event.id)", events.errors?.firstOrNull() ?: "unknown")
            return null
        }

        return events.data
    }

}