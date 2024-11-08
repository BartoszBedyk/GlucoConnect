package pl.example.bluetoothmodule.presentation

import pl.example.bluetoothmodule.domain.BluetoothDevice

data class BluetoothUiState(
    val scannedDevices: List<android.bluetooth.BluetoothDevice> = emptyList(),
    val pairedDevices: List<android.bluetooth.BluetoothDevice> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null,

)
