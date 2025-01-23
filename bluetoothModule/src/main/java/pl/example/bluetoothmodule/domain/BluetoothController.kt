package pl.example.bluetoothmodule.domain

import android.bluetooth.BluetoothGatt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow

interface BluetoothController {
    val isConnected: StateFlow<Boolean>
    val scannedDevices: StateFlow<List<android.bluetooth.BluetoothDevice>>
    val pairedDevices: StateFlow<List<android.bluetooth.BluetoothDevice>>
    val errors: SharedFlow<String>
    private val _receivedDataFlow: MutableSharedFlow<ByteArray>
        get() = MutableSharedFlow<ByteArray>()
    val receivedDataFlow: Flow<ByteArray>


    fun getStoredData(): List<ByteArray>


    //SEARCH AND SCAN
    fun startDiscovery()
    fun stopDiscovery()
    fun release()

    //CONNECTION
    fun startBluetoothServer(): Flow<ConnectionResult>
    fun connectToDevice(device: BluetoothDevice): Flow<ConnectionResult>
    fun connectToGattDevice(device: BluetoothDevice): Flow<ConnectionResult>
    fun closeConnection()

    //Functionalities
    fun sendCommand(gatt: BluetoothGatt, byteArray: ByteArray)

}