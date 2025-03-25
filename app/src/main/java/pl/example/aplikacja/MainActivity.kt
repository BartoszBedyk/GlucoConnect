package pl.example.aplikacja

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import dagger.hilt.android.AndroidEntryPoint
import pl.example.aplikacja.UiElements.MainApp
import pl.example.aplikacja.ui.theme.AplikacjaTheme
import pl.example.bluetoothmodule.presentation.BluetoothViewModel
import pl.example.networkmodule.apiData.enumTypes.UserType
import pl.example.networkmodule.clearToken
import pl.example.networkmodule.getToken
import pl.example.networkmodule.saveToken

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //clearToken(applicationContext)
        saveToken(applicationContext, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJteWF1ZGllbmNlIiwiaXNzIjoibXlpc3N1ZXIiLCJ1c2VySWQiOiJmNjk5MzZkYy0wOTYyLTQ4ZDItYTJjMi1hNWRmNDg1NzM5MTciLCJ1c2VybmFtZSI6Im0ubUB3cC5wbCIsInVzZXJUeXBlIjoiUEFUSUVOVCIsImV4cCI6MTc0MzAwMDI5OH0.V3Y8pwigJJW-nNhQ2vSZfiwczRlTRYOGDbOuvT3g6_o")
        val bluetoothViewModel = ViewModelProvider(this).get(BluetoothViewModel::class.java)
        val token = getToken(applicationContext)

        val userType = if (token != null) {
            val decoded = JWT.decode(token)
            val userTypeString = decoded.getClaim("userType").asString()
            try {
                UserType.valueOf(userTypeString ?: UserType.PATIENT.name)
            } catch (e: IllegalArgumentException) {
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
}


