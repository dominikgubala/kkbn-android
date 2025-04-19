package pl.directsoftware.kkbn.api.data

import pl.directsoftware.kkbn.common.ISerializable
import pl.directsoftware.kkbn.utils.StringHelper
import java.net.URLDecoder
import java.net.URLEncoder
import java.time.LocalTime
import java.time.Duration
import java.time.format.DateTimeFormatter

data class Event(
    val id: Int,
    val title: String,
    val speaker: String?,
    val dateTimeBegin: String?,
    val dateTimeEnd: String?,
    val host: String?,
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

//        val totalMinutes = duration.toMinutes()
//        val hours = totalMinutes / 60
//        val minutes = totalMinutes % 60
//
//        val sb = StringBuilder()
//
//        if (hours != 0L)
//            sb.append("$hours h")
//
//        if (hours != 0L && minutes != 0L)
//            sb.append(", ")
//
//        if (minutes != 0L)
//            sb.append("$minutes min.")
//
//        return sb.toString()
    }


    override fun serialize(): String {
        return URLEncoder.encode("$id^$title^$speaker^$dateTimeBegin^$dateTimeEnd^$host^$currentParticipants^$maxParticipants", "UTF-8")
    }

    companion object {
        fun restore(value: String?): Event? {
            val members = URLDecoder.decode(value, "UTF-8").split('^') ?: return null

            try {
                return Event(
                    members.getOrNull(0)!!.toInt(),
                    StringHelper.getString(members.getOrNull(1)),
                    StringHelper.getStringOrNull(members.getOrNull(2)),
                    StringHelper.getStringOrNull(members.getOrNull(3)),
                    StringHelper.getStringOrNull(members.getOrNull(4)),
                    StringHelper.getStringOrNull(members.getOrNull(5)),
                    members.getOrNull(6)!!.toInt(),
                    members.getOrNull(7)!!.toInt(),
                )
            } catch (_: Exception) {
            }
            return null
        }
    }
}