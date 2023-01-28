package com.example.todolistproject.ui

import com.example.todolistproject.R
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todolistproject.ui.utils.Constants.CHANNEL_ID
import com.example.todolistproject.ui.utils.Constants.EXTRA_TODO_DESCRIPTION
import com.example.todolistproject.ui.utils.Constants.NOTIFICATION_GROUP_KEY
import java.io.File


class TodoAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val todoDescription = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.extras?.getString(EXTRA_TODO_DESCRIPTION, "Default Value")
        } else {
            intent.getStringExtra(EXTRA_TODO_DESCRIPTION)
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Reminder!")
            .setContentText(todoDescription)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(0, builder)
        }
        playNotificationSound(context)
    }

    private fun playNotificationSound(context: Context) {
        try {
            val defaultSoundUri = getSoundUri(context)
            val r = RingtoneManager.getRingtone(context, defaultSoundUri)
            r.play()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun getSoundUri(context: Context) = Uri.parse(
        ContentResolver.SCHEME_ANDROID_RESOURCE
                + File.pathSeparator + File.separator + File.separator
                + context.packageName
                + File.separator
                + R.raw.notification
    )

}