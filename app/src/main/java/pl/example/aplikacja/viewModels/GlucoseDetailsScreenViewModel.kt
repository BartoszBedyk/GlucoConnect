package pl.example.aplikacja.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiMethods.ApiProvider
import java.math.RoundingMode

class GlucoseDetailsScreenViewModel(
    apiProvider: ApiProvider,
    private val RESULT_ID: String,
    private val USER_ID: String
) : ViewModel() {

    private val resultApi = apiProvider.resultApi
    private val userApi = apiProvider.userApi

    private val _glucoseResult = MutableStateFlow<ResearchResult?>(null)
    val glucoseResult: MutableStateFlow<ResearchResult?> = _glucoseResult

    private val _prefUnit = MutableStateFlow<GlucoseUnitType>(GlucoseUnitType.MMOL_PER_L)
    val prefUnit: StateFlow<GlucoseUnitType> = _prefUnit

    init {
        fetchGlucoseResult()

    }

    private fun fetchGlucoseResult() {
        viewModelScope.launch {
            val result = resultApi.getResearchResultsById(RESULT_ID)
            _prefUnit.value = userApi.getUserUnitById(USER_ID) ?: GlucoseUnitType.MG_PER_DL
            _glucoseResult.value = result?.let { convertUnit(it) }

        }
    }

    private fun convertUnit(result: ResearchResult): ResearchResult {
        return if (result.unit != prefUnit.value) {
            val convertedConcentration = when (result.unit) {
                GlucoseUnitType.MG_PER_DL -> result.glucoseConcentration / 18.0182
                GlucoseUnitType.MMOL_PER_L -> result.glucoseConcentration * 18.0182
            }.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
            result.copy(
                glucoseConcentration = convertedConcentration,
                unit = prefUnit.value
            )
        } else {
            result
        }
    }
}