package com.ui.activities.joinmeeting

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.data.*
import com.data.dataHolders.CurrentConnectUserList
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
                    //https://ui2.veriklick.in/video-session/PjcjXhZvePAFGs5G23yQ
                    //getInterviewDetails("PjcjXhZvePAFGs5G23yQ")
                    getInterviewDetails(accessCode)
                    //  showToast(this,"Under Development")
                    hideKeyboard(this)
                }else
                {
                    showToast(this,getString(R.string.txt_url_required))
                }
            }else
            {
                Snackbar.make(it,getString(R.string.txt_no_internet_connection),Snackbar.LENGTH_SHORT).show()
            }
        }
    }

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
                    // Log.d(TAG, "host : ${data.token}  ${data.roomName}")
                    // TwilioHelper.setTwilioCredentials(data.token.toString(), data.roomName.toString())
                    // startActivity(Intent(this@JoinMeetingActivity, VideoActivity::class.java))
                }
                400 -> {
                    dismissProgressDialog()
                    showToast(this,data?.aPIResponse?.message!!)
                    data.videoAccessCode=accessCode
                    //showToast(this, "null values")
                   /* data?.let { CurrentMeetingDataSaver.setData(it) }
                    joinMeetingCandidate(accessCode)*/
                    Log.d(TAG, "getInterviewDetails: user response $data")
                }
                404 -> {
                    dismissProgressDialog()
                  //  showToast(this,data?.aPIResponse?.message!!)
                    showToast(this, data?.aPIResponse?.message.toString())
                }
                401->{
                    dismissProgressDialog()
                    showToast(this,data?.aPIResponse?.message!!)
                   /* data.videoAccessCode=accessCode
                    CurrentMeetingDataSaver.setData(data!!)
                    joinMeetingCandidate(accessCode)
                    CurrentMeetingDataSaver.setData(data)*/
                    Log.d(TAG, "getInterviewDetails: user response $data")
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
                        CurrentConnectUserList.clearList()

                        val intent=Intent(this@JoinMeetingActivity, VideoActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
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
