package com.aiagents.data.repository

import com.aiagents.data.api.*
import com.aiagents.data.model.*

class AiRepository(
    private val geminiKey: String?,
    private val deepSeekKey: String?,
    private val qwenKey: String?,
    private val mistralKey: String?,
    private val openrouterKey: String? = null,
    private val grokKey: String? = null
) {
    private val gemini = geminiKey?.let(::GeminiApi)
    private val deepSeek = deepSeekKey?.let(::DeepSeekApi)
    private val qwen = qwenKey?.let(::QwenApi)
    private val mistral = mistralKey?.let(::MistralApi)
    private val openrouter = openrouterKey?.let(::OpenRouterApi)
    private val grok = grokKey?.let(::GrokApi)

    suspend fun sendMessage(
        provider: AiProvider,
        model: String,
        systemPrompt: String?,
        messages: List<ChatMessage>
    ): String = when (provider) {
        AiProvider.GEMINI -> gemini?.generate(model, systemPrompt, messages)
        AiProvider.DEEPSEEK -> deepSeek?.chat(
            (systemPrompt?.let { listOf(ChatMessage("system", it)) } ?: emptyList()) + messages
        )
        AiProvider.QWEN -> qwen?.chat(
            (systemPrompt?.let { listOf(ChatMessage("system", it)) } ?: emptyList()) + messages
        )
        AiProvider.MISTRAL -> mistral?.chat(
            (systemPrompt?.let { listOf(ChatMessage("system", it)) } ?: emptyList()) + messages
        )
        AiProvider.OPENROUTER -> openrouter?.chat(model,
            (systemPrompt?.let { listOf(ChatMessage("system", it)) } ?: emptyList()) + messages
        )
        AiProvider.GROK -> grok?.chat(model,
            (systemPrompt?.let { listOf(ChatMessage("system", it)) } ?: emptyList()) + messages
        )
    } ?: throw Exception("API ключ для $provider не настроен")

    fun getAvailableProviders(): List<AiProvider> {
        return buildList {
            if (geminiKey != null) add(AiProvider.GEMINI)
            if (deepSeekKey != null) add(AiProvider.DEEPSEEK)
            if (qwenKey != null) add(AiProvider.QWEN)
            if (mistralKey != null) add(AiProvider.MISTRAL)
            if (openrouterKey != null) add(AiProvider.OPENROUTER)
            if (grokKey != null) add(AiProvider.GROK)
        }
    }
}
