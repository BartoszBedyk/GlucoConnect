package pl.example.aplikacja.feature.glucoseresult

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.aplikacja.feature.login.isNetworkAvailable
import pl.example.aplikacja.mappters.toDiabetesType
import pl.example.aplikacja.mappters.toResearchResult
import pl.example.databasemodule.database.repository.GlucoseResultRepository
import pl.example.databasemodule.database.repository.PrefUnitRepository
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiData.enumTypes.DiabetesType
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiMethods.AuthenticationApiInterface
import pl.example.networkmodule.apiMethods.ResultApiInterface
import pl.example.networkmodule.apiMethods.UserApiInterface
import java.math.RoundingMode

class GlucoseDetailsScreenViewModel(
    private val glucoseResultRepository: GlucoseResultRepository,
    private val userRepository: PrefUnitRepository,
    private val resultApi: ResultApiInterface,
    private val userApi: UserApiInterface,
    private val resultId: String,
    private val userId: String,
    private val authenticationApi: AuthenticationApiInterface,
    @ApplicationContext context: Context,
) : ViewModel() {

    private val _glucoseResult = MutableStateFlow<ResearchResult?>(null)
    val glucoseResult: MutableStateFlow<ResearchResult?> = _glucoseResult

    private val _diabetesType = MutableStateFlow<DiabetesType>(DiabetesType.NONE)
    val diabetesType: MutableStateFlow<DiabetesType> = _diabetesType

    private val _prefUnit = MutableStateFlow<GlucoseUnitType>(GlucoseUnitType.MMOL_PER_L)
    val prefUnit: StateFlow<GlucoseUnitType> = _prefUnit

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _healthy = MutableStateFlow<Boolean>(false)
    val healthy: StateFlow<Boolean> = _healthy

    init {
        isApiAvilible(context = context)
        fetchGlucoseResult()
        fechDiabetesType()
    }

    private fun fechDiabetesType() {
        viewModelScope.launch {
            try {
                check(healthy.value) { "API not available" }
                _diabetesType.value = userApi.getUserById(userId)?.diabetesType ?: DiabetesType.NONE
            } catch (e: Exception) {
                _diabetesType.value = userRepository.getUserDiabetesType(userId).toDiabetesType()
                Log.e("GlucoseDetails", "Error fetching diabetes type: ${e.message}")
            }
        }
    }

    private fun fetchGlucoseResult() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                check(healthy.value) { "API not available" }

                val result = resultApi.getResearchResultsById(resultId)
                _prefUnit.value = userApi.getUserUnitById(userId) ?: GlucoseUnitType.MG_PER_DL
                _glucoseResult.value = result?.let { convertUnit(it) }
            } catch (e: Exception) {
                val result = glucoseResultRepository.getResearchResultById(resultId)
                _prefUnit.value = GlucoseUnitType.MG_PER_DL
                if (result != null) {
                    _glucoseResult.value = result.toResearchResult()
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun convertUnit(result: ResearchResult): ResearchResult = if (result.unit != prefUnit.value) {
        val convertedConcentration = when (result.unit) {
            GlucoseUnitType.MG_PER_DL -> result.glucoseConcentration / 18.0182
            GlucoseUnitType.MMOL_PER_L -> result.glucoseConcentration * 18.0182
        }.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
        result.copy(
            glucoseConcentration = convertedConcentration,
            unit = prefUnit.value,
        )
    } else {
        result
    }

    suspend fun deleteGlucoseResult(): Boolean = try {
        resultApi.deleteResearchResult(resultId)
        Log.d("GlucoseDetails", "Glucose result deleted successfully")
        glucoseResultRepository.deleteResearchResult(resultId)
        Log.d("GlucoseDetails", "Glucose result deleted from database")
        true
    } catch (e: Exception) {
        Log.e("GlucoseDetails", "Error deleting glucose result: ${e.message}")
        false
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
