package pl.directsoftware.kkbn.auth

enum class EventMemberType(val code: Int) {
    ANONYMOUS(-1),
    PARTICIPANT(1),
    PARTICIPANT_MANAGEMENT(2),
    HOST(4),
    HOST_MANAGEMENT(8),
    MANAGEMENT(16),

    ;

    override fun toString(): String {
        return "$code"
    }

    companion object {
        fun fromInt(code: Int?): EventMemberType {
            return entries.firstOrNull { it.code == code } ?: ANONYMOUS
        }
    }
}