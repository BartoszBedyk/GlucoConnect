package pl.example.networkmodule.requestData

import kotlinx.serialization.Serializable

@Serializable
data class UserCredentials(
    val email: String,
    val password: String

)
