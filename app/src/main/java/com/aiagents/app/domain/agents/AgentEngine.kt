package com.aiagents.app.domain.agents

import com.aiagents.app.data.model.*
import com.aiagents.app.data.repository.AiRepository

class AgentEngine(private val repository: AiRepository) {

    val agents = listOf(
        AgentConfig("coder", "CodeAgent", "Пишет и редактирует код",
            "Ты — senior-разработчик. Пиши чистый код. CREATE_FILE|path|content, READ_FILE|path, EDIT_FILE|path|content.",
            AiProvider.GEMINI, "gemini-1.5-flash", 0xFF4CAF50, "💻"),
        AgentConfig("planner", "PlannerAgent", "Разбивает задачи на шаги",
            "Ты — архитектор ПО. Разбей запрос на шаги. Агенты: @coder, @file, @research, @review",
            AiProvider.DEEPSEEK, "deepseek-chat", 0xFF2196F3, "📋"),
        AgentConfig("file", "FileAgent", "Работает с файлами",
            "Ты — файловый менеджер. READ_FILE|path, WRITE_FILE|path|content, LIST_DIR|path.",
            AiProvider.QWEN, "qwen-turbo", 0xFFFF9800, "📁"),
        AgentConfig("research", "ResearchAgent", "Ищет информацию",
            "Ты — исследователь. Анализируй глубоко, ищи нетривиальные решения.",
            AiProvider.GEMINI, "gemini-1.5-flash", 0xFF9C27B0, "🔍"),
        AgentConfig("review", "ReviewAgent", "Ревьюит код",
            "Ты — code reviewer. Находи баги, предлагай улучшения, оцени качество (1-10).",
            AiProvider.DEEPSEEK, "deepseek-chat", 0xFFE91E63, "👁️"),
        AgentConfig("translator", "TranslateAgent", "Переводит тексты",
            "Ты — профессиональный переводчик. Переводи точно, сохраняя стиль.",
            AiProvider.QWEN, "qwen-turbo", 0xFF00BCD4, "🌐"),
        AgentConfig("debugger", "DebugAgent", "Авто-помощь при ошибках",
            "Ты — отладчик. Найди причину ошибки и предложи исправление.",
            AiProvider.GEMINI, "gemini-1.5-flash", 0xFFFF5722, "🐛", true,
            "Обнаружена ошибка в коде. Проанализируй и исправь."),
        AgentConfig("summarizer", "SummaryAgent", "Авто-суммаризация",
            "Ты — суммаризатор. Сделай краткое резюме, сохраняя ключевые моменты.",
            AiProvider.MISTRAL, "mistral-tiny", 0xFF795548, "📝", true,
            "Создай краткое резюме этого ответа."),
        AgentConfig("tester", "TestAgent", "Авто-тесты",
            "Ты — QA-инженер. Напиши unit-тесты для предоставленного кода.",
            AiProvider.OPENROUTER, "anthropic/claude-3.5-sonnet", 0xFF607D8B, "🧪", true,
            "Напиши тесты для этого кода.")
    )

    val floatingAgents: List<AgentConfig> get() = agents.filter { it.isFloating }
    val staticAgents: List<AgentConfig> get() = agents.filter { !it.isFloating }

    suspend fun executeAgent(agentId: String, userMessage: String, history: List<ChatMessage> = emptyList()): AgentResult {
        val agent = agents.find { it.id == agentId }
            ?: return AgentResult(agentId, "Агент не найден", isError = true)
        val messages = history + ChatMessage("user", userMessage)
        return try {
            val response = repository.sendMessage(agent.provider, agent.modelName, agent.systemPrompt, messages)
            AgentResult(agentId, response, parseToolCalls(response))
        } catch (e: Exception) {
            AgentResult(agentId, "Ошибка: ${e.message}", isError = true)
        }
    }

    suspend fun executeFloatingAgent(agentId: String, context: String, originalMessage: String): AgentResult {
        val agent = agents.find { it.id == agentId }
            ?: return AgentResult(agentId, "Агент не найден", isError = true)
        val prompt = agent.floatingPrompt ?: agent.systemPrompt
        val fullMessage = "Контекст: $context\n\nОригинальный запрос: $originalMessage\n\n$prompt"
        return executeAgent(agentId, fullMessage)
    }

    fun detectFloatingAgents(message: String, lastResponse: String?): List<String> {
        val triggered = mutableListOf<String>()
        if (message.contains("error", true) || message.contains("exception", true) ||
            message.contains("bug", true) || message.contains("ошибка", true) ||
            message.contains("баг", true) || lastResponse?.contains("error", true) == true) {
            triggered.add("debugger")
        }
        if ((lastResponse?.length ?: 0) > 2000) triggered.add("summarizer")
        if (message.contains("fun ", true) || message.contains("class ", true) || message.contains("def ", true)) {
            triggered.add("tester")
        }
        return triggered.distinct()
    }

    suspend fun executeMultiAgent(userRequest: String): List<AgentResult> {
        val results = mutableListOf<AgentResult>()
        val planResult = executeAgent("planner", userRequest)
        results.add(planResult)
        val plan = planResult.content
        val agentMentions = staticAgents.filter { it.id != "planner" }
            .filter { plan.contains("@${it.id}") || plan.contains(it.name) }
        for (agent in agentMentions) {
            results.add(executeAgent(agent.id, "План: $plan\n\nЗадача: $userRequest\n\nВыполни свою часть.",
                listOf(ChatMessage("assistant", plan))))
        }
        if (agentMentions.isEmpty()) results.add(executeAgent("coder", userRequest))
        return results
    }

    private fun parseToolCalls(text: String): List<ToolCall> {
        val calls = mutableListOf<ToolCall>()
        val regex = "(EDIT_FILE|CREATE_FILE|READ_FILE|WRITE_FILE|LIST_DIR|ANALYZE)\\|([^|]+)(?:\\|(.+))?".toRegex(RegexOption.DOT_MATCHES_ALL)
        regex.findAll(text).forEach { match ->
            calls.add(ToolCall(match.groupValues[1], mapOf("path" to match.groupValues[2].trim(), "content" to match.groupValues[3].trim())))
        }
        return calls
    }
}
