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
import pl.example.aplikacja.mappters.removeQuotes
import pl.example.databasemodule.database.repository.UserMedicationRepository
import pl.example.networkmodule.apiData.UserMedicationResult
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.getToken
import javax.inject.Inject

@HiltViewModel
class MedicationHistoryViewModel @Inject constructor(@ApplicationContext private val context : Context) : ViewModel() {
     private val USER_ID: String = removeQuotes(JWT.decode(getToken(context)).getClaim("userId").toString())

    private val apiProvider = ApiProvider(context)
    private val userMedicationApi = apiProvider.userMedicationApi
    private val userMedicationRepository = UserMedicationRepository(context)


    private val _medicationResults = MutableStateFlow<List<UserMedicationResult>>(emptyList())
    val medicationResults: MutableStateFlow<List<UserMedicationResult>> = _medicationResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init{
        getMedicationHistory()
    }


     fun getMedicationHistory(){
         _isLoading.value = true
        viewModelScope.launch {
            try {
                _medicationResults.value = userMedicationApi.getUserMedicationHistory(USER_ID)!!
            } catch (e: Exception) {
                _medicationResults.value = userMedicationRepository.getUserMedicationHistory(USER_ID).map {
                    UserMedicationResult(
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
                        strength = it.strength)
                }
                Log.e("MedicationHistoryViewModel", "Error fetching medication history", e)
            }finally {
                _isLoading.value = false
            }
        }
    }


}