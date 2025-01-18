package pl.example.aplikacja.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.requestData.ResearchResultCreate

class AddGlucoseResultViewModel(private val apiProvider: ApiProvider, private val USER_ID: String) :
    ViewModel() {
    private val resultApi = apiProvider.resultApi
    private val userApi = apiProvider.userApi

    private val _prefUnit = MutableStateFlow<GlucoseUnitType>(GlucoseUnitType.MG_PER_DL)
    val prefUnit: StateFlow<GlucoseUnitType> = _prefUnit

    init{
        fetchUnit()
    }

        suspend fun addGlucoseResult(form: ResearchResultCreate): Boolean {
            Log.d("AddGlucoseResultViewModel", "Adding glucose result: $form")
            return try {
                resultApi.createResearchResult(form)
                true
            } catch (e: Exception) {
                false
            }
        }

    private fun fetchUnit(){
        viewModelScope.launch {
            _prefUnit.value = userApi.getUserUnitById(USER_ID) ?: GlucoseUnitType.MG_PER_DL
        }
    }

}