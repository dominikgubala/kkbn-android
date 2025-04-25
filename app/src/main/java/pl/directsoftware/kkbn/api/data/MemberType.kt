package pl.directsoftware.kkbn.api.data

import pl.directsoftware.kkbn.auth.EventMemberType

data class MemberType(
    val memberId: Int,
    val memberTypeId: Int
) {
    val type: EventMemberType
    get() = EventMemberType.fromInt(memberTypeId)
}