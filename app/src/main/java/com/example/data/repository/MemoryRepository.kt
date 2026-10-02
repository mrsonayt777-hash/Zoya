package com.example.data.repository

import com.example.data.local.AssistantMemoryEntity
import com.example.data.local.MemoryDao
import kotlinx.coroutines.flow.Flow

class MemoryRepository(private val memoryDao: MemoryDao) {

    val allMemories: Flow<List<AssistantMemoryEntity>> = memoryDao.getAllMemories()

    suspend fun saveMemory(key: String, content: String, category: String = "Preference"): Long {
        return memoryDao.insertMemory(
            AssistantMemoryEntity(
                key = key,
                content = content,
                category = category
            )
        )
    }

    suspend fun deleteMemory(id: Long) {
        memoryDao.deleteMemory(id)
    }

    suspend fun deleteMemoryByKey(key: String) {
        memoryDao.deleteMemoryByKey(key)
    }

    suspend fun clearAll() {
        memoryDao.clearAllMemories()
    }

    suspend fun seedInitialMemoriesIfEmpty() {
        // Helpful defaults
        saveMemory("Identity", "Apex AI is an advanced personal AI assistant focused on accuracy and verified completion.", "System")
        saveMemory("Code Standard", "Prefers Kotlin with Jetpack Compose and clean M3 architecture.", "Preference")
        saveMemory("Operating Principle", "Never claim false completion; verify all important results.", "Principle")
    }
}
