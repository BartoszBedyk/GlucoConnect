package pl.example.aplikacja.viewModels

import androidx.lifecycle.ViewModel
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.requestData.CreateUserForm

class RegistrationStepOneScreenViewModel(private val apiProvider: ApiProvider) : ViewModel() {
    val userApi = apiProvider.userApi

    suspend fun register(login: String, password: String): String? {
        val userCredentials = CreateUserForm(login, password)
        return userApi.createUser(userCredentials)
    }

}