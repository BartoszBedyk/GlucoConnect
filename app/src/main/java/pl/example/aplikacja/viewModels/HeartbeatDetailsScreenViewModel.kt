package pl.example.aplikacja.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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

    init {
        fetchHeartbeatResult()
    }

    private fun fetchHeartbeatResult() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = heartbeatAPi.getHeartBeat(RESULT_ID)
                _heartbeatResult.value = result
            } catch (e: Exception) {
                val result = heartbeatResultRepository.getHeartbeatResultById(RESULT_ID)
                _heartbeatResult.value = result?.let { convertHeartBeatDBtoHeartbeatResult(it) }
            } finally {
                _isLoading.value = false
            }
        }
    }
}