package pl.example.aplikacja.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiMethods.ApiProvider
import javax.inject.Inject


@HiltViewModel
class DownloadViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val apiProvider = ApiProvider(context)
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






