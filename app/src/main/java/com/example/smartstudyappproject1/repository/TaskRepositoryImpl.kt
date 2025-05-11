package com.example.smartstudyappproject1.repository

import com.example.smartstudyappproject1.data.local.TaskDAO
import com.example.smartstudyappproject1.model.Task
import com.example.smartstudyappproject1.tasks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDAO: TaskDAO
) : TaskRepository {
    override suspend fun upsertTask(task: Task) {
        taskDAO.upsertTask(task)
    }

    override suspend fun deleteTask(taskId: Int) {
        taskDAO.deleteTaskByTaskId(taskId)
    }

    override suspend fun getTaskById(taskId: Int): Task? {
        return taskDAO.selectTaskByTaskId(taskId)
    }

    override fun getUpcomingTasksBySubject(subjectInt: Int): Flow<List<Task>> {
        return taskDAO.selectTaskBySubjectId(subjectInt)
            .map { tasks -> tasks.filter { !it.isComplete }}
            .map { tasks -> sortTasks(tasks) }
    }

    override fun getCompletedTasksForSubject(subjectInt: Int): Flow<List<Task>> {
        return taskDAO.selectTaskBySubjectId(subjectInt)
            .map { tasks -> tasks.filter { it.isComplete }}
            .map { tasks -> sortTasks(tasks) }
    }

    override fun getAllUpcomingTasks(): Flow<List<Task>> {
        return taskDAO.getAllTasks().map { tasks->tasks.filter { !it.isComplete } }
            .map { tasks->sortTasks(tasks) }
    }

    private fun sortTasks(tasks:List<Task>):List<Task>{
        return tasks.sortedWith(compareBy<Task>{it.dueDate}.thenByDescending { it.priority})
    }

}