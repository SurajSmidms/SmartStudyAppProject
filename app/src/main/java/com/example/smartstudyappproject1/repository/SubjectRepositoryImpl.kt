package com.example.smartstudyappproject1.repository

import com.example.smartstudyappproject1.data.local.SessionDAO
import com.example.smartstudyappproject1.data.local.SubjectDAO
import com.example.smartstudyappproject1.data.local.TaskDAO
import com.example.smartstudyappproject1.model.Subject
import com.example.smartstudyappproject1.subjects
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val subjectDAO: SubjectDAO,
    private val taskDAO: TaskDAO,
    private val sessionDAO: SessionDAO
):SubjectRepository {
    override suspend fun upsertSubject(subject: Subject) {
        subjectDAO.upsertSubject(subject)
    }

    override fun getTotalSubjectCount(): Flow<Int> {
        return subjectDAO.getTotalSubjectCount()
    }

    override fun getTotalGoalHours(): Flow<Float> {
        return subjectDAO.getTotalGoalHours()
    }

    override suspend fun deleteSubject(subjectId: Int) {
        taskDAO.deleteTaskBySubjectId(subjectId)
        sessionDAO.deleteSessionBySubjectId(subjectId)
        subjectDAO.deleteSubject(subjectId)
    }

    override suspend fun getSubjectById(subjectId: Int): Subject? {
        return subjectDAO.getSubjectById(subjectId)
    }

    override fun getAllSubjects(): Flow<List<Subject>> {
        return subjectDAO.getAllSubject()
    }


}