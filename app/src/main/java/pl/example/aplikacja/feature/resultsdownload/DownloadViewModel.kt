package pl.example.aplikacja.feature.resultsdownload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiMethods.ResultApiInterface
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(private val glucoseApi: ResultApiInterface) : ViewModel() {

    private val _glucoseResults = MutableStateFlow<List<ResearchResult>>(emptyList())
    val glucoseResults: StateFlow<List<ResearchResult>> = _glucoseResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            _isLoading.value = true
            delay(200)
            try {
                val results = glucoseApi.getAllResearchResults()
                _glucoseResults.value = results ?: emptyList()
            } catch (e: Exception) {
                _glucoseResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadDataIfNeeded() {
        if (_glucoseResults.value.isEmpty()) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    val results = glucoseApi.getAllResearchResults()
                    _glucoseResults.value = results ?: emptyList()
                } catch (e: Exception) {
                    _glucoseResults.value = emptyList()
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }
}
