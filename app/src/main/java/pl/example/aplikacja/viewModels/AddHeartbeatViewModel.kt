package pl.example.aplikacja.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.requestData.CreateHeartbeatForm


import pl.example.databasemodule.database.data.HeartbeatDB
import pl.example.databasemodule.database.repository.HeartbeatRepository
import pl.example.networkmodule.apiData.HeartbeatResult

import java.util.Date
import java.util.UUID

class AddHeartbeatViewModel(
    context: Context, private val USER_ID: String
) : ViewModel() {

    private val apiProvider = ApiProvider(context)
    private val heartbeatRepository = HeartbeatRepository(context)
    private val heartbeatApi = apiProvider.heartbeatApi


    suspend fun addHeartbeatResult(form: CreateHeartbeatForm): Boolean {
        Log.d("AddHeartbeatViewModel", "addHeartbeatResult: $form")
        try {
            val id = heartbeatApi.createHeartbeat(form)
            if (id != null) {
                val success = saveToLocalDatabase((id.toString()))
                if (!success) {
                    Log.e("LOCAL", "Failed to save data into local database.")
                }
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
                val converted = convertToHeartbeatDB(heartbeatResult)
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

    private fun convertToHeartbeatDB(apiResult: HeartbeatResult): HeartbeatDB {
        return HeartbeatDB(
            id = apiResult.id,
            userId = apiResult.userId ,
            timestamp = apiResult.timestamp,
            systolicPressure = apiResult.systolicPressure,
            diastolicPressure = apiResult.diastolicPressure,
            pulse = apiResult.pulse,
            note = apiResult.note,
            isSynced = true
        )
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
