package com.ui.activities.deepLinkMeetingJoiner

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.data.*
import com.data.dataHolders.CallStatusHolder
import com.data.dataHolders.CurrentConnectUserList
import com.data.dataHolders.CurrentMeetingDataSaver
import com.data.helpers.TwilioHelper
import com.domain.constant.AppConstants
import com.harvee.yourhealthmate2.ui.privacypolicy.ActivityPrivacyPolicy
import com.ui.activities.joinmeeting.JoinMeetingViewModel
import com.ui.activities.twilioVideo.VideoActivity
import com.veriKlick.*
import com.veriKlick.databinding.LayoutVideoMeetingByLinkBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VideoMeetingByLinkActivity : AppCompatActivity() {

    private lateinit var viewModel: JoinMeetingViewModel
    private lateinit var binding: LayoutVideoMeetingByLinkBinding
    private var accessCode=""
    private val TAG = "deeplinkactivity"
    private var isCallInProgress=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutVideoMeetingByLinkBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(JoinMeetingViewModel::class.java)
        val action: String? = intent?.action
        val data: Uri? = intent?.data

        Log.d(TAG, "onCreate: ${data.toString()}")
        if (checkInternet()){

                val meetingLink= data.toString()
                val accessCodeSplit=meetingLink.splitToSequence("/")

                accessCode= accessCodeSplit.last()
                Log.d(TAG, "onCreate: accessCode is $accessCode")

                getInterviewDetails(accessCode,true)

            }
        else
        {
            showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
            Handler(Looper.getMainLooper()).postDelayed({
                                                        finish()
            },1000)
            //Snackbar.make(it,getString(R.string.txt_no_internet_connection),Snackbar.LENGTH_SHORT).show()
        }
        CallStatusHolder.getCallStatus().observe(this){
            isCallInProgress=it
            Log.d(TAG, "handleObserver: call status is $it")
        }


    }



    fun getInterviewDetails(accessCode:String,isDeeplink:Boolean)
    {
        showProgressDialog()
        Log.d(TAG, "getInterviewDetails: method")
        viewModel.getVideoSessionDetails(accessCode, onDataResponse = { data,event ->

            when (event) {
                200 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "meeting data in 200 ${data}")
                    CurrentMeetingDataSaver.setData(data!!)
                    if (isDeeplink)
                    {
                        joinMeetingCandidate(accessCode)
                    }else
                    {
                        jumptoExistingMeeting()
                    }

                    data.videoAccessCode=accessCode
                    CurrentMeetingDataSaver.setData(data)
                    CallStatusHolder.setLastCallStatus(false)
                    CallStatusHolder.setNavigateData(AppConstants.FROM_AS_GUEST)
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
                    Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                    },1000)
                    //showToast(this, "null values")
                    /*  data?.let { CurrentMeetingDataSaver.setData(it) }
                      joinMeetingCandidate(accessCode)*/
                    Log.d(TAG, "getInterviewDetails: user response $data")
                }
                404 -> {
                    dismissProgressDialog()
                    //  showToast(this,data?.aPIResponse?.message!!)
                    try {
                        showCustomSnackbarOnTop(data?.aPIResponse?.message!!)
                    }catch (e:Exception)
                    {
                        Log.d(TAG, "getInterviewDetails: exception 120 ${e.printStackTrace()}")
                    }

                    Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                    },1000)
                    //showToast(this, data?.aPIResponse?.message.toString())
                }
                401->{
                    dismissProgressDialog()
                    showCustomSnackbarOnTop(data?.aPIResponse?.message!!)
                    Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                    },1000)
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

   private fun privacyPolicy(link:String,action:Int)
    {
        val intent= Intent(this, ActivityPrivacyPolicy::class.java)
        intent.putExtra(AppConstants.PRIVACY_LINK,link)
        intent.putExtra(AppConstants.PRIVACY_ACTION,action)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun showAlertWhenNotHost(isDeeplink: Boolean)
    {
        setHandler().post(kotlinx.coroutines.Runnable {
            val dialog = AlertDialog.Builder(this)
            dialog.setMessage(getString(R.string.txt_not_host_alert))
            dialog.setPositiveButton(getString(R.string.txt_join_again),
                object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        getInterviewDetails(accessCode,isDeeplink)
                    }
                })
            dialog.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    Handler(Looper.getMainLooper()).postDelayed({finish()},1000)
                }
            })
            dialog.setCancelable(false)
            dialog.create()
            dialog.show()
        })
    }

   private fun joinMeetingCandidate(accessCode: String)
    {
        // showProgressDialog()
        viewModel.getVideoSessionCandidate(accessCode, onDataResponse = { data, event->

            when (event) {
                200 -> {
                    dismissProgressDialog()
                    if(data.identity!!.contains("C") && data.roomName?.trim()?.lowercase().equals("nothost") )
                    {
                        showAlertWhenNotHost(true)
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

                              Handler(Looper.getMainLooper()).post {

                                  showPrivacyPolicy(binding.root as ViewGroup,
                                      onClicked = { it, dialog ->
                                          if (it) {
                                              if (isCallInProgress) {
                                                  dialog.dismiss()
                                                 showCustomSnackbarOnTop(getString(R.string.txt_call_in_progress))
                                                  Handler(Looper.getMainLooper()).postDelayed({
                                                      finish()
                                                  }, 1000)
                                              }
                                              else {
                                                 // TwilioHelper.disConnectRoom()
                                                  if (TwilioHelper.getRoomInstance() != null) {
                                                      showMeetingRunningDialog { it ->
                                                          if (it) {
                                                              dialog.dismiss()
                                                          }
                                                      }
                                                  }
                                                  else {
                                                      dialog.dismiss()
                                                     // TwilioHelper.disConnectRoom()
                                                      Handler(Looper.getMainLooper()).postDelayed({
                                                          jumptoVideoActivity()
                                                      },1000)

                                                  }
                                              }
                                          }
                                          else {
                                              dialog.dismiss()
                                              Handler(Looper.getMainLooper()).postDelayed({ finish() }, 1000)
                                              if (TwilioHelper.getRoomInstance() != null) {
                                                  getInterviewDetails(
                                                      TwilioHelper.getMeetingLink(),
                                                      false
                                                  )
                                             }
                                          }
                                      },
                                      onClickedText = { link, action ->
                                          privacyPolicy(link, action)
                                      }
                                  )
                              }


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
                    Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                    },1000)
                }
                404 -> {
                    dismissProgressDialog()
                    showToast(this, "response not success")
                    Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                    },1000)
                }
            }
        })
    }


    private fun jumptoExistingMeeting()
    {
        viewModel.getVideoSessionCandidate(TwilioHelper.getMeetingLink(), onDataResponse = { data, event->
            when (event) {
                200 -> {
                    dismissProgressDialog()
                    if(data.identity!!.contains("C") && data.roomName?.trim()?.lowercase().equals("nothost") )
                    {
                        showAlertWhenNotHost(false)
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
                                if (TwilioHelper.getRoomInstance()==null){
                                jumptoVideoActivity()
                            }else
                                {
                                    TwilioHelper.disConnectRoom()
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        jumptoVideoActivity()
                                    },1000)
                                }
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
                    Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                    },1000)
                }
                404 -> {
                    dismissProgressDialog()
                    showToast(this, "response not success")
                    Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                    },1000)
                }
            }
        })
    }

    private fun jumptoVideoActivity()
    {
        val intent = Intent(
            this@VideoMeetingByLinkActivity,
            VideoActivity::class.java
        )
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        finish()
    }

    private fun  getUserExistingMeetingInfo()
    {



    }


    private fun showMeetingRunningDialog(onclick:(sts:Boolean)->Unit)
    {
        setHandler().post(kotlinx.coroutines.Runnable {
            val dialog = AlertDialog.Builder(this)
            dialog.setMessage(getString(R.string.txt_meeting_already_running))
            dialog.setPositiveButton(getString(R.string.txt_join_anyway),
                object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        TwilioHelper.disConnectRoom()
                        if (!TwilioHelper.getRoomInstance()?.toString().equals("")) {
                            onclick(true)
                            Handler(Looper.getMainLooper()).postDelayed({
                                jumptoVideoActivity()
                            },1000)

                        }
                        else{
                            showCustomSnackbarOnTop("Something went wrong.")
                            Handler(Looper.getMainLooper()).postDelayed({finish()},1000)
                        }
                    }
                })
            dialog.setNegativeButton("Go to Existing Meeting", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    getInterviewDetails(TwilioHelper.getMeetingLink(),false)
                    onclick(true)
                    Handler(Looper.getMainLooper()).postDelayed({finish()},1000)

                }
            })
            dialog.create()
            dialog.show()
        })
    }



}