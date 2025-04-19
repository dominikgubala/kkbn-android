package pl.directsoftware.kkbn.api.http

import pl.directsoftware.kkbn.api.response.Response
import retrofit2.http.GET

interface ApiPrivateService {

    @GET("place") // todo: zmiana hosta na jakiś inny niż ping
    suspend fun ping(): Response<Unit>
}