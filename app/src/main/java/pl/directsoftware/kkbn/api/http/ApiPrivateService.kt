package pl.directsoftware.kkbn.api.http

import pl.directsoftware.kkbn.api.data.Event
import pl.directsoftware.kkbn.api.data.EventHost
import pl.directsoftware.kkbn.api.response.Response
import retrofit2.http.GET

interface ApiPrivateService {

    @GET("place") // todo: zmiana hosta na jakiś inny niż ping
    suspend fun ping(): Response<Unit>

    @GET("events")
    suspend fun getEvents(): Response<List<Event>?>

    @GET("event-hosts")
    suspend fun getEventHosts(): Response<List<EventHost>?>

    @GET("event-participants")
    suspend fun getEventParticipants(): Response<List<EventHost>?>
}