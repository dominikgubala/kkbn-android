package pl.directsoftware.kkbn.repository.base

import pl.directsoftware.kkbn.api.ApiClient
import pl.directsoftware.kkbn.api.ApiPrivateClient

abstract class AbstractRepository {
    protected val service = ApiClient.apiService
    protected val privateService
        get() = ApiPrivateClient.apiService
}