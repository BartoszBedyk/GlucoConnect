package pl.example.databasemodule.database.repository


import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import pl.example.databasemodule.database.ResearchResultManager
import pl.example.databasemodule.database.data.GlucoseResultDB
import pl.example.databasemodule.database.data.GlucoseUnitTypeDB
import pl.example.networkmodule.apiData.ResearchResult
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow
import kotlin.math.sqrt


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

     fun getUserGbA1cById(id: String): Float {
        return calculateGbA1c(id)
    }

     fun getUserStandardDeviationById(id: String): Double {
        return standardDeviation(id)
    }

    private fun calculateGbA1c(id: String) : Float = runBlocking {
        val listOfGlucoseResult: MutableList<GlucoseResultDB>
        try {
            listOfGlucoseResult = dao.getResearchResultsForUser(id).toMutableList()
            var sum = 0.0
            for (glucoseResult in listOfGlucoseResult) {
                sum += if (glucoseResult.unit.toString() == "MG_PER_DL") {
                    glucoseResult.glucoseConcentration
                } else {
                    (glucoseResult.glucoseConcentration * 18.0182)
                }

            }

            val average = sum / listOfGlucoseResult.size

            return@runBlocking BigDecimal((average + 46.7) / 28.7).setScale(2, RoundingMode.HALF_UP).toFloat()
        } catch (_: Exception) {
            return@runBlocking 0.0f
        }

    }

    private fun standardDeviation(id: String): Double = runBlocking {
        try {
            val results = dao.getResearchResultsForUser(id)
                .take(93)
                .map { result ->
                    if (result.unit.toString() == "MG_PER_DL") result.glucoseConcentration
                    else result.glucoseConcentration * 18.0182
                }

            if (results.isEmpty()) return@runBlocking 0.0

            val average = results.average()

            val variance = results.sumOf { (it - average).pow(2) } / results.size
            return@runBlocking BigDecimal(sqrt(variance))
                .setScale(2, RoundingMode.HALF_UP)
                .toDouble()
        } catch (e: Exception) {
            return@runBlocking 0.0
        }
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