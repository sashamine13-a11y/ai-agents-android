package com.aiagents.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(val role: String, val content: String)

enum class AiProvider { GEMINI, DEEPSEEK, QWEN, MISTRAL, OPENROUTER, GROK }

data class AgentConfig(
    val id: String,
    val name: String,
    val description: String,
    val systemPrompt: String,
    val provider: AiProvider,
    val modelName: String,
    val color: Long,
    val icon: String,
    val isFloating: Boolean = false,
    val floatingPrompt: String? = null
)

data class AgentResult(
    val agentId: String,
    val content: String,
    val toolCalls: List<ToolCall> = emptyList(),
    val isError: Boolean = false
)

data class ToolCall(val toolName: String, val parameters: Map<String, String>)

data class ApiKeys(
    val gemini: String = "",
    val deepseek: String = "",
    val qwen: String = "",
    val mistral: String = "",
    val openrouter: String = "",
    val grok: String = ""
) {
    fun isAnySet(): Boolean = gemini.isNotBlank() || deepseek.isNotBlank() ||
            qwen.isNotBlank() || mistral.isNotBlank() || openrouter.isNotBlank() || grok.isNotBlank()
}

enum class AppTheme { SYSTEM, LIGHT, DARK }

enum class AppLanguage(val code: String, val displayName: String) {
    SYSTEM("system", "System"),
    RUSSIAN("ru", "Русский"),
    ENGLISH("en", "English"),
    CHINESE("zh", "中文"),
    SPANISH("es", "Español"),
    GERMAN("de", "Deutsch"),
    FRENCH("fr", "Français")
}

data class AppSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val language: AppLanguage = AppLanguage.SYSTEM,
    val defaultProvider: AiProvider = AiProvider.GEMINI,
    val enableFloatingAgents: Boolean = true
)
