package com.aiagents.app.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.aiagents.app.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val GEMINI = stringPreferencesKey("gemini_key")
        val DEEPSEEK = stringPreferencesKey("deepseek_key")
        val QWEN = stringPreferencesKey("qwen_key")
        val MISTRAL = stringPreferencesKey("mistral_key")
        val OPENROUTER = stringPreferencesKey("openrouter_key")
        val GROK = stringPreferencesKey("grok_key")
        val THEME = stringPreferencesKey("app_theme")
        val LANGUAGE = stringPreferencesKey("app_language")
        val DEFAULT_PROVIDER = stringPreferencesKey("default_provider")
        val FLOATING_AGENTS = booleanPreferencesKey("floating_agents")
    }

    val apiKeys: Flow<ApiKeys> = context.dataStore.data.map { prefs ->
        ApiKeys(
            gemini = prefs[Keys.GEMINI] ?: "",
            deepseek = prefs[Keys.DEEPSEEK] ?: "",
            qwen = prefs[Keys.QWEN] ?: "",
            mistral = prefs[Keys.MISTRAL] ?: "",
            openrouter = prefs[Keys.OPENROUTER] ?: "",
            grok = prefs[Keys.GROK] ?: ""
        )
    }

    val appSettings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            theme = AppTheme.valueOf(prefs[Keys.THEME] ?: "SYSTEM"),
            language = AppLanguage.valueOf(prefs[Keys.LANGUAGE] ?: "SYSTEM"),
            defaultProvider = AiProvider.valueOf(prefs[Keys.DEFAULT_PROVIDER] ?: "GEMINI"),
            enableFloatingAgents = prefs[Keys.FLOATING_AGENTS] ?: true
        )
    }

    suspend fun saveKeys(keys: ApiKeys) {
        context.dataStore.edit { prefs ->
            prefs[Keys.GEMINI] = keys.gemini
            prefs[Keys.DEEPSEEK] = keys.deepseek
            prefs[Keys.QWEN] = keys.qwen
            prefs[Keys.MISTRAL] = keys.mistral
            prefs[Keys.OPENROUTER] = keys.openrouter
            prefs[Keys.GROK] = keys.grok
        }
    }

    suspend fun saveAppSettings(settings: AppSettings) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME] = settings.theme.name
            prefs[Keys.LANGUAGE] = settings.language.name
            prefs[Keys.DEFAULT_PROVIDER] = settings.defaultProvider.name
            prefs[Keys.FLOATING_AGENTS] = settings.enableFloatingAgents
        }
    }

    suspend fun clearKeys() {
        context.dataStore.edit { it.clear() }
    }
}
