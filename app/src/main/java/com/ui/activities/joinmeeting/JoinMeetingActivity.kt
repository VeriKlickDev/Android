package com.ui.activities.joinmeeting

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.data.*
import com.data.dataHolders.CurrentMeetingDataSaver
import com.data.helpers.TwilioHelper
import com.example.twillioproject.R
import com.example.twillioproject.databinding.ActivityJoinMeetingBinding
import com.google.android.material.snackbar.Snackbar
import com.ui.activities.documentviewer.FileDownloader.downloadFile
import com.ui.activities.feedBack.ActivityFeedBackForm
import com.ui.activities.twilioVideo.VideoActivity
import com.ui.activities.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Runnable

@AndroidEntryPoint
class JoinMeetingActivity :AppCompatActivity() {

    private lateinit var binding:ActivityJoinMeetingBinding
    private lateinit var viewModel:JoinMeetingViewModel
    private var accessCode=""
    private  var TAG="videocon"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinMeetingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel=ViewModelProvider(this).get(JoinMeetingViewModel::class.java)

        binding.btnJoin.setOnClickListener {
            if (checkInternet()){
               // startActivity(Intent(this,ActivityAddParticipant::class.java))
                if (binding.etJoinMeeting.text.toString() != "")
                {
                    val meetingLink= binding.etJoinMeeting.text.toString()
                    val accessCodeSplit=meetingLink.splitToSequence("/")

                    accessCode= accessCodeSplit.last()
                    Log.d(TAG, "onCreate: accessCode is $accessCode")

                    //candi https://ui2.veriklick.in/video-session/QbFgKXaYOcaaPlZhmLEm  emulator
                    // host https://ui2.veriklick.in/video-session/mkpeHcXKbF95uRiWiLzJ s10
                    //inv https://ui2.veriklick.in/video-session/iuLGttdaQut1M3c1j8yd   note 9 invterviewer

                    //test
                   // getInterviewDetails("iuLGttdaQut1M3c1j8yd")

                    getInterviewDetails(accessCode)
                    //  showToast(this,"Under Development")
                    InputUtils.hideKeyboard(this)
                }else
                {
                    showToast(this,getString(R.string.txt_url_required))
                }
            }else
            {
                Snackbar.make(it,getString(R.string.txt_no_internet_connection),Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
           // startActivity(Intent(this, ActivityFeedBackForm::class.java))
        }
    }

    /*
    *  I2D8o1imAlVv3JVIxKdG
candidate :- 2dpOPzO1Hcf1z5eSGvLC
    *
    *
    * */
//https://ui2.veriklick.in/video-session/DAvKGK6RJbQUE9gnmR9P
    fun getInterviewDetails(accessCode:String)
    {
        showProgressDialog()
        viewModel.getVideoSessionDetails(accessCode, onDataResponse = { data,event ->

            when (event) {
                200 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "meeting data in 200 ${data}")
                    CurrentMeetingDataSaver.setData(data!!)
                        joinMeetingCandidate(accessCode)
                        CurrentMeetingDataSaver.setData(data)
                   // Log.d(TAG, "host : ${data.token}  ${data.roomName}")
                   // TwilioHelper.setTwilioCredentials(data.token.toString(), data.roomName.toString())
                   // startActivity(Intent(this@JoinMeetingActivity, VideoActivity::class.java))
                }
                400 -> {
                    dismissProgressDialog()
                    showToast(this,data?.aPIResponse?.message!!)
                    //showToast(this, "null values")
                    data?.let { CurrentMeetingDataSaver.setData(it) }
                        joinMeetingCandidate(accessCode)
                }
                404 -> {
                    dismissProgressDialog()
                    showToast(this,data?.aPIResponse?.message!!)
                    showToast(this, data?.aPIResponse?.message.toString())
                }
                401->{
                    dismissProgressDialog()
                    showToast(this,data?.aPIResponse?.message!!)
                    CurrentMeetingDataSaver.setData(data!!)
                    joinMeetingCandidate(accessCode)
                    CurrentMeetingDataSaver.setData(data)
                }
            }
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
                        Log.d("showdialogalert", "joinMeetingCandidate: in dialog show")
                    }else
                    {
                        CurrentMeetingDataSaver.setRoomData(data)
                        Log.d("showdialogalert", "candidate: ${data.token}  ${data.roomName}")
                        TwilioHelper.setTwilioCredentials(data.token.toString(),data.roomName.toString())
                            startActivity(Intent(this@JoinMeetingActivity, VideoActivity::class.java))
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
