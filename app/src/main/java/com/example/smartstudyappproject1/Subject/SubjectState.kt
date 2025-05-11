package com.example.smartstudyappproject1.Subject

import androidx.compose.ui.graphics.Color
import com.example.smartstudyappproject1.model.Session
import com.example.smartstudyappproject1.model.Subject
import com.example.smartstudyappproject1.model.Task

data class SubjectState(
    val currentSubjectId:Int?=null,
    val subjectName:String="",
    val goalStudyHours:String="",
    val subjectCardColors:List<Color> = Subject.subjectCardColor.random(),
    val studiedHours:Float=0f,
    val progress:Float = 0f,
    val recentSessions:List<Session> = emptyList(),
    val upcomingTasks:List<Task> = emptyList(),
    val completedTask:List<Task> = emptyList(),
    val session:Session?=null
)