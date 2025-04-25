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
            //.cache(cache)
            .addInterceptor(AuthInterceptor(bearerToken ?: BASE_TOKEN))
            //.addInterceptor(CacheInterceptor())
            //.addNetworkInterceptor(CacheInterceptor())
            .build()

    val apiService: ApiPrivateService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiPrivateService::class.java)
    }

    fun init(bearerToken: String) {
        this.bearerToken = bearerToken
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


}