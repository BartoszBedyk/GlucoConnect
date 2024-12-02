package pl.example.networkmodule.apiData

import kotlinx.serialization.Serializable
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiData.enumTypes.UserType
import pl.example.networkmodule.serializers.UUIDSerializer
import java.util.UUID

@Serializable
data class UserResult(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val firstName: String?,
    val lastName: String?,
    val email: String,
    val password: String,
    val type: UserType?,
    val isBlocked: Boolean?,
    val prefUint: GlucoseUnitType?
)



