package pl.example.networkmodule.apiData

import kotlinx.serialization.Serializable
import pl.example.networkmodule.apiData.enumTypes.ActivityType
import pl.example.networkmodule.serializers.DateSerializer
import pl.example.networkmodule.serializers.UUIDSerializer
import java.util.Date
import java.util.UUID

@Serializable
data class ActivityResult(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val type: ActivityType?,
    @Serializable(with = DateSerializer::class)
    val creationDate: Date,
    @Serializable(with = UUIDSerializer::class)
    val createdById: UUID
)
