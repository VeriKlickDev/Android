package com.data

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.domain.constant.AppConstants
import com.example.twillioproject.R
import com.ui.activities.twilioVideo.VideoActivity

class MeetingService : Service() {
    private val TAG="checkServiceMeetingService"
    // Binder given to clients
    private  var binder: IBinder= LocalBinder()

  inner class LocalBinder : Binder() {
       fun getService(): MeetingService? {
           Log.d(TAG, "startForeground service: getservice method")
           // Return this instance of ScreenCapturerService so clients can call public methods
           return this@MeetingService
       }
    }

     override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
         Log.d(TAG, "onStartCommand: ")
         return START_NOT_STICKY
     }

     override fun onBind(intent: Intent?): IBinder? {
         Log.d(TAG, "onBind: ")
        return binder
    }

    fun endForeground() {
        stopForeground(true)
    }

    override fun onCreate() {
        Log.d(TAG, "startForeground: service created")
        super.onCreate()
    }

    fun startForeground() {
        Log.d(TAG, "startForeground: in service class started forground ")
        val chan = NotificationChannel(
            AppConstants.MEETING_CHANNEL_ID,
            AppConstants.MEETING_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )

        val videoScreenIntent=Intent(applicationContext,VideoActivity::class.java)

        val pendingIntent=PendingIntent.getActivity(applicationContext,10110,videoScreenIntent,PendingIntent.FLAG_MUTABLE)


        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationId = System.currentTimeMillis().toInt()
        val notificationBuilder: Notification.Builder = Notification.Builder(this@MeetingService, AppConstants.MEETING_CHANNEL_ID)

        val notification: Notification = notificationBuilder
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_videocam_24_blue)
            .setContentTitle("Veriklick Meeting is Running")
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(notificationId, notification)
    }
}