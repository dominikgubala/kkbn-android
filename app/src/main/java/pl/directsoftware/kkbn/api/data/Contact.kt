package pl.directsoftware.kkbn.api.data

data class Contact(
    val name: String,
    val additionalData: String,
    val emails: List<String>,
    val phones: List<String>,
    val heading: String?,
)