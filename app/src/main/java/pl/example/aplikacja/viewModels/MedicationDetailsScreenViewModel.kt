package pl.example.aplikacja.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.aplikacja.mappters.removeQuotes
import pl.example.aplikacja.mappters.toMedicationResult
import pl.example.databasemodule.database.data.UserMedicationDB
import pl.example.databasemodule.database.repository.MedicationRepository
import pl.example.databasemodule.database.repository.UserMedicationRepository
import pl.example.networkmodule.apiData.MedicationResult
import pl.example.networkmodule.apiData.UserMedicationResult
import pl.example.networkmodule.apiMethods.ApiProvider

class MedicationDetailsScreenViewModel(
    private var context: Context,
    private val USER_ID: String,
    private val MEDICATION_ID: String
) : ViewModel() {

    private val apiProvider = ApiProvider(context)

    private val medicationApi = apiProvider.medicationApi
    private val userMedicationAPi = apiProvider.userMedicationApi

    private val userMedicationRepository = UserMedicationRepository(context)
    private val medicationRepository = MedicationRepository(context)

    private val _userMedication = MutableStateFlow<UserMedicationResult?>(null)
    val userMedication: StateFlow<UserMedicationResult?> = _userMedication

    private val _medication = MutableStateFlow<MedicationResult?>(null)
    val medication: StateFlow<MedicationResult?> = _medication

    init {
        fetchUserMediacation()
    }

    private fun fetchUserMediacation() {
        viewModelScope.launch {
            try {
                _medication.value = medicationApi.readMedication(MEDICATION_ID)
                _userMedication.value = userMedicationAPi.getUserMedication(
                    userId = USER_ID,
                    medicationId = MEDICATION_ID
                )
            } catch (
                e: Exception
            ) {
                _medication.value = medicationRepository.getMedicationById(MEDICATION_ID).toMedicationResult()

                if (_userMedication.value == null) {
                    _userMedication.value =
                        parseUserMedicationDBtoUserMedicationResult(
                            userMedicationRepository.getMedicationById(
                                USER_ID,
                                MEDICATION_ID
                            )
                        )
                } else {
                    _userMedication.value =
                        parseUserMedicationDBtoUserMedicationResult(
                            userMedicationRepository.getMedicationById(
                                USER_ID,
                                MEDICATION_ID
                            ), _medication.value
                        )
                }
            }
        }
    }

    suspend fun deleteUserMedicationById(): Boolean {
        try {
            if (getUserMedicationIDByID() != null) {
                Log.d("UM API", "User medication ID: ${getUserMedicationIDByID()}")
                val success =
                    userMedicationAPi.deleteUserMedication(removeQuotes(getUserMedicationIDByID()!!))
                if (success) {
                    userMedicationRepository.deleteMedication(MEDICATION_ID)
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
            val id = userMedicationAPi.getUserMedicationId(USER_ID, MEDICATION_ID)
            Log.e("UM API", "ID: $id")
            return id
        } catch (e: Exception) {
            Log.e(
                "MedicationDetailsScreenViewModel",
                "Error fetching user medication ID: ${e.message}"
            )
        }
        return null
    }

    private fun parseUserMedicationDBtoUserMedicationResult(userMedication: UserMedicationDB?): UserMedicationResult? {
        if (userMedication != null) {
            return UserMedicationResult(
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
                description = ""
            )
        }
        return null
    }

    private fun parseUserMedicationDBtoUserMedicationResult(
        userMedication: UserMedicationDB?,
        medication: MedicationResult?
    ): UserMedicationResult? {
        if (userMedication != null) {
            if (medication != null) {
                return UserMedicationResult(
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
                    description = medication.description
                )
            }
        }
        return null
    }




}