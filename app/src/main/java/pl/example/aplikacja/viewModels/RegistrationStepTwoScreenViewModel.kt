package pl.example.aplikacja.viewModels

import androidx.lifecycle.ViewModel
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.requestData.CreateUserStepTwoForm
import java.util.UUID

class RegistrationStepTwoScreenViewModel(private val apiProvider: ApiProvider) : ViewModel() {
    private val userApi = apiProvider.userApi
    private val authenticationApi = apiProvider.authenticationApi

    suspend fun registerStepTwo(id: String, name: String, lastName: String, prefUnit: String, diabetesType: String, userType: String): Boolean {
        val updateData = CreateUserStepTwoForm(UUID.fromString(id), name, lastName, prefUnit, diabetesType, userType)
        return userApi.createUserStepTwo(updateData)
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