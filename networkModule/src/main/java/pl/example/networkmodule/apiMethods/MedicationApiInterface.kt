package pl.example.networkmodule.apiMethods

import pl.example.networkmodule.apiData.MedicationResult
import pl.example.networkmodule.requestData.CreateMedication

interface MedicationApiInterface {
    suspend fun createMedication(medication: CreateMedication): Boolean
    suspend fun readMedication(id: String): MedicationResult?
    suspend fun getAllMedications(): List<MedicationResult>?
    suspend fun deleteMedication(id: String): Boolean
}