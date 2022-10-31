package com.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.data.dataHolders.CurrentMeetingDataSaver

class MeetingServiceManager {
    private var mService: MeetingService? = null
    private var mContext: Context? = null
    private var currentState =MeetingServiceManagerState.UNBIND_SERVICE

    private val connection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to ScreenCapturerService, cast the IBinder and get
            // ScreenCapturerService instance
            Log.d("checkService", "onServiceConnected: started")
            val binder = service as MeetingService.LocalBinder
            mService = binder.getService()
            currentState = MeetingServiceManagerState.BIND_SERVICE
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d("checkService", "onServiceConnected: started")

        }
    }

    fun meetingManager(context: Context) {
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

        val intent = Intent(mContext, MeetingServiceManager::class.java)
        Log.d("checkService", "onServiceConnected: bind method")
        mContext!!.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun startForeground() {
        Log.d("checkService", "onServiceConnected: startforground")
        mService?.let {
            it.startForeground()
            Log.d("checkService", "onServiceConnected: started forground")
        }
        currentState = MeetingServiceManagerState.START_FOREGROUND
    }

    fun getServiceState()=currentState

    //expanded silver magma grey
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