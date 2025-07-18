package pl.example.aplikacja.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.jwt.JWT
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import pl.example.aplikacja.mappters.removeQuotes
import pl.example.aplikacja.mappters.toMedicationList
import pl.example.aplikacja.mappters.toUserMedicationDB
import pl.example.aplikacja.mappters.toUserMedicationDBList
import pl.example.databasemodule.database.repository.MedicationRepository
import pl.example.databasemodule.database.repository.UserMedicationRepository
import pl.example.networkmodule.apiData.MedicationResult
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.getToken
import pl.example.networkmodule.requestData.CreateUserMedicationForm
import javax.inject.Inject

@HiltViewModel
class AddUserMedicationViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    ViewModel() {

    val USER_ID: String = removeQuotes(JWT.decode(getToken(context)).getClaim("userId").toString())

    private val apiProvider = ApiProvider(context)
    private val userMedicationRepository = UserMedicationRepository(context)
    private val medicationApi = apiProvider.medicationApi
    private val medicationRepository = MedicationRepository(context)
    private val userMedicationApi = apiProvider.userMedicationApi

    private val _medications = MutableStateFlow<List<MedicationResult>>(emptyList())
    val medications: MutableStateFlow<List<MedicationResult>> = _medications

    init {
        fetchMedications()
    }

    private fun fetchMedications() {

        viewModelScope.launch {
            try {
                _medications.value = medicationApi.getAllMedications() ?: emptyList()
            } catch (e: Exception) {
                _medications.value =
                    medicationRepository.getAllMedications().toMedicationList() ?: emptyList()
                Log.e("API", "Failed to fetch medications: ${e.message}", e)
            }
        }
    }

    suspend fun addUserMedication(form: CreateUserMedicationForm): Boolean {
        Log.d("API", "Adding user medication: $form")
        return try {
            val id = userMedicationApi.createUserMedication(form)
            Log.d("API", "User medication added with ID: $id")
            if (id != null) {
                val success = addMedicationToDatabase(removeQuotes(id))
                if (!success) Log.e("LOCALY", "Failed to add medication into local database.")

            }
            true
        } catch (e: Exception) {
            Log.e("API", "Failed to add user medication to API, saving locally: ${e.message}", e)
            saveMedicationLocally(form)
            false
        }
    }

    private suspend fun addMedicationToDatabase(id: String): Boolean {
        return try {
            val medicationResult = userMedicationApi.readUserMedicationByID(id)
            Log.d("LOCALY", "Fetched user medication: $medicationResult")
            if (medicationResult != null) {
                val converted = medicationResult.toUserMedicationDBList().first()
                userMedicationRepository.insert(converted)
                return true
            }
            false
        } catch (e: Exception) {
            Log.e("LOCALY", "Failed to add medication to database: ${e.message}", e)
            false
        }
    }

    private suspend fun saveMedicationLocally(form: CreateUserMedicationForm): Boolean {
        return try {
            val localMedication = form.toUserMedicationDB()
            userMedicationRepository.insert(localMedication)
            Log.d("LOCALY", "User medication saved locally: $localMedication")
            true
        } catch (e: Exception) {
            Log.e("LOCALY", "Failed to save user medication locally: ${e.message}", e)
            false
        }
    }


}
