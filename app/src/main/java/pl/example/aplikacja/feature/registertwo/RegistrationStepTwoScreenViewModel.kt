package pl.example.aplikacja.feature.registertwo

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.example.networkmodule.apiMethods.UserApiInterface
import pl.example.networkmodule.requestData.CreateUserStepTwoForm
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RegistrationStepTwoScreenViewModel @Inject constructor(
    private val userApi: UserApiInterface,
) : ViewModel() {

    suspend fun registerStepTwo(
        id: String,
        name: String,
        lastName: String,
        prefUnit: String,
        diabetesType: String,
        userType: String
    ): Boolean {
        val updateData = CreateUserStepTwoForm(
            UUID.fromString(id),
            name,
            lastName,
            prefUnit,
            diabetesType,
            userType
        )
        return userApi.createUserStepTwo(updateData)
    }

    fun validateForm(name: String, lastName: String): String? {
        val nameTrimmed = name.trim()
        val lastNameTrimmed = lastName.trim()

        if (nameTrimmed.isEmpty() || lastNameTrimmed.isEmpty()) {
            return "Pola nie mogą być puste"
        }
        if (nameTrimmed.length < 2 || lastNameTrimmed.length < 2) {
            return "Imię i nazwisko muszą mieć co najmniej 2 znaki"
        }

        val nameRegex = Regex("^[A-Za-zÀ-ÖØ-öø-ÿŁłŚśŻżŹźĆćŃńĄąĘęÓó'\\-\\s]+\$")

        if (!nameRegex.matches(nameTrimmed) || !nameRegex.matches(lastNameTrimmed)) {
            return "Imię i nazwisko mogą zawierać tylko litery, spacje, myślniki i apostrofy"
        }

        return null
    }
}