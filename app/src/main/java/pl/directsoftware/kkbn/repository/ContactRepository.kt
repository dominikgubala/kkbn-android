package pl.directsoftware.kkbn.repository

import kotlinx.coroutines.runBlocking
import pl.directsoftware.kkbn.api.data.Contact
import pl.directsoftware.kkbn.repository.base.AbstractRepository
import pl.directsoftware.kkbn.utils.Logger

class ContactRepository : AbstractRepository() {

    fun getContacts(): List<Contact>? {
        val downloads = runBlocking { service.getContacts() }
        if (!downloads.success) {
            logger.methodError("getContacts", downloads.errors?.firstOrNull() ?: "unknown")
            return null
        }

        return downloads.data
    }

    companion object {

        private val logger = Logger(ContactRepository::class.java.simpleName)
    }
}