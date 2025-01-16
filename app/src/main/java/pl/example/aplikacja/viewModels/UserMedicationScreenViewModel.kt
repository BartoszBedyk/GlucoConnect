package pl.example.aplikacja.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import pl.example.networkmodule.apiData.UserMedicationResult
import pl.example.networkmodule.apiMethods.ApiProvider

class UserMedicationScreenViewModel(private val apiProvider: ApiProvider,
                                     private val USER_ID: String): ViewModel() {
    val userMedications = apiProvider.userMedicationApi

    private val _medicationResults = MutableStateFlow<List<UserMedicationResult>>(emptyList())
    val medicationResults: MutableStateFlow<List<UserMedicationResult>> = _medicationResults

    init {
        fetchMedicationResults()
    }

    private fun fetchMedicationResults() {
        viewModelScope.launch {
            _medicationResults.value = userMedications.readTodayUserMedication(USER_ID)!!
        }
    }

}