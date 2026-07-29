package com.aiagents.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiagents.app.ui.screens.ChatScreen
import com.aiagents.app.ui.screens.SettingsScreen
import com.aiagents.app.ui.theme.AIAgentsTheme
import com.aiagents.app.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val appSettings by viewModel.appSettings.collectAsState()
            AIAgentsTheme(theme = appSettings.theme) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppContent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AppContent(viewModel: MainViewModel = viewModel()) {
    val showSettings by viewModel.showSettings.collectAsState()
    val apiKeys by viewModel.apiKeys.collectAsState()
    val appSettings by viewModel.appSettings.collectAsState()
    ChatScreen(viewModel = viewModel)
    if (showSettings) {
        SettingsScreen(
            currentKeys = apiKeys, currentSettings = appSettings,
            onSave = { keys, settings -> viewModel.saveKeys(keys); viewModel.saveAppSettings(settings) },
            onDismiss = { viewModel.toggleSettings() }
        )
    }
}
