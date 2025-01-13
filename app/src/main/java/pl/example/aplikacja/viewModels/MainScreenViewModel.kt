import androidx.collection.MutableObjectList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.aplikacja.formatDateTimeSpecificLocale
import pl.example.networkmodule.apis.ResultApi
import pl.example.networkmodule.KtorClient
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.apis.UserApi
import pl.example.networkmodule.getToken
import java.math.RoundingMode

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


    private fun convertUnits(
        items: List<ResearchResult>,
        targetUnit: GlucoseUnitType
    ): List<ResearchResult> {
        return items.map { item ->
            if (item.unit != targetUnit) {
                val convertedConcentration = when (item.unit) {
                    GlucoseUnitType.MG_PER_DL -> item.glucoseConcentration / 18.0182
                    GlucoseUnitType.MMOL_PER_L -> item.glucoseConcentration * 18.0182
                }.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
                item.copy(
                    glucoseConcentration = convertedConcentration,
                    unit = targetUnit
                )
            } else {
                item
            }
        }
    }
}
