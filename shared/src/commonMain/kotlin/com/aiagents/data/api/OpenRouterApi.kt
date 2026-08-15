package com.aiagents.data.api

import com.aiagents.data.model.ChatMessage
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.json.*

class OpenRouterApi(private val apiKey: String) {
    private val client by lazy { HttpClientProvider.createClient(httpClientEngineFactory) }

    suspend fun chat(model: String, messages: List<ChatMessage>): String {
        val body = buildJsonObject {
            put("model", model)
            put("messages", JsonArray(messages.map { msg ->
                buildJsonObject {
                    put("role", msg.role)
                    put("content", msg.content)
                }
            }))
        }

        val response: JsonObject = client.post("https://openrouter.ai/api/v1/chat/completions") {
            header("Authorization", "Bearer $apiKey")
            header("HTTP-Referer", "https://github.com/sashamine13-a11y/ai-agents-android")
            header("X-Title", "AI Agents Android")
            setBody(body)
        }.body()

        return response["choices"]?.jsonArray?.firstOrNull()
            ?.jsonObject?.get("message")?.jsonObject
            ?.get("content")?.jsonPrimitive?.content
            ?: throw Exception("Empty OpenRouter response")
    }
}
