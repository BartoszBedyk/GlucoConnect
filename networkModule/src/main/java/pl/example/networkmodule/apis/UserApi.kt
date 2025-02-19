package pl.example.networkmodule.apis

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import pl.example.networkmodule.KtorClient
import pl.example.networkmodule.apiData.MedicationResult
import pl.example.networkmodule.apiData.UserResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiMethods.UserApiInterface
import pl.example.networkmodule.requestData.CreateUserForm
import pl.example.networkmodule.requestData.UnitUpdate
import pl.example.networkmodule.requestData.UpdateUserNullForm
import pl.example.networkmodule.requestData.UserCreateWIthType

class UserApi(private val ktorClient: KtorClient) : UserApiInterface {
    private val client = ktorClient.client
    private val usersEndpoint: String = "user"

    override suspend fun createUser(form: CreateUserForm): String? {
        try {
            val response = client.post("http://10.0.2.2:8080/$usersEndpoint") {
                contentType(ContentType.Application.Json)
                setBody(form)

            }
            return if (response.status == HttpStatusCode.OK) {
                val responseBody = response.body<Map<String, String>>()
                responseBody["id"]
            } else {
                Log.e("AuthenticationApi", "Request failed with status ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("AuthenticationApi", "Request failed with status ${e.message}")
            return null
        }
    }

    override suspend fun createUserWithType(form: UserCreateWIthType): Boolean {
        return try {
            val response = client.post("http://10.0.2.2:8080/$usersEndpoint/withType") {
                contentType(ContentType.Application.Json)
                setBody(form)
            }
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("UserApi", "Request failed with status ${e.message}")
            false
        }
    }

    override suspend fun getUserById(id: String): UserResult? {
        return try {
            val response = client.get("http://10.0.2.2:8080/$usersEndpoint/$id")
            if (response.status == HttpStatusCode.OK) {
                if (response.contentType()?.match(ContentType.Application.Json) == true) {
                    response.body<UserResult>()
                } else {
                    Log.e("UserApi", "Unexpected content type: ${response.contentType()}")
                    null
                }
            } else {
                Log.e("UserApi", "Request failed with status ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("UserApi", "Error during request: ${e.message}", e)
            null
        }
    }

    override suspend fun blockUser(id: String): Boolean {
        return try {
            val response = client.put("http://10.0.2.2:8080/$usersEndpoint/block/$id")
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("UserApi", "Request failed with status ${e.message}")
            false
        }
    }

    override suspend fun unblockUser(id: String): Boolean {
        return try {
            val response = client.put("http://10.0.2.2:8080/$usersEndpoint/unblock/$id")
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("UserApi", "Request failed with status ${e.message}")
            false
        }
    }

    override suspend fun unitUpdate(form: UnitUpdate): Boolean {
        return try {
            val response = client.put("http://10.0.2.2:8080/$usersEndpoint/unitUpdate") {
                contentType(ContentType.Application.Json)
                setBody(form)
            }
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("UserApi", "Request failed with status ${e.message}")
            false
        }
    }

    override suspend fun updateUserNulls(form: UpdateUserNullForm): Boolean {
        return try {
            val response = client.put("http://10.0.2.2:8080/$usersEndpoint/updateNulls") {
                contentType(ContentType.Application.Json)
                setBody(form)
            }
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("UserApi", "Request failed with status ${e.message}")
            false
        }
    }

    override suspend fun getUserUnitById(id: String): GlucoseUnitType? {
        val response = client.get("http://10.0.2.2:8080/$usersEndpoint/unit/$id")
        return if (response.status == HttpStatusCode.OK) {
            if (response.contentType()?.match(ContentType.Application.Json) == true) {
                response.body<GlucoseUnitType>()
            } else {
                println("Unexpected content type: ${response.contentType()}")
                null
            }
        } else
            return GlucoseUnitType.MG_PER_DL
    }

    override suspend fun getAllUsers(): List<UserResult>? {
        val response = client.get("http://10.0.2.2:8080/$usersEndpoint/all")
        return if (response.status == HttpStatusCode.OK) {
            if (response.contentType()?.match(ContentType.Application.Json) == true) {
                response.body<List<UserResult>>()
            } else {
                println("Unexpected content type: ${response.contentType()}")
                null
            }
        } else {
            Log.e("UserApi", "Request failed with status ${response.status}")
            null
        }
    }


}