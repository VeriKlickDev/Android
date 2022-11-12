package com.ui.activities.twilioVideo.meetingnotificationservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log

class MeetingServiceManager {
    private var mService: MeetingService? = null
    private var mContext: Context? = null
    private var currentState = MeetingServiceManagerState.UNBIND_SERVICE
    private val TAG="meetingsermanager"
    private val connection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to ScreenCapturerService, cast the IBinder and get
            // ScreenCapturerService instance
            Log.d(TAG, "onServiceConnected: started onserviceconnected")
            val binder = service as MeetingService.LocalBinder
            if (binder==null)
            {
                Log.d(TAG, "onServiceConnected: binder null")
            }else
            {
                Log.d(TAG, "onServiceConnected: binder not null")
            }
            mService = binder.getService()

            if (mService==null)
            {
                Log.d(TAG, "onServiceConnected:service service null con")
            }else
            {
                Log.d(TAG, "onServiceConnected:service service not null con")
            }

            currentState = MeetingServiceManagerState.BIND_SERVICE
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d(TAG, "onServiceConnected: disconnected")
        }
    }

    fun meetingManager(context: Context) {
        Log.d(TAG, "meeting manager called")
        mContext = context
        bindService()
    }

    private fun bindService() {
        if(mContext==null)
        {
            Log.d(TAG, "bindService: context null")
        }else
        {
            Log.d(TAG, "bindService: context not null")
        }

        val intent1 = Intent(mContext, MeetingService::class.java)
        Log.d(TAG, "onServiceConnected: bind method")
        mContext!!.bindService(intent1, connection, Context.BIND_AUTO_CREATE)
    }

    fun startForeground() {
        Log.d(TAG, "onServiceConnected: startforground")
        if (mService==null)
        {
            Log.d(TAG, "onServiceConnected:service is null ob")
        }

        mService?.let {
            it.startForeground()
            Log.d(TAG, "onServiceConnected: started forground")
        }
        currentState = MeetingServiceManagerState.START_FOREGROUND
    }

    fun getServiceState()=currentState

    fun endForeground() {
        mService?.let {
         it.endForeground()
        }
        currentState = MeetingServiceManagerState.END_FOREGROUND
    }

    fun unbindService() {

        mContext?.let {
            it.unbindService(connection)
        }
        currentState = MeetingServiceManagerState.UNBIND_SERVICE
    }

}

enum class MeetingServiceManagerState {
    BIND_SERVICE, START_FOREGROUND, END_FOREGROUND, UNBIND_SERVICE
}