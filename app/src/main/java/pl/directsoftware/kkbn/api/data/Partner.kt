package pl.directsoftware.kkbn.api.data

import pl.directsoftware.kkbn.common.ISerializable
import java.net.URLDecoder
import java.net.URLEncoder

data class Partner(
    val avatar: String,
    val url: String?,
    val name: String?,
): ISerializable {
    override fun serialize(): String {
        return URLEncoder.encode("$avatar^$url^$name", "UTF-8")
    }

    companion object {
        fun restore(value: String?): Partner? {
            val members = URLDecoder.decode(value, "UTF-8").split('^') ?: return null

            try {
                return Partner(
                    members.getOrNull(0)!!,
                    members.getOrNull(1),
                    members.getOrNull(2),
                )
            } catch (_: Exception) {
            }
            return null
        }
    }
}