package pl.example.aplikacja.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.databasemodule.database.repository.UserMedicationRepository
import pl.example.networkmodule.apiData.UserMedicationResult
import pl.example.networkmodule.apiMethods.ApiProvider

class UserMedicationScreenViewModel(
    context: Context,
    private val USER_ID: String
) : ViewModel() {

    val apiProvider = ApiProvider(context)
    val userMedications = apiProvider.userMedicationApi
    val userMedicationRepository = UserMedicationRepository(context)

    private val _medicationResults = MutableStateFlow<List<UserMedicationResult>>(emptyList())
    val medicationResults: MutableStateFlow<List<UserMedicationResult>> = _medicationResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchMedicationResults()
    }

    private fun fetchMedicationResults() {

        viewModelScope.launch {
            _isLoading.value = true
            try {
                _medicationResults.value = userMedications.readTodayUserMedication(USER_ID)!!
            } catch (e: Exception) {
                _medicationResults.value = userMedicationRepository.getTodayUserMedication(USER_ID)
            } finally {
                _isLoading.value = false
            }
        }
    }

}