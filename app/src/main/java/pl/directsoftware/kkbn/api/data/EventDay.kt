package pl.directsoftware.kkbn.api.data

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

data class EventDay(
    val id: Int,
    val date: String,
    val events: List<Event>,
) {
    override fun toString(): String {
        return date
    }

    fun getFormattedDate(): String {
        val date = LocalDate.parse(date)
        val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("pl"))
        val day = date.dayOfMonth
        val month = date.month.getDisplayName(TextStyle.SHORT, Locale("pl"))
        val year = date.year

        return "$dayOfWeek, $day $month $year"
    }
}