package pl.example.aplikacja.viewModels


import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.auth0.jwt.JWT
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import pl.example.aplikacja.mappters.removeQuotes
import pl.example.aplikacja.mappters.toHeartbeatResultDB
import pl.example.databasemodule.database.data.HeartbeatDB
import pl.example.databasemodule.database.repository.HeartbeatRepository
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.getToken
import pl.example.networkmodule.requestData.CreateHeartbeatForm
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddHeartbeatViewModel @Inject constructor(

    @ApplicationContext private val context: Context
) : ViewModel() {

    val USER_ID: String = removeQuotes(JWT.decode(getToken(context)).getClaim("userId").toString())

    private val apiProvider = ApiProvider(context)
    private val heartbeatRepository = HeartbeatRepository(context)
    private val heartbeatApi = apiProvider.heartbeatApi


    suspend fun addHeartbeatResult(form: CreateHeartbeatForm): Boolean {
        Log.d("AddHeartbeatViewModel", "addHeartbeatResult: $form")
        try {
            val id = heartbeatApi.createHeartbeat(form)
            val success = saveToLocalDatabase((id.toString()))
            if (!success) {
                Log.e("LOCAL", "Failed to save data into local database.")
            }
            return saveLocally(form)
        } catch (e: Exception) {
            Log.e("API", "Failed to add heartbeat result to API, saving locally: ${e.message}", e)
            return saveLocally(form)
        }
    }


    private suspend fun saveToLocalDatabase(id: String): Boolean {
        return try {
            val heartbeatResult = heartbeatApi.getHeartBeat(id)
            if (heartbeatResult != null) {
                val converted = heartbeatResult.toHeartbeatResultDB()
                heartbeatRepository.insert(converted)
                return true
            }
            false
        } catch (e: Exception) {
            Log.e("LOCAL", "Failed to save heartbeat result to database: ${e.message}", e)
            false
        }
    }


    private suspend fun saveLocally(form: CreateHeartbeatForm): Boolean {
        return try {
            val localResult = convertFormToHeartbeatDB(form)
            Log.e("LOCAL", "Success: $form")
            heartbeatRepository.insert(localResult)
            Log.e("LOCAL, ", "Success")
            true
        } catch (e: Exception) {
            Log.e("LOCAL", "Failed to save heartbeat result locally: ${e.message}", e)
            false
        }
    }


    private fun convertFormToHeartbeatDB(form: CreateHeartbeatForm): HeartbeatDB {
        Log.e("PARSER", "convertFormToHeartbeatDB: $form")
        return HeartbeatDB(
            id = UUID.randomUUID(),
            userId = UUID.fromString(USER_ID),
            timestamp = form.timestamp,
            systolicPressure = form.systolicPressure,
            diastolicPressure = form.diastolicPressure,
            pulse = form.pulse,
            note = form.note,
            isSynced = false
        )
    }

}
