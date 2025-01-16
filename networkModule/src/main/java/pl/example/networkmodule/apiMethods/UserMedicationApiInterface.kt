package pl.example.networkmodule.apiMethods

import pl.example.networkmodule.apiData.UserMedicationResult
import pl.example.networkmodule.requestData.CreateUserMedicationForm

interface UserMedicationApiInterface {
    suspend fun createUserMedication(userMedication: CreateUserMedicationForm): Boolean
    suspend fun readUserMedication(id: String): UserMedicationResult?
    suspend fun deleteUserMedication(id: String): Boolean
    suspend fun deleteUserMedicationForUser(userId: String): Boolean
    suspend fun readTodayUserMedication(id: String): List<UserMedicationResult>?

}