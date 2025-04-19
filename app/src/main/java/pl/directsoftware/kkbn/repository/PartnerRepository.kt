package pl.directsoftware.kkbn.repository

import kotlinx.coroutines.runBlocking
import pl.directsoftware.kkbn.api.data.Partner
import pl.directsoftware.kkbn.repository.base.AbstractRepository
import pl.directsoftware.kkbn.utils.Logger

class PartnerRepository : AbstractRepository() {

    fun getPartners(): List<Partner>? {
        val partners = runBlocking { service.getPartners() }
        if (!partners.success) {
            logger.methodError("getPartners", partners.errors?.firstOrNull() ?: "unknown")
            return null
        }

        return partners.data
    }

    companion object {

        private val logger = Logger(PartnerRepository::class.java.simpleName)
    }
}