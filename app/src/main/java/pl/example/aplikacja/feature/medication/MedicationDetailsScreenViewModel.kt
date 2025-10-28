package pl.example.aplikacja.feature.medication

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
import pl.example.aplikacja.mappters.toMedicationResult
import pl.example.databasemodule.database.data.UserMedicationDB
import pl.example.databasemodule.database.repository.MedicationRepository
import pl.example.databasemodule.database.repository.UserMedicationRepository
import pl.example.networkmodule.apiData.MedicationResult
import pl.example.networkmodule.apiData.UserMedicationResult
import pl.example.networkmodule.apiMethods.MedicationApiInterface
import pl.example.networkmodule.apiMethods.UserMedicationApiInterface
import javax.inject.Inject

@HiltViewModel
class MedicationDetailsScreenViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    private val medicationApi: MedicationApiInterface,
    private val userMedicationAPi: UserMedicationApiInterface,
    private val userMedicationRepository: UserMedicationRepository,
    private val medicationRepository: MedicationRepository,
    jwtHelper: JwtHelper,
) : ViewModel() {

    val userId: String = jwtHelper.getUserId()

    private val _userMedication = MutableStateFlow<UserMedicationResult?>(null)
    val userMedication: StateFlow<UserMedicationResult?> = _userMedication

    private val _medication = MutableStateFlow<MedicationResult?>(null)
    val medication: StateFlow<MedicationResult?> = _medication

    private val _medicationId2 = MutableStateFlow<String?>(null)
    val medicationId2: StateFlow<String?> = _medicationId2

    fun setMedicationId(id: String) {
        _medicationId2.value = id
    }

    fun fetchUserMediacation(umId: String, medicationId: String) {
        viewModelScope.launch {
            try {
                Log.d("UM API", "Fetching user medication")
                _medication.value = medicationApi.readMedication(medicationId)
                _userMedication.value = userMedicationAPi.getUserMedication(
                    userMedicationId = umId,
                )
            } catch (
                e: Exception,
            ) {
                Log.d("UM API", "Fetching user db")
                _medication.value = medicationRepository.getMedicationById(medicationId).toMedicationResult()

                if (_userMedication.value == null) {
                    _userMedication.value =
                        parseUserMedicationDBtoUserMedicationResult(
                            userMedicationRepository.getMedicationById(
                                userId,
                                medicationId2.value!!,
                            ),
                        )
                } else {
                    _userMedication.value =
                        parseUserMedicationDBtoUserMedicationResult(
                            userMedicationRepository.getMedicationById(
                                userId,
                                medicationId2.value!!,
                            ),
                            _medication.value,
                        )
                }
            }
        }
    }

    suspend fun deleteUserMedicationById(umId: String, medicationId: String): Boolean {
        try {
            val success =
                userMedicationAPi.deleteUserMedication(umId)
            if (success) {
                userMedicationRepository.deleteMedication(medicationId2.value!!)
                Log.d("UM API", "User medication deleted successfully")
                return true
            } else {
                return false
            }
        } catch (e: Exception) {
            Log.e(
                "MedicationDetailsScreenViewModel",
                "Error deleting user medication: ${e.message}",
            )
            return false
        }
    }

    private fun parseUserMedicationDBtoUserMedicationResult(userMedication: UserMedicationDB?): UserMedicationResult? {
        if (userMedication != null) {
            return UserMedicationResult(
                id = userMedication.id,
                medicationId = userMedication.medicationId,
                userId = userMedication.userId,
                dosage = userMedication.dosage,
                frequency = userMedication.frequency,
                startDate = userMedication.startDate,
                endDate = userMedication.endDate,
                notes = userMedication.notes,
                medicationName = "",
                manufacturer = null,
                form = null,
                strength = "",
                description = "",
            )
        }
        return null
    }

    private fun parseUserMedicationDBtoUserMedicationResult(
        userMedication: UserMedicationDB?,
        medication: MedicationResult?,
    ): UserMedicationResult? {
        if (userMedication != null) {
            if (medication != null) {
                return UserMedicationResult(
                    id = userMedication.id,
                    medicationId = userMedication.medicationId,
                    userId = userMedication.userId,
                    dosage = userMedication.dosage,
                    frequency = userMedication.frequency,
                    startDate = userMedication.startDate,
                    endDate = userMedication.endDate,
                    notes = userMedication.notes,
                    medicationName = medication.name,
                    manufacturer = medication.manufacturer,
                    form = medication.form,
                    strength = medication.strength,
                    description = medication.description,
                )
            }
        }
        return null
    }
}
