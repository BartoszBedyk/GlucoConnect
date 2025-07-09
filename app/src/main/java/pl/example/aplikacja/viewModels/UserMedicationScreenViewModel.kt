package pl.example.aplikacja.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.jwt.JWT
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.aplikacja.Screens.isNetworkAvailable
import pl.example.aplikacja.mappters.removeQuotes
import pl.example.aplikacja.mappters.toMedicationDBList
import pl.example.aplikacja.mappters.toMedicationList
import pl.example.aplikacja.mappters.toUserMedicationDBList
import pl.example.databasemodule.database.repository.MedicationRepository
import pl.example.databasemodule.database.repository.UserMedicationRepository
import pl.example.networkmodule.apiData.MedicationResult
import pl.example.networkmodule.apiData.UserMedicationResult
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.getToken
import javax.inject.Inject


@HiltViewModel
class UserMedicationScreenViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    private val USER_ID: String = removeQuotes(JWT.decode(getToken(context)).getClaim("userId").toString())

    val apiProvider = ApiProvider(context)
    private val medicationApi = apiProvider.medicationApi
    private val userMedicationsApi = apiProvider.userMedicationApi
    private val userMedicationRepository = UserMedicationRepository(context)
    private val medicationRepository = MedicationRepository(context)
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
                _medicationResults.value = userMedicationsApi.readTodayUserMedication(USER_ID)!!
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
                _userMedication.value = userMedicationsApi.readTodayUserMedication(USER_ID)!!
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

    suspend fun deleteUserMedicationById(): Boolean {
        try {
            if (getUserMedicationIDByID() != null) {
                Log.d("UM API", "User medication ID: ${getUserMedicationIDByID()}")
                val success =
                    userMedicationsApi.deleteUserMedication(removeQuotes(getUserMedicationIDByID()!!))
                if (success) {
                    userMedicationRepository.deleteMedication(getUserMedicationIDByID()!!)
                    Log.d("UM API", "User medication deleted successfully")
                    return true
                } else
                    return false
            } else
                return false
        } catch (e: Exception) {
            Log.e(
                "MedicationDetailsScreenViewModel",
                "Error deleting user medication: ${e.message}"
            )
            return false
        }
    }

    private suspend fun getUserMedicationIDByID(): String? {
        try {
//            val id = userMedicationsApi.getUserMedicationId(USER_ID, MEDICATION_ID)
//            Log.e("UM API", "ID: $id")
//            return id
        } catch (e: Exception) {
            Log.e(
                "MedicationDetailsScreenViewModel",
                "Error fetching user medication ID: ${e.message}"
            )
        }
        return null
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






