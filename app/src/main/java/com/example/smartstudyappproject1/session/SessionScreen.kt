package com.example.smartstudyappproject1.session

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartstudyappproject1.util.DeleteDialog
import com.example.smartstudyappproject1.util.SubjectListBottomSheet
import com.example.smartstudyappproject1.sessionList
import com.example.smartstudyappproject1.util.studySessionsList
import com.example.smartstudyappproject1.subjects
import com.example.smartstudyappproject1.ui.theme.Red
import com.example.smartstudyappproject1.util.Constants
import com.example.smartstudyappproject1.util.SnackbarEvent
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.sql.Time
import kotlin.time.DurationUnit

@Destination(
    deepLinks = [
        DeepLink(action = Intent.ACTION_VIEW,
            uriPattern = "study_smart://dashboard/session")
    ]
)
@Composable
fun SessionScreenRoute(
    navigator: DestinationsNavigator,
    timerService: StudySessionTimerService
){
    val viewModel: SessionViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    SessionScreen(
        state= state,
        snackbarEvent = viewModel.snackbarEventFlow,
        onEvent = viewModel::onEvent,
        onBackButtonClick = {navigator.navigateUp()},
        timerService=timerService
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    state:SessionState,
    snackbarEvent: SharedFlow<SnackbarEvent>,
    onEvent:(SessionEvent)->Unit,
    onBackButtonClick: () -> Unit,
    timerService: StudySessionTimerService
){
    val hours by timerService.hours
    val minutes by timerService.minutes
    val seconds by timerService.seconds
    var currentTimeState by timerService.currentTimeState

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var sheetState = rememberModalBottomSheetState()
    var isBottomSheetOpen by remember { mutableStateOf(false) }
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

    LaunchedEffect(key1 = state.subjects) {
        val subjectId = timerService.subjectId.value
        onEvent(SessionEvent.UpdateSubjectIdAndRelatedSubject(
            subjectId = subjectId,
            relatedToSubject = state.subjects.find{it.subjectId == subjectId}?.name
        ))
    }

    SubjectListBottomSheet(
        sheetState = sheetState,
        isOpen = isBottomSheetOpen,
        subjects = state.subjects,
        onDismissRequest = {isBottomSheetOpen = false},
        onSubjectClicked = {subject->
            scope.launch {
                sheetState.hide()
            }.invokeOnCompletion {
                if (!sheetState.isVisible) isBottomSheetOpen = false
            }
            onEvent(SessionEvent.OnRelatedSubjectChange(subject))
        }
    )
    DeleteDialog(
        isOpen = isDeleteDialogOpen,
        title = "Delete Task",
        bodyText = "Are you sure, you want to delete the session?",
        onDismissRequest = {isDeleteDialogOpen = false},
        onConfirmButtonClick = {
            onEvent(SessionEvent.DeleteSession)
            isDeleteDialogOpen=false}
    )
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = { SessionScreenTopBar(onBackButton = onBackButtonClick)}
    ) {paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                TimerSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds
                )
            }
            item {
                RelatedToSubjectSection(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                    relatedToSubject = state.relatedSubject?:"",
                    selectSubjectButtonClick = {isBottomSheetOpen = true},
                    seconds = seconds
                )
            }
            item {
                ButtonSession(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    startButtonClick = {
                        if(state.subjectId!=null && state.relatedSubject!=null) {
                            SessionServiceHelper.triggerForegroundService(
                                context = context,
                                action = if (currentTimeState == TimerState.STARTED) {
                                    Constants.ACTION_SERVICE_STOP
                                } else {
                                    Constants.ACTION_SERVICE_START
                                }
                            )
                            timerService.subjectId.value = state.subjectId
                        }else{
                            onEvent(SessionEvent.NotifyToUpdateSubject)
                        }
                    },
                    cancelButtonClick = {
                        SessionServiceHelper.triggerForegroundService(
                            context = context,
                            action = Constants.ACTION_SERVICE_CANCEL
                        )
                    },
                    finishButtonClick = {
                        val duration = timerService.duration.toLong(DurationUnit.SECONDS)
                        if(duration>-36){
                            SessionServiceHelper.triggerForegroundService(
                                context = context,
                                action = Constants.ACTION_SERVICE_CANCEL
                            )
                        }
                        onEvent(SessionEvent.SaveSession(duration))
                    },
                    timerState = currentTimeState,
                    seconds = seconds
                )
            }
            studySessionsList(
                sectionTitle = "STUDY SESSIONS HISTORY",
                emptyListText = "You don't have any recent sessions.\n " +
                        "Start a study session to begin recording your progress.",
                sessions = state.sessions,
                onDeleteIconClick = {session->
                    isDeleteDialogOpen = true
                    onEvent(SessionEvent.OnDeleteSessionButtonClick(session))
                },
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreenTopBar(
    onBackButton:()->Unit
){
    TopAppBar(navigationIcon = {
        IconButton(onClick = onBackButton) {
            Icon(imageVector = Icons.Default.ArrowBack,
                contentDescription = "Navigate to Back Screen")
        }
    },
        title = {
            Text(text = "Study Sessions", style = MaterialTheme.typography.headlineSmall)
        }
    )
}

@Composable
fun TimerSection(
    modifier: Modifier,
    hours:String,
    minutes:String,
    seconds:String
){
    Box(
        modifier=modifier, contentAlignment = Alignment.Center
    ){
        Box(modifier = Modifier
            .size(250.dp)
            .border(5.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape))
        Row {
            AnimatedContent(targetState = hours,
                label=hours,
                transitionSpec = { timerTextAnimation() }
                ) { hours->
                Text(
                    text = "$hours:",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                )
            }
            AnimatedContent(targetState = minutes,
                label=minutes,
                transitionSpec = { timerTextAnimation() }
            ) { minutes->
                Text(
                    text = "$minutes:",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                )
            }
            AnimatedContent(targetState = seconds,
                label=seconds,
                transitionSpec = { timerTextAnimation() }
            ) { seconds->
                Text(
                    text = seconds,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                )
            }
        }

    }
}

@Composable
fun RelatedToSubjectSection(
    modifier: Modifier,
    relatedToSubject:String,
    selectSubjectButtonClick:()->Unit,
    seconds: String
){
    Column(modifier=modifier) {
        Text(
            text = "Related to subject",
            style = MaterialTheme.typography.bodySmall
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = relatedToSubject,
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(
                onClick = selectSubjectButtonClick,
                enabled = seconds == "00"
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Subject"
                )
            }
        }
    }
}

