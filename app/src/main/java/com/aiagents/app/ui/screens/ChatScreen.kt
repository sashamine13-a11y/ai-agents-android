package com.aiagents.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiagents.app.ui.components.AgentSelector
import com.aiagents.app.ui.components.MessageBubble
import com.aiagents.app.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: MainViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedAgent by viewModel.selectedAgent.collectAsState()
    val agents = viewModel.availableAgents
    val listState = rememberLazyListState()
    val error by viewModel.error.collectAsState()

    var input by remember { mutableStateOf(TextFieldValue()) }
    var useMultiAgent by remember { mutableStateOf(false) }

    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.attachFile(it) }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    error?.let {
        LaunchedEffect(it) {
            viewModel.dismissError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Agents") },
                actions = {
                    IconButton(onClick = { viewModel.toggleSettings() }) {
                        Icon(Icons.Default.Settings, contentDescription = "Настройки")
                    }
                    IconButton(onClick = { viewModel.clearChat() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Очистить")
                    }
                }
            )
        },
        bottomBar = {
            Column {
                if (agents.isNotEmpty()) {
                    AgentSelector(
                        agents = agents,
                        selectedAgent = selectedAgent,
                        onSelect = { viewModel.selectAgent(it) }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = useMultiAgent,
                            onCheckedChange = { useMultiAgent = it }
                        )
                        Text(
                            "Мульти",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    IconButton(onClick = { filePicker.launch("*/*") }) {
                        Icon(Icons.Default.AttachFile, contentDescription = "Файл")
                    }

                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Введите сообщение…") },
                        maxLines = 4,
                        singleLine = false
                    )

                    IconButton(
                        onClick = {
                            if (input.text.isNotBlank()) {
                                viewModel.sendMessage(input.text, useMultiAgent)
                                input = TextFieldValue()
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Отправить")
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->
                    val isUser = msg.role == "user"
                    val isError = msg.role == "error"
                    val agentColor = if (!isUser && !isError) {
                        agents.find { msg.content.startsWith("**${it.name}") 
                            || msg.content.startsWith("**🤖 ${it.name}")
                            || msg.content.startsWith("**❌ ${it.name}") }?.color?.let { 
                            androidx.compose.ui.graphics.Color(it) 
                        }
                    } else null

                    MessageBubble(
                        message = msg.content,
                        isUser = isUser,
                        isError = isError,
                        agentColor = agentColor
                    )
                }

                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        }
    }
}
