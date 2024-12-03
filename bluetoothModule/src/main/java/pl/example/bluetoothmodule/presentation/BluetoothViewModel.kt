package pl.example.bluetoothmodule.presentation

import android.bluetooth.BluetoothGatt
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.job
import pl.example.bluetoothmodule.domain.BluetoothController
import pl.example.bluetoothmodule.domain.BluetoothDevice
import pl.example.bluetoothmodule.domain.ConnectionResult
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

    private var deviceConnectionJob: Job? = null
    private var bluetoothGatt: BluetoothGatt? = null

    init {
        bluetoothController.isConnected.onEach { isConnected ->
            _state.update { it.copy(isConnected = isConnected) }

        }.launchIn(viewModelScope)

        bluetoothController.errors.onEach { error ->
            _state.update {
                it.copy(
                    errorMessage = error
                )
            }
        }.launchIn(viewModelScope)
    }

    fun connectToGattDevice(device: BluetoothDevice){
        Log.d("CONNECT", "button is clicked")
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = bluetoothController.connectToGattDevice(device)
            .listen()
    }


    fun readMeasurementTime(){
        Log.d("BL_FUN", bluetoothGatt.toString())
        bluetoothGatt?.let {
            bluetoothController.readMeasurementTime(it)
        } ?: Log.d("BL_FUN", "BluetoothGatt is not available.")

    }

    fun disconnectFromDevice(){
        deviceConnectionJob?.cancel()
        _state.update { it.copy(
            isConnecting = false,
            isConnected = false
        ) }
    }

    fun waitForIncomingConnections(){
        _state.update {  it.copy(isConnecting = true)}
        deviceConnectionJob = bluetoothController
            .startBluetoothServer()
            .listen()
    }

    fun startScan() {
        Log.d("SCAN", "Start scan.")
        bluetoothController.startDiscovery()
    }

    fun stopScan() {
        Log.d("SCAN", "Stop scan.")
        bluetoothController.stopDiscovery()
    }

    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when (result) {
                is ConnectionResult.ConnectionEstablished -> {
                    _state.update {
                        it.copy(
                            isConnected = true,
                            isConnecting = false,
                            errorMessage = null
                        )
                    }
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
                    _state.update {
                        it.copy(
                            isConnected = true,
                            isConnecting = false,
                            errorMessage = null
                        )
                    }
                }
            }
        }.catch { throwable ->
            bluetoothController.closeConnection()
            _state.update {
                it.copy(
                    isConnected = false,
                    isConnecting = false
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.release()
    }
}