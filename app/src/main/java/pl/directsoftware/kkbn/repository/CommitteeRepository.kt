package pl.directsoftware.kkbn.repository

import pl.directsoftware.kkbn.api.data.Committee
import pl.directsoftware.kkbn.repository.base.AbstractRepository
import pl.directsoftware.kkbn.utils.Logger

object CommitteeRepository : AbstractRepository() {

    private val logger = Logger(CommitteeRepository::class.java.simpleName)

    suspend fun getCommittee(): List<Committee>? {
        val committeeList = service.getCommittee()
        if (!committeeList.success) {
            logger.methodError("getCommittee", committeeList.errors?.firstOrNull() ?: "unknown")
            return null
        }

        return committeeList.data

    }
}