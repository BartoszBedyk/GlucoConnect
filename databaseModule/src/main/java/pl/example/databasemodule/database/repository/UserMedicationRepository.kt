package pl.example.databasemodule.database.repository

import android.content.Context
import pl.example.databasemodule.database.Dao.UserMedicationDao
import pl.example.databasemodule.database.ResearchResultManager
import pl.example.databasemodule.database.data.UserMedicationDB
import pl.example.networkmodule.apiData.UserMedicationResult

class UserMedicationRepository(context: Context){
    private val database = ResearchResultManager.getDatabase(context)
    private val dao = database.userMedicationDao

     suspend fun insert(userMedicationResult: UserMedicationDB) {
        return dao.insert(userMedicationResult)
    }

    suspend fun insertAll(userMedicationResults: List<UserMedicationDB>) {
        return dao.insertAll(userMedicationResults)
    }

     suspend fun getMedicationsForUser(userId: String): List<UserMedicationDB> {
         return dao.getMedicationsForUser(userId)
    }

     suspend fun deleteMedication(medicationId: String) {
        dao.deleteMedication(medicationId)
    }

     suspend fun getLatestMedicationsForUser(userId: String): List<UserMedicationDB> {
        return dao.getLatestMedicationsForUser(userId)
    }

     suspend fun getMedicationById(
        userId: String,
        medicationId: String
    ): UserMedicationDB? {
        return dao.getMedicationById(userId, medicationId)
    }

    suspend fun getTodayUserMedication(userId: String): List<UserMedicationResult>{
        return dao.getCurrentMedicationsForUser(userId)
    }

    suspend fun getUserMedicationHistory(userId: String): List<UserMedicationResult>{
        return dao.getUserMedicationHistory(userId)

    }
}