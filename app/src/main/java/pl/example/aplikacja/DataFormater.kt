package pl.example.aplikacja


import pl.example.databasemodule.database.data.DiabetesTypeDB
import pl.example.databasemodule.database.data.GlucoseUnitTypeDB
import pl.example.databasemodule.database.data.HeartbeatDB
import pl.example.databasemodule.database.data.GlucoseResultDB
import pl.example.databasemodule.database.data.UserMedicationDB
import pl.example.networkmodule.apiData.HeartbeatResult
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiData.UserMedicationResult
import pl.example.networkmodule.apiData.enumTypes.DiabetesType
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiData.enumTypes.RestrictedUserType
import pl.example.networkmodule.apiData.enumTypes.UserType
import pl.example.networkmodule.requestData.CreateUserMedicationForm
import java.math.RoundingMode
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDateTimeSpecificLocale(date: Date): String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("pl", "PL"))
    //Log.i("GlucometerDate", "without format $date")
    //Log.i("GlucometerDate", "with format ${dateFormat.format(date)}")
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

fun formatUserType(unit: UserType): String {
    return when (unit) {
        UserType.ADMIN -> "Administrator"
        UserType.DOCTOR -> "Lekarz"
        UserType.OBSERVER -> "Obserwator"
        UserType.PATIENT -> "Pacjent"
    }
}

fun formatDiabetesType(type: DiabetesTypeDB): String {
    return when (type) {
        DiabetesTypeDB.TYPE_1 -> "Typ 1"
        DiabetesTypeDB.TYPE_2 -> "Typ 2"
        DiabetesTypeDB.NONE -> "Brak"
        DiabetesTypeDB.LADA -> "LADA"
        DiabetesTypeDB.GESTATIONAL -> "Ciążowa"
        DiabetesTypeDB.MODY -> "MODY"
    }

}

fun formatDiabetesType(type: DiabetesType): String {
    return when (type) {
        DiabetesType.TYPE_1 -> "Typ 1"
        DiabetesType.TYPE_2 -> "Typ 2"
        DiabetesType.NONE -> "Brak"
        DiabetesType.LADA -> "LADA"
        DiabetesType.GESTATIONAL -> "Ciążowa"
        DiabetesType.MODY -> "MODY"
    }

}

fun UserType.toRestrictedUserTypeOrNull(): RestrictedUserType? {
    return when (this) {
        UserType.PATIENT -> RestrictedUserType.PATIENT
        UserType.OBSERVER -> RestrictedUserType.OBSERVER
        else -> null
    }
}

