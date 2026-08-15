package com.aiagents.platform

import com.aiagents.data.model.ApiKeys
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

private val storageDir = File(System.getProperty("user.home"), ".ai-agents")
private val storageFile = File(storageDir, "apikeys.json")

actual object PlatformStorage {
    actual fun init(context: Any?) { /* no-op for desktop */ }

    actual suspend fun loadApiKeys(): ApiKeys = try {
        if (!storageFile.exists()) return ApiKeys()
        val text = storageFile.readText()
        Json.decodeFromString(text)
    } catch (e: Exception) { ApiKeys() }

    actual suspend fun saveApiKeys(keys: ApiKeys) = try {
        storageDir.mkdirs()
        val text = Json.encodeToString(keys)
        storageFile.writeText(text)
    } catch (e: Exception) { /* ignore */ }
}
