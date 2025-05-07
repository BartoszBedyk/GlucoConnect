package pl.example.networkmodule.requestData

import kotlinx.serialization.Serializable
import pl.example.networkmodule.serializers.DateSerializer
import pl.example.networkmodule.serializers.UUIDSerializer
import java.util.Date
import java.util.UUID

@Serializable
data class ResearchResultCreate(
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    val glucoseConcentration: Double,
    val unit: String,
    @Serializable(with = DateSerializer::class) val timestamp: Date,
    val afterMedication: Boolean,
    val emptyStomach: Boolean,
    val notes: String
)
