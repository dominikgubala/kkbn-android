package pl.directsoftware.kkbn.auth

enum class EventMemberType(val code: Int) {
    ANONYMOUS(-1),
    PARTICIPANT(1),
    HOST(2);

    companion object {
        fun fromInt(code: Int): EventMemberType {
            return entries.firstOrNull { it.code == code } ?: ANONYMOUS
        }
    }
}