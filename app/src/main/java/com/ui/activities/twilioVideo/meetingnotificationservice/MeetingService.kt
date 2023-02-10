package com.ui.activities.twilioVideo.meetingnotificationservice

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViews.RemoteView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompatExtras
import androidx.core.content.ContextCompat
import com.data.helpers.TwilioHelper
import com.domain.constant.AppConstants
import com.veriKlick.R
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

    override fun onDestroy() {
        println("destroyed service called")
        TwilioHelper.disConnectRoom()
        super.onDestroy()
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

    @SuppressLint("RemoteViewLayout")
    fun startForeground() {
        Log.d(TAG, "startForeground: in service class started forground ")
        val chan = NotificationChannel(
            AppConstants.MEETING_CHANNEL_ID,
            AppConstants.MEETING_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )

        val videoScreenIntent=Intent(applicationContext,VideoActivity::class.java)
        videoScreenIntent?.setAction(Intent.ACTION_MAIN)
        videoScreenIntent?.setFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP
                    or Intent.FLAG_ACTIVITY_SINGLE_TOP
        )


        val pendingIntent=PendingIntent.getActivity(applicationContext,10110,videoScreenIntent,PendingIntent.FLAG_MUTABLE)


        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationId = System.currentTimeMillis().toInt()

        //   val notificationBuilder: Notification.Builder = Notification.Builder(this@MeetingService, AppConstants.MEETING_CHANNEL_ID)

        val notificationCompat=NotificationCompat.Builder(applicationContext, AppConstants.MEETING_CHANNEL_ID)


        val notificationLayout = RemoteViews(packageName, R.layout.layout_custom_notification)
        val notificationLayoutExpanded = RemoteViews(packageName,  R.layout.layout_custom_notification)


        val notification: Notification = notificationCompat
            .setSmallIcon(R.drawable.ic_videocam_24_blue)
            .setContentTitle("Veriklick Meeting is Running")
            .setColor(ContextCompat.getColor(this, R.color.skyblue_light1))
            //.setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            //.setContent(notificationLayout)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayoutExpanded)
            .build()

            /*.setOngoing(true)
            .setSmallIcon(R.drawable.ic_videocam_24_blue)
            .setContentTitle("Veriklick Meeting is Running")
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentIntent(pendingIntent)
            .build()*/


        startForeground(notificationId, notification)

    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        println("onTaskRemoved called")
        TwilioHelper.disConnectRoom()
        super.onTaskRemoved(rootIntent)
        //do something you want
        //stop service
        this.stopSelf()
    }

}