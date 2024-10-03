package pl.example.bluetoothmodule.permission

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class BluetoothAccessViewModel : ViewModel() {
    var isPermissionGranted by mutableStateOf(true)
        private set

    fun updatePermissionStatus(isGranted: Boolean) {
        isPermissionGranted = isGranted
    }
}


