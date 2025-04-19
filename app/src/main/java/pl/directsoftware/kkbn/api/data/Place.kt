package pl.directsoftware.kkbn.api.data

data class Place(
    val name: String,
    val street: String,
    val postalCode: String,
    val city: String,
    val country: String,
    val phones: List<String>,
    val emails: List<String>,
    val image: String,
    val url: String,
    val mapUrl: String,
)