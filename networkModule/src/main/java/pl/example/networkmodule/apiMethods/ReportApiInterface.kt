package pl.example.networkmodule.apiMethods

import pl.example.networkmodule.requestData.GenerateGlucoseReport
import java.io.File
import java.util.Date

interface ReportApiInterface {
    suspend fun getReportById(reportData: GenerateGlucoseReport): File?
}