package com.example.smartstudyappproject1.repository

import com.example.smartstudyappproject1.data.local.SessionDAO
import com.example.smartstudyappproject1.model.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val sessionDAO: SessionDAO
) : SessionRepository {
    override suspend fun insertSession(session: Session) {
        sessionDAO.insertSession(session)
    }

    override suspend fun deleteSession(session: Session) {
        sessionDAO.deleteSession(session)
    }

    override fun getAllSessions(): Flow<List<Session>> {
        return sessionDAO.getAllSessions().map { sessions->sessions.sortedByDescending { it.date } }
    }

    override fun getRecentFiveSession(): Flow<List<Session>> {
        return sessionDAO.getAllSessions().map { sessions->sessions.sortedByDescending { it.date } }
            .take(count = 5)
    }

    override fun getRecentTenSessionsForSubject(subjectId: Int): Flow<List<Session>> {
        return sessionDAO.selectSessionBySubject(subjectId)
            .map { sessions -> sessions.sortedByDescending { it.date } }
            .take(count = 10)
    }

    override fun getTotalSessionDuration(): Flow<Long> {
        return sessionDAO.getTotalSessionDuration()
    }

    override fun getTotalSessionDurationBySubjectId(subjectId: Int): Flow<Long> {
        return sessionDAO.getTotalSessionDurationBySubjectId(subjectId)
    }


}