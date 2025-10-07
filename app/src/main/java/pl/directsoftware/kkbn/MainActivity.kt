package pl.directsoftware.kkbn

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import pl.directsoftware.kkbn.api.ApiClient
import pl.directsoftware.kkbn.ui.theme.KKBNTheme
import pl.directsoftware.kkbn.ui.theme.MainScreen
import pl.directsoftware.kkbn.ui.theme.SetSystemBars
import pl.directsoftware.kkbn.utils.Logger

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApiClient.cacheDir = this.cacheDir

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            KKBNTheme {
                SetSystemBars()
                MainScreen(viewModel)
            }
        }
    }

    companion object {
        private val logger = Logger(MainActivity::class.java.simpleName)
    }
}