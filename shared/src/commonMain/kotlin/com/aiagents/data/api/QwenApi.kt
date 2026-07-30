package com.aiagents.data.api

import com.aiagents.data.model.ChatMessage

class QwenApi(private val apiKey: String) {
    suspend fun chat(messages: List<ChatMessage>): String {
        return "[qwen response mock]"
    }
}
