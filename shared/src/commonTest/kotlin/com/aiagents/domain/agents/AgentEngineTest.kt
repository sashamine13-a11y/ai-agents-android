package com.aiagents.domain.agents

import com.aiagents.data.model.*
import com.aiagents.data.repository.AiRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class AgentEngineTest {

    @Test
    fun testDetectFloatingAgentsError() {
        val engine = AgentEngine(AiRepository(null, null, null, null))
        val triggered = engine.detectFloatingAgents("there is an error in code", null)
        assertTrue(triggered.contains("debugger"))
    }

    @Test
    fun testDetectFloatingAgentsSummarize() {
        val engine = AgentEngine(AiRepository(null, null, null, null))
        val triggered = engine.detectFloatingAgents("please summarize this text", null)
        assertTrue(triggered.contains("summarizer"))
    }

    @Test
    fun testDetectFloatingAgentsTest() {
        val engine = AgentEngine(AiRepository(null, null, null, null))
        val triggered = engine.detectFloatingAgents("write tests for this", null)
        assertTrue(triggered.contains("tester"))
    }

    @Test
    fun testDetectFloatingAgentsNoTrigger() {
        val engine = AgentEngine(AiRepository(null, null, null, null))
        val triggered = engine.detectFloatingAgents("hello how are you", null)
        assertTrue(triggered.isEmpty())
    }

    @Test
    fun testGetAvailableProvidersEmpty() {
        val repo = AiRepository(null, null, null, null)
        assertEquals(emptyList(), repo.getAvailableProviders())
    }

    @Test
    fun testGetAvailableProvidersGemini() {
        val repo = AiRepository(geminiKey = "key", null, null, null)
        assertEquals(listOf(AiProvider.GEMINI), repo.getAvailableProviders())
    }

    @Test
    fun testGetAvailableProvidersMultiple() {
        val repo = AiRepository("g", "d", null, "m")
        val providers = repo.getAvailableProviders()
        assertEquals(3, providers.size)
        assertTrue(providers.contains(AiProvider.GEMINI))
        assertTrue(providers.contains(AiProvider.DEEPSEEK))
        assertTrue(providers.contains(AiProvider.MISTRAL))
    }

    @Test
    fun testAgentsListNotEmpty() {
        val engine = AgentEngine(AiRepository(null, null, null, null))
        assertTrue(engine.agents.isNotEmpty())
        assertTrue(engine.agents.any { it.id == "coder" })
        assertTrue(engine.agents.any { it.id == "planner" })
    }

    @Test
    fun testFloatingAgentsFiltered() {
        val engine = AgentEngine(AiRepository(null, null, null, null))
        assertTrue(engine.floatingAgents.all { it.isFloating })
        assertTrue(engine.staticAgents.none { it.isFloating })
    }

    @Test
    fun testExecuteAgentNotFound() {
        val engine = AgentEngine(AiRepository(null, null, null, null))
        val result = runTest {
            engine.executeAgent("nonexistent", "test")
        }
        assertTrue(result.isError)
        assertEquals("nonexistent", result.agentId)
    }
}
