package pl.example.aplikacja.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.networkmodule.apiData.MedicationResult
import pl.example.networkmodule.apiData.UserMedicationResult
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.requestData.GetMedicationForm
import java.util.UUID

class MedicationDetailsScreenViewModel(private var apiProvider: ApiProvider, private val USER_ID: String, private val MEDICATION_ID: String): ViewModel() {
    private val medicationApi = apiProvider.medicationApi
    private val userMedicationAPi = apiProvider.userMedicationApi

    private val _userMedication = MutableStateFlow<UserMedicationResult?>(null)
    val userMedication: StateFlow<UserMedicationResult?> = _userMedication

    private val _medication = MutableStateFlow<MedicationResult?>(null)
    val medication: StateFlow<MedicationResult?> = _medication

    init{
        fetchUserMediacation()
    }

    private fun fetchUserMediacation(){
            viewModelScope.launch {
                val getUserMedication : GetMedicationForm = GetMedicationForm(UUID.fromString(USER_ID), UUID.fromString(MEDICATION_ID))
                Log.d("Medication", "fetchUserMediacation: $getUserMedication")
                _medication.value = medicationApi.readMedication(MEDICATION_ID)
               _userMedication.value = userMedicationAPi.getUserMedication(getUserMedication)
            }
    }


}