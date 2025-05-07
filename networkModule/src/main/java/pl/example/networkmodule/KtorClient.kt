package pl.example.networkmodule

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


class KtorClient(context: Context) {
    private val token = getToken(context)
    //val baseUrl = "http://192.168.51.197:8080"
    val baseUrl = "http://10.0.2.2:8080"
    val client = HttpClient(OkHttp) {
        defaultRequest {
            url(baseUrl)
            token?.let {
                headers.append("Authorization", "Bearer ${getToken(context)}")
            }
        }
        install(HttpTimeout){
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 10000
            socketTimeoutMillis = 15000
        }

        install(Logging) {
            logger = Logger.SIMPLE
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }


}

