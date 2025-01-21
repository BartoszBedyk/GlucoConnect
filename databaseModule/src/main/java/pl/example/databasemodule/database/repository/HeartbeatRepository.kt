package pl.example.databasemodule.database.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.example.databasemodule.database.ResearchResultManager
import pl.example.databasemodule.database.data.HeartbeatDB

class HeartbeatRepository(context: Context) {
    private val database = ResearchResultManager.getDatabase(context)
    private val dao = database.heartbeatResultDao

    suspend fun insert(heartbeatResult: HeartbeatDB) = withContext(Dispatchers.IO) {
         dao.insert(heartbeatResult)
    }

    suspend fun getHeartbeatResultsForUser(userId: String): List<HeartbeatDB> {
        return dao.getHeartbeatResultsForUser(userId)

    }

    suspend fun deleteHeartbeatResult(id: String) {
        return dao.deleteHeartbeatResult(id)
    }

    suspend fun getUnsyncedHeartbeatResults(): List<HeartbeatDB> {
        return dao.getUnsyncedHeartbeatResults()
    }

    suspend fun markAsSynced(id: String) {
        return dao.markAsSynced(id)
    }

    suspend fun getLatestHeartbeatResult(): List<HeartbeatDB?> {
        return dao.getLatestHeartbeatResult()
    }

    suspend fun insertAll(results: List<HeartbeatDB>) {
        dao.insertAll(results)
    }

}