package pl.directsoftware.kkbn.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.ui.graphics.vector.ImageVector
import pl.directsoftware.kkbn.R

enum class Route(
    val type: String,
    val textId: Int,
    val main: Boolean,
    val icon: ImageVector,
) {
    AGENDA("agenda", R.string.title_agenda, true, Icons.Default.CalendarMonth),
    NEWS("news", R.string.title_news, true, Icons.Default.Newspaper),
    NEWS_BY_ID("news/{id}", R.string.title_news, false, Icons.Default.Newspaper),
    COMMITTEE("committee", R.string.title_committee, true, Icons.Default.Groups),
    PARTNERS("partners", R.string.title_partners, true, Icons.Default.Handshake),
    PARTNERS_BY_OBJECT("partners/{object}", R.string.title_partners, false, Icons.Default.Handshake),
    EXHIBITORS("exhibitors", R.string.title_exhibitors, true, Icons.Default.Campaign),
    EXHIBITORS_BY_OBJECT("exhibitors/{object}", R.string.title_exhibitors, false, Icons.Default.Campaign),
    PLACE("place", R.string.title_place, true, Icons.Default.LocationOn),
    CONTACT("contact", R.string.title_contacts, true, Icons.Default.ContactPage),
    DOWNLOADS("downloads", R.string.title_downloads, true, Icons.Default.FileDownload),
    EVENT_BY_OBJECT("event/{object}", R.string.title_agenda, false, Icons.Default.Event),
    LOGIN("login", R.string.title_login, true, Icons.AutoMirrored.Filled.Login),
    MY_ACCOUNT("my-account", R.string.title_my_account, true, Icons.Default.AccountCircle),

    EVENT_MANAGEMENT("event-management$", R.string.title_event_management, false, Icons.Default.Info),
    EVENT_MANAGEMENT_BY_OBJECT("event-management/{object}", R.string.title_event_management, false, Icons.Default.Info),
    PARTICIPANTS_MANAGEMENT("participants-management$", R.string.title_participant_management, false, Icons.Default.Info),
    PARTICIPANTS_MANAGEMENT_BY_OBJECT("participants-management/{object}", R.string.title_participant_management, false, Icons.Default.Info),
    HOST_MANAGEMENT("host-management$", R.string.title_host_management, false, Icons.Default.Info),
    HOST_MANAGEMENT_BY_OBJECT("host-management/{object}", R.string.title_host_management, false, Icons.Default.Info),

    ;

    fun setId(identifier: Int): String {
        return type.replace("{id}", "$identifier")
    }

    fun setObject(other: ISerializable): String {
        return type.replace("{object}", other.serialize())
    }

    companion object {
        fun isNested(route: String?): Boolean {
            if (route.isNullOrEmpty()) return false
            return route.endsWith('$') || route.contains("/")
        }
    }
}