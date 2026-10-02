package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ApexAiApp
import com.example.data.remote.GeminiService
import com.example.data.repository.ChatRepository
import com.example.data.repository.DeviceController
import com.example.data.repository.MemoryRepository
import com.example.data.repository.ProjectRepository

class AssistantViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AssistantViewModel::class.java)) {
            val app = context.applicationContext as ApexAiApp
            val db = app.database
            val geminiService = GeminiService()
            val chatRepo = ChatRepository(db.chatDao(), geminiService)
            val projectRepo = ProjectRepository(db.taskDao())
            val memoryRepo = MemoryRepository(db.memoryDao())
            val deviceController = DeviceController(context.applicationContext)

            return AssistantViewModel(
                chatRepository = chatRepo,
                projectRepository = projectRepo,
                memoryRepository = memoryRepo,
                deviceController = deviceController
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
