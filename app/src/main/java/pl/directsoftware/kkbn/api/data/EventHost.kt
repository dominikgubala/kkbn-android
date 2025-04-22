package pl.directsoftware.kkbn.api.data

data class EventHost(
    val id: Int,
    val accessCode: String?,
    val companyName: String?,
    val firstName: String?,
    val lastName: String?,
    val active: Boolean,
    // todo: events
)