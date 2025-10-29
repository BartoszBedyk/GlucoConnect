package pl.example.aplikacja.feature.usermedication

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.aplikacja.JwtHelper
import pl.example.aplikacja.feature.login.isNetworkAvailable
import pl.example.aplikacja.mappters.toMedicationDBList
import pl.example.aplikacja.mappters.toMedicationList
import pl.example.aplikacja.mappters.toUserMedicationDBList
import pl.example.databasemodule.database.repository.MedicationRepository
import pl.example.databasemodule.database.repository.UserMedicationRepository
import pl.example.networkmodule.apiData.MedicationResult
import pl.example.networkmodule.apiData.UserMedicationResult
import pl.example.networkmodule.apiMethods.AuthenticationApiInterface
import pl.example.networkmodule.apiMethods.MedicationApiInterface
import pl.example.networkmodule.apiMethods.UserMedicationApiInterface
import javax.inject.Inject

@HiltViewModel
class UserMedicationScreenViewModel @Inject constructor(
    private val medicationApi: MedicationApiInterface,
    private val userMedicationsApi: UserMedicationApiInterface,
    private val userMedicationRepository: UserMedicationRepository,
    private val medicationRepository: MedicationRepository,
    private val authenticationApi: AuthenticationApiInterface,
    jwtHelper: JwtHelper,
    @ApplicationContext context: Context,
) : ViewModel() {

    private val userId: String = jwtHelper.getUserId()

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
        isApiAvilible(context)

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
                check(healthy.value) { "API not available" }
                Log.i("UserMedicationScreenViewModel", "fetchMedicationResults")
                _medicationResults.value = userMedicationsApi.readTodayUserMedication(userId)!!
            } catch (e: Exception) {
                _medicationResults.value = userMedicationRepository.getTodayUserMedication(userId)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun fetchDataBase() {
        viewModelScope.launch {
            try {
                check(healthy.value) { "API not available" }
                _medication.value = medicationApi.getUnsynced(userId)!!
                _userMedication.value = userMedicationsApi.readTodayUserMedication(userId)!!
                userMedicationRepository.insertAll(userMedication.value.toUserMedicationDBList())
                medicationRepository.insertAll(medication.value.toMedicationDBList())
                medication.value.forEach { medicationResult ->
                    userMedicationsApi.markAsSynced(medicationResult.id.toString())
                }
            } catch (e: Exception) {
                _medication.value =
                    medicationRepository.getAllMedications().toMedicationList()
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
