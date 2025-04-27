package pl.directsoftware.kkbn.repository

import pl.directsoftware.kkbn.api.data.News
import pl.directsoftware.kkbn.repository.base.AbstractRepository
import pl.directsoftware.kkbn.utils.Consts.NO_VALUE
import pl.directsoftware.kkbn.utils.Logger

object NewsRepository : AbstractRepository() {

    private val logger = Logger(NewsRepository::class.java.simpleName)

    suspend fun getNews(): List<News>? {
        val newsList = service.getNews()
        if (!newsList.success) {
            logger.methodError("getNews", newsList.errors?.firstOrNull() ?: "unknown")
            return null
        }

        return newsList.data
    }

    suspend fun getNewsById(id: Int?): News? {
        val news = service.getNewsById(id ?: NO_VALUE)
        if (!news.success) {
            logger.methodError("getNews($id)", news.errors?.firstOrNull() ?: "unknown")
            return null
        }

        return news.data
    }
}