package pl.directsoftware.kkbn.api.data

import org.json.JSONObject
import pl.directsoftware.kkbn.common.ISerializable
import java.net.URLDecoder
import java.net.URLEncoder
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class Event(
    val id: Int,
    val title: String,
    val speaker: String?,
    val date: String?,
    val dateTimeBegin: String?,
    val dateTimeEnd: String?,
    val host: EventHost?,
    val currentParticipants: Int,
    val maxParticipants: Int,
): ISerializable {

    fun getDuration(): String {

        if (dateTimeBegin == null || dateTimeEnd == null) return ""

        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val start = LocalTime.parse(dateTimeBegin, formatter)
        var end = LocalTime.parse(dateTimeEnd, formatter)

        if (end.isBefore(start)) {
            end = end.plusHours(24)
        }

        val duration = Duration.between(start, end)

        return "${duration.toMinutes()} min."
    }

    fun getTime(): String {
        val sb = StringBuilder()

        if (dateTimeBegin != null)
            sb.append(dateTimeBegin)

        if (dateTimeEnd != null) {
            if (sb.isNotEmpty())
                sb.append(" - ")
            sb.append(dateTimeEnd)
        }
        return sb.toString()
    }

    override fun serialize(): String {
        val json = JSONObject().apply {
            put("id", id)
            put("title", title)
            put("speaker", speaker)
            put("date", date)
            put("dateTimeBegin", dateTimeBegin)
            put("dateTimeEnd", dateTimeEnd)
            put("host", host?.toJSONObject())
            put("currentParticipants", currentParticipants)
            put("maxParticipants", maxParticipants)
        }
        return URLEncoder.encode(json.toString(), "UTF-8")
    }

    companion object {
        fun restore(value: String?): Event? {
            if (value == null) return null
            return try {
                val decoded = URLDecoder.decode(value, "UTF-8")
                val json = JSONObject(decoded)
                Event(
                    json.getInt("id"),
                    json.optString("title"),
                    json.opt("speaker")?.toString(),
                    json.opt("date")?.toString(),
                    json.opt("dateTimeBegin")?.toString(),
                    json.opt("dateTimeEnd")?.toString(),
                    EventHost.fromJson(json.optJSONObject("host")),
                    json.optInt("currentParticipants"),
                    json.optInt("maxParticipants"),
                )
            } catch (_: Exception) {
                null
            }
        }
    }
}