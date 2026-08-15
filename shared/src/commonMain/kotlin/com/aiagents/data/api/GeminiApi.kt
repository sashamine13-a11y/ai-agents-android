package com.aiagents.data.api

import com.aiagents.data.model.ChatMessage
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.json.*

class GeminiApi(private val apiKey: String) {
    private val client by lazy { HttpClientProvider.createClient(httpClientEngineFactory) }

    suspend fun generate(model: String, systemPrompt: String?, messages: List<ChatMessage>): String {
        val contents = messages.map { msg ->
            buildJsonObject {
                put("role", if (msg.role == "user") "user" else "model")
                put("parts", buildJsonArray { add(buildJsonObject { put("text", msg.content) }) })
            }
        }

        val body = buildJsonObject {
            if (!systemPrompt.isNullOrBlank()) {
                put("systemInstruction", buildJsonObject {
                    put("parts", buildJsonArray { add(buildJsonObject { put("text", systemPrompt) }) })
                })
            }
            put("contents", JsonArray(contents))
        }

        val response: JsonObject = client.post(
            "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
        ) { setBody(body) }.body()

        return response["candidates"]?.jsonArray?.firstOrNull()
            ?.jsonObject?.get("content")?.jsonObject
            ?.get("parts")?.jsonArray?.firstOrNull()
            ?.jsonObject?.get("text")?.jsonPrimitive?.content
            ?: throw Exception("Empty Gemini response")
    }
}
