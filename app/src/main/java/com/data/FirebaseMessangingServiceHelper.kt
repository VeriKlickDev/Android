package com.data

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.domain.constant.AppConstants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ui.activities.login.loginwithotp.ActivitiyLoginWithOtp
import com.ui.activities.twilioVideo.VideoActivity
import com.veriKlick.R

class FirebaseMessangingServiceHelper : FirebaseMessagingService() {
    private val TAG = "firebasemssg"
    override fun onMessageReceived(message: RemoteMessage) {

        message
        Log.d(
            TAG,
            "onMessageReceived: on message recieved ${message.data} ${message.rawData} ${message.notification.toString()}  ${message}"
        )
        showNotificaiton(message)

       /* val intent=Intent(this,ActivitiyLoginWithOtp::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)*/

        super.onMessageReceived(message)
    }

/*
    {
    data:{
    title:""
    body:""
    }
    }
  */


    private fun showNotificaiton(msg: RemoteMessage) {
        Log.d(TAG, "startForeground: in service class started forground  ${msg.data}")
        val chan = NotificationChannel(
            AppConstants.FIREBASE_CHANNEL_ID,
            AppConstants.FIREBASE_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )

        val map = msg.data.toMap()
        val title = map.get("title")
        val body = map.get("body")

        /*val videoScreenIntent= Intent(applicationContext, VideoActivity::class.java)
        videoScreenIntent?.setAction(Intent.ACTION_MAIN)
        videoScreenIntent?.setFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP
                    or Intent.FLAG_ACTIVITY_SINGLE_TOP
        )*/

        val i = Intent(android.content.Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.veriKlick"));


        val pendingIntent =
            PendingIntent.getActivity(applicationContext, 10002, i, PendingIntent.FLAG_MUTABLE)

        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)


        //   val notificationBuilder: Notification.Builder = Notification.Builder(this@MeetingService, AppConstants.MEETING_CHANNEL_ID)

        val notificationCompat =
            NotificationCompat.Builder(applicationContext, AppConstants.FIREBASE_CHANNEL_ID)


        // val notificationLayout = RemoteViews(packageName, R.layout.layout_custom_notification)
        // val notificationLayoutExpanded = RemoteViews(packageName,  R.layout.layout_custom_notification)


        val notification: Notification = notificationCompat
            .setSmallIcon(R.drawable.ic_img_message)
            .setContentTitle(title)
            .setContentText(body)
            .setColor(ContextCompat.getColor(this, R.color.skyblue_light1))
            //.setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOngoing(true)
            //.setContent(notificationLayout)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            // .setCustomContentView(notificationLayout)
            // .setCustomBigContentView(notificationLayoutExpanded)
            .build()
        manager.notify(AppConstants.FIREBASE_CHANNEL_NAME, 10002, notification)

    }

    private fun showNotificaiton(msg: String, title: String) {
        Log.d(TAG, "startForeground: in service class started forground ")
        val chan = NotificationChannel(
            AppConstants.FIREBASE_CHANNEL_ID,
            AppConstants.FIREBASE_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )

        val i = Intent(android.content.Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.veriKlick"));

        val pendingIntent =
            PendingIntent.getActivity(applicationContext, 10113, i, PendingIntent.FLAG_MUTABLE)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chan)

        //   val notificationBuilder: Notification.Builder = Notification.Builder(this@MeetingService, AppConstants.MEETING_CHANNEL_ID)

        val notificationCompat =
            NotificationCompat.Builder(applicationContext, AppConstants.FIREBASE_CHANNEL_ID)


        // val notificationLayout = RemoteViews(packageName, R.layout.layout_custom_notification)
        // val notificationLayoutExpanded = RemoteViews(packageName,  R.layout.layout_custom_notification)


        val notification: Notification = notificationCompat
            .setSmallIcon(R.drawable.ic_img_message)
            .setContentTitle(title)
            .setContentText(msg)
            .setSubText(msg)
            //.setColor(ContextCompat.getColor(this, R.color.skyblue_light1))
            //.setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentIntent(pendingIntent)
            .setOngoing(false)
            //.setContent(notificationLayout)
            //.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            // .setCustomContentView(notificationLayout)
            // .setCustomBigContentView(notificationLayoutExpanded)
            .build()


        manager.notify(AppConstants.FIREBASE_CHANNEL_NAME, 10005, notification)

    }


}