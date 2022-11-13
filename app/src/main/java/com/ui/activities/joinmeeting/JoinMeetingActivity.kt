package com.ui.activities.joinmeeting

import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.data.*
import com.data.dataHolders.CallStatusHolder
import com.data.dataHolders.CurrentConnectUserList
import com.data.dataHolders.CurrentMeetingDataSaver
import com.data.helpers.TwilioHelper
import com.domain.constant.AppConstants
import com.example.twillioproject.R
import com.example.twillioproject.databinding.ActivityJoinMeetingBinding
import com.harvee.yourhealthmate2.ui.privacypolicy.ActivityPrivacyPolicy
import com.ui.activities.twilioVideo.VideoActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Runnable

@AndroidEntryPoint
class JoinMeetingActivity :AppCompatActivity() {

    private lateinit var binding:ActivityJoinMeetingBinding
    private lateinit var viewModel:JoinMeetingViewModel
    private var accessCode=""
    private  var TAG="videocon"
    private var isCallInProgress=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinMeetingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel=ViewModelProvider(this).get(JoinMeetingViewModel::class.java)

        binding.btnJumpBack.setOnClickListener { onBackPressed() }

        binding.btnJoin.setOnClickListener {
            if (checkInternet()){
                // startActivity(Intent(this,ActivityAddParticipant::class.java))
                if (binding.etJoinMeeting.text.toString() != "")
                {
                    val meetingLink= binding.etJoinMeeting.text.toString()
                    val accessCodeSplit=meetingLink.splitToSequence("/")

                    accessCode= accessCodeSplit.last()
                    Log.d(TAG, "onCreate: accessCode is $accessCode")
                    //host https://ui2.veriklick.in/video-session/EIFfAgSkvLzhZwt9fy4o
                    //interviewer
                    //candidate https://ui2.veriklick.in/video-session/7Oa8Dd6xhwrpKdkgJdRo    412345
                    //5 https://ui2.veriklick.in/video-session/MlH8hjxfv32xguzleXiH
                    //2
                   // getInterviewDetails("7Oa8Dd6xhwrpKdkgJdRo")
                     getInterviewDetails(accessCode)
                    //  showToast(this,"Under Development")
                    hideKeyboard(this)
                }else
                {
                    showCustomSnackbarOnTop(getString(R.string.txt_url_required))
                    //showToast(this,getString(R.string.txt_url_required))
                }
            }else
            {
                showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                //Snackbar.make(it,getString(R.string.txt_no_internet_connection),Snackbar.LENGTH_SHORT).show()
            }
        }

