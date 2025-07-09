package pl.example.networkmodule.requestData

import kotlinx.serialization.Serializable
import pl.example.networkmodule.apiData.enumTypes.ReportPattern
import pl.example.networkmodule.serializers.DateSerializer
import pl.example.networkmodule.serializers.UUIDSerializer
import java.util.Date
import java.util.UUID

@Serializable
data class GenerateGlucoseReport(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    @Serializable(with = DateSerializer::class)
    val startDate: Date,
    @Serializable(with = DateSerializer::class)
    val endDate: Date,
    val reportPattern: ReportPattern
)
