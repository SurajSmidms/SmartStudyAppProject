package com.example.smartstudyappproject1.repository

import com.example.smartstudyappproject1.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun upsertTask(task: Task)

    suspend fun deleteTask(taskId:Int)

    suspend fun getTaskById(taskId: Int):Task?

    fun getUpcomingTasksBySubject(subjectInt:Int):Flow<List<Task>>

    fun getCompletedTasksForSubject(subjectInt:Int):Flow<List<Task>>

    fun getAllUpcomingTasks():Flow<List<Task>>

}