package pl.example.aplikacja.mappters


import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import java.math.RoundingMode
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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

fun parseMeasurement(input: String): Measurement? {
    val parts = input.split(",").map { it.trim() }

    if (parts.size == 3) {
        val date = parts[0].substringAfter("Date:").trim()
        val result = parts[1].substringAfter("Result:").trim()
        val unit = parts[2].substringAfter("Unit:").trim()
        try {
            val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.US)
            val dateParsed: Date = dateFormat.parse(date)
            return Measurement(dateParsed, result.toDouble(), unit)
        }
        catch (e: ParseException) {
            println("Niepoprawny format daty.")
            return null
        }
    } else {
        println("Niepoprawny format stringa.")
        return null
    }

}

data class Measurement(
    val date: Date,
    val result: Double,
    val unit: String
)







