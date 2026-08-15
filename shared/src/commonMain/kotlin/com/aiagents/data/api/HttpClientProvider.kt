package com.aiagents.data.api

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object HttpClientProvider {
    fun createClient(engine: HttpClientEngineFactory<*>): HttpClient = HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = false
                coerceInputValues = true
                explicitNulls = false
            })
        }
        install(Logging) {
            level = LogLevel.HEADERS
            logger = Logger.DEFAULT
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 90000
            connectTimeoutMillis = 20000
            socketTimeoutMillis = 90000
        }
        install(HttpResponseValidator) {
            validateResponse { response ->
                if (response.status.value >= 400) {
                    val body = response.bodyAsText()
                    throw Exception("HTTP ${response.status.value}: $body")
                }
            }
        }
        defaultRequest {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
        expectSuccess = false
    }
}
