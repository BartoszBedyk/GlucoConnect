package pl.example.bluetoothmodule.domain

sealed interface ConnectionResult {
    object ConnectionEstablished : ConnectionResult
    data class  Error(var message: String): ConnectionResult
}