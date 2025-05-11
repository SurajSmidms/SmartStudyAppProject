package com.example.smartstudyappproject1.session

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.example.smartstudyappproject1.MainActivity
import com.example.smartstudyappproject1.util.Constants

object SessionServiceHelper{
    fun clickPendingIntent(context: Context):PendingIntent{
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "study_smart://dashboard/session".toUri(),
            context,
            MainActivity::class.java
        )
        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(
                Constants.CLICK_REQUEST_CODE,
                PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    fun triggerForegroundService(context: Context,action: String){
        Intent(context,StudySessionTimerService::class.java).apply {
            this.action = action
            context.startService(this)
        }
    }
}