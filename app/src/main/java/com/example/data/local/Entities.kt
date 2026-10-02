package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String, // "user", "assistant", "system"
    val content: String,
    val mode: String = "QUICK", // QUICK, EXPLANATION, CODING, RESEARCH, PROJECT, DEVICE, AUTOMATION, CREATIVE, TROUBLESHOOTING
    val timestamp: Long = System.currentTimeMillis(),
    val isError: Boolean = false,
    val isOffline: Boolean = false
)

@Entity(tableName = "project_tasks")
data class ProjectTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: String,
    val title: String,
    val category: String, // e.g., "Requirements", "Architecture", "Implementation", "Testing", "Deployment"
    val status: String, // "NOT_STARTED", "IN_PROGRESS", "BLOCKED", "COMPLETED", "FAILED"
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val orderIndex: Int = 0
)

@Entity(tableName = "assistant_memories")
data class AssistantMemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val key: String,
    val content: String,
    val category: String = "Preference", // "Preference", "Project", "Style", "Fact"
    val timestamp: Long = System.currentTimeMillis()
)
