package pl.example.bluetoothmodule.domain

import android.bluetooth.BluetoothGatt

sealed interface ConnectionResult {
    object ConnectionEstablished : ConnectionResult
    data class  Error(var message: String): ConnectionResult
    data class GattConnected(val gatt: BluetoothGatt) : ConnectionResult
}