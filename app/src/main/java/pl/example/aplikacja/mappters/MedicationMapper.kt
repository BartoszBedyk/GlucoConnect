package pl.example.aplikacja.mappters

import pl.example.databasemodule.database.data.MedicationDB
import pl.example.networkmodule.apiData.MedicationResult

fun MedicationDB.toMedication(): MedicationResult = MedicationResult(
    id = this.id,
    name = this.name,
    description = this.description,
    manufacturer = this.manufacturer,
    form = this.form,
    strength = this.strength,
)

fun MedicationResult.toMedicationDB(): MedicationDB = MedicationDB(
    id = this.id,
    name = this.name,
    description = this.description,
    manufacturer = this.manufacturer,
    form = this.form,
    strength = this.strength,
)

fun List<MedicationDB>.toMedicationList(): List<MedicationResult> = this.map { it.toMedication() }

fun List<MedicationResult>.toMedicationDBList(): List<MedicationDB> = this.map { it.toMedicationDB() }
