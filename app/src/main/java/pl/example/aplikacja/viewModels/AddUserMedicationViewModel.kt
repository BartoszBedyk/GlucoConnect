package pl.example.aplikacja.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.networkmodule.apiData.MedicationResult
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.requestData.CreateUserMedicationForm

class AddUserMedicationViewModel(private val apiProvider: ApiProvider, private val USER_ID: String) : ViewModel() {
    private val medicationApi = apiProvider.medicationApi
    private val userMedicationApi = apiProvider.userMedicationApi

    private val _medications = MutableStateFlow<List<MedicationResult>>(emptyList())
    val medications: MutableStateFlow<List<MedicationResult>> = _medications

    init{
        fetchMedications()
    }

    private fun fetchMedications(){
        viewModelScope.launch {
            _medications.value = medicationApi.getAllMedications() ?: emptyList()
        }
    }

    suspend fun createUserMedication(userMedication: CreateUserMedicationForm): Boolean {
        Log.d("UserMedication", "createUserMedication called with userMedication: $userMedication")
        return try {
            userMedicationApi.createUserMedication(userMedication)
        }
        catch (e: Exception) {
            false
        }
    }

}
