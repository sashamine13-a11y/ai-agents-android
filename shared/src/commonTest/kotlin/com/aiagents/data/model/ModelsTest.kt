package com.aiagents.data.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class ModelsTest {
    @Test
    fun testChatMessageCreation() {
        val msg = ChatMessage("user", "Hello")
        assertEquals("user", msg.role)
        assertEquals("Hello", msg.content)
    }

    @Test
    fun testApiKeysEmpty() {
        val empty = ApiKeys()
        assertFalse(empty.isAnySet())
    }

    @Test
    fun testApiKeysWithGemini() {
        val keys = ApiKeys(gemini = "test-key-123")
        assertTrue(keys.isAnySet())
        assertEquals("test-key-123", keys.gemini)
    }

    @Test
    fun testApiKeysWithDeepSeek() {
        val keys = ApiKeys(deepseek = "ds-key")
        assertTrue(keys.isAnySet())
    }

    @Test
    fun testAgentConfigDefaults() {
        val agent = AgentConfig(
            id = "test",
            name = "TestAgent",
            description = "Test desc",
            systemPrompt = "You are a test",
            provider = AiProvider.GEMINI,
            modelName = "gemini-1.5-flash",
            color = 0xFF0000,
            icon = "🧪"
        )
        assertEquals(false, agent.isFloating)
        assertEquals(null, agent.floatingPrompt)
    }

    @Test
    fun testAgentConfigFloating() {
        val agent = AgentConfig(
            id = "debugger",
            name = "DebugAgent",
            description = "Debug",
            systemPrompt = "Debug",
            provider = AiProvider.GEMINI,
            modelName = "gemini-1.5-flash",
            color = 0xFF0000,
            icon = "🐛",
            isFloating = true,
            floatingPrompt = "Fix this"
        )
        assertTrue(agent.isFloating)
        assertEquals("Fix this", agent.floatingPrompt)
    }

    @Test
    fun testAppSettingsDefaults() {
        val settings = AppSettings()
        assertEquals(AppTheme.SYSTEM, settings.theme)
        assertEquals(AppLanguage.SYSTEM, settings.language)
        assertEquals(AiProvider.GEMINI, settings.defaultProvider)
        assertEquals(true, settings.enableFloatingAgents)
    }

    @Test
    fun testToolCallCreation() {
        val tc = ToolCall("READ_FILE", mapOf("path" to "/test.txt"))
        assertEquals("READ_FILE", tc.toolName)
        assertEquals("/test.txt", tc.parameters["path"])
    }

    @Test
    fun testAgentResultSuccess() {
        val result = AgentResult("coder", "code here")
        assertEquals("coder", result.agentId)
        assertEquals("code here", result.content)
        assertFalse(result.isError)
        assertTrue(result.toolCalls.isEmpty())
    }

    @Test
    fun testAgentResultError() {
        val result = AgentResult("coder", "fail", isError = true)
        assertTrue(result.isError)
    }
}
