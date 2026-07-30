package com.aiagents.data.api

import com.aiagents.data.model.ChatMessage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Placeholder: simple implementation for demonstration. Prefer full API wrappers.
class GeminiApi(private val apiKey: String) {
    suspend fun generate(model: String, systemPrompt: String?, messages: List<ChatMessage>): String {
        // Use Ktor client from HttpClientProvider
        val client = HttpClientProvider.createClient(httpClientEngineFactory)
        // TODO: implement actual request to Gemini
        return "[gemini response mock]"
    }
}
