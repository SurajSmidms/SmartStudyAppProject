package com.example.smartstudyappproject1.session

import com.example.smartstudyappproject1.model.Session
import com.example.smartstudyappproject1.model.Subject

data class SessionState(
    val subjects:List<Subject> = emptyList(),
    val sessions:List<Session> = emptyList(),
    val relatedSubject:String? = null,
    val subjectId:Int? = null,
    val session:Session? = null
)