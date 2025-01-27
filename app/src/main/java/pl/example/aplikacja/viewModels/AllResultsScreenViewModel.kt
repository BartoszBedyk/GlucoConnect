package pl.example.aplikacja.viewModels


import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.aplikacja.convertHeartBeatDBtoHeartbeatResult
import pl.example.aplikacja.convertResearchDBtoResearchResult
import pl.example.aplikacja.convertUnits
import pl.example.aplikacja.stringUnitParser
import pl.example.databasemodule.database.repository.GlucoseResultRepository
import pl.example.databasemodule.database.repository.HeartbeatRepository
import pl.example.databasemodule.database.repository.PrefUnitRepository
import pl.example.networkmodule.apiData.HeartbeatResult
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiMethods.ApiProvider

class AllResultsScreenViewModel(context: Context, private val USER_ID: String) : ViewModel() {

    private val apiProvider = ApiProvider(context)

    private val researchRepository = GlucoseResultRepository(context)
    private val prefUnitRepository = PrefUnitRepository(context)
    private val heartbeatsRepository = HeartbeatRepository(context)

    private val resultApi = apiProvider.resultApi
    private val userApi = apiProvider.userApi
    private val heartbeatApi = apiProvider.heartbeatApi

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading


    private val _glucoseResults = MutableStateFlow<List<ResearchResult>>(emptyList())
    val glucoseResults: StateFlow<List<ResearchResult>> = _glucoseResults

    private val _glucoseResultData = MutableStateFlow<List<ResearchResult>>(emptyList())
    val glucoseResultsData: StateFlow<List<ResearchResult>> = _glucoseResultData

    private val _heartbeatResult = MutableStateFlow<List<HeartbeatResult>>(emptyList())
    val heartbeatResult: StateFlow<List<HeartbeatResult>> = _heartbeatResult

    private val _prefUnit = MutableStateFlow<GlucoseUnitType>(GlucoseUnitType.MMOL_PER_L)
    val prefUnit: StateFlow<GlucoseUnitType> = _prefUnit

    init {
        syncDatabases()
        fetchItemsAsync()
    }

    private fun fetchItemsAsync() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val results = resultApi.getResultsByUserId(USER_ID) ?: emptyList()
                _prefUnit.value = userApi.getUserUnitById(USER_ID) ?: GlucoseUnitType.MMOL_PER_L
                _glucoseResults.value = convertUnits(results, prefUnit.value)
                _heartbeatResult.value = heartbeatApi.readHeartbeatForUser(USER_ID) ?: emptyList()
                researchRepository.insertAllResults(results)
            } catch (e: Exception) {
                _prefUnit.value = stringUnitParser(prefUnitRepository.getUnitByUserId(USER_ID))
                _glucoseResults.value = convertResearchDBtoResearchResult(
                    researchRepository.getAllGlucoseResultsByUserId(USER_ID)
                )
                _glucoseResults.value = convertUnits(_glucoseResults.value, prefUnit.value)
                _heartbeatResult.value = convertHeartBeatDBtoHeartbeatResult(
                    heartbeatsRepository.getHeartbeatResultsForUser(USER_ID)
                )
            } finally {
                _isLoading.value = false
            }
        }
    }


    private fun syncDatabases() {
        viewModelScope.launch {
            try {
                val unsyncedResults = researchRepository.getUnsyncedResearchResults()
                if (unsyncedResults.isNotEmpty()) {
                    convertResearchDBtoResearchResult(unsyncedResults).forEach { result ->
                        try {
                            resultApi.syncResult(result)
                            researchRepository.markAsSynced(result.id.toString())
                        } catch (e: Exception) {
                            Log.e("SYNC", "Failed to sync result: $result", e)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SYNC", "Failed to sync data with API: ${e.message}", e)
            }
        }
    }


}