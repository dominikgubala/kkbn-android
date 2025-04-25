package pl.directsoftware.kkbn.api.defs

import pl.directsoftware.kkbn.auth.EventMemberType
import java.util.Base64

data class AccessCode(
    val email: String,
    val code: String,
    val memberType: EventMemberType,
    val memberId: Int,
) {
    override fun toString(): String {
        return "$email^$code^${memberType.code}"
    }

    val token: String =
        Base64.getEncoder().encodeToString(
            "%s^%s^%d".format(email, code, memberType.code).toByteArray(Charsets.UTF_8)
        )
}