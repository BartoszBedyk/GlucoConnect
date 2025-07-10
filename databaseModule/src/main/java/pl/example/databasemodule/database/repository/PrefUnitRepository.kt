package pl.example.databasemodule.database.repository

import android.content.Context
import pl.example.databasemodule.database.ResearchResultManager
import pl.example.databasemodule.database.data.PrefUnitDB

class PrefUnitRepository(context: Context) {
    private val database = ResearchResultManager.getDatabase(context)
    private val dao = database.prefUnitDao

     suspend fun insert(unit: PrefUnitDB) {
         return dao.insert(unit)
     }

     suspend fun getUnitByUserId(userId: String): String? {
        return dao.getUnitByUserId(userId)
    }

     suspend fun updateGlucoseUnit(userId: String, glucoseUnit: String) {
        return dao.updateGlucoseUnit(userId, glucoseUnit)
    }

     suspend fun getAllNotSynced(): PrefUnitDB {
        return dao.getAllNotSynced()
    }

    suspend fun getUserDiabetesType(userId: String): String {
        return dao.getUserDiabetesType(userId)
    }

     suspend fun sync(userId: String) {
        return dao.sync(userId)
    }
}