@Composable
fun ButtonSession(
    modifier: Modifier,
    startButtonClick: () -> Unit,
    cancelButtonClick: () -> Unit,
    finishButtonClick: () -> Unit,
    timerState: TimerState,
    seconds: String
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Cancel Button
        Button(
            onClick = cancelButtonClick,
            enabled = seconds != "00" && timerState != TimerState.STARTED,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Cancel",
                modifier = Modifier.padding(vertical = 6.dp) // removed horizontal padding
            )
        }

        // Start/Stop/Resume Button
        Button(
            onClick = startButtonClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (timerState == TimerState.STARTED) Red
                else MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = when (timerState) {
                    TimerState.STARTED -> "Stop"
                    TimerState.STOP -> "Resume"
                    else -> "Start"
                },
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }

        // Finish Button
        Button(
            onClick = finishButtonClick,
            enabled = seconds != "00" && timerState != TimerState.STARTED,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Finish",
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }
    }
}


private fun timerTextAnimation(duration:Int = 600):ContentTransform{
    return slideInVertically ( animationSpec = tween(duration))
    {fullHeight -> fullHeight }+ fadeIn(animationSpec = tween(duration))togetherWith
            slideOutVertically (animationSpec = tween(duration))
            {fullHeight -> -fullHeight }+ fadeOut(animationSpec = tween(duration))
}