      /*  LocalBroadcastManager.getInstance(this).registerReceiver(
            incomingCallRecevier,
            IntentFilter(AppConstants.IN_COMING_CALL_ACTION)
        )*/
handleObserver()
    }

    fun handleObserver()
    {
        CallStatusHolder.getCallStatus().observe(this){
            isCallInProgress=it
            Log.d(TAG, "handleObserver: call status is $it")
        }
        
    }
    
    
  /*  val incomingCallRecevier=object: BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            Log.d(TAG, "onReceive: call recieved or attented action  ${p1?.getStringExtra(AppConstants.IN_COMING_CALL_ACTION)}")

            when(p1?.getStringExtra(AppConstants.IN_COMING_CALL_ACTION))
            {
                AppConstants.IN_COMING_CALL_ACTION_RINGING->{

                }
                AppConstants.IN_COMING_CALL_ACTION_ATTENDED->{
                    isCallInProgress=true
                }
                AppConstants.IN_COMING_CALL_ACTION_IDL_ENDED->{
                    isCallInProgress=false
                }

            }
            Log.d(TAG, "onReceive: call recieved or attented")
        }
    }*/

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        super.onDestroy()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }

    fun getInterviewDetails(accessCode:String)
    {
        showProgressDialog()
        Log.d(TAG, "getInterviewDetails: method")
        viewModel.getVideoSessionDetails(accessCode, onDataResponse = { data,event ->

            when (event) {
                200 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "meeting data in 200 ${data}")
                    CurrentMeetingDataSaver.setData(data!!)
                    joinMeetingCandidate(accessCode)
                    data.videoAccessCode=accessCode
                    CurrentMeetingDataSaver.setData(data)
                    Log.d(TAG, "getInterviewDetails: user response $data")
                    //val identityWithoutFirstChar=CurrentMeetingDataSaver.getData().identity?.substring(1,CurrentMeetingDataSaver.getData().identity?.length!!.toInt())
                    //Log.d(TAG, "getInterviewDetails: identity ${CurrentMeetingDataSaver.getData().identity} identitywitoutno $identityWithoutFirstChar")
                // Log.d(TAG, "host : ${data.token}  ${data.roomName}")
                    // TwilioHelper.setTwilioCredentials(data.token.toString(), data.roomName.toString())
                    // startActivity(Intent(this@JoinMeetingActivity, VideoActivity::class.java))
                }
                400 -> {
                    dismissProgressDialog()
                    showCustomSnackbarOnTop(data?.aPIResponse?.message!!)
                    //showToast(this,data?.aPIResponse?.message!!)
                    data.videoAccessCode=accessCode
                    //showToast(this, "null values")
                  /*  data?.let { CurrentMeetingDataSaver.setData(it) }
                    joinMeetingCandidate(accessCode)*/
                    Log.d(TAG, "getInterviewDetails: user response $data")
                }
                404 -> {
                    dismissProgressDialog()
                  //  showToast(this,data?.aPIResponse?.message!!)
                    showCustomSnackbarOnTop(data?.aPIResponse?.message!!)
                    //showToast(this, data?.aPIResponse?.message.toString())
                }
                401->{
                    dismissProgressDialog()
                    showCustomSnackbarOnTop(data?.aPIResponse?.message!!)
                    //showToast(this,data?.aPIResponse?.message!!)
                   /* data.videoAccessCode=accessCode
                    CurrentMeetingDataSaver.setData(data!!)
                    joinMeetingCandidate(accessCode)
                    CurrentMeetingDataSaver.setData(data)*/
                    Log.d(TAG, "getInterviewDetails: user response $data")
                }
            }
            Log.d(TAG, "getInterviewDetails: status ${data?.aPIResponse?.message}")

        })
    }


    fun joinMeetingCandidate(accessCode: String)
    {
        // showProgressDialog()
        viewModel.getVideoSessionCandidate(accessCode, onDataResponse = { data, event->

            when (event) {
                200 -> {
                    dismissProgressDialog()
                    if(data.identity!!.contains("C") && data.roomName?.trim()?.lowercase().equals("nothost") )
                    {
                        showAlertWhenNotHost()
                        viewModel.sendMailToJoin { data, response ->
                            Log.d(TAG, "joinMeetingCandidate: send mail response $data")
                        }
                        Log.d("showdialogalert", "joinMeetingCandidate: in dialog show")
                    }else
                    {
                        CurrentMeetingDataSaver.setRoomData(data)
                        Log.d("showdialogalert", "candidate: ${data.token}  ${data.roomName}")
                        TwilioHelper.setTwilioCredentials(data.token.toString(),data.roomName.toString())
                        CurrentConnectUserList.clearList()

                        requestVideoPermissions {
                            if (it)
                            {

                                 showPrivacyPolicy(binding.root as ViewGroup,onClicked = { it, dialog->
                               if (it)
                               {

                                   if (isCallInProgress)
                                   {
                                       dialog.dismiss()
                                       showCustomSnackbarOnTop(getString(R.string.txt_call_in_progress))
                                   }else
                                   {
                                       val intent=Intent(this@JoinMeetingActivity, VideoActivity::class.java)
                                       startActivity(intent)
                                       overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
                                       dialog.dismiss()
                                   }

                               }else
                               {
                                   dialog.dismiss()
                               }
                                },
                                    onClickedText = {link,action->
                                        privacyPolicy(link,action)
                                    }
                                )
                            }
                            else
                            {
                                showCustomSnackbarOnTop(getString(R.string.txt_permission_required))
                                //showToast(this,getString(R.string.txt_permission_required))
                            }
                        }
                    }
                }
                400 -> {
                    dismissProgressDialog()
                    showToast(this, "null values")
                }
                404 -> {
                    dismissProgressDialog()
                    showToast(this, "response not success")
                }
            }
        })
    }

    fun privacyPolicy(link:String,action:Int)
    {
        val intent=Intent(this, ActivityPrivacyPolicy::class.java)
        intent.putExtra(AppConstants.PRIVACY_LINK,link)
        intent.putExtra(AppConstants.PRIVACY_ACTION,action)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }


    fun showAlertWhenNotHost()
    {
        setHandler().post(Runnable{
            val dialog=AlertDialog.Builder(this)
            dialog.setMessage(getString(R.string.txt_not_host_alert))
            dialog.setPositiveButton(getString(R.string.txt_join_again),object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    getInterviewDetails(accessCode)
                }
            })
            dialog.setNegativeButton("Cancel" ,object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {

                }
            })
            dialog.create()
            dialog.show()
        })
    }


}
