package com.aiagents.data.api

import com.aiagents.data.model.ChatMessage
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.json.*

class QwenApi(private val apiKey: String) {
    private val client by lazy { HttpClientProvider.createClient(httpClientEngineFactory) }

    suspend fun chat(messages: List<ChatMessage>): String {
        val body = buildJsonObject {
            put("model", "qwen-turbo")
            put("input", buildJsonObject {
                put("messages", JsonArray(messages.map { msg ->
                    buildJsonObject {
                        put("role", msg.role)
                        put("content", msg.content)
                    }
                }))
            })
            put("parameters", buildJsonObject {
                put("result_format", "message")
            })
        }

        val response: JsonObject = client.post(
            "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation"
        ) {
            header("Authorization", "Bearer $apiKey")
            setBody(body)
        }.body()

        return response["output"]?.jsonObject
            ?.get("choices")?.jsonArray?.firstOrNull()
            ?.jsonObject?.get("message")?.jsonObject
            ?.get("content")?.jsonPrimitive?.content
            ?: throw Exception("Empty Qwen response")
    }
}
