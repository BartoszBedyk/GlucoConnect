package pl.example.networkmodule.requestData

import kotlinx.serialization.Serializable
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.serializers.UUIDSerializer
import java.util.UUID

@Serializable
data class UnitUpdate(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val newUnit: GlucoseUnitType
)
