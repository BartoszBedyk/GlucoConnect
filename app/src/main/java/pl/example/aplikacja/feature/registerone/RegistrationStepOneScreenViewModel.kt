package pl.example.aplikacja.feature.registerone

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.example.networkmodule.apiMethods.UserApiInterface
import pl.example.networkmodule.requestData.CreateUserStepOneForm
import javax.inject.Inject

@HiltViewModel
class RegistrationStepOneScreenViewModel @Inject constructor(private val userApi: UserApiInterface) : ViewModel() {

    suspend fun register(login: String, password: String): String? {
        return userApi.createUserStepOne(CreateUserStepOneForm(login, password))
    }
}
