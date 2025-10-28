package pl.example.aplikacja.feature.addheartbeat

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.example.aplikacja.JwtHelper
import pl.example.aplikacja.mappters.toHeartbeatResultDB
import pl.example.databasemodule.database.data.HeartbeatDB
import pl.example.databasemodule.database.repository.HeartbeatRepository
import pl.example.networkmodule.apiMethods.HeartbeatApiInterface
import pl.example.networkmodule.requestData.CreateHeartbeatForm
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddHeartbeatViewModel @Inject constructor(

    private val heartbeatApi: HeartbeatApiInterface,
    private val heartbeatRepository: HeartbeatRepository,
    jwtHelper: JwtHelper,
) : ViewModel() {

    val userId: String = jwtHelper.getUserId()

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

    private suspend fun saveLocally(form: CreateHeartbeatForm): Boolean = try {
        val localResult = convertFormToHeartbeatDB(form)
        Log.e("LOCAL", "Success: $form")
        heartbeatRepository.insert(localResult)
        Log.e("LOCAL, ", "Success")
        true
    } catch (e: Exception) {
        Log.e("LOCAL", "Failed to save heartbeat result locally: ${e.message}", e)
        false
    }

    private fun convertFormToHeartbeatDB(form: CreateHeartbeatForm): HeartbeatDB {
        Log.e("PARSER", "convertFormToHeartbeatDB: $form")
        return HeartbeatDB(
            id = UUID.randomUUID(),
            userId = UUID.fromString(userId),
            timestamp = form.timestamp,
            systolicPressure = form.systolicPressure,
            diastolicPressure = form.diastolicPressure,
            pulse = form.pulse,
            note = form.note,
            isSynced = false,
        )
    }
}
