package pl.directsoftware.kkbn.api.data

data class Committee(
    val fullName: String,
    val work: String,
    val role: String?,
    val main: Boolean,
    val avatar: String?,
) {
    val key: String = "$fullName^$work^$role"
}