package pl.example.bluetoothmodule.presentation

import pl.example.bluetoothmodule.domain.BluetoothDevice

data class BluetoothUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList()

)
