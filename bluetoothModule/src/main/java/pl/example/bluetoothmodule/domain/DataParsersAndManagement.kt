package pl.example.bluetoothmodule.domain

import android.util.Log
import java.util.Calendar
import java.util.Date


fun parseDeviceModelResponse(response: ByteArray): String {
    val modelHigh = (response[3].toInt() and 0xFF).toString(16).padStart(2, '0').uppercase()
    val modelLow = (response[2].toInt() and 0xFF).toString(16).padStart(2, '0').uppercase()

    return "Device Model: $modelHigh$modelLow"
}

fun parseClockTimeResponse(response: ByteArray): String {
    val dayMonthYear = ((response[3].toInt() and 0xFF) shl 8) or (response[2].toInt() and 0xFF)
    val year = 2000 + ((dayMonthYear shr 9) and 0x7F)
    val month = (dayMonthYear shr 5) and 0x0F
    val day = dayMonthYear and 0x1F

    val minute = response[4].toInt() and 0x3F
    val hour = response[5].toInt() and 0x1F

    Log.d("Parsuje", "%02d-%02d-%04d %02d:%02d".format(day, month, year, hour, minute))
    return "%02d-%02d-%04d %02d:%02d".format(day, month, year, hour, minute)
}

fun parseMeasurementTimeToDate(frame: ByteArray): Date? {
    val year = 2000 + ((frame[3].toInt() ushr 1) and 0x7F) // 7 bitów na rok
    val month =
        ((frame[3].toInt() shl 3) or (frame[2].toInt() ushr 5)) and 0x0F // 4 bity na miesiąc
    val day = frame[2].toInt() and 0x1F // 5 bitów na dzień


    val hour = frame[5].toInt() and 0x1F // 5 bitów na godzinę
    val minute = frame[4].toInt() and 0x3F // 6 bitów na minutę


    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month - 1)
    calendar.set(Calendar.DAY_OF_MONTH, day)
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    Log.e("DATE", calendar.time.toString())
    return calendar.time
}


fun parseSerialNumber(frame: ByteArray): String? {
    val serialPart = frame.slice(2..5)
    Log.i("PARSER", serialPart.joinToString("") { "%02X".format(it) })
    return serialPart.joinToString("") { "%02X".format(it) }
}

fun combineSerialNumber(part1: String?, part2: String?): String? {
    if (part1 == null || part2 == null) return null
    return part2 + part1
}


fun responseManagement(frame: ByteArray) {
    if (frame.size != 8) {
        throw IllegalArgumentException("Invalid frame length: ${frame.size}, expected 8 bytes.")
    }
    val cmd = frame[1].toInt()
    when (cmd) {
        0x23 -> parseClockTimeResponse(frame)
        0x24 -> parseDeviceModelResponse(frame)
        0x25 -> parseMeasurementTimeToDate(frame)
        0x27 -> parseSerialNumber(frame)
        0x28 -> parseSerialNumber(frame)
        else -> throw IllegalArgumentException("Invalid response command: ${frame[1]}.")
    }
}







