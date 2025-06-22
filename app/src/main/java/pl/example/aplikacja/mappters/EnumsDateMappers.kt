package pl.example.aplikacja.mappters

import pl.example.databasemodule.database.data.DiabetesTypeDB
import pl.example.networkmodule.apiData.enumTypes.DiabetesType
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiData.enumTypes.RestrictedUserType
import pl.example.networkmodule.apiData.enumTypes.UserType
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
fun stringUnitParser(string: String?): GlucoseUnitType {
    return when (string) {
        "mg/dL" -> GlucoseUnitType.MG_PER_DL
        "mmol/l" -> GlucoseUnitType.MMOL_PER_L
        "MG_PER_DL" -> GlucoseUnitType.MG_PER_DL
        "MMOL_PER_L" -> GlucoseUnitType.MMOL_PER_L
        else -> GlucoseUnitType.MG_PER_DL
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