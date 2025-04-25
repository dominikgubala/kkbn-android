package pl.directsoftware.kkbn.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

    fun parseTimeToCalendar(time: String?): Calendar? {
        try {
            if (time.isNullOrEmpty()) return null
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = sdf.parse(time)!!

            val calendar = Calendar.getInstance()
            calendar.time = date

            val now = Calendar.getInstance()
            calendar.set(Calendar.YEAR, now.get(Calendar.YEAR))
            calendar.set(Calendar.MONTH, now.get(Calendar.MONTH))
            calendar.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))

            return calendar
        } catch (_: Exception) { }
        return null
    }
}