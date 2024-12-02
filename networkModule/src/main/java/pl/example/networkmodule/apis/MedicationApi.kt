package pl.example.networkmodule.apis

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import pl.example.networkmodule.KtorClient
import pl.example.networkmodule.apiData.MedicationResult
import pl.example.networkmodule.apiData.UserMedicationResult
import pl.example.networkmodule.requestData.CreateMedication

class MedicationApi(private val ktorClient: KtorClient) {
    private val client = ktorClient.client
    private val medicationEndpoint: String = "medications"

    suspend fun createMedication(medication: CreateMedication): Boolean {
        return try {
            val response = client.post("http://10.0.2.2:8080/$medicationEndpoint") {
                contentType(ContentType.Application.Json)
                setBody(medication)
            }
            response.status == HttpStatusCode.Created
        } catch (e: Exception) {
            Log.e("MedicationApi", "Request failed with status ${e.message}")
            false
        }

    }

    suspend fun readMedication(id: String): MedicationResult? {
        val response = client.get("http://10.0.2.2:8080/$medicationEndpoint/$id")
        return if (response.status == HttpStatusCode.OK) {
            if (response.contentType()?.match(ContentType.Application.Json) == true) {
                response.body<MedicationResult>()
            } else {
                println("Unexpected content type: ${response.contentType()}")
                null
            }
        } else {
            Log.e("MedicationApi", "Request failed with status ${response.status}")
            null
        }
    }

    suspend fun getAllMedications(): List<MedicationResult>? {
        val response = client.get("http://10.0.2.2:8080/$medicationEndpoint/all")
        return if (response.status == HttpStatusCode.OK) {
            if (response.contentType()?.match(ContentType.Application.Json) == true) {
                response.body<List<MedicationResult>>()
            } else {
                println("Unexpected content type: ${response.contentType()}")
                null
            }
        } else {
            Log.e("MedicationApi", "Request failed with status ${response.status}")
            null
        }
    }

    suspend fun deleteMedication(id: String): Boolean {
        return try {
            val response = client.delete("http://10.0.2.2:8080/$medicationEndpoint/$id")
            response.status == HttpStatusCode.OK

        } catch (e: Exception) {
            Log.e("MedicationApi", "Request failed with status ${e.message}")
            false


        }
    }
}