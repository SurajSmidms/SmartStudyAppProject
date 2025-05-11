package com.example.smartstudyappproject1.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.smartstudyappproject1.model.Session
import com.example.smartstudyappproject1.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDAO {

    @Insert
    suspend fun insertSession(session:Session)

    @Delete
    suspend fun deleteSession(session:Session)

    @Query("SELECT * FROM Session")
    fun getAllSessions():Flow<List<Session>>

    @Query("SELECT * FROM Session where sessionSubjectId=:subjectId")
    fun selectSessionBySubject(subjectId:Int):Flow<List<Session>>

    @Query("SELECT SUM(duration) FROM Session")
    fun getTotalSessionDuration():Flow<Long>

    @Query("SELECT SUM(duration) from Session where sessionSubjectId=:subjectId")
    fun getTotalSessionDurationBySubjectId(subjectId: Int):Flow<Long>

    @Query("DELETE FROM Session Where sessionSubjectId=:subjectId")
    fun deleteSessionBySubjectId(subjectId: Int)

}