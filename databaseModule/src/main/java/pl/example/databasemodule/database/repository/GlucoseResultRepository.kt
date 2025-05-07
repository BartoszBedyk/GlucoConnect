package pl.example.databasemodule.database.repository


import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.example.databasemodule.database.data.GlucoseUnitTypeDB
import pl.example.databasemodule.database.data.GlucoseResultDB
import pl.example.databasemodule.database.ResearchResultManager
import pl.example.networkmodule.apiData.ResearchResult


class GlucoseResultRepository(context: Context)  {
    private val database = ResearchResultManager.getDatabase(context)
    private val dao = database.glucoseResultDao

    suspend fun insert(researchResult: GlucoseResultDB) = withContext(Dispatchers.IO) {
        dao.insert(researchResult)
    }

    suspend fun getAllGlucoseResultsByUserId(userId: String): List<GlucoseResultDB> {
        return dao.getResearchResultsForUser(userId)
    }

    suspend fun getResearchResultById(id: String): GlucoseResultDB? {
        return dao.getResearchResultById(id)
    }


    suspend fun deleteResearchResult(id: String) {
        return dao.deleteResearchResult(id)
    }

    suspend fun getUnsyncedResearchResults(): List<GlucoseResultDB> {
        return dao.getUnsyncedResearchResults()
    }

    suspend fun markAsSynced(id: String) {
        return dao.markAsSynced(id)
    }

    suspend fun getLatestThreeResearchResult(userId: String): List<GlucoseResultDB> {
        return dao.getLatestThreeResearchResult(userId)
    }

    suspend fun insertAllResults(results: List<ResearchResult>) {
        val dbResults = results.map { result ->
            GlucoseResultDB(
                id = result.id,
                glucoseConcentration = result.glucoseConcentration,
                unit = stringUnitDBParser(result.unit.toString()),
                timestamp = result.timestamp,
                userId = result.userId,
                deletedOn = result.deletedOn,
                lastUpdatedOn = result.lastUpdatedOn,
                afterMedication = result.afterMedication,
                emptyStomach = result.emptyStomach,
                notes = result.notes,
                isSynced = true
            )
        }
        dao.insertAll(dbResults)
    }

    fun stringUnitDBParser(string: String?): GlucoseUnitTypeDB {
        return when (string) {
            "mg/dL" -> GlucoseUnitTypeDB.MG_PER_DL
            "mmol/l" -> GlucoseUnitTypeDB.MMOL_PER_L
            "MG_PER_DL" -> GlucoseUnitTypeDB.MG_PER_DL
            "MMOL_PER_L" -> GlucoseUnitTypeDB.MMOL_PER_L
            else -> GlucoseUnitTypeDB.MG_PER_DL
        }

    }




}