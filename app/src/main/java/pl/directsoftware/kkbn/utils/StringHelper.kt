package pl.directsoftware.kkbn.utils

object StringHelper {
    fun getStringOrNull(value: String?): String? {
        if (value.isNullOrEmpty()) return null
        if (value == "null") return null
        return value
    }

    fun getString(value: String?): String {
        if (value.isNullOrEmpty()) throw IllegalArgumentException("Value is null")
        if (value == "null") throw IllegalArgumentException("Value is null")
        return value
    }
}