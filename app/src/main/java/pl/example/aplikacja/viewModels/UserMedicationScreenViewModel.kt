package pl.example.aplikacja.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.aplikacja.Screens.isNetworkAvailable
import pl.example.databasemodule.database.data.MedicationDB
import pl.example.databasemodule.database.data.UserMedicationDB
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
    private val authenticationApi = apiProvider.authenticationApi


    private val _healthy = MutableStateFlow<Boolean>(false)
    val healthy: StateFlow<Boolean> = _healthy

    private val _medicationResults = MutableStateFlow<List<UserMedicationResult>>(emptyList())
    val medicationResults: MutableStateFlow<List<UserMedicationResult>> = _medicationResults

    private val _medication = MutableStateFlow<List<MedicationResult>>(emptyList())
    val medication: MutableStateFlow<List<MedicationResult>> = _medication

    private val _userMedication = MutableStateFlow<List<UserMedicationResult>>(emptyList())
    val userMedication: MutableStateFlow<List<UserMedicationResult>> = _userMedication

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        isApiAvilible(apiProvider.innerContext)

        viewModelScope.launch {
            healthy.collect { isHealthy ->
                if (isHealthy) {
                    fetchDataBase()
                    fetchMedicationResults()
                } else {
                    fetchMedicationResults()
                }
            }
        }
    }


    private fun fetchMedicationResults() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (!healthy.value) throw IllegalStateException("API not available")
                Log.i("UserMedicationScreenViewModel", "fetchMedicationResults")
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
                if (!healthy.value) throw IllegalStateException("API not available")
                _medication.value = medicationApi.getUnsynced(USER_ID)!!
                _userMedication.value = userMedications.readTodayUserMedication(USER_ID)!!
                userMedicationRepository.insertAll(userMedication.value.toUserMedicationDBList())
                medicationRepository.insertAll(medication.value.toMedicationDBList())
                medication.value.forEach { medicationResult ->
                    userMedications.markAsSynced(medicationResult.id.toString())
                }
            } catch (e: Exception) {
                _medication.value =
                    medicationRepository.getAllMedications().toMedicationResultList()
            }
        }

    }

    private var lastCheckedTime = 0L

    fun isApiAvilible(context: Context) {
        val now = System.currentTimeMillis()
        if (now - lastCheckedTime < 10_000) return
        lastCheckedTime = now

        viewModelScope.launch {
            try {
                val apiAvailable = authenticationApi.isApiAvlible()
                val networkAvailable = isNetworkAvailable(context)

                Log.d("HealthCheck", "API: $apiAvailable, Network: $networkAvailable")

                _healthy.value = apiAvailable == true && networkAvailable
                Log.d("HealthCheck", "Healthy: ${_healthy.value}")
            } catch (e: Exception) {
                Log.e("HealthCheck", "Error while checking health", e)
                _healthy.value = false
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

fun List<UserMedicationResult>.toUserMedicationDBList(): List<UserMedicationDB> {
    return this.map { result ->
        UserMedicationDB(
            userId = result.userId,
            medicationId = result.medicationId,
            dosage = result.dosage,
            frequency = result.frequency,
            startDate = result.startDate,
            endDate = result.endDate,
            notes = result.notes,
            isSynced = true)
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






