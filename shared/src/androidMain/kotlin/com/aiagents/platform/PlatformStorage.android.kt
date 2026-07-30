package com.aiagents.platform

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aiagents.data.model.ApiKeys
import kotlinx.coroutines.flow.first

private val Context.aiAgentsDataStore by preferencesDataStore("ai_agents_prefs")

actual object PlatformStorage {
    private lateinit var ctx: Context

    actual fun init(context: Any?) {
        ctx = (context as? Context) ?: throw IllegalArgumentException("Context required for Android PlatformStorage")
    }

    actual suspend fun loadApiKeys(): ApiKeys {
        val prefs = ctx.aiAgentsDataStore.data.first()
        return ApiKeys(
            gemini = prefs[stringPreferencesKey("gemini")] ?: "",
            deepseek = prefs[stringPreferencesKey("deepseek")] ?: "",
            qwen = prefs[stringPreferencesKey("qwen")] ?: "",
            mistral = prefs[stringPreferencesKey("mistral")] ?: "",
            openrouter = prefs[stringPreferencesKey("openrouter")] ?: "",
            grok = prefs[stringPreferencesKey("grok")] ?: ""
        )
    }

    actual suspend fun saveApiKeys(keys: ApiKeys) {
        ctx.aiAgentsDataStore.edit { prefs ->
            prefs[stringPreferencesKey("gemini")] = keys.gemini
            prefs[stringPreferencesKey("deepseek")] = keys.deepseek
            prefs[stringPreferencesKey("qwen")] = keys.qwen
            prefs[stringPreferencesKey("mistral")] = keys.mistral
            prefs[stringPreferencesKey("openrouter")] = keys.openrouter
            prefs[stringPreferencesKey("grok")] = keys.grok
        }
    }
}
