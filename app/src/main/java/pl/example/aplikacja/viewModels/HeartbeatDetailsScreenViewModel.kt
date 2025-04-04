package pl.example.aplikacja.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.aplikacja.Screens.isNetworkAvailable
import pl.example.aplikacja.convertHeartBeatDBtoHeartbeatResult
import pl.example.aplikacja.convertResearchDBtoResearchResult
import pl.example.databasemodule.database.repository.HeartbeatRepository
import pl.example.networkmodule.apiData.HeartbeatResult
import pl.example.networkmodule.apiMethods.ApiProvider

class HeartbeatDetailsScreenViewModel(
    context: Context,
    private val RESULT_ID: String
) : ViewModel() {
    private val apiProvider = ApiProvider(context)
    private val heartbeatResultRepository = HeartbeatRepository(context)

    private val heartbeatAPi = apiProvider.heartbeatApi

    private val _heartbeatResult = MutableStateFlow<HeartbeatResult?>(null)
    val heartbeatResult: StateFlow<HeartbeatResult?> = _heartbeatResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading


    private val authenticationApi = apiProvider.authenticationApi


    private val _healthy = MutableStateFlow<Boolean>(false)
    val healthy: StateFlow<Boolean> = _healthy

    init {
        isApiAvilible(apiProvider.innerContext)
        fetchHeartbeatResult()
    }

    private fun fetchHeartbeatResult() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (!healthy.value) throw IllegalStateException("API not available")


                val result = heartbeatAPi.getHeartBeat(RESULT_ID)
                _heartbeatResult.value = result
            } catch (e: Exception) {
                Log.e("GlucoseDetails", "podejmie pobranie z bazy")
                val result = heartbeatResultRepository.getHeartbeatResultById(RESULT_ID)
                Log.e("GlucoseDetails", "podejmie pobranie z bazy ${result?.systolicPressure}")
                _heartbeatResult.value = result?.let { convertHeartBeatDBtoHeartbeatResult(it) }
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun deleteHeartbeatResult(): Boolean {
        var deleted: Boolean = false
        viewModelScope.launch {
            try {
                heartbeatAPi.deleteHeartbeat(RESULT_ID)
                heartbeatResultRepository.deleteHeartbeatResult(RESULT_ID)
                deleted = true
            } catch (e: Exception) {
                Log.e("GlucoseDetails", "Error deleting glucose result: ${e.message}")
                deleted = false
            }
        }
        return deleted
    }

    var lastCheckedTime = 0L
    private fun isApiAvilible(context: Context) {
        val now = System.currentTimeMillis()
        if (now - lastCheckedTime < 10_000) return
        lastCheckedTime = now

        viewModelScope.launch {
            try {
                _healthy?.value =
                    authenticationApi.isApiAvlible() == true && isNetworkAvailable(context)
            } catch (e: Exception) {
                _healthy?.value = false
            }
        }
    }
}