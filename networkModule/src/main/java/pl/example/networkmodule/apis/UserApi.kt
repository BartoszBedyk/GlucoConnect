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
import pl.example.networkmodule.apiData.UserResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiMethods.UserApiInterface
import pl.example.networkmodule.requestData.CreateUserStepOneForm
import pl.example.networkmodule.requestData.CreateUserStepTwoForm
import pl.example.networkmodule.requestData.UnitUpdate
import pl.example.networkmodule.requestData.UpdateUserNullForm
import pl.example.networkmodule.requestData.UserCreateWIthType

class UserApi(private val ktorClient: KtorClient) : UserApiInterface {
    private val client = ktorClient.client
    private val usersEndpoint: String = "user"
    private val adress = ktorClient.baseUrl

    override suspend fun createUserStepOne(form: CreateUserStepOneForm): String? {

        Log.d("CreateUserDebug", Json.encodeToString(form))

        try {
            val response = client.post("$adress/createUserStepOne") {
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

    override suspend fun createUserStepTwo(form: CreateUserStepTwoForm): Boolean {
        Log.d("CreateUserDebug", Json.encodeToString(form))

        return try {
            val response = client.put("$adress/createUserStepTwo") {
                contentType(ContentType.Application.Json)
                setBody(form)

            }
            response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created

        } catch (e: Exception) {
            Log.e("AuthenticationApi", "Request failed with status ${e.message}")
            false
        }
    }

    override suspend fun createUserWithType(form: UserCreateWIthType): Boolean {
        return try {
            val response = client.post("$adress/createUser/withType") {
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
            val response = client.delete("$adress/$usersEndpoint/$id")
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("UserApi", "Request failed with status ${e.message}")
            false
        }
    }

    override suspend fun resetPassword(id: String, newPassword: String): Boolean {
        return try {
            val response = client.put("$adress/$usersEndpoint/$id/$newPassword/reset-password")
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("UserApi", "Request failed with status ${e.message}")
            false
        }
    }


    override suspend fun getUserById(id: String): UserResult? {
        return try {
            val response = client.get("$adress/$usersEndpoint/$id")
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
            val response = client.put("$adress/$usersEndpoint/block/$id")
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("UserApi", "Request failed with status ${e.message}")
            false
        }
    }

    override suspend fun unblockUser(id: String): Boolean {
        return try {
            val response = client.put("$adress/$usersEndpoint/unblock/$id")
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("UserApi", "Request failed with status ${e.message}")
            false
        }
    }

    override suspend fun unitUpdate(form: UnitUpdate): Boolean {
        return try {
            val response = client.put("$adress/$usersEndpoint/unitUpdate") {
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
            val response = client.put("$adress/$usersEndpoint/updateNulls") {
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
            val response = client.put("$adress/createUser/updateNulls") {
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
        val response = client.get("$adress/$usersEndpoint/unit/$id")
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
        val response = client.get("$adress/$usersEndpoint/all")
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
            val response = client.get("$adress/$usersEndpoint/observe/$partOne/$partTwo")
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
            val response = client.put("$adress/$usersEndpoint/$id/type/$type}")
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
            val response = client.put("$adress/$usersEndpoint/$id/type/$type")
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