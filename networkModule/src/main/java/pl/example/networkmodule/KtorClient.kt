package pl.example.networkmodule

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


class KtorClient(context: Context) {
    private val token = getToken(context)
    val baseUrl = "http://192.168.1.25:8080"
    val context = context
    //val baseUrl = "http://10.0.2.2:8080"
    val client = HttpClient(OkHttp) {
        defaultRequest {
            url(baseUrl)
            token?.let {
                headers[HttpHeaders.Authorization] = "Bearer $it"
            }
        }
        install(HttpTimeout){
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 10000
            socketTimeoutMillis = 15000
        }

        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }


}

