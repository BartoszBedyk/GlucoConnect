package pl.example.aplikacja.viewModels


import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.aplikacja.convertResearchDBtoResearchResult
import pl.example.aplikacja.convertUnits
import pl.example.databasemodule.database.repository.GlucoseResultRepository
import pl.example.networkmodule.apiData.HeartbeatResult
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiMethods.ApiProvider

class AllResultsScreenViewModel(context: Context, private val USER_ID: String) : ViewModel() {

    private val apiProvider = ApiProvider(context)

    private val researchRepository = GlucoseResultRepository(context)

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

                Log.e("NO WIFI", "Failed to fetch data from API: ${e.message}", e)

                _glucoseResults.value = convertResearchDBtoResearchResult(
                    researchRepository.getResearchResultsForUser(USER_ID)
                )

                _glucoseResultData.value = convertResearchDBtoResearchResult(
                    researchRepository.getResearchResultsForUser(USER_ID)
                )
                _heartbeatResult.value = emptyList()
            } finally {
                _isLoading.value=false
            }
        }
    }


    private fun syncDatabases() {
        viewModelScope.launch {
            try {
                val unsyncedResults = researchRepository.getUnsyncedResearchResults()
                if (unsyncedResults.isNotEmpty()) {
                    convertResearchDBtoResearchResult(unsyncedResults).forEach { result ->
                        try{
                            resultApi.syncResult(result)
                            Log.d("SYNC", "Syncing result: $result")
                            researchRepository.markAsSynced(result.id.toString())
                            Log.d("SYNC", "Synced MARK: ${result.id}")
                        }catch (e: Exception){
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