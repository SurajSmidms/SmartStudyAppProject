package com.example.smartstudyappproject1.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.smartstudyappproject1.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDAO {

    @Upsert
    suspend fun upsertTask(task:Task)

    @Query("DELETE FROM Task where taskId=:taskId")
    suspend fun deleteTaskByTaskId(taskId:Int)

    @Query("DELETE FROM Task where taskSubjectId=:subjectId")
    suspend fun deleteTaskBySubjectId(subjectId:Int)

    @Query("SELECT * FROM Task where taskId=:taskId")
    suspend fun selectTaskByTaskId(taskId:Int):Task?

    @Query("SELECT * FROM Task where taskSubjectId=:subjectId")
    fun selectTaskBySubjectId(subjectId:Int):Flow<List<Task>>

    @Query("SELECT * FROM Task")
    fun getAllTasks():Flow<List<Task>>


}