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
import pl.directsoftware.kkbn.api.data.Event
import pl.directsoftware.kkbn.api.data.EventDay
import pl.directsoftware.kkbn.api.data.EventHost
import pl.directsoftware.kkbn.api.data.News
import pl.directsoftware.kkbn.api.data.Partner
import pl.directsoftware.kkbn.auth.EventMemberType
import pl.directsoftware.kkbn.defs.Setting
import pl.directsoftware.kkbn.repository.AgendaRepository
import pl.directsoftware.kkbn.repository.CommitteeRepository
import pl.directsoftware.kkbn.repository.CommonRepository
import pl.directsoftware.kkbn.repository.EventMemberRepository
import pl.directsoftware.kkbn.repository.EventRepository
import pl.directsoftware.kkbn.repository.NewsRepository
import pl.directsoftware.kkbn.repository.PartnerRepository
import pl.directsoftware.kkbn.utils.FileHelper.loadImageFromUrl
import pl.directsoftware.kkbn.utils.Logger
import pl.directsoftware.kkbn.utils.SessionManager
import pl.directsoftware.kkbn.utils.Settings
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
            val token = runBlocking { Settings.readString(Setting.BEARER_TOKEN) }
            if (!token.isNullOrEmpty()) {
                ApiPrivateClient.init(token)
                if (ApiPrivateClient.ping()) {
                    runBlocking {
                        val email = Settings.readString(Setting.USERNAME)
                        val memberId = Settings.readInt(Setting.MEMBER_ID)
                        val memberType = EventMemberType.fromInt(Settings.readInt(Setting.MEMBER_TYPE)) //EventMemberType.MANAGEMENT todo EventMemberType.fromInt(Settings.readInt(Setting.MEMBER_TYPE))
                        SessionManager.authorize(email!!, memberType, memberId)
                    }
                    _isLoggedIn.value = true
                    logger.debug("Automatyczna autentykacja zakończona powodzeniem")
                }
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
            SessionManager.destroy()
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

    fun getNews(): List<News>? {
        try {
            val newsList = NewsRepository.getNews()
            newsList?.forEach { news ->
                news.bitmap = runBlocking { loadImageFromUrl("https://kkbn.pl/${news.imagePath}") }
            }
            return newsList
        } catch (e: Exception) {
            logger.methodException("getNews", e)
        }
        return null
    }

    fun getNews(id: Int): News? {
        try {
            return NewsRepository.getNewsById(id)
        } catch (e: Exception) {
            logger.methodException("getNews($id)", e)
        }
        return null
    }

    fun getAgendaEvents(): List<EventDay>? {
        try {
            return AgendaRepository.getEvents()
        } catch (e: Exception) {
            logger.methodException("getAgendaEvents", e)
        }
        return null
    }

    fun getCommittee(): List<Committee>? {
        try {
            return CommitteeRepository.getCommittee()
        } catch (e: Exception) {
            logger.methodException("getCommittee", e)
        }
        return null
    }

    fun getPartners(): List<Partner>? {
        try {
            return PartnerRepository.getPartners()
        } catch (e: Exception) {
            logger.methodException("getPartners", e)
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

    fun updateEvent(
        event: Event
    ): Event? {
        return EventRepository.updateEvent(event)
    }

    fun registerParticipant(
        eventId: Int,
        memberId: Int,
    ): Event? {
        return EventRepository.registerParticipant(eventId, memberId)
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