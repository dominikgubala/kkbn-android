package pl.directsoftware.kkbn.api.data

import org.json.JSONObject
import pl.directsoftware.kkbn.common.ISerializable
import java.net.URLDecoder
import java.net.URLEncoder

data class EventHost(
    val id: Int,
    val email: String?,
    val accessCode: String?,
    val companyName: String?,
    val firstName: String?,
    val lastName: String?,
    val active: Boolean,
    // todo: events
) : ISerializable {

    constructor(id: Int) : this(id, null, null, null, null, null, false)

    override fun toJSONObject(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("email", email)
            put("accessCode", accessCode)
            put("companyName", companyName)
            put("firstName", firstName)
            put("lastName", lastName)
            put("active", active)
        }
    }

    override fun serialize(): String {
        return URLEncoder.encode(toJSONObject().toString(), "UTF-8")
    }

    override fun toString(): String {
        return "$firstName $lastName".trim()
    }

    companion object {
        fun restore(value: String?): EventHost? {
            if (value == null) return null
            return try {
                val decoded = URLDecoder.decode(value, "UTF-8")
                val json = JSONObject(decoded)
                EventHost(
                    json.getInt("id"),
                    json.opt("email")?.toString(),
                    json.opt("accessCode")?.toString(),
                    json.opt("companyName")?.toString(),
                    json.opt("firstName")?.toString(),
                    json.opt("lastName")?.toString(),
                    json.optBoolean("active", false)
                )
            } catch (_: Exception) {
                null
            }
        }

        fun fromJson(json: JSONObject?): EventHost? {
            return try {
                if (json == null) return null
                EventHost(
                    json.getInt("id"),
                    json.opt("email")?.toString(),
                    json.opt("accessCode")?.toString(),
                    json.opt("companyName")?.toString(),
                    json.opt("firstName")?.toString(),
                    json.opt("lastName")?.toString(),
                    json.optBoolean("active", false)
                )
            } catch (_: Exception) {
                null
            }
        }
    }

}