package pl.directsoftware.kkbn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import pl.directsoftware.kkbn.api.ApiClient
import pl.directsoftware.kkbn.ui.theme.KKBNTheme
import pl.directsoftware.kkbn.ui.theme.MainScreen
import pl.directsoftware.kkbn.utils.Logger

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApiClient.cacheDir = this.cacheDir

        enableEdgeToEdge()
        setContent {
            KKBNTheme {
                MainScreen(viewModel)
            }
        }
    }

    companion object {
        private val logger = Logger(MainActivity::class.java.simpleName)
    }
}