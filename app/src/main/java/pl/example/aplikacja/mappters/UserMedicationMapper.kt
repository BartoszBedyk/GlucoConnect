package pl.example.aplikacja.mappters

import pl.example.databasemodule.database.data.MedicationDB
import pl.example.databasemodule.database.data.UserMedicationDB
import pl.example.networkmodule.apiData.MedicationResult
import pl.example.networkmodule.apiData.UserMedicationResult
import pl.example.networkmodule.requestData.CreateUserMedicationForm

fun UserMedicationResult.toUserMedicationDB(): UserMedicationDB {
    return this.let { form ->
        UserMedicationDB(
            medicationId = form.medicationId,
            userId = form.userId,
            dosage = form.dosage,
            frequency = form.frequency,
            startDate = form.startDate,
            endDate = form.endDate,
            notes = form.notes,
            isSynced = true
        )
    }
}

fun CreateUserMedicationForm.toUserMedicationDB(): UserMedicationDB {
    return this.let { form ->
        UserMedicationDB(
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
}


fun List<UserMedicationResult>.toUserMedicationDBList(): List<UserMedicationDB> {
    return this.map { it.toUserMedicationDB() }
}

fun MedicationDB?.toMedicationResult(): MedicationResult? {
    if (this == null) return null
    return MedicationResult(
        id = this.id,
        name = this.name,
        manufacturer = this.manufacturer,
        form = this.form,
        strength = this.strength,
        description = this.description
    )
}

fun parseUserMedicationDBtoUserMedicationResult(
    userMedication: UserMedicationDB?,
    medication: MedicationResult?
): UserMedicationResult? {
    if (userMedication != null) {
        if (medication != null) {
            return UserMedicationResult(
                medicationId = userMedication.medicationId,
                userId = userMedication.userId,
                dosage = userMedication.dosage,
                frequency = userMedication.frequency,
                startDate = userMedication.startDate,
                endDate = userMedication.endDate,
                notes = userMedication.notes,
                medicationName = medication.name,
                manufacturer = medication.manufacturer,
                form = medication.form,
                strength = medication.strength,
                description = medication.description
            )
        }
    }
    return null
}

fun UserMedicationDB?.toUserMedicationResult(): UserMedicationResult? {
    if (this == null) return null
    return UserMedicationResult(
        medicationId = this.medicationId,
        userId = this.userId,
        dosage = this.dosage,
        frequency = this.frequency,
        startDate = this.startDate,
        endDate = this.endDate,
        notes = this.notes,
        medicationName = "",
        manufacturer = "",
        form = "",
        strength = "",
        description = ""
    )
}