package pl.example.aplikacja.viewModels

import androidx.lifecycle.ViewModel
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.requestData.UpdateUserNullForm
import java.util.UUID

class RegistrationStepTwoScreenViewModel(private val apiProvider: ApiProvider) : ViewModel() {
    private val userApi = apiProvider.userApi

    suspend fun registerStepTwo(id: String, name: String, lastName: String, prefUnit: String): Boolean {
        val updateData = UpdateUserNullForm(UUID.fromString(id), name, lastName, prefUnit)
        return userApi.updateUserNulls(updateData)
    }

}