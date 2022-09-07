package com.ui.activities.twilioVideo.ScreenSharingCapturing

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log

class ScreenShareCapturerManager {
    private var mService: ScreenSharingService? = null
    private var mContext: Context? = null
    private var currentState =ScreenCapturerManagerState.UNBIND_SERVICE

    private val connection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to ScreenCapturerService, cast the IBinder and get
            // ScreenCapturerService instance
            Log.d("checkService", "onServiceConnected: started")
            val binder = service as ScreenSharingService.LocalBinder
            mService = binder.getService()
            currentState = ScreenCapturerManagerState.BIND_SERVICE
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d("checkService", "onServiceConnected: started")

        }
    }

    fun ScreenCapturerManager(context: Context) {
        mContext = context
        bindService()
    }

    private fun bindService() {
        if(mContext==null)
        {
            Log.d("checkService", "bindService: context null")
        }else
        {
            Log.d("checkService", "bindService: context not null")
        }

        val intent = Intent(mContext, ScreenSharingService::class.java)
        Log.d("checkService", "onServiceConnected: bind method")
        mContext!!.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun startForeground() {
        Log.d("checkService", "onServiceConnected: startforground")
        mService?.let {
            it.startForeground()
            Log.d("checkService", "onServiceConnected: started forground")
        }
        currentState = ScreenCapturerManagerState.START_FOREGROUND
    }

    fun endForeground() {
        mService?.let {
         it.endForeground()
        }
        currentState = ScreenCapturerManagerState.END_FOREGROUND
    }

    fun unbindService() {

        mContext?.let {
            it.unbindService(connection)
        }
        currentState = ScreenCapturerManagerState.UNBIND_SERVICE
    }
    


}

enum class ScreenCapturerManagerState {
    BIND_SERVICE, START_FOREGROUND, END_FOREGROUND, UNBIND_SERVICE
}