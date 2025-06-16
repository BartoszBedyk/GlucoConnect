package pl.example.aplikacja.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.jwt.JWT
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import pl.example.aplikacja.removeQuotes
import pl.example.networkmodule.apiData.UserMedicationResult
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.getToken
import javax.inject.Inject

@HiltViewModel
class MedicationHistoryViewModel @Inject constructor(@ApplicationContext private val context : Context) : ViewModel() {
     private val USER_ID: String = removeQuotes(JWT.decode(getToken(context)).getClaim("userId").toString())

    private val apiProvider = ApiProvider(context)
    private val userMedicationApi = apiProvider.userMedicationApi


    private val _medicationResults = MutableStateFlow<List<UserMedicationResult>>(emptyList())
    val medicationResults: MutableStateFlow<List<UserMedicationResult>> = _medicationResults

    init{
        getMedicationHistory()
    }


     fun getMedicationHistory(){
        viewModelScope.launch {
            try {
                _medicationResults.value = userMedicationApi.getUserMedicationHistory(USER_ID)!!
            } catch (e: Exception) {
                throw e
            }
        }
    }


}