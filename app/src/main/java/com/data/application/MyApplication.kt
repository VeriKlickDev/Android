package com.data.application

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telecom.Call
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.data.dataHolders.CallStatusHolder
import com.data.dataHolders.DataStoreHelper
import com.data.dataHolders.WeeksDataHolder
import com.data.helpers.TwilioHelper
import com.domain.constant.AppConstants
//import com.data.twiliochattemp.ChatClientWrapper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication :Application() {
    override fun onCreate() {
        super.onCreate()
        DataStoreHelper.getInstance(this)
        WeeksDataHolder.setCalendarInstance()
        //ChatClientWrapper.createInstance()


        LocalBroadcastManager.getInstance(this).registerReceiver(
            incomingCallRecevier,
            IntentFilter(AppConstants.IN_COMING_CALL_ACTION)
        )

    }
    private val TAG="appClassCheck"
    val incomingCallRecevier=object: BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            Log.d(TAG, "onReceive: call recieved or attented action  ${p1?.getStringExtra(AppConstants.IN_COMING_CALL_ACTION)}")

            when(p1?.getStringExtra(AppConstants.IN_COMING_CALL_ACTION))
            {
                AppConstants.IN_COMING_CALL_ACTION_RINGING->{

                }
                AppConstants.IN_COMING_CALL_ACTION_ATTENDED->{
                    CallStatusHolder.setCallInprogressStatus(true)
                    TwilioHelper.disConnectRoom()
                }
                AppConstants.IN_COMING_CALL_ACTION_IDL_ENDED->{
                    CallStatusHolder.setCallInprogressStatus(false)
                }
            }
            Log.d(TAG, "onReceive: call recieved or attented ${p1?.getStringExtra(AppConstants.IN_COMING_CALL_ACTION)}")
        }
    }


}