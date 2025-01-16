package pl.example.aplikacja.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.aplikacja.convertUnits
import pl.example.networkmodule.apiData.HeartbeatResult
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiMethods.ApiProvider

class AllResultsScreenViewModel(apiProvider: ApiProvider, private val USER_ID: String) : ViewModel(){


    private val resultApi = apiProvider.resultApi
    private val userApi = apiProvider.userApi
    private val heartbeatApi = apiProvider.heartbeatApi


    private val _glucoseResults = MutableStateFlow<List<ResearchResult>>(emptyList())
    val glucoseResults: StateFlow<List<ResearchResult>> = _glucoseResults

    private val _heartbeatResult = MutableStateFlow<List<HeartbeatResult>>(emptyList())
    val heartbeatResult: StateFlow<List<HeartbeatResult>> = _heartbeatResult

    private val _prefUnit = MutableStateFlow<GlucoseUnitType>(GlucoseUnitType.MMOL_PER_L)
    val prefUnit: StateFlow<GlucoseUnitType> = _prefUnit

    init {
        fetchItemsAsync()
    }

    private fun fetchItemsAsync() {
        viewModelScope.launch {
            val results = resultApi.getResultsByUserId(USER_ID) ?: emptyList()
            _prefUnit.value = userApi.getUserUnitById(USER_ID) ?: GlucoseUnitType.MMOL_PER_L
            _glucoseResults.value = convertUnits(results, prefUnit.value)
            _heartbeatResult.value = heartbeatApi.readHeartbeatForUser(USER_ID) ?: emptyList()
        }
    }



}