fun formatUserType(unit: RestrictedUserType): String {
    return when (unit) {
        RestrictedUserType.PATIENT -> "Pacjent"
        RestrictedUserType.OBSERVER -> "Obserwator"
        RestrictedUserType.BRAK -> "Wybierz typ"
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


fun stringUnitParser(string: String?): GlucoseUnitType {
    return when (string) {
        "mg/dL" -> GlucoseUnitType.MG_PER_DL
        "mmol/l" -> GlucoseUnitType.MMOL_PER_L
        "MG_PER_DL" -> GlucoseUnitType.MG_PER_DL
        "MMOL_PER_L" -> GlucoseUnitType.MMOL_PER_L
        else -> GlucoseUnitType.MG_PER_DL
    }

}




fun convertResearchDBtoResearchResult(glucoseResultDB: GlucoseResultDB): ResearchResult {
    val dbResult =  ResearchResult(
        id = glucoseResultDB.id,
        glucoseConcentration = glucoseResultDB.glucoseConcentration,
        unit = convertGlucoseUnitType(glucoseResultDB.unit),
        timestamp = glucoseResultDB.timestamp,
        userId = glucoseResultDB.userId,
        deletedOn = glucoseResultDB.deletedOn,
        lastUpdatedOn = glucoseResultDB.lastUpdatedOn,
        afterMedication = glucoseResultDB.afterMedication,
        emptyStomach = glucoseResultDB.emptyStomach,
        notes = glucoseResultDB.notes
    )
    return dbResult


}

fun convertResearchResultToResearchDB(researchResultDB: ResearchResult): GlucoseResultDB {
    val dbResult =  GlucoseResultDB(
        id = researchResultDB.id,
        glucoseConcentration = researchResultDB.glucoseConcentration,
        unit = convertGlucoseUnitType(researchResultDB.unit),
        timestamp = researchResultDB.timestamp,
        userId = researchResultDB.userId,
        deletedOn = researchResultDB.deletedOn,
        lastUpdatedOn = researchResultDB.lastUpdatedOn,
        afterMedication = researchResultDB.afterMedication,
        emptyStomach = researchResultDB.emptyStomach,
        notes = researchResultDB.notes
    )
    return dbResult


}

private fun convertGlucoseUnitType(dbUnit: GlucoseUnitTypeDB): GlucoseUnitType {
    return when (dbUnit) {
        GlucoseUnitTypeDB.MG_PER_DL -> GlucoseUnitType.MG_PER_DL
        GlucoseUnitTypeDB.MMOL_PER_L -> GlucoseUnitType.MMOL_PER_L
        else -> throw IllegalArgumentException("Unknown GlucoseUnitTypeDB: $dbUnit")
    }
}

private fun convertGlucoseUnitType(dbUnit: GlucoseUnitType): GlucoseUnitTypeDB {
    return when (dbUnit) {
        GlucoseUnitType.MG_PER_DL -> GlucoseUnitTypeDB.MG_PER_DL
        GlucoseUnitType.MMOL_PER_L -> GlucoseUnitTypeDB.MMOL_PER_L
        else -> throw IllegalArgumentException("Unknown GlucoseUnitTypeDB: $dbUnit")
    }
}

fun convertResearchDBtoResearchResult(glucoseResultDB: List<GlucoseResultDB>): List<ResearchResult> {
    return glucoseResultDB.map { dbResult ->
        ResearchResult(
            id = dbResult.id,
            glucoseConcentration = dbResult.glucoseConcentration,
            unit = convertGlucoseUnitType(dbResult.unit),
            timestamp = dbResult.timestamp,
            userId = dbResult.userId,
            deletedOn = dbResult.deletedOn,
            lastUpdatedOn = dbResult.lastUpdatedOn,
            afterMedication = dbResult.afterMedication,
            emptyStomach = dbResult.emptyStomach,
            notes = dbResult.notes
        )
    }
}

fun convertMedicationResultToMedicationDB(result: UserMedicationResult): UserMedicationDB {
    return UserMedicationDB(
        medicationId = result.medicationId,
        userId = result.userId,
        dosage = result.dosage,
        frequency = result.frequency,
        startDate = result.startDate,
        endDate = result.endDate,
        notes = result.notes,
        isSynced = true
    )
}


fun convertMedicationFormToMedicationDB(form: CreateUserMedicationForm): UserMedicationDB {
    return UserMedicationDB(
        medicationId = form.medicationId,
        userId = form.userId,
        dosage = form.dosage,
        frequency = form.frequency,
        startDate = form.startDate,
        endDate = form.endDate,
        notes = form.notes,
        isSynced = false
    )
}


fun convertHeartBeatDBtoHeartbeatResult(researchResultDB: List<HeartbeatDB>): List<HeartbeatResult> {
    return researchResultDB.map { dbResult ->
        HeartbeatResult(
            id = dbResult.id,
            userId = dbResult.userId,
            timestamp = dbResult.timestamp,
            systolicPressure = dbResult.systolicPressure,
            diastolicPressure = dbResult.diastolicPressure,
            pulse = dbResult.pulse,
            note = dbResult.note
        )
    }
}

fun convertHeartBeatDBtoHeartbeatResult(researchResultDB: HeartbeatDB): HeartbeatResult {
    return HeartbeatResult(
            id = researchResultDB.id,
            userId = researchResultDB.userId,
            timestamp = researchResultDB.timestamp,
            systolicPressure = researchResultDB.systolicPressure,
            diastolicPressure = researchResultDB.diastolicPressure,
            pulse = researchResultDB.pulse,
            note = researchResultDB.note
        )
    }


fun convertHeartbeatResultToHeartBeatDB(researchResultDB: List<HeartbeatResult>): List<HeartbeatDB> {
    return researchResultDB.map { dbResult ->
        HeartbeatDB(
            id = dbResult.id,
            userId = dbResult.userId,
            timestamp = dbResult.timestamp,
            systolicPressure = dbResult.systolicPressure,
            diastolicPressure = dbResult.diastolicPressure,
            pulse = dbResult.pulse,
            note = dbResult.note
        )
    }
}

fun toUserType(userType: String): UserType{
    return when (userType) {
        "ADMIN" -> UserType.ADMIN
        "PATIENT" -> UserType.PATIENT
        "DOCTOR" -> UserType.DOCTOR
        "OBSERVER" -> UserType.OBSERVER

        else -> {
            UserType.PATIENT
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







