package pl.directsoftware.kkbn.api

import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import pl.directsoftware.kkbn.api.http.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

object ApiClient {

    const val BASE_URL = "https://kkbn-bkp.simptestcert.pl/api/"
    const val BASE_TOKEN = "cHVibGljQHJlcXVlc3QucGxeRkZGRkZGXi0x"
    private const val CACHE_SIZE = 10L * 1024 * 1024

    lateinit var cacheDir: File

    private val cache: Cache
        get() = Cache(cacheDir, CACHE_SIZE)

    private val client: OkHttpClient
        get() = OkHttpClient.Builder()
        .cache(cache)
        .addInterceptor(AuthInterceptor(BASE_TOKEN))
        .addInterceptor(CacheInterceptor())
        .addNetworkInterceptor(CacheInterceptor())
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private class CacheInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val response = chain.proceed(chain.request())
            return response.newBuilder()
                .header("Cache-Control", "public, max-age=300")
                .build()
        }
    }

    fun clearCache() {
        cache.evictAll()
    }
}