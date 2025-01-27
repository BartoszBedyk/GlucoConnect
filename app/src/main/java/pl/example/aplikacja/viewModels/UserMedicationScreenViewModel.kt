package pl.example.aplikacja.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.databasemodule.database.data.MedicationDB
import pl.example.databasemodule.database.repository.MedicationRepository
import pl.example.databasemodule.database.repository.UserMedicationRepository
import pl.example.networkmodule.apiData.MedicationResult
import pl.example.networkmodule.apiData.UserMedicationResult
import pl.example.networkmodule.apiMethods.ApiProvider

class UserMedicationScreenViewModel(
    context: Context,
    private val USER_ID: String
) : ViewModel() {

    val apiProvider = ApiProvider(context)
    val medicationApi = apiProvider.medicationApi
    val userMedications = apiProvider.userMedicationApi
    val userMedicationRepository = UserMedicationRepository(context)
    val medicationRepository = MedicationRepository(context)

    private val _medicationResults = MutableStateFlow<List<UserMedicationResult>>(emptyList())
    val medicationResults: MutableStateFlow<List<UserMedicationResult>> = _medicationResults

    private val _medication = MutableStateFlow<List<MedicationResult>>(emptyList())
    val medication: MutableStateFlow<List<MedicationResult>> = _medication

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchDataBase()
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

    private fun fetchDataBase() {
        viewModelScope.launch {
            try {
                _medication.value = medicationApi.getUnsynced(USER_ID)!!
                medicationRepository.insertAll(medication.value.toMedicationDBList())
                medication.value.forEach { medicationResult ->
                    userMedications.markAsSynced(medicationResult.id.toString())
                }
            } catch (e: Exception) {
                _medication.value = medicationRepository.getAllMedications().toMedicationResultList()
            }
        }

    }

}


fun List<MedicationResult>.toMedicationDBList(): List<MedicationDB> {
    return this.map { result ->
        MedicationDB(
            id = result.id,
            name = result.name,
            description = result.description,
            manufacturer = result.manufacturer,
            form = result.form,
            strength = result.strength
        )
    }
}

fun List<MedicationDB>.toMedicationResultList(): List<MedicationResult> {
    return this.map { db ->
        MedicationResult(
            id = db.id,
            name = db.name,
            description = db.description,
            manufacturer = db.manufacturer,
            form = db.form,
            strength = db.strength
        )
    }
}

