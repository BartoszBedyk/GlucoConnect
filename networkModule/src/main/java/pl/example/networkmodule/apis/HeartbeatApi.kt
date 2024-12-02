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
import pl.example.networkmodule.apiData.HeartbeatResult
import pl.example.networkmodule.requestData.CreateHeartbeatForm

class HeartbeatApi(private val ktorClient: KtorClient) {
    private val client = ktorClient.client
    private val heartbeatEndpoint: String = "heartbeat"

    suspend fun createHeartbeat(heartbeat: CreateHeartbeatForm): Boolean {
        return try {
            val response = client.post("http://10.0.2.2:8080/$heartbeatEndpoint") {
                contentType(ContentType.Application.Json)
                setBody(heartbeat)
            }
            response.status == HttpStatusCode.Created
        } catch (e: Exception) {
            Log.e("HeartbeatApi", "Request failed with status ${e.message}")
            false
        }
    }

    suspend fun getHeartBeat(id: String): HeartbeatResult? {
        return try {
            val response = client.get("http://10.0.2.2:8080/$heartbeatEndpoint/$id")
            if (response.status == HttpStatusCode.OK) {
                if (response.contentType()?.match(ContentType.Application.Json) == true) {
                    response.body<HeartbeatResult>()
                } else {
                    println("Unexpected content type: ${response.contentType()}")
                    null
                }
            } else {
                Log.e("HeartbeatApi", "Request failed with status ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("HeartbeatApi", "Request failed with status ${e.message}")
            null
        }
    }

    suspend fun readHeartbeatForUser(userId: String): List<HeartbeatResult>? {
        return try {
            val response = client.get("http://10.0.2.2:8080/$heartbeatEndpoint/user/$userId")
            if (response.status == HttpStatusCode.OK)
                if (response.contentType()?.match(ContentType.Application.Json) == true) {
                    response.body<List<HeartbeatResult>>()
                } else {
                    println("Unexpected content type: ${response.contentType()}")
                    null
                }
            else {
                Log.e("HeartbeatApi", "Request failed with status ${response.status}")
                null
            }

        } catch (e: Exception) {
            Log.e("HeartbeatApi", "Request failed with status ${e.message}")
            null
        }

    }

    suspend fun deleteHeartbeat(id: String): Boolean {
        return try {
            val response = client.delete("http://10.0.2.2:8080/$heartbeatEndpoint/$id")
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("HeartbeatApi", "Request failed with status ${e.message}")
            false
        }
    }

    suspend fun deleteHeartbeatsForUser(userId: String): Boolean {
        return try {
            val response = client.delete("http://10.0.2.2:8080/$heartbeatEndpoint/user/$userId")
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("HeartbeatApi", "Request failed with status ${e.message}")
            false
        }
    }

}