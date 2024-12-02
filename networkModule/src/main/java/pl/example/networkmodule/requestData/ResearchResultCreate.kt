package pl.example.networkmodule.requestData

import kotlinx.serialization.Serializable
import pl.example.networkmodule.serializers.DateSerializer
import java.util.Date

@Serializable
data class ResearchResultCreate(
    val sequenceNumber: Int,
    val glucoseConcentration: Double,
    val unit: String,
    @Serializable(with = DateSerializer::class) val timestamp: Date
)
