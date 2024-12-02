package pl.example.bluetoothmodule.domain

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.data.Data
import java.util.UUID

class MyBleManager(context: Context) : BleManager(context) {

    private val SERVICE_UUID = UUID.fromString("00001523-1212-efde-1523-785feabcd123")
    private val CHARACTERISTIC_UUID = UUID.fromString("00001524-1212-efde-1523-785feabcd123")
    private var myCharacteristic: BluetoothGattCharacteristic? = null

    override fun getGattCallback(): BleManagerGattCallback = MyManagerGattCallback()

    private inner class MyManagerGattCallback : BleManagerGattCallback() {

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            val myService = gatt.getService(SERVICE_UUID)
            myCharacteristic = myService?.getCharacteristic(CHARACTERISTIC_UUID)
            if (myCharacteristic == null) {
                Log.e("MyBleManager", "Charakterystyka jest null.")
            }
            return myCharacteristic != null
        }

        override fun initialize() {
            super.initialize()
            writeCharacteristic(myCharacteristic, byteArrayOf(0x51.toByte(), 0x54.toByte(), 0x00, 0x00, 0x00, 0x00, 0xA3.toByte(), 0x17.toByte()))
                .done { Log.d("MyBleManager", "Urządzenie gotowe do komunikacji.") }
                .fail { _, status -> Log.e("MyBleManager", "Błąd inicjalizacji: $status") }
                .enqueue()

            enableNotifications(myCharacteristic)
                .done { Log.d("MyBleManager", "Powiadomienia włączone.") }
                .fail { _, status -> Log.e("MyBleManager", "Nie udało się włączyć powiadomień: $status") }
                .enqueue()
        }

        override fun onDeviceDisconnected() {
            myCharacteristic = null
            Log.d("MyBleManager", "Połączenie zakończone.")
        }

        override fun onServicesInvalidated() {
            myCharacteristic = null
            Log.d("MyBleManager", "Usługi  unieważnione.")
        }
    }

    fun fetchLastMeasurement(callback: (dateTime: String?, result: Int?) -> Unit) {
        initializeDevice {
            val commandDate = byteArrayOf(0x51.toByte(), 0x23.toByte(), 0x00, 0x00, 0x00, 0x00, 0xA3.toByte(), 0x00)
            commandDate[7] = calculateChecksum(commandDate)

            writeCharacteristic(myCharacteristic, commandDate)
                .with { _, data ->
                    Log.d("MyBleManager", "Komenda wysłana (data): ${commandDate.joinToString { "0x%02X".format(it) }}")
                    Log.d("MyBleManager", "Odebrane dane (data): ${data.value?.joinToString { "0x%02X".format(it) }}")
                    val dateTime = parseDateTime(data)

                    val commandResult = byteArrayOf(0x51.toByte(), 0x26.toByte(), 0x00, 0x00, 0x00, 0x00, 0xA3.toByte(), 0x00)
                    commandResult[7] = calculateChecksum(commandResult)

                    Handler(Looper.getMainLooper()).postDelayed({
                        writeCharacteristic(myCharacteristic, commandResult)
                            .with { _, resultData ->
                                Log.d("MyBleManager", "Komenda wysłana (wynik): ${commandResult.joinToString { "0x%02X".format(it) }}")
                                Log.d("MyBleManager", "Odebrane dane (wynik): ${resultData.value?.joinToString { "0x%02X".format(it) }}")
                                val result = parseResult(resultData)
                                callback(dateTime, result)
                            }
                            .fail { _, status -> Log.e("MyBleManager", "Błąd odczytu wyniku: $status") }
                            .enqueue()
                    }, 5000)
                }
                .fail { _, status -> Log.e("MyBleManager", "Błąd odczytu daty: $status") }
                .enqueue()
        }
    }


    private fun calculateChecksum(frame: ByteArray): Byte {
        val checksum = frame.dropLast(1).sumOf { it.toUByte().toInt() } and 0xFF
        Log.d("MyBleManager", "Suma kontrolna: 0x${checksum.toString(16).uppercase()}")
        return checksum.toByte()
    }

    private fun parseDateTime(data: Data): String? {
        if (data.size() < 8) return null

        val rawDate = (data.getByte(3)?.toInt() ?: 0) or ((data.getByte(4)?.toInt() ?: 0) shl 8)
        val rawTime = data.getByte(5)?.toInt() ?: 0

        val year = 2000 + (rawDate shr 9)
        val month = (rawDate shr 5) and 0x0F
        val day = rawDate and 0x1F

        val hour = (rawTime shr 6) and 0x1F
        val minute = rawTime and 0x3F

        return "$day-$month-$year $hour:$minute"
    }

    fun initializeDevice(callback: () -> Unit) {
        val initCommand = byteArrayOf(0x51.toByte(), 0x54.toByte(), 0x00, 0x00, 0x00, 0x00, 0xA3.toByte(), 0x00)
        initCommand[7] = calculateChecksum(initCommand)

        writeCharacteristic(myCharacteristic, initCommand)
            .with { _, _ ->
                Log.d("MyBleManager", "Urządzenie zainicjalizowane..")
                callback()
            }
            .fail { _, status -> Log.e("MyBleManager", "Błąd inicjalizacji: $status") }
            .enqueue()
    }



    private fun parseResult(data: Data): Int? {
        if (data.size() < 8) return null

        val highByte = data.getByte(3)?.toInt() ?: return null
        val lowByte = data.getByte(4)?.toInt() ?: return null
        return (highByte shl 8) or lowByte
    }
}