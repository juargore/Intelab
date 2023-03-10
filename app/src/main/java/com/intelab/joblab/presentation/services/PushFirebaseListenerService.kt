package com.intelab.joblab.presentation.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.intelab.joblab.BuildConfig
import com.intelab.joblab.R
import com.intelab.joblab.presentation.base.utils._notificationAction
import com.intelab.joblab.presentation.base.utils._notificationMessage
import com.intelab.joblab.presentation.base.utils._notificationTitle
import com.intelab.joblab.presentation.ui.home.activity.HomeActivity

class PushFirebaseListenerService : FirebaseMessagingService() {

    companion object { var action = "" }

    @Suppress("RedundantOverride")
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val title = remoteMessage.data[_notificationTitle]
        val message = remoteMessage.data[_notificationMessage]
        val action = remoteMessage.data[_notificationAction]

        NotificationBuilder(applicationContext).setUpNotification(title, message, action)
    }
}

class NotificationBuilder(val context: Context) {
    fun setUpNotification(title: String?, message: String?, action: String?) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "intelab_channel_01"
        val channelDesc = "This is Intelab Channel"
        val channelName = "intelab_channel"
        val importance = NotificationManager.IMPORTANCE_HIGH

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(channelId, channelName, importance).apply {
                this.description = channelDesc
                this.enableLights(true)
                this.lightColor = Color.RED
                this.setShowBadge(false)
            }

            notificationManager.createNotificationChannel(mChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle())
            .setSmallIcon(if(BuildConfig.DEBUG) R.drawable.joblab_notif_qa else R.drawable.joblab_notif_prod)
            .setAutoCancel(true)

        val notificationIntent = Intent(context, HomeActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        PushFirebaseListenerService.action = action ?: ""

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        notificationBuilder.setContentIntent(pendingIntent)
        notificationManager.notify(0, notificationBuilder.build())
    }
}