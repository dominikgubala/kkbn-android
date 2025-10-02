package pl.directsoftware.kkbn.api.data

data class EventParticipant(
    val id: Int,
    val email: String?,
    val accessCode: String?,
    val companyName: String?,
    val firstName: String?,
    val lastName: String?,
    val active: Boolean?,
    val events: List<Event>?,
) {
    val description: String
        get() {
            var str = firstName.orEmpty()
            str += " " + lastName.orEmpty()

            return str.ifBlank { email.orEmpty() }
        }

    override fun toString(): String {
        return "id: $id; email: $email; companyName: $companyName; events: ${events?.size}"
    }
}