package pl.directsoftware.kkbn.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.directsoftware.kkbn.R
import pl.directsoftware.kkbn.common.Route

object SessionManager {

    private val _authenticated: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val authenticated: StateFlow<Boolean> = _authenticated.asStateFlow()

    fun login(
        email: String,
        token: String,
        navController: NavController,
        context: Context,
    ) {
        if (email == "" || token == "") {
            Toast.makeText(
                context,
                R.string.auth_error,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            _authenticated.value = true
            Toast.makeText(
                context,
                R.string.logged_in,
                Toast.LENGTH_SHORT
            ).show()

            navController.navigate(Route.NEWS.type)
        }
    }
}