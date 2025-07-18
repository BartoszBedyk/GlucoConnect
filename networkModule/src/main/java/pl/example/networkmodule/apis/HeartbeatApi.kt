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
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiMethods.HeartbeatApiInterface
import pl.example.networkmodule.requestData.CreateHeartbeatForm

class HeartbeatApi(private val ktorClient: KtorClient) : HeartbeatApiInterface {
    private val client = ktorClient.client
    private val heartbeatEndpoint: String = "heartbeat"
    private val adress = ktorClient.baseUrl

    override suspend fun createHeartbeat(heartbeat: CreateHeartbeatForm): Boolean {
        return try {
            val response = client.post("$adress/$heartbeatEndpoint") {
                contentType(ContentType.Application.Json)
                setBody(heartbeat)
            }
            response.status == HttpStatusCode.Created
        } catch (e: Exception) {
            Log.e("HeartbeatApi", "Request failed with status ${e.message}")
            false
        }
    }

    override suspend fun getHeartBeat(id: String): HeartbeatResult? {
        val response = client.get("$adress/$heartbeatEndpoint/$id")
        return if (response.status == HttpStatusCode.OK) {
            if (response.contentType()?.match(ContentType.Application.Json) == true) {
                response.body<HeartbeatResult>()
            } else {
                println("Unexpected content type: ${response.contentType()}")
                null
            }
        } else {
            Log.e("ResultApi", "Request failed with status ${response.status}")
            null
        }
    }


    override suspend fun readHeartbeatForUser(userId: String): List<HeartbeatResult>? {
        return try {
            val response = client.get("$adress/$heartbeatEndpoint/user/$userId")
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

    override suspend fun deleteHeartbeat(id: String): Boolean {
        return try {
            val response = client.delete("$adress/$heartbeatEndpoint/$id")
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("HeartbeatApi", "Request failed with status ${e.message}")
            false
        }
    }

    override suspend fun deleteHeartbeatsForUser(userId: String): Boolean {
        return try {
            val response = client.delete("$adress/$heartbeatEndpoint/user/$userId")
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("HeartbeatApi", "Request failed with status ${e.message}")
            false
        }
    }

    override suspend fun getThreeHeartbeatResults(userId: String): List<HeartbeatResult>? {
        return try {
            val response = client.get("$adress/$heartbeatEndpoint/three/$userId")
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

}