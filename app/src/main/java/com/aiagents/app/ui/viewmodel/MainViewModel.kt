package com.aiagents.app.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aiagents.app.data.datastore.SettingsDataStore
import com.aiagents.app.data.model.*
import com.aiagents.app.data.repository.AiRepository
import com.aiagents.app.domain.agents.AgentEngine
import com.aiagents.app.domain.files.FileManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsDataStore = SettingsDataStore(application)
    private val fileManager = FileManager(application)

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _selectedAgent = MutableStateFlow<AgentConfig?>(null)
    val selectedAgent: StateFlow<AgentConfig?> = _selectedAgent.asStateFlow()
    private val _apiKeys = MutableStateFlow(ApiKeys())
    val apiKeys: StateFlow<ApiKeys> = _apiKeys.asStateFlow()
    private val _showSettings = MutableStateFlow(false)
    val showSettings: StateFlow<Boolean> = _showSettings.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    private val _appSettings = MutableStateFlow(AppSettings())
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    private var agentEngine: AgentEngine? = null
    val availableAgents: List<AgentConfig> get() = agentEngine?.staticAgents ?: emptyList()
    val floatingAgents: List<AgentConfig> get() = agentEngine?.floatingAgents ?: emptyList()

    init {
        viewModelScope.launch { settingsDataStore.apiKeys.collect { _apiKeys.value = it; updateRepository(it) } }
        viewModelScope.launch { settingsDataStore.appSettings.collect { _appSettings.value = it } }
    }

    private fun updateRepository(keys: ApiKeys) {
        val repo = AiRepository(
            keys.gemini.takeIf { it.isNotBlank() }, keys.deepseek.takeIf { it.isNotBlank() },
            keys.qwen.takeIf { it.isNotBlank() }, keys.mistral.takeIf { it.isNotBlank() },
            keys.openrouter.takeIf { it.isNotBlank() }, keys.grok.takeIf { it.isNotBlank() }
        )
        agentEngine = AgentEngine(repo)
        if (_selectedAgent.value == null) _selectedAgent.value = agentEngine!!.staticAgents.firstOrNull()
    }

    fun selectAgent(agent: AgentConfig) { _selectedAgent.value = agent }

    fun sendMessage(text: String, useMultiAgent: Boolean = false, useFloating: Boolean = true) {
        if (text.isBlank()) return
        if (!apiKeys.value.isAnySet()) { _error.value = "Добавьте API ключи"; _showSettings.value = true; return }
        viewModelScope.launch {
            _isLoading.value = true; _error.value = null
            _messages.value += ChatMessage("user", text)
            try {
                val engine = agentEngine ?: throw Exception("Агенты не инициализированы")
                val results = if (useMultiAgent) engine.executeMultiAgent(text)
                else listOf(engine.executeAgent((_selectedAgent.value ?: engine.staticAgents.first()).id, text, _messages.value))
                results.forEach { result ->
                    val name = engine.agents.find { it.id == result.agentId }?.name ?: result.agentId
                    val prefix = if (result.isError) "❌" else "🤖"
                    _messages.value += ChatMessage(
                        if (result.isError) "error" else "assistant",
                        "**$prefix $name**\n\n${result.content}"
                    )
                    result.toolCalls.forEach { executeToolCall(it) }
                }
                if (useFloating && _appSettings.value.enableFloatingAgents) {
                    val lastResponse = results.lastOrNull()?.content ?: ""
                    engine.detectFloatingAgents(text, lastResponse).forEach { fId ->
                        val fResult = engine.executeFloatingAgent(fId, lastResponse, text)
                        val fAgent = engine.floatingAgents.find { it.id == fId }
                        _messages.value += ChatMessage(
                            "assistant",
                            "**✨ ${fAgent?.name ?: fId}**\n\n${fResult.content}"
                        )
                    }
                }
            } catch (e: Exception) { _messages.value += ChatMessage("error", "❌ Ошибка: ${e.message}") }
            finally { _isLoading.value = false }
        }
    }

    private fun executeToolCall(tool: ToolCall) {
        try {
            when (tool.toolName) {
                "READ_FILE" -> {
                    val content = fileManager.readFile(Uri.parse(tool.parameters["path"]))
                    _messages.value += ChatMessage("system", "📄 **Файл**\n```\n${content.take(2000)}\n```")
                }
                "LIST_DIR" -> {
                    val files = fileManager.listFiles(Uri.parse(tool.parameters["path"]))
                    _messages.value += ChatMessage("system", "📁 **Файлы**\n${files.joinToString("\n") { "• ${it.name}" }}")
                }
                else -> _messages.value += ChatMessage("system", "🔧 Команда: ${tool.toolName}")
            }
        } catch (e: Exception) { _messages.value += ChatMessage("error", "Tool error: ${e.message}") }
    }

    fun saveKeys(keys: ApiKeys) { viewModelScope.launch { settingsDataStore.saveKeys(keys); _showSettings.value = false } }
    fun saveAppSettings(settings: AppSettings) { viewModelScope.launch { settingsDataStore.saveAppSettings(settings) } }
    fun toggleSettings() { _showSettings.value = !_showSettings.value }
    fun dismissError() { _error.value = null }
    fun clearChat() { _messages.value = emptyList() }
    fun attachFile(uri: Uri) {
        try {
            val name = fileManager.getFileName(uri) ?: "файл"
            val content = fileManager.readFile(uri)
            _messages.value += ChatMessage("user", "📎 **$name**\n```\n${content.take(500)}\n```")
        } catch (e: Exception) { _error.value = "Не удалось прочитать файл: ${e.message}" }
    }
}
