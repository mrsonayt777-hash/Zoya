package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AssistantMemoryEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ProjectTaskEntity
import com.example.data.remote.MasterSystemPrompt
import com.example.data.repository.ChatRepository
import com.example.data.repository.DeviceController
import com.example.data.repository.MemoryRepository
import com.example.data.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class OperatingLoopStep(val label: String, val description: String) {
    IDLE("Ready", "Awaiting user objective"),
    UNDERSTAND("Understand", "Analyzing user goal and constraints"),
    PLAN("Plan", "Formulating strategy & architecture"),
    DECOMPOSE("Decompose", "Breaking down subtasks & dependencies"),
    PRIORITIZE("Prioritize", "Sequencing highest-value actions"),
    EXECUTE("Execute", "Generating structured synthesis / code"),
    VERIFY("Verify", "Evaluating accuracy against constraints"),
    REPORT("Report", "Delivering final verified outcome")
}

data class UiState(
    val currentMode: MasterSystemPrompt.Mode = MasterSystemPrompt.Mode.QUICK,
    val currentLoopStep: OperatingLoopStep = OperatingLoopStep.IDLE,
    val isGenerating: Boolean = false,
    val errorMessage: String? = null,
    val isSpeaking: Boolean = false,
    val apiKeyInput: String = "",
    val activeModel: String = "gemini-3.5-flash",
    val selectedProjectId: String = "default",
    val flashlightOn: Boolean = false,
    val batteryLevel: Int = 100,
    val isCharging: Boolean = false,
    val systemInfo: String = ""
)

class AssistantViewModel(
    private val chatRepository: ChatRepository,
    private val projectRepository: ProjectRepository,
    private val memoryRepository: MemoryRepository,
    val deviceController: DeviceController
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val messages: StateFlow<List<ChatMessageEntity>> = chatRepository.allMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<ProjectTaskEntity>> = projectRepository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val memories: StateFlow<List<AssistantMemoryEntity>> = memoryRepository.allMemories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        refreshDeviceMetrics()
        viewModelScope.launch {
            memoryRepository.seedInitialMemoriesIfEmpty()
        }
    }

    fun setMode(mode: MasterSystemPrompt.Mode) {
        _uiState.value = _uiState.value.copy(currentMode = mode)
        deviceController.vibrate(20)
    }

    fun setApiKey(key: String) {
        _uiState.value = _uiState.value.copy(apiKeyInput = key)
    }

    fun setModel(model: String) {
        _uiState.value = _uiState.value.copy(activeModel = model)
    }

    fun refreshDeviceMetrics() {
        val battery = deviceController.getBatteryLevel()
        val charging = deviceController.isDeviceCharging()
        val sysInfo = deviceController.getSystemInfo()
        _uiState.value = _uiState.value.copy(
            batteryLevel = battery,
            isCharging = charging,
            systemInfo = sysInfo,
            flashlightOn = deviceController.isFlashlightOn.value
        )
    }

    fun toggleFlashlight() {
        val success = deviceController.toggleFlashlight()
        if (success) {
            _uiState.value = _uiState.value.copy(flashlightOn = deviceController.isFlashlightOn.value)
        }
    }

    fun speak(text: String) {
        deviceController.speak(text)
        _uiState.value = _uiState.value.copy(isSpeaking = true)
    }

    fun stopSpeaking() {
        deviceController.stopSpeaking()
        _uiState.value = _uiState.value.copy(isSpeaking = false)
    }

    fun copyText(text: String, label: String = "Apex AI Output"): Boolean {
        return deviceController.copyToClipboard(text, label)
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return
        val currentMode = _uiState.value.currentMode

        viewModelScope.launch {
            // Save user prompt to Room
            chatRepository.saveMessage(
                role = "user",
                content = userText.trim(),
                mode = currentMode.name
            )

            _uiState.value = _uiState.value.copy(
                isGenerating = true,
                currentLoopStep = OperatingLoopStep.UNDERSTAND
            )

            // Step progression through Master Operating Loop
            kotlinx.coroutines.delay(150)
            _uiState.value = _uiState.value.copy(currentLoopStep = OperatingLoopStep.PLAN)
            kotlinx.coroutines.delay(150)
            _uiState.value = _uiState.value.copy(currentLoopStep = OperatingLoopStep.DECOMPOSE)

            // Prepare history & memories
            val existingMessages = messages.value
            val history = existingMessages.takeLast(6).map { it.role to it.content }
            val activeMemories = memories.value.map { "${it.key}: ${it.content}" }

            _uiState.value = _uiState.value.copy(currentLoopStep = OperatingLoopStep.EXECUTE)

            val result = chatRepository.generateAssistantResponse(
                prompt = userText,
                mode = currentMode,
                history = history,
                memories = activeMemories,
                apiKeyOverride = _uiState.value.apiKeyInput.takeIf { it.isNotBlank() },
                modelName = _uiState.value.activeModel
            )

            _uiState.value = _uiState.value.copy(currentLoopStep = OperatingLoopStep.VERIFY)
            kotlinx.coroutines.delay(100)

            result.onSuccess { responseText ->
                chatRepository.saveMessage(
                    role = "assistant",
                    content = responseText,
                    mode = currentMode.name,
                    isError = false
                )

                // If in PROJECT mode, also decompose into interactive task manager
                if (currentMode == MasterSystemPrompt.Mode.PROJECT) {
                    projectRepository.addDefaultProjectDecomposition(userText)
                }

                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    currentLoopStep = OperatingLoopStep.REPORT
                )
            }.onFailure { err ->
                val errText = "Error encountered: ${err.localizedMessage ?: "Unknown error"}. Proposing retry or offline diagnostic."
                chatRepository.saveMessage(
                    role = "assistant",
                    content = errText,
                    mode = currentMode.name,
                    isError = true
                )
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    currentLoopStep = OperatingLoopStep.IDLE,
                    errorMessage = err.localizedMessage
                )
            }
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            chatRepository.clearHistory()
            _uiState.value = _uiState.value.copy(currentLoopStep = OperatingLoopStep.IDLE)
        }
    }

    fun deleteMessage(id: Long) {
        viewModelScope.launch {
            chatRepository.deleteMessage(id)
        }
    }

    // Project Task Management
    fun updateTaskStatus(taskId: Long, newStatus: String) {
        viewModelScope.launch {
            projectRepository.updateTaskStatus(taskId, newStatus)
            deviceController.vibrate(30)
        }
    }

    fun addTask(title: String, category: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            projectRepository.addTask(
                projectId = _uiState.value.selectedProjectId,
                title = title.trim(),
                category = category
            )
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            projectRepository.deleteTask(taskId)
        }
    }

    fun clearAllTasks() {
        viewModelScope.launch {
            projectRepository.clearAllTasks()
        }
    }

    // Memory Management
    fun addMemory(key: String, content: String, category: String = "Preference") {
        if (key.isBlank() || content.isBlank()) return
        viewModelScope.launch {
            memoryRepository.saveMemory(key.trim(), content.trim(), category)
            deviceController.vibrate(30)
        }
    }

    fun deleteMemory(id: Long) {
        viewModelScope.launch {
            memoryRepository.deleteMemory(id)
        }
    }

    fun clearAllMemories() {
        viewModelScope.launch {
            memoryRepository.clearAll()
        }
    }

    override fun onCleared() {
        super.onCleared()
        deviceController.shutdown()
    }
}
