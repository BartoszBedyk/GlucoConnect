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
import pl.example.aplikacja.convertHeartBeatDBtoHeartbeatResult
import pl.example.aplikacja.convertResearchDBtoResearchResult
import pl.example.aplikacja.convertUnits
import pl.example.aplikacja.stringUnitParser
import pl.example.databasemodule.database.data.PrefUnitDB
import pl.example.databasemodule.database.repository.GlucoseResultRepository
import pl.example.databasemodule.database.repository.HeartbeatRepository
import pl.example.databasemodule.database.repository.PrefUnitRepository
import pl.example.networkmodule.apiData.HeartbeatResult
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiData.enumTypes.UserType
import pl.example.networkmodule.apiMethods.ApiProvider

class MainScreenViewModel(context: Context, private val USER_ID: String) :
    ViewModel() {

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

    private val _userType = MutableStateFlow<UserType>(UserType.PATIENT)
    val userType: StateFlow<UserType> = _userType

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
        getUserType()
        fetchItemsAsync()
    }


    private fun fetchItemsAsync() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (!healthy.value) throw IllegalStateException("API not available")

                    val results = resultApi.getThreeResultsById(USER_ID) ?: emptyList()
                    _prefUnit.value =
                        userApi.getUserUnitById(USER_ID) ?: GlucoseUnitType.MMOL_PER_L
                    prefUnitRepository.insert(
                        PrefUnitDB(
                            userId = USER_ID,
                            glucoseUnit = _prefUnit.value.toString(),
                            isSynced = true
                        )
                    )

                    researchRepository.insertAllResults(results)

                    _heartbeatItems.value =
                        heartApi.getThreeHeartbeatResults(USER_ID) ?: emptyList()
                    _threeGlucoseItems.value = convertUnits(results, prefUnit.value)

            } catch (e: Exception) {
                withContext(Dispatchers.IO) {
                    val localResults = researchRepository.getLatestThreeResearchResult(USER_ID)
                    val localHeartbeats =
                        heartbeatRepository.getThreeHeartbeatById(USER_ID) ?: emptyList()
                    _prefUnit.value = stringUnitParser(prefUnitRepository.getUnitByUserId(USER_ID))
                    _threeGlucoseItems.value = convertUnits(
                        localResults.map { convertResearchDBtoResearchResult(it) },
                        prefUnit.value
                    )
                    _heartbeatItems.value = convertHeartBeatDBtoHeartbeatResult(localHeartbeats)
                }
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun getUserType() {
        try{
            if (!healthy.value) throw IllegalStateException("API not available")
            viewModelScope.launch {
                _userType.value = userApi.getUserById(USER_ID)?.type ?: UserType.PATIENT
            }
        }catch (e: Exception){
            return
        }

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
