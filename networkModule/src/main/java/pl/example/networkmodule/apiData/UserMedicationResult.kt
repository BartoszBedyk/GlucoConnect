package pl.example.networkmodule.apiData

import kotlinx.serialization.Serializable
import pl.example.networkmodule.serializers.DateSerializer
import pl.example.networkmodule.serializers.UUIDSerializer
import java.util.Date
import java.util.UUID

@Serializable
data class UserMedicationResult(
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val medicationId: UUID,
    val dosage: String,
    val frequency: String,
    @Serializable(with = DateSerializer::class)
    val startDate: Date?,
    @Serializable(with = DateSerializer::class)
    val endDate: Date?,
    val notes: String?,
    val medicationName: String,
    val description: String?,
    val manufacturer: String?,
    val form: String?,
    val strength: String?
)
