package pl.directsoftware.kkbn.api.http

import okhttp3.RequestBody
import pl.directsoftware.kkbn.api.data.Committee
import pl.directsoftware.kkbn.api.data.Contact
import pl.directsoftware.kkbn.api.data.Download
import pl.directsoftware.kkbn.api.data.EventDay
import pl.directsoftware.kkbn.api.data.Exhibitor
import pl.directsoftware.kkbn.api.data.MemberType
import pl.directsoftware.kkbn.api.data.News
import pl.directsoftware.kkbn.api.data.Partner
import pl.directsoftware.kkbn.api.data.Place
import pl.directsoftware.kkbn.api.response.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @GET("news")
    suspend fun getNews(): Response<List<News>>

    @GET("news/{id}")
    suspend fun getNewsById(@Path("id") userId: Int): Response<News>

    @GET("committee")
    suspend fun getCommittee(): Response<List<Committee>>

    @GET("partners")
    suspend fun getPartners(): Response<List<Partner>>

    @GET("exhibitors")
    suspend fun getExhibitors(): Response<List<Exhibitor>>

    @GET("place")
    suspend fun getPlace(): Response<Place>

    @GET("downloads")
    suspend fun getDownloads(): Response<List<Download>>

    @GET("contacts")
    suspend fun getContacts(): Response<List<Contact>>

    @GET("agenda")
    suspend fun getAgenda(): Response<List<EventDay>>

    @Multipart
    @POST("authorize")
    suspend fun authorize(
        @Part("email") email: RequestBody,
        @Part("code") code: RequestBody
    ): Response<MemberType>
}