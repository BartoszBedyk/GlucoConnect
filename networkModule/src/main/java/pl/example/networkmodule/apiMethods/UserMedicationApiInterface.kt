package pl.example.networkmodule.apiMethods

import pl.example.networkmodule.apiData.UserMedicationResult
import pl.example.networkmodule.requestData.CreateUserMedicationForm

interface UserMedicationApiInterface {
    suspend fun createUserMedication(userMedication: CreateUserMedicationForm): String?
    suspend fun readUserMedication(id: String): UserMedicationResult?
    suspend fun deleteUserMedication(id: String): Boolean
    suspend fun deleteUserMedicationForUser(userId: String): Boolean
    suspend fun readTodayUserMedication(id: String): List<UserMedicationResult>?
    suspend fun getUserMedication(userId: String, medicationId: String): UserMedicationResult?
    suspend fun readUserMedicationByID(umId: String): List<UserMedicationResult>?
    suspend fun markAsSynced(userId: String): Boolean
    suspend fun getUserMedicationId(id: String, medicationId: String): String?

}