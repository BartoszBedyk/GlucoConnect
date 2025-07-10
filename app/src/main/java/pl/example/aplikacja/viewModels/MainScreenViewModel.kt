
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.example.aplikacja.Screens.isNetworkAvailable
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
import pl.example.networkmodule.apiData.HeartbeatResult
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiData.enumTypes.DiabetesType
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiMethods.ApiProvider

class MainScreenViewModel(context: Context, private val USER_ID: String) : ViewModel() {

    private val apiProvider = ApiProvider(context)
    private val resultApi = apiProvider.resultApi
    private val heartApi = apiProvider.heartbeatApi
    private val userApi = apiProvider.userApi
    private val researchRepository = GlucoseResultRepository(context)
    private val heartbeatRepository = HeartbeatRepository(context)
    private val prefUnitRepository = PrefUnitRepository(context)


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _threeGlucoseItems = MutableStateFlow<List<ResearchResult>>(emptyList())
    val threeGlucoseItems: StateFlow<List<ResearchResult>> = _threeGlucoseItems

    private val _userDiabetesType = MutableStateFlow<DiabetesType>(DiabetesType.NONE)
    val userDiabetesType: StateFlow<DiabetesType> = _userDiabetesType

    private val _userHb1AcValue = MutableStateFlow(0.0f)
    val userHb1AcValue: StateFlow<Float> = _userHb1AcValue

    private val _heartbeatItems = MutableStateFlow<List<HeartbeatResult>>(emptyList())
    val heartbeatItems: StateFlow<List<HeartbeatResult>> = _heartbeatItems

    private val _prefUnit = MutableStateFlow<GlucoseUnitType>(GlucoseUnitType.MMOL_PER_L)
    val prefUnit: StateFlow<GlucoseUnitType> = _prefUnit


    private val authenticationApi = apiProvider.authenticationApi


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
            _isLoading.value = true
            try {
                if (!healthy.value) throw IllegalStateException("API not available")
                val results = resultApi.getThreeResultsById(USER_ID) ?: emptyList()
                _prefUnit.value = userApi.getUserUnitById(USER_ID) ?: GlucoseUnitType.MMOL_PER_L
                _userDiabetesType.value = userApi.getUserById(USER_ID)?.diabetesType ?: DiabetesType.NONE
                prefUnitRepository.insert(
                    PrefUnitDB(
                        userId = USER_ID,
                        glucoseUnit = _prefUnit.value.toString(),
                        isSynced = true,
                        diabetesType = _userDiabetesType.value.toDiabetesTypeDB()
                    )
                )

                researchRepository.insertAllResults(results)

                _heartbeatItems.value = heartApi.getThreeHeartbeatResults(USER_ID) ?: emptyList()
                _threeGlucoseItems.value = convertUnits(results, prefUnit.value)

            } catch (e: Exception) {
                //Log.e("MainScreenViewModel", "Error fetching items", e)
                withContext(Dispatchers.IO) {
                    val localResults = researchRepository.getLatestThreeResearchResult(USER_ID)
                    _userDiabetesType.value = prefUnitRepository.getUserDiabetesType(USER_ID).toDiabetesType()
                    val localHeartbeats =
                        heartbeatRepository.getThreeHeartbeatById(USER_ID)
                    _prefUnit.value = stringUnitParser(prefUnitRepository.getUnitByUserId(USER_ID))
                    _threeGlucoseItems.value = convertUnits(
                        localResults.map { it.toResearchResult() }, prefUnit.value
                    )
                    _heartbeatItems.value = localHeartbeats.toHeartbeatResultList()
                }
            } finally {
                _isLoading.value = false
            }
        }
    }


    private suspend fun getUserDiabetesType() {
        try {
            if (!healthy.value) throw IllegalStateException("API not available")
            viewModelScope.launch {
                _userDiabetesType.value =
                    userApi.getUserById(USER_ID)?.diabetesType ?: DiabetesType.NONE
            }
        } catch (e: Exception) {
            //Log.e("MainScreenViewModel", "Error fetching items", e)
            withContext(Dispatchers.IO){
                _userDiabetesType.value = prefUnitRepository.getUserDiabetesType(USER_ID).toDiabetesType()
            }

        }

    }

    private fun getUserHb1AcValue() {
        viewModelScope.launch {
            try {
                if (!healthy.value) throw IllegalStateException("API not available")
                _userHb1AcValue.value = resultApi.getHb1AcResultById(USER_ID) ?: 0.0f
            } catch (e: Exception) {
                //Log.e("MainScreenViewModel", "Error fetching items", e)
                _userHb1AcValue.value = researchRepository.getUserGbA1cById(USER_ID)
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
                Log.d("HealthCheck", "Healthy: ${_healthy.value}")
            } catch (e: Exception) {
                Log.e("HealthCheck", "Error while checking health", e)
                _healthy.value = false
            }
        }
    }


}
