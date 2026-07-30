package com.aiagents.data.api

import com.aiagents.data.model.ChatMessage

class DeepSeekApi(private val apiKey: String) {
    suspend fun chat(messages: List<ChatMessage>): String {
        return "[deepseek response mock]"
    }
}
