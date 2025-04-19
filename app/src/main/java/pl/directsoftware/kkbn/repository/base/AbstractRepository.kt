package pl.directsoftware.kkbn.repository.base

import pl.directsoftware.kkbn.api.ApiClient

abstract class AbstractRepository {
    protected val service = ApiClient.apiService

}