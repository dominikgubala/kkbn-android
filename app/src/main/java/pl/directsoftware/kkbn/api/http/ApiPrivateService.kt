package pl.directsoftware.kkbn.api.http

import pl.directsoftware.kkbn.api.data.Event
import pl.directsoftware.kkbn.api.data.EventHost
import pl.directsoftware.kkbn.api.data.Profile
import pl.directsoftware.kkbn.api.response.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiPrivateService {

    @GET("place") // todo: zmiana hosta na jakiś inny niż ping
    suspend fun ping(): Response<Unit>

    @GET("events")
    suspend fun getEvents(): Response<List<Event>?>

    @GET("event-hosts")
    suspend fun getEventHosts(): Response<List<EventHost>?>

    @GET("event-participants")
    suspend fun getEventParticipants(): Response<List<EventHost>?>

    @FormUrlEncoded
    @POST("events/{id}")
    suspend fun updateEvent(
        @Path("id") id: Int,
        @Field("title") title: String,
        @Field("dateTimeBegin") dateTimeBegin: String?,
        @Field("dateTimeEnd") dateTimeEnd: String?,
        @Field("host") host: Int?,
        @Field("maxParticipants") maxParticipants: Int,
    ): Response<Event?>

    @FormUrlEncoded
    @POST("events/{id}/register")
    suspend fun registerParticipant(
        @Path("id") id: Int,
        @Field("memberId") memberId: Int,
    ): Response<Event?>

    @FormUrlEncoded
    @POST("events/{id}/unregister")
    suspend fun unregisterParticipant(
        @Path("id") id: Int,
        @Field("memberId") memberId: Int,
    ): Response<Event?>

    @FormUrlEncoded
    @POST("event-hosts/{id}")
    suspend fun updateEventHost(
        @Path("id") id: Int,
        @Field("active") title: Boolean,
    ): Response<EventHost?>

    @FormUrlEncoded
    @POST("event-participants/{id}")
    suspend fun updateEventParticipant(
        @Path("id") id: Int,
        @Field("active") title: Boolean,
    ): Response<EventHost?>

    @GET("profile/participant-events/{eventId}/is-registered")
    suspend fun isRegistered(
        @Path("eventId") eventId: Int
    ): Response<Map<String,Boolean>>

    @GET("profile")
    suspend fun getProfile(): Response<Profile>
}