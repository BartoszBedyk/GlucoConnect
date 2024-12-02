package pl.example.networkmodule.requestData

import kotlinx.serialization.Serializable
import pl.example.networkmodule.apiData.enumTypes.UserType

@Serializable
data class UserCreateWIthType(
    val email: String,
    val password: String,
    val userType: UserType
)
