package pl.directsoftware.kkbn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pl.directsoftware.kkbn.api.ApiPrivateClient
import pl.directsoftware.kkbn.api.data.Committee
import pl.directsoftware.kkbn.api.data.Contact
import pl.directsoftware.kkbn.api.data.Download
import pl.directsoftware.kkbn.api.data.Event
import pl.directsoftware.kkbn.api.data.EventDay
import pl.directsoftware.kkbn.api.data.EventHost
import pl.directsoftware.kkbn.api.data.EventParticipant
import pl.directsoftware.kkbn.api.data.Exhibitor
import pl.directsoftware.kkbn.api.data.News
import pl.directsoftware.kkbn.api.data.Partner
import pl.directsoftware.kkbn.api.data.Place
import pl.directsoftware.kkbn.api.data.Profile
import pl.directsoftware.kkbn.auth.EventMemberType
import pl.directsoftware.kkbn.defs.Setting
import pl.directsoftware.kkbn.repository.AgendaRepository
import pl.directsoftware.kkbn.repository.CommitteeRepository
import pl.directsoftware.kkbn.repository.CommonRepository
import pl.directsoftware.kkbn.repository.ContactRepository
import pl.directsoftware.kkbn.repository.DownloadRepository
import pl.directsoftware.kkbn.repository.EventMemberRepository
import pl.directsoftware.kkbn.repository.EventRepository
import pl.directsoftware.kkbn.repository.ExhibitorRepository
import pl.directsoftware.kkbn.repository.NewsRepository
import pl.directsoftware.kkbn.repository.PartnerRepository
import pl.directsoftware.kkbn.repository.PlaceRepository
import pl.directsoftware.kkbn.repository.ProfileRepository
import pl.directsoftware.kkbn.utils.FileHelper.loadImageFromUrl
import pl.directsoftware.kkbn.utils.Logger
import pl.directsoftware.kkbn.utils.SessionManager
import pl.directsoftware.kkbn.utils.Settings
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(

) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _refreshing = MutableStateFlow(false)
    val refreshing: StateFlow<Boolean> = _refreshing

    var onRefresh: (() -> Unit)? = null

    init {
        try {
            Settings.generateSessionToken()
            val bearerToken = runBlocking { Settings.readString(Setting.BEARER_TOKEN) }
            if (!bearerToken.isNullOrEmpty()) {
                logger.info("Bearer token exists, attempting to connect to secured API.")
                ApiPrivateClient.init(bearerToken)
                if (ApiPrivateClient.ping()) {
                    val profile = runBlocking { getProfile() }
                    if (profile != null) {
                        runBlocking {
                            val email = Settings.readString(Setting.USERNAME)
                            val memberId = Settings.readInt(Setting.MEMBER_ID)
                            val memberType = EventMemberType.fromInt(Settings.readInt(Setting.MEMBER_TYPE))
                            SessionManager.authorize(email!!, memberType, memberId)
                        }
                        _isLoggedIn.value = true
                        logger.info("Auto-login completed successfully")
                    }
                } else logger.info("Auto-login failed")
            }
        } catch (e: Exception) {
            logger.methodException("init", e)
        }
    }

    fun login(
        email: String,
        code: String,
    ): Boolean {
        if (_isLoggedIn.value)
            return true

        val accessCode = CommonRepository.authorize(email, code)
        if (accessCode != null && accessCode.memberType != EventMemberType.ANONYMOUS) {
            try {
                ApiPrivateClient.init(accessCode.token)
                if (ApiPrivateClient.ping()) {
                    SessionManager.authorize(email, accessCode.memberType, accessCode.memberId)
                    viewModelScope.launch(Dispatchers.IO) {
                        Settings.writeString(Setting.USERNAME, email)
                        Settings.writeInt(Setting.MEMBER_TYPE, accessCode.memberType.code)
                        Settings.writeString(Setting.BEARER_TOKEN, accessCode.token)
                        Settings.writeInt(Setting.MEMBER_ID, accessCode.memberId)
                    }
                    _isLoggedIn.value = true
                    return true
                }
            } catch (e: Exception) {
                logger.methodException("login", e)
            }
        }

        return false
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            Settings.writeString(Setting.USERNAME, "")
            Settings.writeString(Setting.BEARER_TOKEN, "")
            Settings.writeInt(Setting.MEMBER_TYPE, EventMemberType.ANONYMOUS.code)
            Settings.writeInt(Setting.MEMBER_ID, -1)
            Settings.generateSessionToken()
            SessionManager.destroy()
            ApiPrivateClient.init(null)
        }

        _isLoggedIn.value = false
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _refreshing.value = true
            onRefresh?.invoke()
            _refreshing.value = false
        }
    }

    suspend fun getNews(): List<News>? {
        try {
            val newsList = NewsRepository.getNews()
            newsList?.forEach { news ->
                news.bitmap = loadImageFromUrl("https://kkbn-bkp.simptestcert.pl/${news.imagePath}")
            }
            return newsList
        } catch (e: Exception) {
            logger.methodException("getNews", e)
        }
        return null
    }

    suspend fun getNews(id: Int): News? {
        try {
            return NewsRepository.getNewsById(id)
        } catch (e: Exception) {
            logger.methodException("getNews($id)", e)
        }
        return null
    }

    suspend fun getAgendaEvents(): List<EventDay>? {
        try {
            return AgendaRepository.getEvents()
        } catch (e: Exception) {
            logger.methodException("getAgendaEvents", e)
        }
        return null
    }

    suspend fun getCommittee(): List<Committee>? {
        try {
            return CommitteeRepository.getCommittee()
        } catch (e: Exception) {
            logger.methodException("getCommittee", e)
        }
        return null
    }

    suspend fun getPartners(): List<Partner>? {
        try {
            return PartnerRepository.getPartners()
        } catch (e: Exception) {
            logger.methodException("getPartners", e)
        }
        return null
    }

    suspend fun getExhibitors(): List<Exhibitor>? {
        try {
            return ExhibitorRepository.getExhibitors()
        } catch (e: Exception) {
            logger.methodException("getExhibitors", e)
        }
        return null
    }

    suspend fun getPlace(): Place? {
        try {
            val place = PlaceRepository.getPlace() ?: return null
            place.bitmap = loadImageFromUrl("https://kkbn-bkp.simptestcert.pl/${place.image}")
            return place
        } catch (e: Exception) {
            logger.methodException("getPlace", e)
        }
        return null
    }

    suspend fun getContacts(): List<Contact>? {
        try {
            return ContactRepository.getContacts()
        } catch (e: Exception) {
            logger.methodException("getContacts", e)
        }
        return null
    }

    suspend fun getDownloads(): List<Download>? {
        try {
            return DownloadRepository.getDownloads()
        } catch (e: Exception) {
            logger.methodException("getDownloads", e)
        }
        return null
    }

    suspend fun getProfile(): Profile? {
        try {
            return ProfileRepository.getProfile()
        } catch (e: HttpException) {
            logger.methodException("getProfile", e)
            if (e.code() == 401) {
                logger.info("Forced logout due to HTTP 401 error")
                logout()
            }
        } catch (e: Exception) {
            logger.methodException("getProfile", e)
        }
        return null
    }

    fun getEvents(): List<Event>? {
        try {
            return EventRepository.getEvents()
        } catch (e: Exception) {
            logger.methodException("getEvents", e)
        }
        return null
    }

    fun getEventHosts(): List<EventHost>? {
        try {
            return EventMemberRepository.getEventHosts()
        } catch (e: Exception) {
            logger.methodException("getEventHosts", e)
        }
        return null
    }

    fun getEventHostMap(): MutableMap<Int,String> {
        val map = mutableMapOf<Int,String>()
        getEventHosts()?.forEach {
            map[it.id] = "${it.firstName} ${it.lastName}"
        }
        return map
    }

    fun getEventParticipants(): List<EventHost>? {
        try {
            return EventMemberRepository.getEventParticipants()
        } catch (e: Exception) {
            logger.methodException("getEventParticipants", e)
        }
        return null
    }

    fun getEventParticipants(eventId: Int): List<EventParticipant>? {
        try {
            return EventMemberRepository.getEventParticipants(eventId)
        } catch (e: Exception) {
            logger.methodException("getEventParticipants", e)
        }
        return null
    }

    fun updateEvent(
        event: Event
    ): Event? {
        return EventRepository.updateEvent(event)
    }

    fun registerParticipant(
        eventId: Int,
        memberId: Int,
        memberType: EventMemberType,
    ): Event? {
        return EventRepository.registerParticipant(eventId, memberId, memberType)
    }

    fun unregisterParticipant(
        eventId: Int,
        memberId: Int,
        memberType: EventMemberType,
    ): Event? {
        return EventRepository.unregisterParticipant(eventId, memberId, memberType)
    }

    fun unregisterParticipant(
        eventId: Int,
        participantId: Int,
    ): Boolean {
        return EventRepository.unregisterParticipantAsHost(eventId, participantId)
    }

    suspend fun isParticipantRegistered(eventId: Int): Boolean {
        return EventMemberRepository.isRegistered(eventId)
    }

    fun updateEventHost(
        host: EventHost
    ): EventHost? {
        return EventMemberRepository.updateEventHost(host)
    }

    fun updateEventParticipant(
        participant: EventHost
    ): EventHost? {
        return EventMemberRepository.updateEventParticipant(participant)
    }

    companion object {
        private val logger = Logger(AppViewModel::class.java.simpleName)
    }
}