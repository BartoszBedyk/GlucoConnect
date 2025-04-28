package pl.example.aplikacja.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import pl.example.bluetoothmodule.presentation.BluetoothViewModel


@Composable
fun GlucometerAdminScreen(
    bluetoothViewModel: BluetoothViewModel,
    navController: NavController
) {
    val recivedData by bluetoothViewModel.lastMeasurement.collectAsState()
    Column(
        modifier = Modifier.padding(16.dp).padding(bottom = 80.dp)
    ) {
        
        
        Text(text = "Zarządzanie glukometrem", color = MaterialTheme.colorScheme.primary)

        Text(text = recivedData.toString())

        TextButton(onClick = {
            bluetoothViewModel.viewModelScope.launch {
                bluetoothViewModel.readGlucometerTime()
            }
        }) {
            Text(text = "Sprawdź czas")
        }

        TextButton(onClick = {
            bluetoothViewModel.viewModelScope.launch {
                bluetoothViewModel.clearMemory()
            }
        }) {
            Text(text = "Wyczyść pamięć urządzenia")
        }

        TextButton(onClick = {
            bluetoothViewModel.viewModelScope.launch {
                bluetoothViewModel.readDeviceSerialNumber()
            }
        }) {
            Text(text = "Pobierz numer seryjny urządzenia")
        }

        TextButton(onClick = {
            bluetoothViewModel.viewModelScope.launch {
                bluetoothViewModel.turnOffDevice()
            }
        }) {
            Text(text = "Wyłącz urządzenie")
        }

        TextButton(onClick = {
            bluetoothViewModel.viewModelScope.launch {
                bluetoothViewModel.setGlucometerTime()
            }
        }) {
            Text(text = "Ustaw czas")
        }
    }
}