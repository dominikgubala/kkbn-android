package pl.directsoftware.kkbn.repository

import pl.directsoftware.kkbn.api.data.Download
import pl.directsoftware.kkbn.repository.base.AbstractRepository
import pl.directsoftware.kkbn.utils.Logger

object DownloadRepository : AbstractRepository() {

    private val logger = Logger(DownloadRepository::class.java.simpleName)

    suspend fun getDownloads(): List<Download>? {
        val downloads = service.getDownloads()
        if (!downloads.success) {
            logger.methodError("getExhibitors", downloads.errors?.firstOrNull() ?: "unknown")
            return null
        }

        return downloads.data
    }
}