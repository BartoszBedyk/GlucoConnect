package pl.example.aplikacja


import pl.example.databasemodule.database.data.GlucoseUnitTypeDB
import pl.example.databasemodule.database.data.ResearchResultDB
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.requestData.ResearchResultCreate
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

fun formatDateTimeSpecificLocale(date: Date): String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("pl", "PL"))
    return dateFormat.format(date)
}

fun formatDateTimeWithoutLocale(date: Date): String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
    return dateFormat.format(date)
}

fun formatDateTimeWithoutTime(date: Date?): String {
    if(date == null) return "Nie określono"
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


fun convertResearchDBtoResearchResult(researchResultDB: ResearchResultDB): ResearchResult {
    val dbResult =  ResearchResult(
        id = researchResultDB.id,
        sequenceNumber = researchResultDB.sequenceNumber,
        glucoseConcentration = researchResultDB.glucoseConcentration,
        unit = convertGlucoseUnitType(researchResultDB.unit),
        timestamp = researchResultDB.timestamp,
        userId = researchResultDB.userId,
        deletedOn = researchResultDB.deletedOn,
        lastUpdatedOn = researchResultDB.lastUpdatedOn
    )
    return dbResult


}

fun convertResearchResultToResearchDB(researchResultDB: ResearchResult): ResearchResultDB {
    val dbResult =  ResearchResultDB(
        id = researchResultDB.id,
        sequenceNumber = researchResultDB.sequenceNumber,
        glucoseConcentration = researchResultDB.glucoseConcentration,
        unit = convertGlucoseUnitType(researchResultDB.unit),
        timestamp = researchResultDB.timestamp,
        userId = researchResultDB.userId,
        deletedOn = researchResultDB.deletedOn,
        lastUpdatedOn = researchResultDB.lastUpdatedOn
    )
    return dbResult


}

private fun convertGlucoseUnitType(dbUnit: GlucoseUnitTypeDB): GlucoseUnitType {
    return when (dbUnit) {
        GlucoseUnitTypeDB.MG_PER_DL -> GlucoseUnitType.MMOL_PER_L
        GlucoseUnitTypeDB.MMOL_PER_L -> GlucoseUnitType.MMOL_PER_L
        else -> throw IllegalArgumentException("Unknown GlucoseUnitTypeDB: $dbUnit")
    }
}

private fun convertGlucoseUnitType(dbUnit: GlucoseUnitType): GlucoseUnitTypeDB {
    return when (dbUnit) {
        GlucoseUnitType.MG_PER_DL -> GlucoseUnitTypeDB.MMOL_PER_L
        GlucoseUnitType.MMOL_PER_L -> GlucoseUnitTypeDB.MMOL_PER_L
        else -> throw IllegalArgumentException("Unknown GlucoseUnitTypeDB: $dbUnit")
    }
}

fun convertResearchDBtoResearchResult(researchResultDB: List<ResearchResultDB>): List<ResearchResult> {
    return researchResultDB.map { dbResult ->
        ResearchResult(
            id = dbResult.id,
            sequenceNumber = dbResult.sequenceNumber,
            glucoseConcentration = dbResult.glucoseConcentration,
            unit = convertGlucoseUnitType(dbResult.unit),
            timestamp = dbResult.timestamp,
            userId = dbResult.userId,
            deletedOn = dbResult.deletedOn,
            lastUpdatedOn = dbResult.lastUpdatedOn
        )
    }
}



