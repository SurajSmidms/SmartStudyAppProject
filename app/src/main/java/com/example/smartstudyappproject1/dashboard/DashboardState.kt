package com.example.smartstudyappproject1.dashboard

import androidx.compose.ui.graphics.Color
import com.example.smartstudyappproject1.model.Session
import com.example.smartstudyappproject1.model.Subject

data class DashboardState(
    val totalSubjectCount:Int = 0,
    val totalStudiedHours: Long = 0L,
    val totalGoalStudyHours:Float = 0f,
    val subjects:List<Subject> = emptyList(),
    val subjectName:String = "",
    val goalStudyHours:String="",
    var subjectCardColors:List<Color> = Subject.subjectCardColor.random(),
    val session:Session?=null
)