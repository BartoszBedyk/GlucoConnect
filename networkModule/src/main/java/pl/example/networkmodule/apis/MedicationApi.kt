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
import pl.example.networkmodule.apiMethods.MedicationApiInterface
import pl.example.networkmodule.requestData.CreateMedication

class MedicationApi(private val ktorClient: KtorClient) : MedicationApiInterface {
    private val client = ktorClient.client
    private val medicationEndpoint: String = "medications"
    private val adress = ktorClient.baseUrl

    override suspend fun createMedication(medication: CreateMedication): Boolean {
        return try {
            val response = client.post("$adress/$medicationEndpoint") {
                contentType(ContentType.Application.Json)
                setBody(medication)
            }
            response.status == HttpStatusCode.Created
        } catch (e: Exception) {
            Log.e("MedicationApi", "Request failed with status ${e.message}")
            false
        }

    }

    override suspend fun readMedication(id: String): MedicationResult? {
        val response = client.get("$adress/$medicationEndpoint/$id")
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

    override suspend fun getAllMedications(): List<MedicationResult>? {
        val response = client.get("$adress/$medicationEndpoint/all")
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

    override suspend fun deleteMedication(id: String): Boolean {
        return try {
            val response = client.delete("$adress/$medicationEndpoint/$id")
            response.status == HttpStatusCode.OK

        } catch (e: Exception) {
            Log.e("MedicationApi", "Request failed with status ${e.message}")
            false


        }
    }

    override suspend fun getUnsynced(userId: String): List<MedicationResult>? {
        return try {
            val response = client.get("$adress/$medicationEndpoint/$userId/unsynced") {
                contentType(ContentType.Application.Json)
            }

            if (response.status == HttpStatusCode.OK) {
                response.body<List<MedicationResult>>()
            } else {
                Log.e("MedicationApi", "Request failed with status ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("MedicationApi", "Request failed with exception: ${e.message}")
            null
        }
    }

}