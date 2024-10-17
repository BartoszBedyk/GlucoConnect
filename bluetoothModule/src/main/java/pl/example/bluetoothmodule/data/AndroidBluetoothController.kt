package pl.example.bluetoothmodule.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
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

    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val scannedDevices: StateFlow<List<BluetoothDevice>>
        get() = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val pairedDevices: StateFlow<List<BluetoothDevice>>
        get() = _pairedDevices.asStateFlow()

    private val _errors = MutableSharedFlow<String>()
    override val errors: SharedFlow<String>
        get() = _errors.asSharedFlow()


    private val foundDeviceReceiver = FoundDeviceReceiver { device ->
        _scannedDevices.update { devices ->
            val newDevice = device.toBluetoothDeviceDomain()
            if (newDevice in devices) devices else devices + newDevice
        }
    }
    private var bluetoothStateReceiver = BluetoothStateReceiver{isConnected, bluetoothDevice ->
    if(bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == true){
        _isConnected.update { isConnected }
    } else{
        CoroutineScope(Dispatchers.IO).launch{
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
       return flow{
           if(!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)){
               throw SecurityException("No BLUETOOTH_CONNECT permission.")
           }
           currentServerSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
               "Alikacja Inżynierska",
               UUID.fromString(SERVICE_UUID)
           )
           var shouldLoop = true
           while(shouldLoop){
               currentClientSocket = try{
                    currentServerSocket?.accept()
               }catch (e: IOException){
                   shouldLoop = false
                null
               }
               emit(ConnectionResult.ConnectionEstablished)
               currentServerSocket?.let{
                   currentServerSocket?.close()
               }

           }
       }.onCompletion {
           closeConnection()
       }.flowOn(Dispatchers.IO)
    }

    override fun connectToDevice(device: BluetoothDevice): Flow<ConnectionResult> {
        Log.d("CONNECT", device.name ?: "noName")
        return flow{
            if(!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)){
                throw SecurityException("No BLUETOOTH_CONNECT permission.")
            }

            val bluetoothDevice = bluetoothAdapter?.getRemoteDevice(device.address)



            currentClientSocket = bluetoothDevice
                ?.createRfcommSocketToServiceRecord(
                    UUID.fromString(SERVICE_UUID)
                )
            stopDiscovery()
            if(bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == false) {

            }
            currentClientSocket?.let{socket ->
                try{
                    socket.connect()
                    emit(ConnectionResult.ConnectionEstablished)

                }catch (e: IOException){
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
            ?.map { it.toBluetoothDeviceDomain() }
            ?.also { devices ->
                _pairedDevices.update { devices }
            }
    }

    private fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    companion object{
        const val SERVICE_UUID = "60085fad-5373-44ef-a3e6-138472e5becf"
    }

    override fun connectToGattDevice(device: BluetoothDeviceDomain, context: Context): Flow<ConnectionResult> {
        return callbackFlow {
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No BLUETOOTH_CONNECT permission.")
            }

            // Uzyskanie BluetoothAdapter z BluetoothManager
            val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val bluetoothAdapter = bluetoothManager.adapter

            if (bluetoothAdapter == null) {
                trySend(ConnectionResult.Error("Bluetooth adapter is not available"))
                return@callbackFlow
            }

            // Uzyskanie instancji Android BluetoothDevice z adresu
            val androidBluetoothDevice: AndroidBluetoothDevice? = bluetoothAdapter.getRemoteDevice(device.address)

            if (androidBluetoothDevice == null) {
                trySend(ConnectionResult.Error("Device not found"))
                return@callbackFlow
            }

            val gattCallback = object : BluetoothGattCallback() {
                override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.d("GATT", "Connected to GATT server.")
                        gatt.discoverServices()
                        trySend(ConnectionResult.ConnectionEstablished)
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        Log.d("GATT", "Disconnected from GATT server.")
                        trySend(ConnectionResult.Error("Disconnected from GATT server"))
                        gatt.close()
                    }
                }

                override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        val service = gatt.getService(UUID.fromString("00001523-1212-efde-1523-785feabcd123"))
                        if (service != null) {
                            val characteristic = service.getCharacteristic(UUID.fromString("00001524-1212-efde-1523-785feabcd123"))
                            if (characteristic != null) {
                                characteristic.setValue("Your data".toByteArray())
                                gatt.writeCharacteristic(characteristic)
                            } else {
                                trySend(ConnectionResult.Error("Characteristic not found"))
                            }
                        } else {
                            trySend(ConnectionResult.Error("Service not found"))
                        }
                    } else {
                        trySend(ConnectionResult.Error("Service discovery failed"))
                    }
                }

                override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        Log.d("GATT", "Characteristic written successfully")
                    } else {
                        trySend(ConnectionResult.Error("Failed to write characteristic"))
                    }
                }
            }

            // Rozpocznij połączenie GATT z urządzeniem AndroidBluetoothDevice
            val bluetoothGatt = androidBluetoothDevice.connectGatt(context, false, gattCallback)

            // Sprawdź, czy połączenie GATT zostało nawiązane
            if (bluetoothGatt == null) {
                trySend(ConnectionResult.Error("Failed to connect to GATT"))
            }

            awaitClose {
                bluetoothGatt?.close()
            }
        }.flowOn(Dispatchers.IO)
    }
}