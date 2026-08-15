package com.aiagents.data.api

import com.aiagents.data.model.ChatMessage
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.json.*

class MistralApi(private val apiKey: String) {
    private val client by lazy { HttpClientProvider.createClient(httpClientEngineFactory) }

    suspend fun chat(messages: List<ChatMessage>): String {
        val body = buildJsonObject {
            put("model", "mistral-tiny")
            put("messages", JsonArray(messages.map { msg ->
                buildJsonObject {
                    put("role", msg.role)
                    put("content", msg.content)
                }
            }))
        }

        val response: JsonObject = client.post("https://api.mistral.ai/v1/chat/completions") {
            header("Authorization", "Bearer $apiKey")
            setBody(body)
        }.body()

        return response["choices"]?.jsonArray?.firstOrNull()
            ?.jsonObject?.get("message")?.jsonObject
            ?.get("content")?.jsonPrimitive?.content
            ?: throw Exception("Empty Mistral response")
    }
}
