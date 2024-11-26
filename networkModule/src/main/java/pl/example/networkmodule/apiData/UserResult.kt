package pl.example.networkmodule.apiData

import kotlinx.serialization.Serializable
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiData.enumTypes.UserType

@Serializable
data class UserResult(
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val email: String,
    val password: String,
    val type: UserType?,
    val isBlocked: Boolean?,
    val prefUint: GlucoseUnitType?
)



