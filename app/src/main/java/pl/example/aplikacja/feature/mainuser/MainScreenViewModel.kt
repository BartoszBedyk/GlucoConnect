package pl.example.aplikacja.feature.mainuser

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.example.aplikacja.JwtHelper
import pl.example.aplikacja.feature.login.isNetworkAvailable
import pl.example.aplikacja.mappters.convertUnits
import pl.example.aplikacja.mappters.stringUnitParser
import pl.example.aplikacja.mappters.toDiabetesType
import pl.example.aplikacja.mappters.toDiabetesTypeDB
import pl.example.aplikacja.mappters.toHeartbeatResultList
import pl.example.aplikacja.mappters.toResearchResult
import pl.example.databasemodule.database.data.PrefUnitDB
import pl.example.databasemodule.database.repository.GlucoseResultRepository
import pl.example.databasemodule.database.repository.HeartbeatRepository
import pl.example.databasemodule.database.repository.PrefUnitRepository
import pl.example.networkmodule.apiData.enumTypes.DiabetesType
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.apiMethods.AuthenticationApiInterface
import pl.example.networkmodule.apiMethods.HeartbeatApiInterface
import pl.example.networkmodule.apiMethods.ResultApiInterface
import pl.example.networkmodule.apiMethods.UserApiInterface
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val resultApi: ResultApiInterface,
    private val heartApi: HeartbeatApiInterface,
    private val userApi: UserApiInterface,
    private val authenticationApi: AuthenticationApiInterface,
    private val researchRepository: GlucoseResultRepository,
    private val heartbeatRepository: HeartbeatRepository,
    private val prefUnitRepository: PrefUnitRepository,
    jwtHelper: JwtHelper,
    apiProvider: ApiProvider

) : ViewModel() {

    val USER_ID: String = jwtHelper.getUserId()


    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState

    private val _healthy = MutableStateFlow<Boolean>(false)
    val healthy: StateFlow<Boolean> = _healthy

    init {
        Log.d("ViewModelInit", "_healthy initialized: $_healthy")
        isApiAvilible(apiProvider.innerContext)

        viewModelScope.launch {
            healthy.collect { isHealthy ->
                if (isHealthy) {
                    getUserDiabetesType()
                    getUserHb1AcValue()
                    fetchItemsAsync()
                } else {
                    getUserDiabetesType()
                    getUserHb1AcValue()
                    fetchItemsAsync()
                }
            }

        }

    }


    private fun fetchItemsAsync() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                if (!healthy.value) throw IllegalStateException("API not available")

                Log.d("ViewModel", "UserId: $USER_ID")
                val results = resultApi.getThreeResultsById(USER_ID) ?: emptyList()
                val unit = userApi.getUserUnitById(USER_ID) ?: GlucoseUnitType.MMOL_PER_L
                val diabetesType = userApi.getUserById(USER_ID)?.diabetesType ?: DiabetesType.NONE
                val heartbeat = heartApi.getThreeHeartbeatResults(USER_ID) ?: emptyList()

                prefUnitRepository.insert(
                    PrefUnitDB(
                        userId = USER_ID,
                        glucoseUnit = unit.toString(),
                        isSynced = true,
                        diabetesType = diabetesType.toDiabetesTypeDB()
                    )
                )
                researchRepository.insertAllResults(results)

                _uiState.value = _uiState.value.copy(
                    glucoseItems = convertUnits(results, unit),
                    heartbeatItems = heartbeat,
                    prefUnit = unit,
                    userDiabetesType = diabetesType
                )
            } catch (e: Exception) {
                withContext(Dispatchers.IO) {
                    val localResults = researchRepository.getLatestThreeResearchResult(USER_ID)
                    val localDiabetesType =
                        prefUnitRepository.getUserDiabetesType(USER_ID).toDiabetesType()
                    val localUnit = stringUnitParser(prefUnitRepository.getUnitByUserId(USER_ID))
                    val localHeartbeats = heartbeatRepository.getThreeHeartbeatById(USER_ID)

                    _uiState.value = _uiState.value.copy(
                        glucoseItems = convertUnits(
                            localResults.map { it.toResearchResult() },
                            localUnit
                        ),
                        heartbeatItems = localHeartbeats.toHeartbeatResultList(),
                        prefUnit = localUnit,
                        userDiabetesType = localDiabetesType
                    )
                }
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }


    private suspend fun getUserDiabetesType() {
        try {
            if (!healthy.value) throw IllegalStateException("API not available")

            viewModelScope.launch {
                val diabetesType = userApi.getUserById(USER_ID)?.diabetesType ?: DiabetesType.NONE
                _uiState.value = _uiState.value.copy(userDiabetesType = diabetesType)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.IO) {
                val localType = try {
                    prefUnitRepository.getUserDiabetesType(USER_ID).toDiabetesType()
                } catch (e: Exception) {
                    DiabetesType.NONE
                }
                _uiState.value = _uiState.value.copy(userDiabetesType = localType)
            }
        }
    }


    private fun getUserHb1AcValue() {
        viewModelScope.launch {
            try {
                if (!healthy.value) throw IllegalStateException("API not available")
                val value = resultApi.getHb1AcResultById(USER_ID) ?: 0.0f
                _uiState.value = _uiState.value.copy(userHb1AcValue = value)
            } catch (e: Exception) {
                val localValue = researchRepository.getUserGbA1cById(USER_ID)
                _uiState.value = _uiState.value.copy(userHb1AcValue = localValue)
            }
        }
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
                _uiState.value =
                    _uiState.value.copy(isHealthy = apiAvailable == true && networkAvailable)

                Log.d("HealthCheck", "Healthy: ${_healthy.value}")
            } catch (e: Exception) {
                Log.e("HealthCheck", "Error while checking health", e)
                _healthy.value = false
                _uiState.value = _uiState.value.copy(isHealthy = false)
            }
        }
    }


}