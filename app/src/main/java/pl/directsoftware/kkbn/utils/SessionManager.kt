package pl.directsoftware.kkbn.utils

import pl.directsoftware.kkbn.auth.EventMemberType
import pl.directsoftware.kkbn.auth.MemberRole

object SessionManager {

    var identifier: String? = null
        private set

    private var memberType: EventMemberType = EventMemberType.ANONYMOUS

    fun authorize(identifier: String, memberType: EventMemberType) {
        this.identifier = identifier
        this.memberType = memberType
    }

    fun isGranted(role: MemberRole): Boolean {
        return when (role) {
            MemberRole.EDIT_PARTICIPANT ->
                (memberType == EventMemberType.PARTICIPANT_MANAGEMENT || memberType == EventMemberType.MANAGEMENT)
            MemberRole.EDIT_HOST ->
                (memberType == EventMemberType.HOST_MANAGEMENT || memberType == EventMemberType.MANAGEMENT)
            MemberRole.EDIT_ALL -> memberType == EventMemberType.MANAGEMENT
        }
    }
}