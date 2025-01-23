package pl.example.databasemodule.database.repository


import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.example.databasemodule.database.data.GlucoseUnitTypeDB
import pl.example.databasemodule.database.data.ResearchResultDB
import pl.example.databasemodule.database.ResearchResultManager
import pl.example.networkmodule.apiData.ResearchResult


class GlucoseResultRepository(context: Context)  {
    private val database = ResearchResultManager.getDatabase(context)
    private val dao = database.researchResultDao

    suspend fun insert(researchResult: ResearchResultDB) = withContext(Dispatchers.IO) {
        dao.insert(researchResult)
    }

    suspend fun getResearchResultsForUser(userId: String): List<ResearchResultDB> {
        return dao.getResearchResultsForUser(userId)
    }

    suspend fun getResearchResultById(id: String): ResearchResultDB? {
        return dao.getResearchResultById(id)
    }


    suspend fun deleteResearchResult(id: String) {
        return dao.deleteResearchResult(id)
    }

    suspend fun getUnsyncedResearchResults(): List<ResearchResultDB> {
        return dao.getUnsyncedResearchResults()
    }

    suspend fun markAsSynced(id: String) {
        return dao.markAsSynced(id)
    }

    suspend fun getLatestThreeResearchResult(userId: String): List<ResearchResultDB> {
        return dao.getLatestThreeResearchResult(userId)
    }

    suspend fun insertAllResults(results: List<ResearchResult>) {

        val dbResults = results.map { result ->
            ResearchResultDB(
                id = result.id,
                sequenceNumber = result.sequenceNumber,
                glucoseConcentration = result.glucoseConcentration,
                unit = GlucoseUnitTypeDB.valueOf(result.unit.name),
                timestamp = result.timestamp,
                userId = result.userId,
                deletedOn = result.deletedOn,
                lastUpdatedOn = result.lastUpdatedOn,
                isSynced = true
            )
        }

        dao.insertAll(dbResults)
    }




}