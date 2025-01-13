package pl.example.aplikacja

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import pl.example.aplikacja.UiElements.MainApp
import pl.example.aplikacja.ui.theme.AplikacjaTheme
import pl.example.bluetoothmodule.presentation.BluetoothViewModel

@AndroidEntryPoint
//@HiltAndroidApp
class MainActivity : ComponentActivity() {
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bluetoothViewModel: BluetoothViewModel by viewModels()
        val navBarViewModel: BottomNavBarViewModel by viewModels()

        setContent {
            AplikacjaTheme {
                MainApp(navBarViewModel, bluetoothViewModel)
            }
        }
    }
}


