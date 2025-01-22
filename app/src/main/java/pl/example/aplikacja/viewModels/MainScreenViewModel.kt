import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.example.aplikacja.convertResearchDBtoResearchResult
import pl.example.aplikacja.convertUnits
import pl.example.databasemodule.database.repository.GlucoseResultRepository
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiMethods.ApiProvider

class MainScreenViewModel(context: Context, private val USER_ID: String) :
    ViewModel() {

    private val apiProvider = ApiProvider(context)
    private val resultApi = apiProvider.resultApi
    private val userApi = apiProvider.userApi
    private val researchRepository = GlucoseResultRepository(context)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _threeGlucoseItems = MutableStateFlow<List<ResearchResult>>(emptyList())
    val threeGlucoseItems: StateFlow<List<ResearchResult>> = _threeGlucoseItems

    private val _prefUnit = MutableStateFlow<GlucoseUnitType>(GlucoseUnitType.MMOL_PER_L)
    val prefUnit: StateFlow<GlucoseUnitType> = _prefUnit

    init {
        fetchItemsAsync()
    }

    private fun fetchItemsAsync() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val results = resultApi.getThreeResultsById(USER_ID) ?: emptyList()
                _prefUnit.value = userApi.getUserUnitById(USER_ID) ?: GlucoseUnitType.MMOL_PER_L

                researchRepository.insertAllResults(results)


                _threeGlucoseItems.value = convertUnits(results, prefUnit.value)
            } catch (e: Exception) {
                withContext(Dispatchers.IO) {
                    val localResults = researchRepository.getLatestThreeResearchResult(USER_ID)
                    _threeGlucoseItems.value = convertUnits(
                        localResults.map { convertResearchDBtoResearchResult(it) },
                        prefUnit.value
                    )
                }
            } finally {
                _isLoading.value = false
            }
        }
    }


}
