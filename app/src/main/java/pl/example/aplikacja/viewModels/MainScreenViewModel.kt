import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.aplikacja.convertUnits
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiMethods.ApiProvider

class MainScreenViewModel(private val apiProvider: ApiProvider, private val USER_ID: String) :
    ViewModel() {

    private val resultApi = apiProvider.resultApi
    private val userApi = apiProvider.userApi


    private val _threeGlucoseItems = MutableStateFlow<List<ResearchResult>>(emptyList())
    val threeGlucoseItems: StateFlow<List<ResearchResult>> = _threeGlucoseItems

    private val _prefUnit = MutableStateFlow<GlucoseUnitType>(GlucoseUnitType.MMOL_PER_L)
    val prefUnit: StateFlow<GlucoseUnitType> = _prefUnit

    init {
        fetchItemsAsync()
    }

    private fun fetchItemsAsync() {
        viewModelScope.launch {
            val results = resultApi.getThreeResultsById(USER_ID) ?: emptyList()
            _prefUnit.value = userApi.getUserUnitById(USER_ID) ?: GlucoseUnitType.MMOL_PER_L
            _threeGlucoseItems.value = convertUnits(results, prefUnit.value)
        }
    }


}
