package pl.directsoftware.kkbn.repository

import kotlinx.coroutines.runBlocking
import pl.directsoftware.kkbn.api.data.News
import pl.directsoftware.kkbn.repository.base.AbstractRepository
import pl.directsoftware.kkbn.utils.Consts.NO_VALUE
import pl.directsoftware.kkbn.utils.Logger

object NewsRepository : AbstractRepository() {

    private val logger = Logger(NewsRepository::class.java.simpleName)

    fun getNews(): List<News>? {
        val newsList = runBlocking { service.getNews() }
        if (!newsList.success) {
            logger.methodError("getNews", newsList.errors?.firstOrNull() ?: "unknown")
            return null
        }

        return newsList.data
    }

    fun getNewsById(id: Int?): News? {
        val news = runBlocking { service.getNewsById(id ?: NO_VALUE) }
        if (!news.success) {
            logger.methodError("getNews", news.errors?.firstOrNull() ?: "unknown")
            return null
        }

        return news.data
    }
}