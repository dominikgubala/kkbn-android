package pl.directsoftware.kkbn.repository

import kotlinx.coroutines.runBlocking
import pl.directsoftware.kkbn.api.defs.AccessCode
import pl.directsoftware.kkbn.repository.base.AbstractRepository
import pl.directsoftware.kkbn.utils.Logger

object CommonRepository : AbstractRepository() {

    private val logger = Logger(CommonRepository::class.java.simpleName)

    fun authorize(email: String, code: String): AccessCode? {
        val place = runBlocking { service.authorize(email, code) }
        if (!place.success || place.data == null) {
            logger.methodError("authorize", place.errors?.firstOrNull() ?: "unknown")
            return null
        }

        return AccessCode(
            email,
            code,
            place.data.type,
            place.data.memberId
        )
    }
}