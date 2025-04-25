package pl.directsoftware.kkbn.utils

import pl.directsoftware.kkbn.auth.EventMemberType
import pl.directsoftware.kkbn.auth.MemberRole

object SessionManager {

    var identifier: String? = null
        private set

    var memberId: Int? = null
        private set

    private var memberType: EventMemberType = EventMemberType.ANONYMOUS

    fun authorize(identifier: String, memberType: EventMemberType, memberId: Int?) {
        this.identifier = identifier
        this.memberType = memberType
        this.memberId = memberId
    }

    fun destroy() {
        this.identifier = null
        this.memberType = EventMemberType.ANONYMOUS
        this.memberId = -1
    }

    fun isGranted(role: MemberRole): Boolean {
        return when (role) {
            MemberRole.EDIT_PARTICIPANT ->
                (memberType == EventMemberType.PARTICIPANT_MANAGEMENT || memberType == EventMemberType.MANAGEMENT)
            MemberRole.EDIT_HOST ->
                (memberType == EventMemberType.HOST_MANAGEMENT || memberType == EventMemberType.MANAGEMENT)
            MemberRole.EDIT_ALL -> memberType == EventMemberType.MANAGEMENT
            MemberRole.PARTICIPANT ->
                (memberType == EventMemberType.MANAGEMENT)
        }
    }
}