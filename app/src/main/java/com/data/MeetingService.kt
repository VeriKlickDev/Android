package com.data

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.domain.constant.AppConstants
import com.example.twillioproject.R

class MeetingService : Service() {

    // Binder given to clients
    private  var binder: IBinder= LocalBinder()

  inner class LocalBinder : Binder() {
       fun getService(): MeetingService? {
           // Return this instance of ScreenCapturerService so clients can call public methods
           return this@MeetingService
       }
    }

     override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
         return START_NOT_STICKY
     }

     override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    fun endForeground() {
        stopForeground(true)
    }

    override fun onCreate() {
        Log.d("checkservice", "startForeground: service created")
        super.onCreate()
    }

    fun startForeground() {
        Log.d("checkservice", "startForeground: in service class started forground ")
        val chan = NotificationChannel(
            AppConstants.MEETING_CHANNEL_ID,
            AppConstants.MEETING_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationId = System.currentTimeMillis().toInt()
        val notificationBuilder: Notification.Builder =
            Notification.Builder(this@MeetingService, AppConstants.MEETING_CHANNEL_ID)
        val notification: Notification = notificationBuilder
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_share_screen_white)
            .setContentTitle("Veriklick Meeting is Running")
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        //manager.notify(notificationId,notification)
        startForeground(notificationId, notification)
    }
}