package com.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.domain.constant.AppConstants

class ScreenLockEventDetector : BroadcastReceiver() {
    private val TAG="lockeventdetect"
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.getAction().equals(Intent.ACTION_USER_PRESENT)){
            Log.d(TAG, "onReceive: unlock")
            val intent1=Intent()
            intent1.putExtra(AppConstants.SCREEN_LOCK_ACTION,true)
            intent1.setAction(AppConstants.SCREEN_LOCK_ACTION)
            LocalBroadcastManager.getInstance(context!!).sendBroadcast(Intent(intent1))
        }else if (intent?.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            Log.d(TAG, "Phone locked");
            val intent2=Intent()
            intent2.putExtra(AppConstants.SCREEN_LOCK_ACTION,false)
            intent2.setAction(AppConstants.SCREEN_LOCK_ACTION)
            LocalBroadcastManager.getInstance(context!!).sendBroadcast(Intent(intent2))
        }
    }
}