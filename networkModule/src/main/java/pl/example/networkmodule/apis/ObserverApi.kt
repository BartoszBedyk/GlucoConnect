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
import pl.example.networkmodule.apiData.ObserverResult
import pl.example.networkmodule.apiMethods.ObserverApiInterface
import pl.example.networkmodule.requestData.CreateObserver
import java.util.*

class ObserverApi(private val ktorClient: KtorClient) : ObserverApiInterface {
    private val client = ktorClient.client
    private val observerEndpoint: String = "observer"
    private val adress = ktorClient.baseUrl

    override suspend fun observe(createObserver: CreateObserver): UUID? {
        return try {
            val response = client.post("$adress/$observerEndpoint") {
                contentType(ContentType.Application.Json)
                setBody(createObserver)
            }
            if (response.status == HttpStatusCode.Created) {
                val responseBody = response.body<Map<String, String>>()
                responseBody["observerId"]?.let { UUID.fromString(it) }
            } else {
                Log.e("ObserverApi", "Request failed with status ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("ObserverApi", "Request failed with exception: ${e.message}", e)
            null
        }
    }

    override suspend fun getObservedAcceptedByObserverId(observerId: String): List<ObserverResult>? {

        val response = client.get("$adress/$observerEndpoint/$observerId/accepted")
        return if (response.status == HttpStatusCode.OK) {
            if (response.contentType()?.match(ContentType.Application.Json) == true) {
                response.body<List<ObserverResult>>()
            } else {
                Log.e("ObserverApi", "Request failed with status ${response.status}")
                null
            }
        } else
            null
    }


    override suspend fun getObservedUnAcceptedByObserverId(observerId: String): List<ObserverResult>? {

        val response = client.get("$adress/$observerEndpoint/$observerId/unaccepted")
        return if (response.status == HttpStatusCode.OK) {
            if (response.contentType()?.match(ContentType.Application.Json) == true) {

                response.body<List<ObserverResult>>()
            } else {
                Log.e("ObserverApi", "Request failed with status ${response.status}")
                null
            }
        } else
            null
    }


    override suspend fun acceptObservation(createObserver: CreateObserver): Boolean {
        return try {
            val response = client.put("$adress/$observerEndpoint/accept") {
                contentType(ContentType.Application.Json)
                setBody(createObserver)
            }
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("ObserverApi", "Request failed with exception: ${e.message}", e)
            false
        }
    }

    override suspend fun unAcceptObservation(createObserver: CreateObserver): Boolean {
        return try {
            val response = client.put("$adress/$observerEndpoint/unaccept") {
                contentType(ContentType.Application.Json)
                setBody(createObserver)
            }
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("ObserverApi", "Request failed with exception: ${e.message}", e)
            false
        }
    }

    override suspend fun getObservatorByObservedIdAccepted(observedId: String): List<ObserverResult>? {
        val response = client.get("$adress/$observerEndpoint/accepted/$observedId")
        return if (response.status == HttpStatusCode.OK) {
            if (response.contentType()?.match(ContentType.Application.Json) == true) {
                Log.e("Accepted", response.body<List<ObserverResult>>().toString())
                response.body<List<ObserverResult>>()
            } else {
                Log.e("ObserverApi", "Request failed with status ${response.status}")
                null
            }
        } else
            null
    }

    override suspend fun getObservatorByObservedIdUnAccepted(observedId: String): List<ObserverResult>? {
        val response = client.get("$adress/$observerEndpoint/pending/$observedId")
        return if (response.status == HttpStatusCode.OK) {
            if (response.contentType()?.match(ContentType.Application.Json) == true) {
                Log.e("UNaccepted", response.body<List<ObserverResult>>().toString())
                response.body<List<ObserverResult>>()
            } else {
                Log.e("ObserverApi", "Request failed with status ${response.status}")
                null
            }
        } else
            null
    }

}

