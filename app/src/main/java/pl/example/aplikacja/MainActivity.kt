package pl.example.aplikacja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.auth0.jwt.JWT
import dagger.hilt.android.AndroidEntryPoint
import pl.example.aplikacja.UiElements.MainApp
import pl.example.aplikacja.ui.theme.AplikacjaTheme
import pl.example.bluetoothmodule.presentation.BluetoothViewModel
import pl.example.databasemodule.database.security.isDeviceRooted
import pl.example.databasemodule.database.security.loadBase
import pl.example.databasemodule.database.security.wipeAppDataAndExit
import pl.example.networkmodule.apiData.enumTypes.UserType
import pl.example.networkmodule.getToken

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { false }

        super.onCreate(savedInstanceState)
        // deleteDatabase(this)
        loadBase(this)
        if (isDeviceRooted(this)) {
            wipeAppDataAndExit(this)
            return
        }

            val bluetoothViewModel = ViewModelProvider(this).get(BluetoothViewModel::class.java)
        val token = getToken(applicationContext) ?: savedInstanceState?.getString("token")

        val userType = if (token != null) {
            val decoded = JWT.decode(token)
            val userTypeString = decoded.getClaim("userType").asString()
            try {
                UserType.valueOf(userTypeString ?: UserType.PATIENT.name)
            } catch (_: IllegalArgumentException) {
                UserType.PATIENT
            }
        } else {
            UserType.PATIENT
        }

        val navBarViewModel = ViewModelProvider(this, BottomNavBarViewModelFactory(userType))
            .get(BottomNavBarViewModel::class.java)

        setContent {
            AplikacjaTheme {
                MainApp(navBarViewModel, bluetoothViewModel)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("token", getToken(applicationContext))
    }
}
