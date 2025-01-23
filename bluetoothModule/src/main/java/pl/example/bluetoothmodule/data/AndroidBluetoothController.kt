package pl.example.bluetoothmodule.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.example.bluetoothmodule.domain.BluetoothController
import pl.example.bluetoothmodule.domain.BluetoothDevice
import pl.example.bluetoothmodule.domain.BluetoothDeviceDomain
import pl.example.bluetoothmodule.domain.ConnectionResult
import pl.example.bluetoothmodule.domain.MyBleManager.Companion.CHARACTERISTIC_UUID
import pl.example.bluetoothmodule.domain.parseMeasurementTimeToDate
import pl.example.bluetoothmodule.domain.responseManagement
import pl.example.bluetoothmodule.presentation.BluetoothViewModel
import java.io.IOException
import java.util.UUID
import android.bluetooth.BluetoothDevice as AndroidBluetoothDevice

@SuppressLint("MissingPermission")
class AndroidBluetoothController(private val context: Context) : BluetoothController {


    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }
    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean>
        get() = _isConnected.asStateFlow()

    private val _scannedDevices =
        MutableStateFlow<List<android.bluetooth.BluetoothDevice>>(emptyList())
    override val scannedDevices: StateFlow<List<android.bluetooth.BluetoothDevice>>
        get() = _scannedDevices.asStateFlow()

    private val _pairedDevices =
        MutableStateFlow<List<android.bluetooth.BluetoothDevice>>(emptyList())
    override val pairedDevices: StateFlow<List<android.bluetooth.BluetoothDevice>>
        get() = _pairedDevices.asStateFlow()

    private val _errors = MutableSharedFlow<String>()
    override val errors: SharedFlow<String>
        get() = _errors.asSharedFlow()

    private val _receivedDataFlow = MutableSharedFlow<ByteArray>()
    override val receivedDataFlow: Flow<ByteArray> = _receivedDataFlow.asSharedFlow()

    private val receivedDataList = mutableListOf<ByteArray>()

    override fun getStoredData(): List<ByteArray> {
        return synchronized(receivedDataList) { receivedDataList.toList() }
    }



    private val foundDeviceReceiver = FoundDeviceReceiver { device ->
        _scannedDevices.update { devices ->
            if (device in devices) devices else (devices + device)
        }
    }
    private var bluetoothStateReceiver = BluetoothStateReceiver { isConnected, bluetoothDevice ->
        if (bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == true) {
            _isConnected.update { isConnected }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                _errors.tryEmit("Can not connect to a non-paired device.")
            }

        }
    }

    private var currentServerSocket: BluetoothServerSocket? = null
    private var currentClientSocket: BluetoothSocket? = null


    init {
        updatePairedDevices()
        context.registerReceiver(
            bluetoothStateReceiver,
            IntentFilter().apply {
                addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
                addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECTED)
            }

        )
    }

    override fun startDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }
        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND)
        )

        updatePairedDevices()

        bluetoothAdapter?.startDiscovery()

    }

    override fun stopDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }
        bluetoothAdapter?.cancelDiscovery()
    }

    override fun release() {
        context.unregisterReceiver(foundDeviceReceiver)
        context.unregisterReceiver(bluetoothStateReceiver)
        closeConnection()
    }

    override fun startBluetoothServer(): Flow<ConnectionResult> {
        return flow {
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No BLUETOOTH_CONNECT permission.")
            }
            currentServerSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                "Aplikacja Inżynierska",
                UUID.fromString(SERVICE_UUID)
            )
            var shouldLoop = true
            while (shouldLoop) {
                currentClientSocket = try {
                    currentServerSocket?.accept()
                } catch (e: IOException) {
                    shouldLoop = false
                    null
                }
                emit(ConnectionResult.ConnectionEstablished)
                currentServerSocket?.let {
                    currentServerSocket?.close()
                }

            }
        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

    override fun connectToDevice(device: BluetoothDevice): Flow<ConnectionResult> {
        Log.d("CONNECT", device.name ?: "Bluetooth Device")
        return flow {
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No BLUETOOTH_CONNECT permission.")
            }

            val bluetoothDevice = bluetoothAdapter?.getRemoteDevice(device.address)



            currentClientSocket = bluetoothDevice
                ?.createRfcommSocketToServiceRecord(
                    UUID.fromString(SERVICE_UUID)
                )
            stopDiscovery()
            if (bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == false) {

            }
            currentClientSocket?.let { socket ->
                try {
                    socket.connect()
                    emit(ConnectionResult.ConnectionEstablished)

                } catch (e: IOException) {
                    socket.close()
                    currentClientSocket = null
                    emit(ConnectionResult.Error("Connection was interrupted"))
                }
            }

        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

    override fun closeConnection() {
        currentClientSocket?.close()
        currentServerSocket?.close()
        currentClientSocket = null
        currentServerSocket = null
    }


    private fun updatePairedDevices() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            return
        }
        bluetoothAdapter
            ?.bondedDevices
            ?.map { it }
            ?.also { devices ->
                _pairedDevices.update { devices }
            }
    }

    private fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val SERVICE_UUID = "00001523-1212-efde-1523-785feabcd123"
        const val CHARACTERISTIC_UUID = "00001524-1212-efde-1523-785feabcd123"
        const val CLIENT_CHARACTERISTIC_CONFIG_UUID = "00002a51-0000-1000-8000-00805f9b34fb"
    }

    override fun connectToGattDevice(
        device: BluetoothDeviceDomain,
        //context: Context
    ): Flow<ConnectionResult> {
        return callbackFlow {
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No BLUETOOTH_CONNECT permission.")
            }

            val androidBluetoothDevice: AndroidBluetoothDevice? =
                bluetoothAdapter?.getRemoteDevice(device.address)
            if (androidBluetoothDevice == null) {
                trySend(ConnectionResult.Error("Device not found"))
                return@callbackFlow
            }

            val gattCallback = object : BluetoothGattCallback() {
                override fun onConnectionStateChange(
                    gatt: BluetoothGatt,
                    status: Int,
                    newState: Int
                ) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.d("GATT", "Connected to GATT server.")
                        gatt.discoverServices()
                        trySend(ConnectionResult.GattConnected(gatt))
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        Log.d("GATT", "Disconnected from GATT server with status=$status")
                        trySend(ConnectionResult.Error("Disconnected from GATT server"))
                        gatt.close()
                    }
                }

                override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        val service = gatt.getService(UUID.fromString(SERVICE_UUID))
                        val characteristic =
                            service?.getCharacteristic(UUID.fromString(CHARACTERISTIC_UUID))
                        if (characteristic != null) {
                            Log.d("GATT_DISCOVERED", "Characteristic is nullable.")
                            enableNotifications(gatt, characteristic)
                        }
                        if (characteristic != null) {
                            if (gatt.readCharacteristic(characteristic)) {
                                Log.d("GATT_DISCOVERED", "Characteristic read request initiated.")
                            } else {
                                Log.d(
                                    "GATT_DISCOVERED",
                                    "Failed to initiate characteristic read request."
                                )
                                trySend(ConnectionResult.Error("Failed to initiate read request"))

                            }
                        } else {
                            trySend(ConnectionResult.Error("Characteristic not found"))
                        }
                    } else {
                        trySend(ConnectionResult.Error("Service discovery failed"))
                    }
                    //sendCommand(gatt) tutaj będzie pobranie wyniku czy coś, ablo execute queue, lub stack
                }

                override fun onCharacteristicWrite(
                    gatt: BluetoothGatt,
                    characteristic: BluetoothGattCharacteristic,
                    status: Int
                ) {
                    when (status) {
                        BluetoothGatt.GATT_SUCCESS -> {
                            Log.d(
                                "GATT_WRITE",
                                "Characteristic write successful: ${characteristic.uuid}"
                            )
                        }

                        BluetoothGatt.GATT_WRITE_NOT_PERMITTED -> {
                            Log.e(
                                "GATT_WRITE",
                                "Write not permitted on characteristic: ${characteristic.uuid}"
                            )
                        }

                        BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH -> {
                            Log.e(
                                "GATT_WRITE",
                                "Invalid attribute length for characteristic: ${characteristic.uuid}"
                            )
                        }

                        BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED -> {
                            Log.e(
                                "GATT_WRITE",
                                "Request not supported for characteristic: ${characteristic.uuid}"
                            )
                        }

                        else -> {
                            Log.e(
                                "GATT_WRITE",
                                "Write failed with status: $status for characteristic: ${characteristic.uuid}"
                            )
                        }
                    }

                }


                @Deprecated("Deprecated in Java")
                override fun onCharacteristicChanged(
                    gatt: BluetoothGatt?,
                    characteristic: BluetoothGattCharacteristic?
                ) {
                    Log.d("GATT_CHANGED", "Characteristic changed.")
                    val receivedData = characteristic?.value
                    if (receivedData != null) {
                        if (receivedData.isNotEmpty() && receivedData[0] == 0x54.toByte()) {
                            Log.d(
                                "GATT_NOTIFICATION",
                                "Received notification for entering communication mode."
                            )
                            handleCommunicationModeEntry()
                        } else {
                            Log.d("GATT_DATA", "Data received: ${receivedData.contentToString()}")
                        }
                    }
                    if (characteristic?.uuid == UUID.fromString(CHARACTERISTIC_UUID)) {
                        val receivedData = characteristic?.value
                        if (receivedData != null && receivedData.isNotEmpty()) {
                            synchronized(receivedDataList) {
                                receivedDataList.add(receivedData) // Dodanie do listy
                            }
                            CoroutineScope(Dispatchers.IO).launch {
                                _receivedDataFlow.emit(receivedData) // Emitowanie do Flow
                            }
                        }
                    }
                }
            }


            val bluetoothGatt = androidBluetoothDevice.connectGatt(context, false, gattCallback)
            if (bluetoothGatt == null) {
                trySend(ConnectionResult.Error("Failed to connect to GATT"))
            }

            awaitClose { bluetoothGatt?.close() }
        }.flowOn(Dispatchers.IO)
    }


    override fun sendCommand(gatt: BluetoothGatt, byteArray: ByteArray) {
        val checksum = calculateChecksum(byteArray)

        val commandWithCheckSum = byteArray + checksum
        //println(commandWithCheckSum.joinToString(", ") { it.toString() })


        val service = gatt.getService(UUID.fromString(SERVICE_UUID))
        val characteristic =
            service?.getCharacteristic(UUID.fromString(CHARACTERISTIC_UUID))

        if (characteristic != null) {
            if ((characteristic.properties and BluetoothGattCharacteristic.PROPERTY_WRITE) == 0) {
                Log.d("BL_FUN", "Characteristic does not support writing.")
                return
            }
            val descriptor =
                characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG_UUID))
            if (descriptor != null) {
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                if (gatt.writeDescriptor(descriptor)) {
                    Log.d("BL_FUN", "Notification enabled successfully.")
                } else {
                    Log.d("BL_FUN", "Failed to enable notification.")
                    return
                }
            }

            characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            Handler(Looper.getMainLooper()).postDelayed({
                characteristic.setValue(commandWithCheckSum)
                val writeSuccess = gatt.writeCharacteristic(characteristic)
                if (writeSuccess) {
                    Log.d("BL_FUN", "Command written successfully.")
                } else {
                    Log.d("BL_FUN", "Failed to write command to characteristic.")
                }
            }, 500)
        } else {
            Log.d("BL_FUN", "Characteristic not found.")
        }
    }

    private fun enableNotifications(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic
    ) {
        gatt.setCharacteristicNotification(characteristic, true)
        val descriptor =
            characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG_UUID))
        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        val success = gatt.writeDescriptor(descriptor)
        Log.d("GATT_NOTIFY", "Notification descriptor write: $success")
    }


    private fun calculateChecksum(data: ByteArray): Byte {
        val sum = data.sumOf { it.toUByte().toInt() }
        return (sum % 256).toByte()
    }

    private fun handleCommunicationModeEntry() {
        Log.d("COMM_MODE", "Device is now ready for commands.")
    }


}