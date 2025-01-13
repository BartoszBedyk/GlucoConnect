package pl.example.networkmodule.apiData

import kotlinx.serialization.Serializable
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.serializers.DateSerializer
import pl.example.networkmodule.serializers.UUIDSerializer
import java.util.Date
import java.util.UUID

@Serializable
data class ResearchResult(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val sequenceNumber: Int,
    var glucoseConcentration: Double,
    var unit: GlucoseUnitType,
    @Serializable(with = DateSerializer::class)
    val timestamp: Date,
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID?,
    @Serializable(with = DateSerializer::class)
    val deletedOn: Date?,
    @Serializable(with = DateSerializer::class)
    val lastUpdatedOn: Date?
)
