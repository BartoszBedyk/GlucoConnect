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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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

        Log.d("CreateUserDebug", Json.encodeToString(form))

        try {
            val response = client.post("http://10.0.2.2:8080/createUser") {
                contentType(ContentType.Application.Json)
                setBody(form)

            }
            return if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
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
            val response = client.post("http://10.0.2.2:8080/createUser/withType") {
                contentType(ContentType.Application.Json)
                setBody(form)
            }
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("UserApi", "Request failed with status ${e.message}")
            false
        }
    }

    override suspend fun deleteUser(id: String): Boolean {
        return try {
            val response = client.delete("http://10.0.2.2:8080/$usersEndpoint/$id")
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("UserApi", "Request failed with status ${e.message}")
            false
        }
    }

    override suspend fun resetPassword(id: String, newPassword: String): Boolean {
        return try {
            val response = client.put("http://10.0.2.2:8080/$usersEndpoint/$id/$newPassword/reset-password")
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

    override suspend fun giveUserNulls(form: UpdateUserNullForm): Boolean {
        return try {
            val response = client.put("http://10.0.2.2:8080/createUser/updateNulls") {
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

    override suspend fun observe(partOne: String, partTwo: String): UserResult? {
        return try {
            val response = client.get("http://10.0.2.2:8080/$usersEndpoint/observe/$partOne/$partTwo")
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

    override suspend fun changeUserType(id: String, type: String): Boolean {
        return try {
            val response = client.put("http://10.0.2.2:8080/$usersEndpoint/$id/type/$type}")
            if (response.status == HttpStatusCode.OK) {
                true
            } else {
              false
            }
        } catch (e: Exception) {
            Log.e("UserApi", "Error during request: ${e.message}", e)
            false
        }
    }

    override suspend fun giveUserType(id: String, type: String): Boolean {
        return try {
            val response = client.put("http://10.0.2.2:8080/createUser/$id/type/$type")
            if (response.status == HttpStatusCode.OK) {
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("UserApi", "Error during request: ${e.message}", e)
            false
        }
    }


}