package com.aiagents.app.data.api

import com.aiagents.app.data.model.ChatMessage
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

class OpenRouterApi(private val apiKey: String) {

    @Serializable
    data class ORRequest(
        val model: String,
        val messages: List<ChatMessage>,
        val temperature: Float = 0.7f,
        val max_tokens: Int = 8192
    )

    @Serializable
    data class ORResponse(
        val choices: List<Choice>? = null,
        val error: ORError? = null
    )

    @Serializable
    data class ORError(val message: String? = null, val code: String? = null)

    @Serializable
    data class Choice(val message: ChatMessage)

    suspend fun chat(model: String, messages: List<ChatMessage>): String {
        val response: ORResponse = HttpClientProvider.client.post(
            "https://openrouter.ai/api/v1/chat/completions"
        ) {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            header("HTTP-Referer", "https://github.com/sashamine13-a11y/ai-agents-android")
            header("X-Title", "AI Agents Android")
            contentType(ContentType.Application.Json)
            setBody(ORRequest(model = model, messages = messages))
        }.body()

        response.error?.let { error ->
            throw Exception("OpenRouter error: ${error.message ?: error.code ?: "Unknown"}")
        }

        return response.choices?.firstOrNull()?.message?.content
            ?: throw Exception("Пустой ответ от OpenRouter")
    }
}
