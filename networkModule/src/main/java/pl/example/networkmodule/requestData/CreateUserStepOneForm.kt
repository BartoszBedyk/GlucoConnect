package pl.example.networkmodule.requestData

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserStepOneForm(
    val email: String,
    val password: String
)
