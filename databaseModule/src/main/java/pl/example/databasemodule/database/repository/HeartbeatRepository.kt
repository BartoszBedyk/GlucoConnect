package pl.example.databasemodule.database.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.example.databasemodule.database.ResearchResultManager
import pl.example.databasemodule.database.data.HeartbeatResultDB

class HeartbeatRepository(context: Context) {
    private val database = ResearchResultManager.getDatabase(context)
    private val dao = database.heartbeatResultDao

    suspend fun insert(heartbeatResult: HeartbeatResultDB) = withContext(Dispatchers.IO) {
         dao.insert(heartbeatResult)
    }

    suspend fun getHeartbeatResultsForUser(userId: String): List<HeartbeatResultDB> {
        return dao.getHeartbeatResultsForUser(userId)

    }

    suspend fun deleteHeartbeatResult(id: String) {
        return dao.deleteHeartbeatResult(id)
    }

    suspend fun getUnsyncedHeartbeatResults(): List<HeartbeatResultDB> {
        return dao.getUnsyncedHeartbeatResults()
    }

    suspend fun markAsSynced(id: String) {
        return dao.markAsSynced(id)
    }

    suspend fun getLatestHeartbeatResult(): List<HeartbeatResultDB?> {
        return dao.getLatestHeartbeatResult()
    }

    suspend fun insertAll(results: List<HeartbeatResultDB>) {
        dao.insertAll(results)
    }

}