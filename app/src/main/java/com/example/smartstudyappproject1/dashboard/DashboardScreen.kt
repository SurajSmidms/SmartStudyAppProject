package com.example.smartstudyappproject1.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartstudyappproject1.util.AddSubjectDialog
import com.example.smartstudyappproject1.util.CountCard
import com.example.smartstudyappproject1.util.DeleteDialog
import com.example.smartstudyappproject1.R
import com.example.smartstudyappproject1.Subject.SubjectScreenNavArgs
import com.example.smartstudyappproject1.util.SubjectCard
import com.example.smartstudyappproject1.destinations.SessionScreenRouteDestination
import com.example.smartstudyappproject1.destinations.SubjectScreenRouteDestination
import com.example.smartstudyappproject1.destinations.TaskScreenRouteDestination
import com.example.smartstudyappproject1.model.Session
import com.example.smartstudyappproject1.model.Subject
import com.example.smartstudyappproject1.model.Task
import com.example.smartstudyappproject1.sessionList
import com.example.smartstudyappproject1.util.studySessionsList
import com.example.smartstudyappproject1.subjects
import com.example.smartstudyappproject1.task.TaskScreenNavArgs
import com.example.smartstudyappproject1.util.taskList
import com.example.smartstudyappproject1.tasks
import com.example.smartstudyappproject1.util.SnackbarEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@RootNavGraph(start = true)
@Destination
@Composable
fun DashboardScreenRoute(
    navigator:DestinationsNavigator
){
    val viewModel:DashboardViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val recentSessions by viewModel.recentSessions.collectAsStateWithLifecycle()
    DashboardScreen(
        state = state,
        tasks=tasks,
        recentSessions = recentSessions,
        onEvent = viewModel::onEvent,
        snackbarEvent = viewModel.snackbarEventFlow,
        onSubjectCardClick = {
            subjectId->subjectId?.let {
                val navArg = SubjectScreenNavArgs(subjectId = subjectId)
                navigator.navigate(SubjectScreenRouteDestination(navArgs=navArg))
            }
        },
        onTaskCardClick = { taskId->
            val navArg = TaskScreenNavArgs(taskId = taskId, subjectId = null)
            navigator.navigate(TaskScreenRouteDestination(navArgs=navArg))
        },
        onStartSessionButtonClick = {
            navigator.navigate(SessionScreenRouteDestination())
        }
    )
}

@Composable
fun DashboardScreen(
    state: DashboardState,
    tasks:List<Task>,
    recentSessions:List<Session>,
    onEvent:(DashboardEvent)->Unit,
    snackbarEvent: SharedFlow<SnackbarEvent>,
    onSubjectCardClick:(Int?)->Unit,
    onTaskCardClick:(Int?)->Unit,
    onStartSessionButtonClick:()->Unit
){
    var isAddSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isDeleteDialogOpen by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        snackbarEvent.collectLatest{event->
            when(event){
                is SnackbarEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )
                }

                SnackbarEvent.NavigateUp -> {}
            }
        }
    }

    AddSubjectDialog(
        isOpen = isAddSubjectDialogOpen,
        onDismissRequest = { isAddSubjectDialogOpen = false },
        onConfirmButtonClick = {
            onEvent(DashboardEvent.SaveSubject)
            isAddSubjectDialogOpen = false },
        selectedColors = state.subjectCardColors,
        onColorChange = { onEvent(DashboardEvent.onSubjectCardColorChange(it)) },
        subjectName = state.subjectName,
        goalHours = state.goalStudyHours,
        onSubjectNameChange = { onEvent(DashboardEvent.onSubjectNameChange(it)) },
        onGoalHoursChange = { onEvent(DashboardEvent.onGoalStudyHoursChange(it)) }
    )
    DeleteDialog(
        isOpen = isDeleteDialogOpen,
        title = "Delete Session",
        bodyText = "Are you sure, you want to delete the session?",
        onDismissRequest = {isDeleteDialogOpen = false},
        onConfirmButtonClick = {
            onEvent(DashboardEvent.DeleteSession)
            isDeleteDialogOpen=false}
    )
    Scaffold (
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = { DashboardScreenTopBar() }
    ){
        paddingValues ->
        LazyColumn (modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)){
            item {
                CountCardSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    subjectCount = state.totalSubjectCount,
                    studiedHours = state.totalStudiedHours.toString(),
                    goalHours = state.totalGoalStudyHours.toString()
                )
            }
            item {
                SubjectCardsSection(
                    modifier = Modifier.fillMaxWidth(),
                    subjectList = state.subjects,
                    onAddIconClicked = {
                        isAddSubjectDialogOpen = true
                    },
                    onSubjectCardClick = onSubjectCardClick
                )
            }
            item {
                Button(
                    onClick = onStartSessionButtonClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp, vertical = 20.dp)
                ) {
                    Text(text="Start Study Session")
                }
            }
            taskList(
                sectionTitle = "UPCOMING TASKS",
                emptyListText = "You don't have any upcoming tasks.\n " +
                        "Click the + button in subject screen to add new task.",
                tasks = tasks,
                onTaskCardClick = onTaskCardClick,
                onCheckBoxClickAction = { onEvent(DashboardEvent.onTaskIsCompleteChange(it)) }
            )
            studySessionsList(
                sectionTitle = "RECENT STUDY SESSIONS",
                emptyListText = "You don't have any recent sessions.\n " +
                        "Start a study session to begin recording your progress.",
                sessions = recentSessions,
                onDeleteIconClick = {
                    onEvent(DashboardEvent.onDeleteSessionButtonClick(it))
                    isDeleteDialogOpen = true},
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreenTopBar(){
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "StudySmart",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    )
}

@Composable
fun CountCardSection(
    modifier: Modifier,
    subjectCount:Int,
    studiedHours:String,
    goalHours:String
){
    Row (modifier=modifier){
        CountCard(headingText = "Subject Count", count = "$subjectCount", modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(headingText = "Studied Hours", count = studiedHours, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(headingText = "Goal Study Hours", count = goalHours, modifier = Modifier.weight(1f))

    }
}

@Composable
fun SubjectCardsSection(
    modifier: Modifier,
    subjectList:List<Subject>,
    emptyListText:String="You don't have any subjects.\n Click the + button to add new subjects.",
    onAddIconClicked:()->Unit,
    onSubjectCardClick:(Int?)->Unit
){
    Column (modifier = modifier){
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) { Text(text = "SUBJECTS", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(12.dp)
        )
            IconButton(onClick = {onAddIconClicked()}) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Subject")
            }
        }
        if(subjectList.isEmpty()){
            Image(
                modifier= Modifier
                    .size(150.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(R.drawable.book),
                contentDescription = emptyListText
            )
            Text(text = emptyListText,
                modifier=Modifier.fillMaxWidth(),
                style=MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
                )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
        ) {
            items(subjectList){
                subject->
                SubjectCard(
                    subjectName = subject.name,
                    gradientColors = subject.colors.map { Color(it) },
                    onClick = {onSubjectCardClick(subject.subjectId)}
                )
            }
        }

    }
}