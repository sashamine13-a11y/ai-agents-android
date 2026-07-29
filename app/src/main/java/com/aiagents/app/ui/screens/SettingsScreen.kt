package com.aiagents.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.aiagents.app.data.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentKeys: ApiKeys,
    currentSettings: AppSettings = AppSettings(),
    onSave: (ApiKeys, AppSettings) -> Unit,
    onDismiss: () -> Unit
) {
    var gemini by remember { mutableStateOf(currentKeys.gemini) }
    var deepseek by remember { mutableStateOf(currentKeys.deepseek) }
    var qwen by remember { mutableStateOf(currentKeys.qwen) }
    var mistral by remember { mutableStateOf(currentKeys.mistral) }
    var openrouter by remember { mutableStateOf(currentKeys.openrouter) }
    var grok by remember { mutableStateOf(currentKeys.grok) }
    var showPasswords by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf(currentSettings.theme) }
    var selectedLanguage by remember { mutableStateOf(currentSettings.language) }
    var enableFloating by remember { mutableStateOf(currentSettings.enableFloatingAgents) }
    var expandedTheme by remember { mutableStateOf(false) }
    var expandedLang by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Настройки") },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("API Ключи", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                Text("Введите хотя бы один ключ. Все ключи хранятся локально.", style = MaterialTheme.typography.bodySmall)
                ApiKeyField("Gemini", gemini, { gemini = it }, showPasswords)
                ApiKeyField("DeepSeek", deepseek, { deepseek = it }, showPasswords)
                ApiKeyField("Qwen", qwen, { qwen = it }, showPasswords)
                ApiKeyField("Mistral", mistral, { mistral = it }, showPasswords)
                ApiKeyField("OpenRouter", openrouter, { openrouter = it }, showPasswords)
                ApiKeyField("Grok (xAI)", grok, { grok = it }, showPasswords)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { showPasswords = !showPasswords }) { Text(if (showPasswords) "Скрыть" else "Показать") }
                }
                HorizontalDivider()
                Text("Тема", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                ExposedDropdownMenuBox(expanded = expandedTheme, onExpandedChange = { expandedTheme = it }) {
                    OutlinedTextField(
                        value = when (selectedTheme) { AppTheme.SYSTEM -> "Системная"; AppTheme.LIGHT -> "Светлая"; AppTheme.DARK -> "Тёмная" },
                        onValueChange = {}, readOnly = true, label = { Text("Тема") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedTheme) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expandedTheme, onDismissRequest = { expandedTheme = false }) {
                        AppTheme.entries.forEach { theme ->
                            DropdownMenuItem(text = { Text(when (theme) { AppTheme.SYSTEM -> "🌓 Системная"; AppTheme.LIGHT -> "☀️ Светлая"; AppTheme.DARK -> "🌙 Тёмная" }) },
                                onClick = { selectedTheme = theme; expandedTheme = false })
                        }
                    }
                }
                HorizontalDivider()
                Text("Язык", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                ExposedDropdownMenuBox(expanded = expandedLang, onExpandedChange = { expandedLang = it }) {
                    OutlinedTextField(
                        value = selectedLanguage.displayName, onValueChange = {}, readOnly = true,
                        label = { Text("Язык") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedLang) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expandedLang, onDismissRequest = { expandedLang = false }) {
                        AppLanguage.entries.forEach { lang ->
                            DropdownMenuItem(text = { Text(lang.displayName) },
                                onClick = { selectedLanguage = lang; expandedLang = false })
                        }
                    }
                }
                HorizontalDivider()
                Text("Плавающие агенты", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = enableFloating, onCheckedChange = { enableFloating = it })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Автоматически вызывать плавающих агентов")
                }
                Text("Плавающие агенты автоматически помогают при ошибках, суммируют длинные ответы и генерируют тесты.",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        confirmButton = {
            Button(onClick = { onSave(ApiKeys(gemini, deepseek, qwen, mistral, openrouter, grok),
                AppSettings(selectedTheme, selectedLanguage, enableFloatingAgents = enableFloating)) }) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}

@Composable
private fun ApiKeyField(label: String, value: String, onValueChange: (String) -> Unit, showPasswords: Boolean) {
    OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(label) }, singleLine = true,
        visualTransformation = if (showPasswords) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii, imeAction = ImeAction.Next),
        modifier = Modifier.fillMaxWidth())
}
