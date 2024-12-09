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
import pl.example.networkmodule.apiData.UserResult
import pl.example.networkmodule.requestData.CreateUserForm
import pl.example.networkmodule.requestData.UnitUpdate
import pl.example.networkmodule.requestData.UpdateUserNullForm
import pl.example.networkmodule.requestData.UserCreateWIthType

class UserApi(private val ktorClient: KtorClient) {
    private val client = ktorClient.client
    private val usersEndpoint: String = "user"

    suspend fun createUser(form: CreateUserForm): Boolean {
        return try {
            val response = client.post("http://10.0.2.2:8080/$usersEndpoint") {
                contentType(ContentType.Application.Json)
                setBody(form)
            }
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("UserApi", "Request failed with status ${e.message}")
            false
        }
    }

    suspend fun createUserWithType(form: UserCreateWIthType): Boolean {
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

    suspend fun getUserById(id: String): UserResult? {
        val response = client.get("http://10.0.2.2:8080/$usersEndpoint/$id")
        return if (response.status == HttpStatusCode.OK) {
            if (response.contentType()?.match(ContentType.Application.Json) == true) {
                response.body<UserResult>()
            } else {
                println("Unexpected content type: ${response.contentType()}")
                null
            }
        } else {
            Log.e("UserApi", "Request failed with status ${response.status}")
            null
        }
    }

    suspend fun blockUser(id: String): Boolean {
        return try {
            val response = client.put("http://10.0.2.2:8080/$usersEndpoint/block/$id")
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("UserApi", "Request failed with status ${e.message}")
            false
        }
    }

    suspend fun unblockUser(id: String): Boolean {
        return try {
            val response = client.put("http://10.0.2.2:8080/$usersEndpoint/unblock/$id")
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("UserApi", "Request failed with status ${e.message}")
            false
        }
    }

    suspend fun unitUpdate(form: UnitUpdate): Boolean {
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

    suspend fun updateUserNulls(form: UpdateUserNullForm): Boolean {
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


}