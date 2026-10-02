package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearHistory()

    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteMessage(id: Long)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM project_tasks ORDER BY orderIndex ASC, id ASC")
    fun getAllTasks(): Flow<List<ProjectTaskEntity>>

    @Query("SELECT * FROM project_tasks WHERE projectId = :projectId ORDER BY orderIndex ASC, id ASC")
    fun getTasksForProject(projectId: String): Flow<List<ProjectTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: ProjectTaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<ProjectTaskEntity>)

    @Update
    suspend fun updateTask(task: ProjectTaskEntity)

    @Query("UPDATE project_tasks SET status = :status WHERE id = :id")
    suspend fun updateTaskStatus(id: Long, status: String)

    @Query("DELETE FROM project_tasks WHERE id = :id")
    suspend fun deleteTask(id: Long)

    @Query("DELETE FROM project_tasks WHERE projectId = :projectId")
    suspend fun deleteTasksByProject(projectId: String)

    @Query("DELETE FROM project_tasks")
    suspend fun clearAllTasks()
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM assistant_memories ORDER BY timestamp DESC")
    fun getAllMemories(): Flow<List<AssistantMemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: AssistantMemoryEntity): Long

    @Query("DELETE FROM assistant_memories WHERE id = :id")
    suspend fun deleteMemory(id: Long)

    @Query("DELETE FROM assistant_memories WHERE `key` = :key")
    suspend fun deleteMemoryByKey(key: String)

    @Query("DELETE FROM assistant_memories")
    suspend fun clearAllMemories()
}
