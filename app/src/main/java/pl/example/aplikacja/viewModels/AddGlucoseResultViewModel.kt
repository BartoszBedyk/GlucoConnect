package pl.example.aplikacja.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.aplikacja.convertResearchResultToResearchDB
import pl.example.aplikacja.removeQuotes
import pl.example.databasemodule.database.repository.GlucoseResultRepository
import pl.example.databasemodule.database.data.GlucoseUnitTypeDB
import pl.example.databasemodule.database.data.ResearchResultDB
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.requestData.ResearchResultCreate
import java.util.Date
import java.util.UUID

class AddGlucoseResultViewModel(private val context: Context, private val USER_ID: String) :
    ViewModel() {

    private val apiProvider = ApiProvider(context)
    private val researchRepository = GlucoseResultRepository(context)

    private val resultApi = apiProvider.resultApi
    private val userApi = apiProvider.userApi

    private val _prefUnit = MutableStateFlow<GlucoseUnitType>(GlucoseUnitType.MG_PER_DL)
    val prefUnit: StateFlow<GlucoseUnitType> = _prefUnit

    init {
        fetchUnit()
    }

    suspend fun addGlucoseResult(form: ResearchResultCreate): Boolean {
        Log.d("API", "Adding glucose result: $form")
         try {
            val id = resultApi.createResearchResult(form)
            if (id != null) {
                val success = addIntoDatabase(removeQuotes(id))
                if (!success) {
                    Log.e("LOCALY", "Failed to add data into local database.")
                }
                return success
            }
             return saveLocally(form)
        } catch (e: Exception) {
            Log.e("API", "Failed to add glucose result to API, saving locally: ${e.message}", e)
            return true
        }
    }


    private suspend fun addIntoDatabase(id: String): Boolean {
        return try {
            val researchResult = resultApi.getResearchResultsById(id)
            Log.d("LOCALY ", "Fetched research result: $researchResult")
            if (researchResult != null) {
                val converted = convertResearchResultToResearchDB(researchResult)
                researchRepository.insert(converted)
                return true
            }
            false
        } catch (e: Exception) {
            Log.e("LOCALY", "Failed to add result to database: ${e.message}", e)
            false
        }
    }

    private suspend fun saveLocally(form: ResearchResultCreate): Boolean {
        try {
            val localResult = convertResearchResultCreateToResearchDB(form)
            researchRepository.insert(localResult)
            Log.d("LOCALY", "Glucose result saved locally: $localResult")
            return true
        } catch (e: Exception) {
            Log.e("LOCALY", "Failed to save glucose result locally: ${e.message}", e)
            return false
        }
    }

    private fun convertResearchResultCreateToResearchDB(form: ResearchResultCreate): ResearchResultDB {
        return ResearchResultDB(
            id = UUID.randomUUID(),
            sequenceNumber = form.sequenceNumber,
            glucoseConcentration = form.glucoseConcentration,
            unit = GlucoseUnitTypeDB.valueOf(form.unit),
            timestamp = form.timestamp,
            userId = UUID.fromString(USER_ID),
            deletedOn = null,
            lastUpdatedOn = Date(),
            isSynced = false
        )
    }


    private fun fetchUnit() {
        viewModelScope.launch {
            try {
                _prefUnit.value = userApi.getUserUnitById(USER_ID) ?: GlucoseUnitType.MG_PER_DL
            } catch (e: Exception) {
                Log.e("API", "Failed to fetch user unit: ${e.message}", e)
            }
        }
    }
}
