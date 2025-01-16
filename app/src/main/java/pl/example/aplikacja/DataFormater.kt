package pl.example.aplikacja

import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDateTimeSpecificLocale(date: Date): String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("pl", "PL"))
    return dateFormat.format(date)
}

fun formatDateTimeWithoutLocale(date: Date): String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
    return dateFormat.format(date)
}

fun formatDateTimeWithoutTime(date: Date): String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy")
    return dateFormat.format(date)
}

fun removeQuotes(id: String): String {
    return id.replace("\"", "")
}

fun formatUnit(unit: GlucoseUnitType): String {
    return when (unit) {
        GlucoseUnitType.MMOL_PER_L -> "mmol/l"
        GlucoseUnitType.MG_PER_DL -> "mg/dL"
    }

}

fun convertUnits(
    items: List<ResearchResult>,
    targetUnit: GlucoseUnitType
): List<ResearchResult> {
    return items.map { item ->
        if (item.unit != targetUnit) {
            val convertedConcentration = when (item.unit) {
                GlucoseUnitType.MG_PER_DL -> item.glucoseConcentration / 18.0182
                GlucoseUnitType.MMOL_PER_L -> item.glucoseConcentration * 18.0182
            }.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
            item.copy(
                glucoseConcentration = convertedConcentration,
                unit = targetUnit
            )
        } else {
            item
        }
    }
}