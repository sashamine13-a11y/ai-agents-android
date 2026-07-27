package com.aiagents.app.domain.agents

import com.aiagents.app.data.model.*
import com.aiagents.app.data.repository.AiRepository

class AgentEngine(private val repository: AiRepository) {

    val agents = listOf(
        AgentConfig(
            id = "coder",
            name = "CodeAgent",
            description = "Пишет и редактирует код",
            systemPrompt = """Ты — опытный senior-разработчик. 
Правила:
1. Пиши чистый, документированный код
2. Используй современные практики
3. Для создания файла: CREATE_FILE|path|content
4. Для чтения файла: READ_FILE|path
5. Для редактирования: EDIT_FILE|path|content
Отвечай на русском.""",
            provider = AiProvider.GEMINI,
            modelName = "gemini-1.5-flash",
            color = 0xFF4CAF50,
            icon = "💻"
        ),
        AgentConfig(
            id = "planner",
            name = "PlannerAgent",
            description = "Разбивает задачи на шаги",
            systemPrompt = """Ты — архитектор ПО. Разбей запрос на шаги.
Формат:
PLAN:
1. [шаг] -> @[агент]
2. [шаг] -> @[агент]

Агенты: @coder, @file, @research, @review""",
            provider = AiProvider.DEEPSEEK,
            modelName = "deepseek-chat",
            color = 0xFF2196F3,
            icon = "📋"
        ),
        AgentConfig(
            id = "file",
            name = "FileAgent",
            description = "Работает с файлами",
            systemPrompt = """Ты — файловый менеджер.
Команды:
READ_FILE|path — прочитать
WRITE_FILE|path|content — записать
LIST_DIR|path — список файлов
Отвечай кратко.""",
            provider = AiProvider.QWEN,
            modelName = "qwen-turbo",
            color = 0xFFFF9800,
            icon = "📁"
        ),
        AgentConfig(
            id = "research",
            name = "ResearchAgent",
            description = "Ищет информацию и анализирует",
            systemPrompt = "Ты — исследователь. Анализируй глубоко, ищи нетривиальные решения.",
            provider = AiProvider.GEMINI,
            modelName = "gemini-1.5-flash",
            color = 0xFF9C27B0,
            icon = "🔍"
        ),
        AgentConfig(
            id = "review",
            name = "ReviewAgent",
            description = "Ревьюит код и находит баги",
            systemPrompt = """Ты — code reviewer. 
1. Находи баги и уязвимости
2. Предлагай улучшения
3. Оцени качество кода (1-10)
4. Дай конкретные рекомендации""",
            provider = AiProvider.DEEPSEEK,
            modelName = "deepseek-chat",
            color = 0xFFE91E63,
            icon = "👁️"
        ),
        AgentConfig(
            id = "translator",
            name = "TranslateAgent",
            description = "Переводит между языками",
            systemPrompt = "Ты — профессиональный переводчик. Переводи точно, сохраняя стиль и терминологию.",
            provider = AiProvider.QWEN,
            modelName = "qwen-turbo",
            color = 0xFF00BCD4,
            icon = "🌐"
        )
    )

    suspend fun executeAgent(
        agentId: String,
        userMessage: String,
        history: List<ChatMessage> = emptyList()
    ): AgentResult {
        val agent = agents.find { it.id == agentId } 
            ?: return AgentResult(agentId, "Агент не найден", isError = true)

        val messages = history + ChatMessage("user", userMessage)

        return try {
            val response = repository.sendMessage(
                provider = agent.provider,
                model = agent.modelName,
                systemPrompt = agent.systemPrompt,
                messages = messages
            )
            val toolCalls = parseToolCalls(response)
            AgentResult(agentId, response, toolCalls)
        } catch (e: Exception) {
            AgentResult(agentId, "Ошибка: ${e.message}", isError = true)
        }
    }

    suspend fun executeMultiAgent(userRequest: String): List<AgentResult> {
        val results = mutableListOf<AgentResult>()

        val planResult = executeAgent("planner", userRequest)
        results.add(planResult)

        val plan = planResult.content
        val agentMentions = agents.filter { it.id != "planner" }
            .filter { plan.contains("@${it.id}") || plan.contains(it.name) }

        for (mentionedAgent in agentMentions) {
            val result = executeAgent(
                mentionedAgent.id,
                "План: $plan\n\nЗадача: $userRequest\n\nВыполни свою часть.",
                history = listOf(ChatMessage("assistant", plan))
            )
            results.add(result)
        }

        if (agentMentions.isEmpty()) {
            val fallback = executeAgent("coder", userRequest)
            results.add(fallback)
        }

        return results
    }

    private fun parseToolCalls(text: String): List<ToolCall> {
        val calls = mutableListOf<ToolCall>()
        val regex = "(EDIT_FILE|CREATE_FILE|READ_FILE|WRITE_FILE|LIST_DIR|ANALYZE)\|([^|]+)(?:\|(.+))?".toRegex(RegexOption.DOT_MATCHES_ALL)

        regex.findAll(text).forEach { match ->
            calls.add(ToolCall(
                toolName = match.groupValues[1],
                parameters = mapOf(
                    "path" to match.groupValues[2].trim(),
                    "content" to match.groupValues[3].trim()
                )
            ))
        }
        return calls
    }
}
