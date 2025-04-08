package pl.example.bluetoothmodule.presentation

import android.bluetooth.BluetoothGatt
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import pl.example.bluetoothmodule.domain.BluetoothController
import pl.example.bluetoothmodule.domain.BluetoothDevice
import pl.example.bluetoothmodule.domain.ConnectionResult
import pl.example.bluetoothmodule.domain.responseManagement
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothController: BluetoothController
) : ViewModel() {

    private val _state = MutableStateFlow(BluetoothUiState())
    val state = combine(
        bluetoothController.scannedDevices, bluetoothController.pairedDevices, _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices, pairedDevices = pairedDevices
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    private val _receivedDataFlow = MutableSharedFlow<ByteArray>()
    val receivedDataFlow: Flow<ByteArray> = _receivedDataFlow.asSharedFlow()

    private val _loadingBluetooth = MutableStateFlow(false)
    val loadingBluetooth: StateFlow<Boolean> = _loadingBluetooth

    private val _connectedState = MutableStateFlow(false)
    val connectedState: StateFlow<Boolean> = _connectedState


    private var deviceConnectionJob: Job? = null
    private var bluetoothGatt: BluetoothGatt? = null

    init {
        bluetoothController.isConnected.onEach { isConnected ->
            _state.update { it.copy(isConnected = isConnected) }
        }.launchIn(viewModelScope)

        bluetoothController.errors.onEach { error ->
            _state.update { it.copy(errorMessage = error) }
        }.launchIn(viewModelScope)

        bluetoothController.receivedDataFlow.onEach { data ->
            Log.d("ViewModel", "Received data: ${data.contentToString()}")
            _receivedDataFlow.emit(data)
        }.launchIn(viewModelScope)

    }

    fun getStoredData(): List<ByteArray> {
        return bluetoothController.getStoredData()
    }


    fun startScan() {
        bluetoothController.startDiscovery()
        _loadingBluetooth.value = true
    }

    fun stopScan() {
        bluetoothController.stopDiscovery()
        _loadingBluetooth.value = false
    }

    fun waitForIncomingConnections() {
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = bluetoothController.startBluetoothServer().listen()
    }

    fun connectToGattDevice(device: BluetoothDevice) {
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = bluetoothController.connectToGattDevice(device).listen()
        _loadingBluetooth.value = false
    }

    fun sendCommandAndWaitForResponse(command: ByteArray): Flow<ByteArray?> = flow {
        bluetoothGatt?.let {
            bluetoothController.sendCommand(it, command)
            val response = bluetoothController.receivedDataFlow.firstOrNull { it.isNotEmpty() }
            emit(response)
        } ?: run {
            emit(null)
        }
        _loadingBluetooth.value = false
    }


    val _lastMeasurement = MutableStateFlow("")
    val lastMeasurement: StateFlow<String> = _lastMeasurement

    suspend fun readLastMeasurement(): String {
        val commandTime =
            byteArrayOf(0x51.toByte(), 0x25.toByte(), 0x00, 0x00, 0x00, 0x00, 0xA3.toByte())
        val timeResponse = sendCommandAndWaitForResponse(commandTime).firstOrNull()
        val resultTime = timeResponse?.let { responseManagement(it) } ?: ""

        val commandResult =
            byteArrayOf(0x51.toByte(), 0x26.toByte(), 0x00, 0x00, 0x00, 0x00, 0xA3.toByte())
        val resultResponse = sendCommandAndWaitForResponse(commandResult).firstOrNull()
        val resultData = resultResponse?.let { responseManagement(it) } ?: ""

        _lastMeasurement.value = resultTime + "\n" + resultData

        Log.i("BluetoothViewModel", "_lastMeasurement: ${_lastMeasurement.value}")
        return _lastMeasurement.value
    }

    suspend fun readDeviceSerialNumber(): String {
        val commandTime =
            byteArrayOf(0x51.toByte(), 0x27.toByte(), 0x00, 0x00, 0x00, 0x00, 0xA3.toByte())
        val timeResponse = sendCommandAndWaitForResponse(commandTime).firstOrNull()
        val part1 = timeResponse?.let { responseManagement(it) } ?: ""

        val commandResult =
            byteArrayOf(0x51.toByte(), 0x28.toByte(), 0x00, 0x00, 0x00, 0x00, 0xA3.toByte())
        val resultResponse = sendCommandAndWaitForResponse(commandResult).firstOrNull()
        val part2 = resultResponse?.let { responseManagement(it) } ?: ""

        _lastMeasurement.value = part1 + "\n" + part2

        Log.i("BluetoothViewModel", "_lastMeasurement: ${_lastMeasurement.value}")
        return _lastMeasurement.value
    }

    suspend fun readGlucometerTime(): String {
        val commandTime = byteArrayOf(
            0x51.toByte(), 0x23.toByte(), 0x00, 0x00, 0x00, 0x00, 0xA3.toByte()
        )
        val timeResponse = sendCommandAndWaitForResponse(commandTime).firstOrNull()
        val resultTime = timeResponse?.let { responseManagement(it) } ?: ""
        _lastMeasurement.value = resultTime
        return _lastMeasurement.value
    }

    suspend fun setGlucometerTime(): String {
        val commandTime = getActualDateTimeCommand()
        val timeResponse = sendCommandAndWaitForResponse(commandTime).firstOrNull()
        val resultTime = timeResponse?.let { responseManagement(it) } ?: ""
        _lastMeasurement.value = resultTime
        return _lastMeasurement.value
    }

    suspend fun turnOffDevice(): String {
        val commandTime = byteArrayOf(
            0x51.toByte(), 0x50.toByte(), 0x00, 0x00, 0x00, 0x00, 0xA3.toByte()
        )
        val timeResponse = sendCommandAndWaitForResponse(commandTime).firstOrNull()
        val resultTime = timeResponse?.let { responseManagement(it) } ?: ""
        _lastMeasurement.value = resultTime
        return _lastMeasurement.value
    }

    suspend fun clearMemory(): String {
        val commandTime = byteArrayOf(
            0x51.toByte(), 0x52.toByte(), 0x00, 0x00, 0x00, 0x00, 0xA3.toByte()
        )
        val timeResponse = sendCommandAndWaitForResponse(commandTime).firstOrNull()
        val resultTime = timeResponse?.let { responseManagement(it) } ?: ""
        _lastMeasurement.value = resultTime
        return _lastMeasurement.value
    }


    fun disconnectFromDevice() {
        deviceConnectionJob?.cancel()
        _state.update { it.copy(isConnecting = false, isConnected = false) }
        bluetoothController.closeConnection()
    }

    private fun Flow<ConnectionResult>.listen(): Job = onEach { result ->
        when (result) {
            is ConnectionResult.ConnectionEstablished -> {
                _state.update { it.copy(isConnected = true, isConnecting = false) }
            }

            is ConnectionResult.Error -> {
                _state.update {
                    it.copy(
                        isConnected = false, isConnecting = false, errorMessage = result.message
                    )
                }
            }

            is ConnectionResult.GattConnected -> {
                bluetoothGatt = result.gatt
                _state.update { it.copy(isConnected = true, isConnecting = false) }
            }
        }
    }.launchIn(viewModelScope)

    override fun onCleared() {
        super.onCleared()
        try {
            bluetoothController.release()
        } catch (e: IllegalArgumentException) {
            Log.e(
                "BluetoothViewModel", "Błąd: Próba wyrejestrowania nieistniejącego odbiornika!", e
            )
        }
    }


    private fun getActualDateTimeCommand(): ByteArray {
        val date = Date()

        val calendar = Calendar.getInstance().apply { time = date }
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1 // MONTH is 0-based
        val year = calendar.get(Calendar.YEAR) - 2000 // Assuming year offset from 2000

        val data1_0 = (day and 0x1F) or ((month and 0x0F) shl 5) or ((year and 0x7F) shl 9)
        val data0: Byte = (data1_0 and 0xFF).toByte()
        val data1: Byte = ((data1_0 shr 8) and 0xFF).toByte()

        val data2: Byte = calendar.get(Calendar.MINUTE).toByte()

        val data3: Byte = calendar.get(Calendar.HOUR_OF_DAY).toByte()

        return byteArrayOf(0x51.toByte(), 0x23.toByte(), data0, data1, data2, data3, 0xA3.toByte())
    }
}


