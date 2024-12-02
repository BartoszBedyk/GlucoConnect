package pl.example.networkmodule.apiData

import kotlinx.serialization.Serializable
import pl.example.networkmodule.serializers.UUIDSerializer
import java.util.UUID


@Serializable
data class MedicationResult(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val description: String?,
    val manufacturer: String?,
    val form: String?,
    val strength: String?
)

