package com.example.smartstudyappproject1

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import com.example.smartstudyappproject1.destinations.SessionScreenRouteDestination
import com.example.smartstudyappproject1.model.Session
import com.example.smartstudyappproject1.model.Subject
import com.example.smartstudyappproject1.model.Task
import com.example.smartstudyappproject1.session.StudySessionTimerService
import com.example.smartstudyappproject1.ui.theme.SmartStudyAppProject1Theme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var isBound by mutableStateOf(false)
    private lateinit var timerService : StudySessionTimerService
    private val connection = object : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as StudySessionTimerService.StudySessionTimeBinder
            timerService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }

    }

    override fun onStart(){
        super.onStart()
        Intent(this,StudySessionTimerService::class.java).also { intent ->
            bindService(intent,connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            if(isBound) {
                SmartStudyAppProject1Theme {
                    DestinationsNavHost(
                        navGraph = NavGraphs.root,
                        dependenciesContainerBuilder = {
                            dependency(SessionScreenRouteDestination){
                                timerService
                            }
                        }
                    )
                }
            }
        }
        requestPermission()
    }

    private fun requestPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
    }
    override fun onStop(){
        super.onStop()
        unbindService(connection)
        isBound = false
    }
}


val subjects = listOf(
    Subject(
        name = "English", goalHours = 10f, colors = Subject.subjectCardColor[0].map { it.toArgb() },
        subjectId = 0
    ),
    Subject(
        name = "Physics", goalHours = 10f, colors = Subject.subjectCardColor[1].map { it.toArgb() },
        subjectId = 1
    ),
    Subject(
        name = "Maths", goalHours = 10f, colors = Subject.subjectCardColor[2].map { it.toArgb() },
        subjectId = 2
    ),
    Subject(
        name = "Geography", goalHours = 10f, colors = Subject.subjectCardColor[3].map { it.toArgb() },
        subjectId = 3
    ),
    Subject(
        name = "Fine Arts", goalHours = 10f, colors = Subject.subjectCardColor[4].map { it.toArgb() },
        subjectId = 4
    )
)
val tasks = listOf(
    Task(
        title = "Prepare notes",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedToSubject = "",
        isComplete = false,
        taskSubjectId = 1,
        taskId = 1
    ),
    Task(
        title = "Do Homework",
        description = "",
        dueDate = 0L,
        priority = 2,
        relatedToSubject = "",
        isComplete = false,
        taskSubjectId = 2,
        taskId = 2
    ),
    Task(
        title = "Go Coaching",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedToSubject = "",
        isComplete = false,
        taskSubjectId = 3,
        taskId = 3
    ),
    Task(
        title = "Assignment",
        description = "",
        dueDate = 0L,
        priority = 2,
        relatedToSubject = "",
        isComplete = false,
        taskSubjectId = 4,
        taskId = 4
    ),
    Task(
        title = "Write Poem",
        description = "",
        dueDate = 0L,
        priority = 0,
        relatedToSubject = "",
        isComplete = true,
        taskSubjectId = 0,
        taskId = 5
    )
)
val sessionList = listOf(
    Session(relatedToSubject = "English",
        date=0L,
        duration = 2,
        sessionSubjectId = 0,
        sessionId = 0
    ),
    Session(relatedToSubject = "Physics",
        date=0L,
        duration = 3,
        sessionSubjectId = 1,
        sessionId = 1
    ),
    Session(relatedToSubject = "Maths",
        date=0L,
        duration = 2,
        sessionSubjectId = 2,
        sessionId = 2
    ),
    Session(relatedToSubject = "Geography",
        date=0L,
        duration = 2,
        sessionSubjectId = 3,
        sessionId = 3
    ),
    Session(relatedToSubject = "Fine Arts",
        date=0L,
        duration = 2,
        sessionSubjectId = 4,
        sessionId = 4
    ),
)