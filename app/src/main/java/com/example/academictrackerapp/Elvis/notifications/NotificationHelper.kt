package com.example.academictrackerapp.elvis.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.academictrackerapp.R
import java.util.UUID

class NotificationHelper(val context: Context) {

    companion object {
        private const val CHANNEL_ID = "REMINDER_NOTIFICATIONS"
        private const val CHANNEL_NAME = "Reminder Notifications"
        private const val CHANNEL_DESCRIPTION = "Displays user reminders"
    }

    @SuppressLint("MissingPermission")
    fun createNotification(title: String, message: String) {
        createNotificationChannel()

        val intent = Intent(context, NotificationsFragment::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val icon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_notifications_black_24dp)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_art_track_24)
            .setLargeIcon(icon)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(UUID.randomUUID().hashCode(), notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
