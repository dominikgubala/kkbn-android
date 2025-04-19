package pl.directsoftware.kkbn.common

import pl.directsoftware.kkbn.R
import pl.directsoftware.kkbn.utils.SessionManager

enum class Route(
    val type: String,
    val textId: Int,
    val main: Boolean = true,
) {
    AGENDA("agenda", R.string.title_agenda),
    NEWS("news", R.string.title_news),
    NEWS_BY_ID("news/{id}", R.string.title_news, false),
    COMMITTEE("committee", R.string.title_committee),
    PARTNERS("partners", R.string.title_partners),
    PARTNERS_BY_OBJECT("partners/{object}", R.string.title_partners, false),
    EXHIBITORS("exhibitors", R.string.title_exhibitors),
    EXHIBITORS_BY_OBJECT("exhibitors/{object}", R.string.title_exhibitors, false),
    PLACE("place", R.string.title_place),
    CONTACT("contact", R.string.title_contacts),
    DOWNLOADS("downloads", R.string.title_downloads),
    EVENT_BY_OBJECT("event/{object}", R.string.title_agenda, false),
    LOGIN("login", R.string.title_login)

    ;

    fun setId(identifier: Int): String {
        return type.replace("{id}", "$identifier")
    }

    fun setObject(other: ISerializable): String {
        return type.replace("{object}", other.serialize())
    }

    fun isGranted(): Boolean {
        if (this == LOGIN) return !SessionManager.authenticated.value
        return true
    }
}