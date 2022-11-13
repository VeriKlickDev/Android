package com.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.domain.constant.AppConstants

class InComingCallDetector() : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {

        val intent=Intent()

        val state = p1?.getStringExtra(TelephonyManager.EXTRA_STATE)

        if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
            intent.putExtra(AppConstants.IN_COMING_CALL_ACTION,AppConstants.IN_COMING_CALL_ACTION_RINGING)
            p0?.let { LocalBroadcastManager.getInstance(it).sendBroadcast(intent.setAction(AppConstants.IN_COMING_CALL_ACTION)) }
        }
        if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))){
            //CALL RECIEVED
            intent.putExtra(AppConstants.IN_COMING_CALL_ACTION,AppConstants.IN_COMING_CALL_ACTION_ATTENDED)
            p0?.let { LocalBroadcastManager.getInstance(it).sendBroadcast(intent.setAction(AppConstants.IN_COMING_CALL_ACTION)) }
        }
        if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
            //ENDED CALL
            intent.putExtra(AppConstants.IN_COMING_CALL_ACTION,AppConstants.IN_COMING_CALL_ACTION_IDL_ENDED)
            p0?.let { LocalBroadcastManager.getInstance(it).sendBroadcast(intent.setAction(AppConstants.IN_COMING_CALL_ACTION)) }
        }
        Log.d("callstatusc", "onReceive: call state ${TelephonyManager.EXTRA_STATE_IDLE}")

    }
}