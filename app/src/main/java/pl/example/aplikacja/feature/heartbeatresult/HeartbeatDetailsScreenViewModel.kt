package pl.example.aplikacja.feature.heartbeatresult

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.aplikacja.feature.login.isNetworkAvailable
import pl.example.aplikacja.mappters.toHeartbeatResult
import pl.example.databasemodule.database.repository.HeartbeatRepository
import pl.example.networkmodule.apiData.HeartbeatResult
import pl.example.networkmodule.apiMethods.AuthenticationApiInterface
import pl.example.networkmodule.apiMethods.HeartbeatApiInterface

class HeartbeatDetailsScreenViewModel(
    private val heartbeatResultRepository: HeartbeatRepository,
    private val heartbeatApi: HeartbeatApiInterface,
    private val authenticationApi: AuthenticationApiInterface,
    @ApplicationContext context: Context,
    private val resultId: String,
) : ViewModel() {

    private val _heartbeatResult = MutableStateFlow<HeartbeatResult?>(null)
    val heartbeatResult: StateFlow<HeartbeatResult?> = _heartbeatResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _healthy = MutableStateFlow<Boolean>(false)
    val healthy: StateFlow<Boolean> = _healthy

    init {
        isApiAvilible(context)
        viewModelScope.launch {
            healthy.collect { isHealthy ->
                if (isHealthy) {
                    fetchHeartbeatResult()
                } else {
                    fetchHeartbeatResult()
                }
            }
        }
    }

    private fun fetchHeartbeatResult() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (!healthy.value) throw IllegalStateException("API not available")

                val result = heartbeatApi.getHeartBeat(resultId)
                _heartbeatResult.value = result
            } catch (e: Exception) {
                Log.e("GlucoseDetails", "podejmie pobranie z bazy")
                val result = heartbeatResultRepository.getHeartbeatResultById(resultId)
                Log.e("GlucoseDetails", "podejmie pobranie z bazy ${result?.systolicPressure}")
                _heartbeatResult.value = result?.toHeartbeatResult()
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun deleteHeartbeatResult(): Boolean {
        var deleted: Boolean = false
        viewModelScope.launch {
            try {
                heartbeatApi.deleteHeartbeat(resultId)
                heartbeatResultRepository.deleteHeartbeatResult(resultId)
                deleted = true
            } catch (e: Exception) {
                Log.e("GlucoseDetails", "Error deleting glucose result: ${e.message}")
                deleted = false
            }
        }
        return deleted
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
