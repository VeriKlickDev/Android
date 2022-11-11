package com.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.domain.IncomingCallCallback
import com.domain.constant.AppConstants

class InComingCallDetector() : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val state = p1?.getStringExtra(TelephonyManager.EXTRA_STATE)

        if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){

        }
        if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))){
            p0?.let { LocalBroadcastManager.getInstance(it).sendBroadcast(Intent(AppConstants.IN_COMING_CALL_ACTION)) }
        }
        if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
            p0?.let { LocalBroadcastManager.getInstance(it).sendBroadcast(Intent(AppConstants.IN_COMING_CALL_ACTION)) }
        }
        Log.d("callstatusc", "onReceive: call state ${TelephonyManager.EXTRA_STATE_IDLE}")

    }
}