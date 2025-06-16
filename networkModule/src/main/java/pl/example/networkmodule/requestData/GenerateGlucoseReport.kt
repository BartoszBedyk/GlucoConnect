package pl.example.networkmodule.requestData

import kotlinx.serialization.Serializable
import pl.example.networkmodule.apiData.enumTypes.ReportPattern
import pl.example.networkmodule.serializers.DateSerializer
import pl.example.networkmodule.serializers.UUIDSerializer
import java.util.Date

@Serializable
data class GenerateGlucoseReport(
    val uuid: String,
    @Serializable(with = DateSerializer::class)
    val startDate: Date,
    @Serializable(with = DateSerializer::class)
    val endDate: Date,
    val reportPattern: ReportPattern = ReportPattern.STANDARD_GLUCOSE
)
