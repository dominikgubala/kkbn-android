package pl.directsoftware.kkbn.repository

import kotlinx.coroutines.runBlocking
import pl.directsoftware.kkbn.api.data.Download
import pl.directsoftware.kkbn.repository.base.AbstractRepository
import pl.directsoftware.kkbn.utils.Logger

class DownloadRepository : AbstractRepository() {

    fun getDownloads(): List<Download>? {
        val downloads = runBlocking { service.getDownloads() }
        if (!downloads.success) {
            logger.methodError("getExhibitors", downloads.errors?.firstOrNull() ?: "unknown")
            return null
        }

        return downloads.data
    }

    companion object {

        private val logger = Logger(DownloadRepository::class.java.simpleName)
    }
}