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
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.KtorClient
import pl.example.networkmodule.apiMethods.ResultApiInterface
import pl.example.networkmodule.requestData.ResearchResultCreate
import pl.example.networkmodule.requestData.ResearchResultUpdate

class ResultApi(private val ktorClient: KtorClient): ResultApiInterface {

    private val client = ktorClient.client
    private val resultsEndpoint: String = "results"

    override suspend fun getResearchResultsById(id: String): ResearchResult? {
        val response = client.get("http://10.0.2.2:8080/$resultsEndpoint/$id")
        return if (response.status == HttpStatusCode.OK) {
            if (response.contentType()?.match(ContentType.Application.Json) == true) {
                response.body<ResearchResult>()
            } else {
                println("Unexpected content type: ${response.contentType()}")
                null
            }
        } else {
            Log.e("ResultApi", "Request failed with status ${response.status}")
            null
        }
    }

    override suspend fun getAllResearchResults(): List<ResearchResult>? {
        val response = client.get("http://10.0.2.2:8080/$resultsEndpoint/all")

        return if (response.status == HttpStatusCode.OK) {

            if (response.contentType()?.match(ContentType.Application.Json) == true) {
                response.body<List<ResearchResult>>()
            } else {
                println("Unexpected content type: ${response.contentType()}")
                null
            }
        } else {
            Log.e("ResultApi", "Request failed with status ${response.status}")
            null
        }

    }

    override suspend fun getThreeResultsById(id: String): List<ResearchResult>? {
        val response = client.get("http://10.0.2.2:8080/$resultsEndpoint/three/$id")
        return if (response.status == HttpStatusCode.OK) {
            if (response.contentType()?.match(ContentType.Application.Json) == true) {
                response.body<List<ResearchResult>>()
            } else {
                println("Unexpected content type: ${response.contentType()}")
                null
            }
        }
        else {
            Log.e("ResultApi", "Request failed with status ${response.status}")
            null
        }
    }

    override suspend fun getResultsByUserId(id: String): List<ResearchResult>? {
        val response = client.get("http://10.0.2.2:8080/$resultsEndpoint/all/$id")
        return if (response.status == HttpStatusCode.OK) {
            if (response.contentType()?.match(ContentType.Application.Json) == true) {
                response.body<List<ResearchResult>>()
            } else {
                println("Unexpected content type: ${response.contentType()}")
                null
            }
        }
        else {
            Log.e("ResultApi", "Request failed with status ${response.status}")
            null
        }
    }

//    suspend fun getResearchResultsByUserId(userId: String): List<ResearchResult>? {
//        val response = client.get("http://10.0.2.2:8080/results/user/$userId")
//        return if (response.status == HttpStatusCode.OK) {
//            if (response.contentType()?.match(ContentType.Application.Json) == true) {
//                response.body<List<ResearchResult>>()
//            } else {
//                println("Unexpected content type: ${response.contentType()}")
//                null
//            }
//        } else {
//            Log.e("ResultApi", "Request failed with status ${response.status}")
//            null
//        }
//    }

    override suspend fun updateResearchResult(updateForm: ResearchResultUpdate): Boolean {
        return try {
            val response = client.put("http://10.0.2.2:8080/$resultsEndpoint/update") {
                contentType(ContentType.Application.Json)
                setBody(updateForm)
            }
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("ResultApi", "Request failed with status ${e.message}")
            false
        }
    }

    override suspend fun createResearchResult(createForm: ResearchResultCreate): Boolean {
        return try{
            val response = client.post("http://10.0.2.2:8080/$resultsEndpoint") {
                contentType(ContentType.Application.Json)
                setBody(createForm)

            }
            response.status == HttpStatusCode.OK

        }catch (e: Exception){
            Log.e("ResultApi", "Request failed with status ${e.message}")
            false
        }
    }

    override suspend fun deleteResearchResult(id: String): Boolean {
        return try {
            val response = client.delete("http://10.0.2.2:8080/$resultsEndpoint/delete/$id")
            response.status == HttpStatusCode.OK
        } catch (e: Exception){
            Log.e("ResultApi", "Request failed with status ${e.message}")
            false
        }
    }

    override suspend fun safeDeleteResearchResult(id: String): Boolean {
        return try {
            val response = client.delete("http://10.0.2.2:8080/$resultsEndpoint/safeDelete/$id")
            response.status == HttpStatusCode.OK
        } catch (e: Exception){
            Log.e("ResultApi", "Request failed with status ${e.message}")
            false
        }
    }
}