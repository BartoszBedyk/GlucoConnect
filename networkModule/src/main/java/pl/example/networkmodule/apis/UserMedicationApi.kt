package pl.example.networkmodule.apis

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import pl.example.networkmodule.KtorClient
import pl.example.networkmodule.apiData.UserMedicationResult
import pl.example.networkmodule.apiData.UserResult
import pl.example.networkmodule.apiMethods.UserMedicationApiInterface
import pl.example.networkmodule.requestData.CreateUserMedicationForm

class UserMedicationApi(private val ktorClient: KtorClient) : UserMedicationApiInterface {
    private val client = ktorClient.client

    private val usersMedicationEndpoint: String = "user-medications"
    private val adress = ktorClient.baseUrl


    override suspend fun createUserMedication(userMedication: CreateUserMedicationForm): String? {
        return try {
            val response = client.post("$adress/$usersMedicationEndpoint") {
                contentType(ContentType.Application.Json)
                setBody(userMedication)
            }
            return response.body()
        } catch (e: Exception) {
            Log.e("UserMedicationApi", "Request failed with status ${e.message}")
            return null
        }
    }

    override suspend fun readUserMedication(id: String): UserMedicationResult? {
        val response = client.get("$adress/$usersMedicationEndpoint/$id")
        return if (response.status == HttpStatusCode.OK) {
            if (response.contentType()?.match(ContentType.Application.Json) == true) {
                response.body<UserMedicationResult>()
            } else {
                println("Unexpected content type: ${response.contentType()}")
                null
            }
        } else {
            Log.e("UserMedicationApi", "Request failed with status ${response.status}")
            null
        }
    }

    override suspend fun deleteUserMedication(id: String): Boolean {
        return try {
            val response = client.delete("$adress/$usersMedicationEndpoint/$id")
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("UserMedicationApi", "Request failed with status ${e.message}")
            false
        }
    }

    override suspend fun deleteUserMedicationForUser(userId: String): Boolean {
        return try {
            val response = client.delete("$adress/$usersMedicationEndpoint/user/$userId")
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("UserMedicationApi", "Request failed with status ${e.message}")
            false
        }
    }

    override suspend fun readTodayUserMedication(id: String): List<UserMedicationResult>? {
        return try {
            val response = client.get("$adress/$usersMedicationEndpoint/today/$id") {
                contentType(ContentType.Application.Json)
            }

            if (response.status == HttpStatusCode.OK) {
                if (response.contentType()?.match(ContentType.Application.Json) == true) {
                    response.body()
                } else {
                    Log.e("UserMedicationApi", "Unexpected content type: ${response.contentType()}")
                    null
                }
            } else {
                Log.e("UserMedicationApi", "Request failed with status ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("UserMedicationApi", "Request failed with exception: ${e.message}")
            null
        }
    }

    override suspend fun getUserMedicationHistory(userId: String): List<UserMedicationResult>? {
        return try {
            val response = client.get("$adress/$usersMedicationEndpoint/history/$userId") {
                contentType(ContentType.Application.Json)
            }

            if (response.status == HttpStatusCode.OK) {
                if (response.contentType()?.match(ContentType.Application.Json) == true) {
                    response.body()
                } else {
                    Log.e("UserMedicationApi", "Unexpected content type: ${response.contentType()}")
                    null
                }
            } else {
                Log.e("UserMedicationApi", "Request failed with status ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("UserMedicationApi", "Request failed with exception: ${e.message}")
            null
        }
    }


    override suspend fun getUserMedication(userId: String, medicationId: String): UserMedicationResult? {
        return try {
            val response = client.get("$adress/$usersMedicationEndpoint/user/$userId/$medicationId")

            if (response.status == HttpStatusCode.OK) {
                response.body()
            } else {
                Log.e("UserMedicationApi", "Request failed with status ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("UserMedicationApi", "Request failed with exception: ${e.message}")
            null
        }
    }

    override suspend fun readUserMedicationByID(umId: String): List<UserMedicationResult>? {
        return try {
            val response = client.get("$adress/$usersMedicationEndpoint/um/$umId")

            if (response.status == HttpStatusCode.OK) {
                response.body()
            } else {
                Log.e("UserMedicationApi", "Request failed with status ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("UserMedicationApi", "Request failed with exception: ${e.message}")
            null
        }
    }

    override suspend fun markAsSynced(userId: String): Boolean {
        return try {
            val response = client.put("$adress/$usersMedicationEndpoint/$userId/sync") {
                contentType(ContentType.Application.Json)
            }
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("UserMedicationApi", "Request failed with exception: ${e.message}")
            false
        }
    }

    override suspend fun getUserMedicationId(id: String, medicationId: String): String? {
        return try {
            val response = client.get("$adress/$usersMedicationEndpoint/umById/$id/$medicationId") {
                contentType(ContentType.Application.Json)
            }

            if (response.status == HttpStatusCode.OK) {
                response.body<String>()
            } else {
                Log.e("UserMedicationApi", "Request failed with status ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("UserMedicationApi", "Request failed with exception: ${e.message}")
            null
        }
    }




}