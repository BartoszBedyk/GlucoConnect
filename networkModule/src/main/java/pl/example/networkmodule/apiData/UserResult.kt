package pl.example.networkmodule.apiData

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiData.enumTypes.UserType
import pl.example.networkmodule.serializers.UUIDSerializer
import java.util.UUID

@Serializable
data class UserResult(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    var firstName: String?,
    var lastName: String?,
    var email: String,
    val password: String,
    val type: UserType?,
    var isBlocked: Boolean?,
    var prefUint: GlucoseUnitType?
)



