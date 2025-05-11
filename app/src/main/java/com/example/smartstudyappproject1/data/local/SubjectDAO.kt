package com.example.smartstudyappproject1.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.smartstudyappproject1.model.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDAO {
    @Upsert
    suspend fun upsertSubject(subject: Subject)

    @Query("SELECT COUNT(*) FROM SUBJECT")
    fun getTotalSubjectCount():Flow<Int>

    @Query("Select SUM(goalHours) from SUBJECT")
    fun getTotalGoalHours():Flow<Float>

    @Query("Select * from Subject where subjectId=:subjectId")
    suspend fun getSubjectById(subjectId:Int):Subject?

    @Query("Delete from subject where subjectId = :subjectId")
    suspend fun deleteSubject(subjectId:Int)

    @Query("SELECT * FROM Subject")
    fun getAllSubject():Flow<List<Subject>>
}