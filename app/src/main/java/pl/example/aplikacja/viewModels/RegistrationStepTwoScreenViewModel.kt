package pl.example.aplikacja.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import pl.example.aplikacja.Screens.isNetworkAvailable
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.requestData.UpdateUserNullForm
import pl.example.networkmodule.requestData.UserCredentials
import pl.example.networkmodule.saveToken
import java.util.UUID

class RegistrationStepTwoScreenViewModel(private val apiProvider: ApiProvider) : ViewModel() {
    private val userApi = apiProvider.userApi
    private val authenticationApi = apiProvider.authenticationApi

    suspend fun registerStepTwo(id: String, name: String, lastName: String, prefUnit: String): Boolean {
        val updateData = UpdateUserNullForm(UUID.fromString(id), name, lastName, prefUnit, "NONE")
        return userApi.giveUserNulls(updateData)
    }


    suspend fun updateType(id: String, type: String) {
        try {
            userApi.giveUserType(id, type)
        }catch (
            e: Exception
        ){
            e.printStackTrace()
        }

    }



}