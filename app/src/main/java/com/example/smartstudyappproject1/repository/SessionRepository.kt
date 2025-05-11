package com.example.smartstudyappproject1.repository

import com.example.smartstudyappproject1.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    suspend fun insertSession(session:Session)

    suspend fun deleteSession(session: Session)

    fun getAllSessions():Flow<List<Session>>

    fun getRecentFiveSession(): Flow<List<Session>>

    fun getRecentTenSessionsForSubject(subjectId: Int): Flow<List<Session>>

    fun getTotalSessionDuration():Flow<Long>

    fun getTotalSessionDurationBySubjectId(subjectId:Int):Flow<Long>

}