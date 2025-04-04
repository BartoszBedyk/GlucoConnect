package pl.example.networkmodule.apis

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import pl.example.networkmodule.KtorClient
import pl.example.networkmodule.apiMethods.AuthenticationApiInterface
import pl.example.networkmodule.requestData.UserCredentials

class AuthenticationApi(private val ktorClient: KtorClient): AuthenticationApiInterface {
    private val client = ktorClient.client

    override suspend fun login(userCredentials: UserCredentials): String? {
        try {
            val response = client.post("http://10.0.2.2:8080/login") {
                contentType(ContentType.Application.Json)
                setBody(userCredentials)
            }

            return if (response.status == HttpStatusCode.OK) {
                val responseBody = response.body<Map<String, String>>()
                responseBody["token"]
            } else {
                Log.e("AuthenticationApi", "Request failed with status ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("AuthenticationApi", "Request failed with status ${e.message}")
            return null
        }
    }

    override suspend fun refreshToken(token: String): String? {
        try {
            val response = client.post("http://10.0.2.2:8080/refresh-token") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
            }

            return if (response.status == HttpStatusCode.OK) {
                val responseBody = response.body<Map<String, String>>()
                responseBody["token"]
            } else {
                Log.e("AuthenticationApi", "Refresh failed with status ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("AuthenticationApi", "Refresh failed: ${e.message}")
            return null
        }
    }

    override suspend fun isApiAvlible(): Boolean {
        return try {
            val response = client.get("http://10.0.2.2:8080/health") {
                timeout {
                    requestTimeoutMillis = 2000
                }
                contentType(ContentType.Application.Json)
            }
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("AuthenticationApi", "API availability check failed: ${e.message}")
            false
        }
    }


}