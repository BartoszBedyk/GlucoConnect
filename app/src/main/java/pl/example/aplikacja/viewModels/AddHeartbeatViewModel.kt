package pl.example.aplikacja.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.requestData.CreateHeartbeatForm

class AddHeartbeatViewModel(private val apiProvider: ApiProvider) :
    ViewModel() {
    private val heartbeatApi = apiProvider.heartbeatApi

    suspend fun addHeartbeatResult(form: CreateHeartbeatForm): Boolean {
        Log.d("AddHeartbeatViewModel", "addHeartbeatResult: $form")
        return try {
            heartbeatApi.createHeartbeat(form)
            true
        } catch (e: Exception) {
            false
        }
    }


}