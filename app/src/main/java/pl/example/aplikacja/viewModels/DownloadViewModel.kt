package pl.example.aplikacja.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiMethods.ApiProvider


class DownloadViewModel(apiProvider: ApiProvider) : ViewModel() {
    private val glucoseApi = apiProvider.resultApi

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

class DownloadViewModelFactory(private val apiProvider: ApiProvider) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DownloadViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DownloadViewModel(apiProvider) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}






