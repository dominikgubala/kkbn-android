package pl.directsoftware.kkbn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pl.directsoftware.kkbn.api.ApiPrivateClient
import pl.directsoftware.kkbn.auth.EventMemberType
import pl.directsoftware.kkbn.defs.Setting
import pl.directsoftware.kkbn.repository.CommonRepository
import pl.directsoftware.kkbn.utils.Logger
import pl.directsoftware.kkbn.utils.Settings

class AppViewModel : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)

    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    init {
        val token = runBlocking { Settings.readString(Setting.BEARER_TOKEN) }
        if (token != null) {
            ApiPrivateClient.init(token)
            if (ApiPrivateClient.ping()) {
                _isLoggedIn.value = true
                logger.debug("Automatyczna autentykacja zakończona powodzeniem")
            }
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
                    viewModelScope.launch(Dispatchers.IO) {
                        Settings.writeString(Setting.BEARER_TOKEN, accessCode.token)
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

    companion object {
        private val logger = Logger(AppViewModel::class.java.simpleName)
    }
}