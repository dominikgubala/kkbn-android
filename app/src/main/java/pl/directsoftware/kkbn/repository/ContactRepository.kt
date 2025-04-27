package pl.directsoftware.kkbn.repository

import pl.directsoftware.kkbn.api.data.Contact
import pl.directsoftware.kkbn.repository.base.AbstractRepository
import pl.directsoftware.kkbn.utils.Logger

object ContactRepository : AbstractRepository() {

    private val logger = Logger(ContactRepository::class.java.simpleName)

    suspend fun getContacts(): List<Contact>? {
        val downloads = service.getContacts()
        if (!downloads.success) {
            logger.methodError("getContacts", downloads.errors?.firstOrNull() ?: "unknown")
            return null
        }

        return downloads.data
    }
}