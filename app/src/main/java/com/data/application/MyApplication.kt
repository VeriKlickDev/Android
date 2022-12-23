package com.data.application

//import com.data.twiliochattemp.ChatClientWrapper
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.data.dataHolders.CallStatusHolder
import com.data.dataHolders.DataStoreHelper
import com.data.dataHolders.MicMuteUnMuteHolder
import com.data.dataHolders.WeeksDataHolder
import com.data.helpers.TwilioHelper
import com.domain.constant.AppConstants
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MyApplication :Application() {
    override fun onCreate() {
        super.onCreate()
        DataStoreHelper.getInstance(this)
        WeeksDataHolder.setCalendarInstance()
        //ChatClientWrapper.createInstance()

        val mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mFirebaseAnalytics.setUserId("veriklick")

        LocalBroadcastManager.getInstance(this).registerReceiver(
            incomingCallRecevier,
            IntentFilter(AppConstants.IN_COMING_CALL_ACTION)
        )

        LocalBroadcastManager.getInstance(this).registerReceiver(
            screenLockEventReciever,
            IntentFilter(AppConstants.SCREEN_LOCK_ACTION)
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

 val screenLockEventReciever= object : BroadcastReceiver() {
     override fun onReceive(context: Context?, intent: Intent?) {
         if(intent!!.getBooleanExtra(AppConstants.SCREEN_LOCK_ACTION,false))
         {
             Log.d(
                 TAG,
                 "onReceive: application ${
                     intent!!.getBooleanExtra(
                         AppConstants.SCREEN_LOCK_ACTION,
                         false
                     )
                 }"
             )
             MicMuteUnMuteHolder.setVideoHideStatus(true,false)
             MicMuteUnMuteHolder.setLocalAudioMicStatus(true,false)
            // MicMuteUnMuteHolder.setMicStatus(true)
            // MicMuteUnMuteHolder.setVideoStatus(true)
         }else
         {
             MicMuteUnMuteHolder.setVideoHideStatus(false,false)
             MicMuteUnMuteHolder.setLocalAudioMicStatus(false,false)
            // MicMuteUnMuteHolder.setMicStatus(false)
            // MicMuteUnMuteHolder.setVideoStatus(false)
             Log.d(
                 TAG,
                 "onReceive: application ${
                     intent!!.getBooleanExtra(
                         AppConstants.SCREEN_LOCK_ACTION,
                         false
                     )
                 }"
             )
         }
     }
 }



}