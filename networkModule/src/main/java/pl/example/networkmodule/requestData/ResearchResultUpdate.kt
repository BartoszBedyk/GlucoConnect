package pl.example.networkmodule.requestData

import kotlinx.serialization.Serializable
import pl.example.networkmodule.serializers.DateSerializer
import pl.example.networkmodule.serializers.UUIDSerializer
import java.util.Date
import java.util.UUID

@Serializable
data class ResearchResultUpdate(
    val sequenceNumber: Int,
    val glucoseConcentration: Double,
    val unit: String,
    @Serializable(with = DateSerializer::class)
    val timestamp: Date,
    @Serializable(with = UUIDSerializer::class)
    val id: UUID
)
