package pl.example.aplikacja.feature.mainuser

import pl.example.networkmodule.apiData.HeartbeatResult
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiData.enumTypes.DiabetesType
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType

data class MainUiState(
    val isLoading: Boolean = false,
    val glucoseItems: List<ResearchResult> = emptyList(),
    val heartbeatItems: List<HeartbeatResult> = emptyList(),
    val userDiabetesType: DiabetesType = DiabetesType.NONE,
    val userHb1AcValue: Float = 0.0f,
    val prefUnit: GlucoseUnitType = GlucoseUnitType.MMOL_PER_L,
    val isHealthy: Boolean = false
)