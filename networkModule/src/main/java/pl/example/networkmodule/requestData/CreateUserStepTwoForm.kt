package pl.example.networkmodule.requestData

import kotlinx.serialization.Serializable
import pl.example.networkmodule.serializers.UUIDSerializer
import java.util.UUID

@Serializable
data class CreateUserStepTwoForm(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val firstName: String,
    val lastName: String,
    val prefUnit: String,
    val diabetes: String,
    val userType: String
)
