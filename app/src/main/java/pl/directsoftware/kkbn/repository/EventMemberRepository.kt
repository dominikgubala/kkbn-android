package pl.directsoftware.kkbn.repository

import kotlinx.coroutines.runBlocking
import pl.directsoftware.kkbn.api.data.EventHost
import pl.directsoftware.kkbn.api.data.EventParticipant
import pl.directsoftware.kkbn.repository.base.AbstractRepository
import pl.directsoftware.kkbn.utils.Logger

object EventMemberRepository : AbstractRepository() {

    private val logger = Logger(EventMemberRepository::class.java.simpleName)

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

    fun getEventParticipants(id: Int): List<EventParticipant>? {
        val events = runBlocking { privateService.getEventParticipants(id) }
        if (!events.success) {
            logger.methodError("getEventParticipants", events.errors?.firstOrNull() ?: "unknown")
            return null
        }

        return events.data
    }

    fun updateEventHost(host: EventHost): EventHost? {
        try {
            val events = runBlocking { privateService.updateEventHost(
                host.id,
                host.active,
            ) }
            if (!events.success) {
                logger.methodError("updateEventHost(${host.id})", events.errors?.firstOrNull() ?: "unknown")
                return null
            }
            return events.data
        } catch (e: Exception) {
            logger.methodException("updateEventHost(${host.id})", e)
        }
        return null
    }

    fun updateEventParticipant(participant: EventHost): EventHost? {
        try {
            val events = runBlocking { privateService.updateEventParticipant(
                participant.id,
                participant.active,
            ) }
            if (!events.success) {
                logger.methodError("updateEventParticipant(${participant.id})", events.errors?.firstOrNull() ?: "unknown")
                return null
            }
            return events.data
        } catch (e: Exception) {
            logger.methodException("updateEventHost(${participant.id})", e)
        }
        return null
    }

    suspend fun isRegistered(eventId: Int): Boolean {
        try {
            val events = privateService.isRegistered(eventId)
            if (!events.success) {
                logger.methodError("isRegistered($eventId)", events.errors?.firstOrNull() ?: "unknown")
                return false
            }
            return events.data?.getOrDefault("registered", false) ?: false
        } catch (e: Exception) {
            logger.methodException("isRegistered($eventId)", e)
        }
        return false
    }

}