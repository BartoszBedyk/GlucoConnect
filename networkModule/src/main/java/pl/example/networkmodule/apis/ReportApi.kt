package pl.example.networkmodule.apis

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.example.networkmodule.KtorClient
import pl.example.networkmodule.apiMethods.ReportApiInterface
import pl.example.networkmodule.requestData.GenerateGlucoseReport
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.sql.Date
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class ReportApi(private val ktorClient: KtorClient) : ReportApiInterface {
    private val client = ktorClient.client
    private val reportEndpoint: String = "report"
    private val adress = ktorClient.baseUrl
    private val context = ktorClient.context


    override suspend fun getReportById(reportData: GenerateGlucoseReport): File? {
        return try {
            val response = client.post("$adress/$reportEndpoint") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Pdf)
                setBody(reportData)
            }
            if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
                val now = ZonedDateTime.now(ZoneId.systemDefault())
                val rounded = now.withSecond(0).withNano(0)
                val timestamp = rounded.toInstant().toEpochMilli()
                val file = File(context.cacheDir, "report_$timestamp.pdf")
                val pdfBytes = response.body<ByteArray>()


                savePdfToDownloads(context, pdfBytes, "report_$timestamp.pdf")
                return file
            } else {
                Log.e("ReportApi", "Błąd: ${response.status}")
                null
            }

        } catch (e: Exception) {
            Log.e("HeartbeatApi", "Request failed with status ${e.message}")
            null
        }
    }

     private fun savePdfToDownloads(context: Context, pdfBytes: ByteArray, fileName: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            ?: throw IOException("Nie udało się utworzyć pliku w MediaStore")

        resolver.openOutputStream(uri).use { outputStream ->
            outputStream?.write(pdfBytes)
        }
    }
}


