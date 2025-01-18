package pl.example.networkmodule.requestData

import kotlinx.serialization.Serializable
import pl.example.networkmodule.serializers.UUIDSerializer
import java.util.UUID

@Serializable
data class GetMedicationForm(
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val medicationId: UUID,
)
