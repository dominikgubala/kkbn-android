package pl.directsoftware.kkbn.api.data

data class Profile(
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val companyName: String?,
    val active: Boolean,
    val events: List<Event>?,
    val hostEvents: List<Event>?,
)