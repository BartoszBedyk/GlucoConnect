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

class MyBleManager(context: Context): BleManager(context) {
    private val SERVICE_UUID = UUID.fromString("00001523-1212-efde-1523-785feabcd123")
    private val CHARACTERISTIC_UUID = UUID.fromString("00001524-1212-efde-1523-785feabcd123")
    private var myCharacteristic: BluetoothGattCharacteristic? = null

    override fun getGattCallback(): BleManagerGattCallback = MyManagerGattCallback()

    private inner class MyManagerGattCallback : BleManagerGattCallback() {

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            val myService = gatt.getService(SERVICE_UUID)
            myCharacteristic = myService?.getCharacteristic(CHARACTERISTIC_UUID)
            return myCharacteristic != null
        }

        override fun initialize() {
            super.initialize()
            enableNotifications(myCharacteristic)
                .done { Log.d("MyBleManager", "Powiadomienia włączone") }
                .enqueue()
        }

        override fun onDeviceDisconnected() {
            myCharacteristic = null
            Log.d("MyBleManager", "Połączenie zakończone")
        }

        override fun onServicesInvalidated() {
            myCharacteristic = null
            Log.d("MyBleManager", "Usługi zostały unieważnione - ponowna inicjalizacja wymagana")

        }
    }

    // Funkcja do wysyłania komendy oraz odbierania wyników
    fun fetchLastMeasurement(callback: (dateTime: String?, result: Int?) -> Unit) {
        val commandDate = byteArrayOf(51.toByte(), 0x23, 0x00, 0x00, 0x00, 0x00, 0xA3.toByte(), 0x00)
        commandDate[7] = calculateChecksum(commandDate)

        writeCharacteristic(myCharacteristic, commandDate)
            .with { _, data ->
                Log.d("MyBleManager", "Send date: ${commandDate.joinToString { "0x%02X".format(it) }}")
                Log.d("MyBleManager", "raw data: ${data.value?.joinToString { "0x%02X".format(it) }}")
                val dateTime = parseDateTime(data)
                Log.d("MyBleManager", "Otrzymano datę i czas: $dateTime")


                val commandResult = byteArrayOf(0x51.toByte(), 0x24, 0x00, 0x00, 0x00, 0x00, 0xA3.toByte(), 0x00)
                commandResult[7] = calculateChecksum(commandResult)  // Obliczamy i ustawiamy sumę kontrolną

                Handler(Looper.getMainLooper()).postDelayed({
                    writeCharacteristic(myCharacteristic, commandResult)
                        .with { _, resultData ->
                            Log.d("MyBleManager", "Surowe dane Result: ${resultData.value?.joinToString { "0x%02X".format(it) }}")
                            val result = parseResult(resultData)
                            Log.d("MyBleManager", "Otrzymano wynik: $result")
                            callback(dateTime, result)
                        }
                        .enqueue()
                }, 2000)
            }
            .enqueue()
    }

    
    private fun calculateChecksum(frame: ByteArray): Byte {
        val checksum = frame.dropLast(1).sumOf { it.toInt() } and 0xFF
        Log.d("MyBleManager", "Calculated checksum: 0x${checksum.toString(16)}")
        return checksum.toByte()
    }



    // Funkcja do parsowania daty i czasu
    private fun parseDateTime(data: Data): String? {
        if (data.size() < 8) return null

        val rawDate = (data.getByte(3)?.toInt() ?: 0) or (data.getByte(4)?.toInt() ?: 0 shl 8)
        val rawTime = data.getByte(5)?.toInt() ?: 0

        val year = 2000 + (rawDate shr 9)
        val month = (rawDate shr 5) and 0x0F
        val day = rawDate and 0x1F

        val minute = rawTime and 0x3F
        val hour = (rawTime shr 6) and 0x1F

        Log.d("MyBleManager", "Parsed date: year=$year, month=$month, day=$day")
        Log.d("MyBleManager", "Parsed time: hour=$hour, minute=$minute")

        return "$day-$month-$year $hour:$minute"
    }




    private fun parseResult(data: Data): Int? {


        if (data.size() < 8) return null

        val highByte = data.getByte(3)?.toInt() ?: return null
        val lowByte = data.getByte(4)?.toInt() ?: return null
        val result = (highByte shl 8) or lowByte

        Log.d("MyBleManager", "Parsed result: highByte=0x${highByte.toString(16)}, lowByte=0x${lowByte.toString(16)}, result=$result")
        return result
    }


    fun setNotificationCallback(callback: (data: Data) -> Unit) {
        setNotificationCallback(myCharacteristic)
            .with { _, data -> callback(data) }
    }

}