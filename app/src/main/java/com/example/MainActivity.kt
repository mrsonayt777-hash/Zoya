package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.ui.screens.AssistantChatScreen
import com.example.ui.screens.DeviceToolsScreen
import com.example.ui.screens.MemoryManagerScreen
import com.example.ui.screens.ProjectManagerScreen
import com.example.ui.screens.SettingsDialog
import com.example.ui.screens.WorkflowsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AssistantViewModel
import com.example.ui.viewmodel.AssistantViewModelFactory

enum class AppTab(val label: String, val icon: ImageVector) {
    CHAT("Chat", Icons.Default.ChatBubble),
    PROJECTS("Projects", Icons.Default.AccountTree),
    WORKFLOWS("Workflows", Icons.Default.AltRoute),
    MEMORY("Memory", Icons.Default.Psychology),
    TOOLS("Tools", Icons.Default.Smartphone)
}

class MainActivity : ComponentActivity() {

    private val viewModel: AssistantViewModel by viewModels {
        AssistantViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: AssistantViewModel) {
    var currentTab by remember { mutableStateOf(AppTab.CHAT) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val memories by viewModel.memories.collectAsState()

    // Handle back button when not on Chat tab
    if (currentTab != AppTab.CHAT) {
        BackHandler {
            currentTab = AppTab.CHAT
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(modifier = Modifier.testTag("bottom_navigation_bar")) {
                AppTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        when (currentTab) {
            AppTab.CHAT -> {
                AssistantChatScreen(
                    viewModel = viewModel,
                    uiState = uiState,
                    messages = messages,
                    onOpenSettings = { showSettingsDialog = true },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            AppTab.PROJECTS -> {
                ProjectManagerScreen(
                    viewModel = viewModel,
                    tasks = tasks,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            AppTab.WORKFLOWS -> {
                WorkflowsScreen(
                    onSelectWorkflow = { prompt, mode ->
                        viewModel.setMode(mode)
                        currentTab = AppTab.CHAT
                        viewModel.sendMessage(prompt)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            AppTab.MEMORY -> {
                MemoryManagerScreen(
                    viewModel = viewModel,
                    memories = memories,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            AppTab.TOOLS -> {
                DeviceToolsScreen(
                    viewModel = viewModel,
                    uiState = uiState,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }

    if (showSettingsDialog) {
        SettingsDialog(
            currentApiKey = uiState.apiKeyInput,
            currentModel = uiState.activeModel,
            onDismiss = { showSettingsDialog = false },
            onSave = { key, model ->
                viewModel.setApiKey(key)
                viewModel.setModel(model)
                showSettingsDialog = false
            }
        )
    }
}
