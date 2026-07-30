package com.aiagents.data.api

import com.aiagents.data.model.ChatMessage

class OpenRouterApi(private val apiKey: String) {
    suspend fun chat(model: String, messages: List<ChatMessage>): String {
        return "[openrouter response mock]"
    }
}
