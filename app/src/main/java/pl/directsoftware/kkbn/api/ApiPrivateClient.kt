package pl.directsoftware.kkbn.api

import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import pl.directsoftware.kkbn.api.ApiClient.BASE_TOKEN
import pl.directsoftware.kkbn.api.ApiClient.BASE_URL
import pl.directsoftware.kkbn.api.http.ApiPrivateService
import pl.directsoftware.kkbn.utils.Logger
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiPrivateClient {

    private val logger = Logger(ApiPrivateClient::class.java.simpleName)

    private var bearerToken: String? = null

    private val client: OkHttpClient
        get() = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(bearerToken ?: BASE_TOKEN))
            .build()

    private var _apiService: ApiPrivateService = createApiService()

    val apiService: ApiPrivateService
        get() = _apiService

    fun init(token: String?) {
        bearerToken = token
        _apiService = createApiService()
    }

    fun ping(): Boolean {
        return runBlocking {
            try {
                apiService.ping().success
            } catch (e: Exception) {
                logger.methodException("ping", e)
                false
            }
        }
    }

    private fun createApiService(): ApiPrivateService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiPrivateService::class.java)
    }
}