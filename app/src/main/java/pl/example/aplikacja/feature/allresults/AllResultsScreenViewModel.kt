package pl.example.aplikacja.feature.allresults

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.aplikacja.JwtHelper
import pl.example.aplikacja.feature.login.isNetworkAvailable
import pl.example.aplikacja.mappters.convertUnits
import pl.example.aplikacja.mappters.stringUnitParser
import pl.example.aplikacja.mappters.toHeartbeatResultList
import pl.example.aplikacja.mappters.toResearchResult
import pl.example.aplikacja.mappters.toResearchResultList
import pl.example.databasemodule.database.repository.GlucoseResultRepository
import pl.example.databasemodule.database.repository.HeartbeatRepository
import pl.example.databasemodule.database.repository.PrefUnitRepository
import pl.example.networkmodule.apiData.HeartbeatResult
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiMethods.AuthenticationApiInterface
import pl.example.networkmodule.apiMethods.HeartbeatApiInterface
import pl.example.networkmodule.apiMethods.ResultApiInterface
import pl.example.networkmodule.apiMethods.UserApiInterface
import javax.inject.Inject

@HiltViewModel
class AllResultsScreenViewModel @Inject constructor(
    private val resultApi: ResultApiInterface,
    private val userApi: UserApiInterface,
    private val heartbeatApi: HeartbeatApiInterface,
    private val researchRepository: GlucoseResultRepository,
    private val prefUnitRepository: PrefUnitRepository,
    private val heartbeatsRepository: HeartbeatRepository,
    private val authenticationApi: AuthenticationApiInterface,
    @ApplicationContext private val context: Context,
    jwtHelper: JwtHelper,
) : ViewModel() {

    private val userId: String = jwtHelper.getUserId()

    private val _healthy = MutableStateFlow<Boolean>(false)
    val healthy: StateFlow<Boolean> = _healthy

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _glucoseResults = MutableStateFlow<List<ResearchResult>>(emptyList())
    val glucoseResults: StateFlow<List<ResearchResult>> = _glucoseResults

    private val _glucoseResultData = MutableStateFlow<List<ResearchResult>>(emptyList())
    val glucoseResultData: StateFlow<List<ResearchResult>> = _glucoseResultData

    private val _heartbeatResult = MutableStateFlow<List<HeartbeatResult>>(emptyList())
    val heartbeatResult: StateFlow<List<HeartbeatResult>> = _heartbeatResult

    private val _prefUnit = MutableStateFlow<GlucoseUnitType>(GlucoseUnitType.MMOL_PER_L)
    val prefUnit: StateFlow<GlucoseUnitType> = _prefUnit

    init {
        isApiAvilible(context)

        viewModelScope.launch {
            healthy.collect { isHealthy ->
                if (isHealthy) {
                    syncDatabases()
                    fetchItemsAsync()
                } else {
                    fetchItemsAsync()
                }
            }
        }
    }

    private fun fetchItemsAsync() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                if (!healthy.value) throw IllegalStateException("API not available")

                val results = resultApi.getResultsByUserId(userId) ?: emptyList()
                _prefUnit.value = userApi.getUserUnitById(userId) ?: GlucoseUnitType.MMOL_PER_L
                _glucoseResults.value = convertUnits(results, prefUnit.value)
                _heartbeatResult.value = heartbeatApi.readHeartbeatForUser(userId) ?: emptyList()
                researchRepository.insertAllResults(results)
            } catch (e: Exception) {
                _prefUnit.value = stringUnitParser(prefUnitRepository.getUnitByUserId(userId))
                _glucoseResults.value =
                    researchRepository.getAllGlucoseResultsByUserId(userId).toResearchResultList()

                _glucoseResults.value = convertUnits(_glucoseResults.value, prefUnit.value)
                _heartbeatResult.value = heartbeatsRepository.getHeartbeatResultsForUser(userId).toHeartbeatResultList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun syncDatabases() {
        viewModelScope.launch {
            try {
                if (!healthy.value) throw IllegalStateException("API not available")

                val unsyncedResults = researchRepository.getUnsyncedResearchResults()
                Log.i("SYNC", "List of unsynced + ${unsyncedResults.size}")
                if (unsyncedResults.isNotEmpty()) {
                    unsyncedResults.forEach { result ->
                        try {
                            resultApi.syncResult(result.toResearchResult())
                            researchRepository.markAsSynced(result.id.toString())
                        } catch (e: Exception) {
                            Log.e("SYNC", "Failed to sync result: $result", e)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.i("SYNC", "Failed to sync data with API: ${e.message}", e)
            }
        }
    }

    private var lastCheckedTime = 0L

    fun isApiAvilible(context: Context) {
        val now = System.currentTimeMillis()
        if (now - lastCheckedTime < 10_000) return
        lastCheckedTime = now

        viewModelScope.launch {
            try {
                val apiAvailable = authenticationApi.isApiAvlible()
                val networkAvailable = isNetworkAvailable(context)

                Log.d("HealthCheck", "API: $apiAvailable, Network: $networkAvailable")

                _healthy.value = apiAvailable == true && networkAvailable
                Log.d("HealthCheck", "Healthy: ${_healthy.value}")
            } catch (e: Exception) {
                Log.e("HealthCheck", "Error while checking health", e)
                _healthy.value = false
            }
        }
    }
}
