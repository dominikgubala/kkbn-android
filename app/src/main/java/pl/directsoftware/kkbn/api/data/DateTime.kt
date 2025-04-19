package pl.directsoftware.kkbn.api.data

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

data class DateTime(
    val date: String,
    val timezoneType: Int,
    val timezone: String
) {
    private fun toDate(): Date {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        val localDateTime = LocalDateTime.parse(date, formatter);
        val zoneId = ZoneId.of(timezone);
        return Date.from(localDateTime.atZone(zoneId).toInstant());
    }

    fun getDateStr(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(toDate());
    }
}