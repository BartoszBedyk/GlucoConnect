package pl.example.networkmodule.apiMock

import pl.example.networkmodule.apiMethods.ReportApiInterface
import pl.example.networkmodule.requestData.GenerateGlucoseReport
import java.io.File
import java.util.Date

class ReportApiMock : ReportApiInterface {
    override suspend fun getReportById(reportData: GenerateGlucoseReport): File? {
        return null
    }

}