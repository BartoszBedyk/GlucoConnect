package pl.example.aplikacja

import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDateTimeSpecificLocale(date: Date): String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("pl", "PL"))
    return dateFormat.format(date)
}

fun removeQuotes(id: String): String {
    return id.replace("\"", "")
}

fun formatUnit(unit: GlucoseUnitType): String {
    return when (unit) {
        GlucoseUnitType.MMOL_PER_L -> "mmol/l"
        GlucoseUnitType.MG_PER_DL -> "mg/dl"
    }

}

fun isMockTest(): Boolean {
    return true
}