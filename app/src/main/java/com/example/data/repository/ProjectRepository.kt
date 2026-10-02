package com.example.data.repository

import com.example.data.local.ProjectTaskEntity
import com.example.data.local.TaskDao
import kotlinx.coroutines.flow.Flow

class ProjectRepository(private val taskDao: TaskDao) {

    val allTasks: Flow<List<ProjectTaskEntity>> = taskDao.getAllTasks()

    fun getTasksForProject(projectId: String): Flow<List<ProjectTaskEntity>> {
        return taskDao.getTasksForProject(projectId)
    }

    suspend fun addTask(
        projectId: String,
        title: String,
        category: String,
        status: String = "NOT_STARTED",
        notes: String = ""
    ): Long {
        return taskDao.insertTask(
            ProjectTaskEntity(
                projectId = projectId,
                title = title,
                category = category,
                status = status,
                notes = notes
            )
        )
    }

    suspend fun addDefaultProjectDecomposition(projectGoal: String, projectId: String = "default") {
        taskDao.deleteTasksByProject(projectId)

        val defaultSubtasks = listOf(
            ProjectTaskEntity(
                projectId = projectId,
                title = "Define user requirements and constraints for '$projectGoal'",
                category = "Requirements",
                status = "COMPLETED",
                orderIndex = 1
            ),
            ProjectTaskEntity(
                projectId = projectId,
                title = "Design architectural topology & data schema",
                category = "Architecture",
                status = "IN_PROGRESS",
                orderIndex = 2
            ),
            ProjectTaskEntity(
                projectId = projectId,
                title = "Implement core business logic & state models",
                category = "Implementation",
                status = "NOT_STARTED",
                orderIndex = 3
            ),
            ProjectTaskEntity(
                projectId = projectId,
                title = "Execute security, validation & error handling",
                category = "Implementation",
                status = "NOT_STARTED",
                orderIndex = 4
            ),
            ProjectTaskEntity(
                projectId = projectId,
                title = "Run automated verification & edge case tests",
                category = "Testing",
                status = "NOT_STARTED",
                orderIndex = 5
            ),
            ProjectTaskEntity(
                projectId = projectId,
                title = "Prepare deployment build configuration & runbook",
                category = "Deployment",
                status = "NOT_STARTED",
                orderIndex = 6
            )
        )
        taskDao.insertTasks(defaultSubtasks)
    }

    suspend fun updateTaskStatus(id: Long, status: String) {
        taskDao.updateTaskStatus(id, status)
    }

    suspend fun updateTask(task: ProjectTaskEntity) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(id: Long) {
        taskDao.deleteTask(id)
    }

    suspend fun clearAllTasks() {
        taskDao.clearAllTasks()
    }
}
