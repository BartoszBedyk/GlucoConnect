package pl.example.networkmodule.requestData

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserForm(
    val email: String,
    val password: String
)
