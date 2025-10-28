package pl.example.aplikacja.feature.medicationhistory

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.aplikacja.JwtHelper
import pl.example.databasemodule.database.repository.UserMedicationRepository
import pl.example.networkmodule.apiData.UserMedicationResult
import pl.example.networkmodule.apiMethods.UserMedicationApiInterface
import javax.inject.Inject

@HiltViewModel
class MedicationHistoryViewModel @Inject constructor(
    private val userMedicationApi: UserMedicationApiInterface,
    private val userMedicationRepository: UserMedicationRepository,
    jwtHelper: JwtHelper,
) : ViewModel() {
    private val userId: String = jwtHelper.getUserId()

    private val _medicationResults = MutableStateFlow<List<UserMedicationResult>>(emptyList())
    val medicationResults: MutableStateFlow<List<UserMedicationResult>> = _medicationResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        getMedicationHistory()
    }

    fun getMedicationHistory() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                _medicationResults.value = userMedicationApi.getUserMedicationHistory(userId)!!
            } catch (e: Exception) {
                _medicationResults.value = userMedicationRepository.getUserMedicationHistory(userId).map {
                    UserMedicationResult(
                        id = it.id,
                        userId = it.userId,
                        medicationId = it.medicationId,
                        dosage = it.dosage,
                        frequency = it.frequency,
                        startDate = it.startDate,
                        endDate = it.endDate,
                        notes = it.notes,
                        medicationName = it.medicationName,
                        description = it.description,
                        manufacturer = it.manufacturer,
                        form = it.form,
                        strength = it.strength,
                    )
                }
                Log.e("MedicationHistoryViewModel", "Error fetching medication history", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
