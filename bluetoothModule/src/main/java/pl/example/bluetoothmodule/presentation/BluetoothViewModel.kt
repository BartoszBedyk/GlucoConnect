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
import kotlinx.coroutines.launch
import pl.example.bluetoothmodule.domain.BluetoothController
import pl.example.bluetoothmodule.domain.BluetoothDevice
import pl.example.bluetoothmodule.domain.ConnectionResult
import pl.example.bluetoothmodule.domain.responseManagement
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothController: BluetoothController
) : ViewModel() {

    private val _state = MutableStateFlow(BluetoothUiState())
    val state = combine(
        bluetoothController.scannedDevices,
        bluetoothController.pairedDevices,
        _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    private val _receivedDataFlow = MutableSharedFlow<ByteArray>()
    val receivedDataFlow: Flow<ByteArray> = _receivedDataFlow.asSharedFlow()



    //dane odebrane w onChange
    private val receivedDataList: MutableList<ByteArray> = mutableListOf()


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



    suspend fun readGlucometerTime(): String {
        val command = byteArrayOf(
            0x51.toByte(),
            0x23.toByte(),
            0x00, 0x00, 0x00, 0x00,
            0xA3.toByte()
        )
        val glucometereClock = sendCommandAndWaitForResponse(command).firstOrNull()
        val returnString = glucometereClock?.let { responseManagement(it) }
        return returnString ?: ""
    }


    fun startScan() {
        bluetoothController.startDiscovery()
    }

    fun stopScan() {
        bluetoothController.stopDiscovery()
    }

    fun waitForIncomingConnections() {
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = bluetoothController.startBluetoothServer().listen()
    }

    fun connectToGattDevice(device: BluetoothDevice) {
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = bluetoothController.connectToGattDevice(device).listen()
    }

    fun sendCommandAndWaitForResponse(command: ByteArray): Flow<ByteArray?> = flow {
        bluetoothGatt?.let {
           // Log.i("BluetoothViewModel", "Sending command: ${command.joinToString(" ")}")
            bluetoothController.sendCommand(it, command)
           // Log.i("BluetoothViewModel", "Command sent.")

            // Pobranie pierwszego pakietu danych z `_receivedDataFlow`
            val response = bluetoothController.receivedDataFlow.firstOrNull { it.isNotEmpty() }
            emit(response)
            //Log.d("BluetoothViewModel", "Response emitted: ${response?.contentToString()}")
        } ?: run {
            //Log.e("BluetoothViewModel", "BluetoothGatt is not connected.")
            emit(null)
        }
    }


    val _lastMeasurement = MutableStateFlow("")
    val lastMeasurement: StateFlow<String> = _lastMeasurement

    suspend fun readLastMeasurement(): String {
        Log.i("BluetoothViewModel", "Reading last measurement...")

        // Komenda do odczytu czasu
        val commandTime = byteArrayOf(0x51.toByte(), 0x25.toByte(), 0x00, 0x00, 0x00, 0x00, 0xA3.toByte())
        val timeResponse = sendCommandAndWaitForResponse(commandTime).firstOrNull()
        val resultTime = timeResponse?.let { responseManagement(it) } ?: ""

        // Komenda do odczytu ostatniego pomiaru
        val commandResult = byteArrayOf(0x51.toByte(), 0x26.toByte(), 0x00, 0x00, 0x00, 0x00, 0xA3.toByte())
        val resultResponse = sendCommandAndWaitForResponse(commandResult).firstOrNull()
        val resultData = resultResponse?.let { responseManagement(it) } ?: ""

        // Aktualizacja stanu złożonego wyniku
        _lastMeasurement.value = resultTime + "\n" + resultData

        Log.i("BluetoothViewModel", "_lastMeasurement: ${_lastMeasurement.value}")
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
                        isConnected = false,
                        isConnecting = false,
                        errorMessage = result.message
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
        bluetoothController.release()
    }
}
