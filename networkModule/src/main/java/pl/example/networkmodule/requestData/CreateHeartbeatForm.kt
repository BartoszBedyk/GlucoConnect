package pl.example.networkmodule.requestData

import kotlinx.serialization.Serializable
import pl.example.networkmodule.serializers.DateSerializer
import pl.example.networkmodule.serializers.UUIDSerializer
import java.util.Date
import java.util.UUID

@Serializable
data class CreateHeartbeatForm(
    @Serializable(with = UUIDSerializer::class) val userId: UUID,
    @Serializable(with = DateSerializer::class) val timestamp: Date,
    val systolicPressure: Int,
    val diastolicPressure: Int,
    val pulse: Int,
    val note: String
)
