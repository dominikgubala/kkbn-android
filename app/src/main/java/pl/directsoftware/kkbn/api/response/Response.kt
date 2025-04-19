package pl.directsoftware.kkbn.api.response

data class Response<T>(
    val success: Boolean,
    val data: T?,
    val errors: List<String>?
)