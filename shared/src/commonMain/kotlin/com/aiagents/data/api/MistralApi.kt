package com.aiagents.data.api

import com.aiagents.data.model.ChatMessage

class MistralApi(private val apiKey: String) {
    suspend fun chat(messages: List<ChatMessage>): String {
        return "[mistral response mock]"
    }
}
