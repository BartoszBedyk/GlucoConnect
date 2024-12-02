package pl.example.networkmodule.requestData

import kotlinx.serialization.Serializable
import pl.example.networkmodule.serializers.UUIDSerializer
import java.util.UUID

@Serializable
data class UpdateUserNullForm(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val firstName: String,
    val lastName: String,
    val prefUint: String
)
