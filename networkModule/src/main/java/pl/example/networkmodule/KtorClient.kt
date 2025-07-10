package pl.example.networkmodule

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


class KtorClient(context: Context) {
    private val token = getToken(context)
    val baseUrl = "https://192.168.1.26:8443"
    val context = context
    //val baseUrl = "http://10.0.2.2:8080"
    val client = HttpClient(OkHttp) {
        engine {
            preconfigured = createOkHttpClient()
        }
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

    private fun createOkHttpClient(): OkHttpClient {
        val (sslSocketFactory, trustManager) = getCustomSslSocketFactory()

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustManager)
            .addInterceptor(logging)
            .build()
    }

    private fun getCustomSslSocketFactory(): Pair<javax.net.ssl.SSLSocketFactory, X509TrustManager> {
        val cf = CertificateFactory.getInstance("X.509")
        val inputStream = context.resources.openRawResource(R.raw.ktor)
        val certificate = cf.generateCertificate(inputStream)
        inputStream.close()

        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ktor", certificate)

        val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        tmf.init(keyStore)
        val trustManager = tmf.trustManagers.filterIsInstance<X509TrustManager>().first()

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf(trustManager), null)

        return Pair(sslContext.socketFactory, trustManager)
    }





}

