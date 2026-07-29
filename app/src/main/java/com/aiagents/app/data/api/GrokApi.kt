package com.aiagents.app.data.api

import com.aiagents.app.data.model.ChatMessage
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

class GrokApi(private val apiKey: String) {

    @Serializable
    data class GrokRequest(
        val model: String = "grok-2",
        val messages: List<ChatMessage>,
        val temperature: Float = 0.7f,
        val max_tokens: Int = 8192
    )

    @Serializable
    data class GrokResponse(
        val choices: List<Choice>? = null,
        val error: GrokError? = null
    )

    @Serializable
    data class GrokError(val message: String? = null, val type: String? = null)

    @Serializable
    data class Choice(val message: ChatMessage)

    suspend fun chat(model: String, messages: List<ChatMessage>): String {
        val response: GrokResponse = HttpClientProvider.client.post(
            "https://api.x.ai/v1/chat/completions"
        ) {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(GrokRequest(model = model, messages = messages))
        }.body()

        response.error?.let { error ->
            throw Exception("Grok error: ${error.message ?: error.type ?: "Unknown"}")
        }

        return response.choices?.firstOrNull()?.message?.content
            ?: throw Exception("Пустой ответ от Grok")
    }
}
