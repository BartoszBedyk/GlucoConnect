package pl.example.databasemodule.database.repository

import android.content.Context
import pl.example.databasemodule.database.Dao.MedicationDao
import pl.example.databasemodule.database.ResearchResultManager
import pl.example.databasemodule.database.data.MedicationDB

class MedicationRepository(context: Context){
    private val database = ResearchResultManager.getDatabase(context)
    private val dao = database.medicationDao

     suspend fun insert(medicationResult: MedicationDB) {
        dao.insert(medicationResult)
    }

     suspend fun getMedicationById(id: String): MedicationDB? {
       return  dao.getMedicationById(id)
    }
     suspend fun insertAll(results: List<MedicationDB>) {
        dao.insertAll(results)
    }

     suspend fun getAllMedications(): List<MedicationDB> {
        return dao.getAllMedications()
    }

     suspend fun deleteMedication(id: String) {
        return dao.deleteMedication(id)
    }

}