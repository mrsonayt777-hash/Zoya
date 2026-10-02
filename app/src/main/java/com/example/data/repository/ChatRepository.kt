package com.example.data.repository

import com.example.data.local.ChatDao
import com.example.data.local.ChatMessageEntity
import com.example.data.remote.GeminiService
import com.example.data.remote.MasterSystemPrompt
import kotlinx.coroutines.flow.Flow

class ChatRepository(
    private val chatDao: ChatDao,
    private val geminiService: GeminiService
) {
    val allMessages: Flow<List<ChatMessageEntity>> = chatDao.getAllMessages()

    suspend fun saveMessage(
        role: String,
        content: String,
        mode: String,
        isError: Boolean = false,
        isOffline: Boolean = false
    ): Long {
        return chatDao.insertMessage(
            ChatMessageEntity(
                role = role,
                content = content,
                mode = mode,
                isError = isError,
                isOffline = isOffline
            )
        )
    }

    suspend fun generateAssistantResponse(
        prompt: String,
        mode: MasterSystemPrompt.Mode,
        history: List<Pair<String, String>>,
        memories: List<String>,
        apiKeyOverride: String?,
        modelName: String
    ): Result<String> {
        return geminiService.generateResponse(
            prompt = prompt,
            mode = mode,
            conversationHistory = history,
            memories = memories,
            apiKeyOverride = apiKeyOverride,
            modelName = modelName
        )
    }

    suspend fun clearHistory() {
        chatDao.clearHistory()
    }

    suspend fun deleteMessage(id: Long) {
        chatDao.deleteMessage(id)
    }
}
