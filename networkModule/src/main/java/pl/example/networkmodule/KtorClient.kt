package pl.example.networkmodule

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.Date
import java.util.UUID


class KtorClient {
    private val client = HttpClient(OkHttp) {
        defaultRequest { url("http://10.0.2.2:8080/") }

        install(Logging) {
            logger = Logger.SIMPLE

        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }

    }

    suspend fun getResearchResultsById(id: String): ResearchResult? {
        val response = client.get("http://10.0.2.2:8080/results/$id")
        return if (response.status == HttpStatusCode.OK) {
            if (response.contentType()?.match(ContentType.Application.Json) == true) {
                response.body<ResearchResult>()
            } else {
                println("Unexpected content type: ${response.contentType()}")
                null
            }
        } else {
            println("Request failed with status ${response.status}")
            null
        }
    }
}

@Serializable
data class ResearchResult(
    val id: String,
    val sequenceNumber: Int,
    val glucoseConcentration: Double,
    val unit: String,
    val timestamp: String,
    val userId: String?,
    val deletedOn: String?,
    val lastUpdatedOn: String?
)