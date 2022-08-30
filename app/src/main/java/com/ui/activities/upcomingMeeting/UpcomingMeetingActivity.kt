package com.ui.activities.upcomingMeeting

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.collegeproject.DataStoreHelper
import com.data.*
import com.domain.BaseModels.BodyScheduledMeetingBean
import com.domain.BaseModels.NewInterviewDetails
import com.example.twillioproject.R
import com.example.twillioproject.databinding.ActivityUpcomingMeetingBinding
import com.google.android.material.snackbar.Snackbar
import com.ui.activities.twilioVideo.VideoActivity
import com.ui.activities.login.LoginActivity
import com.ui.listadapters.UpcomingMeetingAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UpcomingMeetingActivity : AppCompatActivity() {
    private val TAG = "upcomingMeeting"
    private lateinit var binding: ActivityUpcomingMeetingBinding
    private lateinit var adapter: UpcomingMeetingAdapter
    private lateinit var viewModel: UpComingMeetingViewModel
    private lateinit var accessCode:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpcomingMeetingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(UpComingMeetingViewModel::class.java)

        binding.swipetorefresh.setOnRefreshListener {
            if (checkInternet()) {
                handleUpcomingMeetingsList()
            }
            else {
                Snackbar.make(
                    binding.root, getString(R.string.txt_no_internet_connection),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnLogout.setOnClickListener {
            DataStoreHelper.clearData()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.rvUpcomingMeeting.layoutManager = LinearLayoutManager(this)

        handleObserver()

        if (checkInternet()) {
            handleUpcomingMeetingsList()
        }
        else {
            Snackbar.make(
                binding.root, getString(R.string.txt_no_internet_connection),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleUpcomingMeetingsList() {
        var ob: BodyScheduledMeetingBean? = null
        Log.d(TAG, "handleUpcomingMeetingsList: handleupcoming meeting list")
        var recruiterId = ""
        var userId = ""
        var recruiterEmail=""
        CoroutineScope(Dispatchers.IO).launch {
            recruiterId = DataStoreHelper.getMeetingRecruiterid()
            userId = DataStoreHelper.getMeetingUserId()
            recruiterEmail=DataStoreHelper.getUserEmail()
        }

        ob = BodyScheduledMeetingBean(
            Recruiter = recruiterId,
            Subscriber = userId,
            fromdate = getCurrentDate().toString(),
            todate = getIntervalMonthDate().toString(),
            from = getCurrentUtcFormatedDate(),
            to = getCurrentUtcFormatedDateIntervalofMonth(),
            RecruiterEmail = recruiterEmail,
            Isweb = true
        )
        Log.d(TAG, "handleUpcomingMeetingsList: ${ob?.Recruiter} ${ob?.Subscriber}")
        Log.d("datecheck", "\n ${ob?.fromdate}  ${ob?.todate}  \n ${ob.from}  ${ob.to} ")

        if (ob != null) {
            viewModel.getScheduledMeetingList(
                actionProgress = {
                    if (it == 1) {
                        binding.swipetorefresh.isRefreshing = false
                        showProgressDialog()
                    }
                    else {
                       binding.swipetorefresh.isRefreshing = false
                        dismissProgressDialog()
                    }
                },
                response = { result, exception ->
                    dismissProgressDialog()
                    binding.swipetorefresh.isRefreshing = false
                },
                bodyScheduledMeetingBean = ob!!
            )
        }
    }

    private fun handleObserver() {
        Log.d(TAG, "handleObserver: out observer method")
        binding.tvNoData.visibility = View.VISIBLE

        viewModel.scheduledMeetingLiveData.observe(this, Observer {


            Log.d(TAG, "handleObserver: out observer ${it.size}")

            binding.tvNoData.visibility = View.GONE
            Log.d(TAG, "handleObserver: size ${it.size}")
            //Log.d(TAG, "handleObserver: size ${it}")
            adapter = UpcomingMeetingAdapter(this, it) { data,videoAccessCode ->
                handleJoin(data,videoAccessCode)
            }
            binding.rvUpcomingMeeting.adapter = adapter
            adapter.notifyDataSetChanged()
            //     binding.tvNoData.visibility=View.VISIBLE
        })
    }

    fun handleJoin(data: NewInterviewDetails,videoAccessCode:String) {
        Log.d(TAG, "handleJoin: on clicked ${data.VideoCallAccessCode}")
        //getInterviewDetails()
        if (checkInternet()) {
            accessCode=videoAccessCode
            getInterviewDetails(videoAccessCode)
           // getAccessCodeById(data)
           // showToast(this,"Under Development")
        }
        else {
            Snackbar.make(
                binding.root, getString(R.string.txt_no_internet_connection),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }


    fun getAccessCodeById(data: NewInterviewDetails)
    {
        Log.d(TAG, "getAccessCodeById: data gotted ${data?.interviewId}")
        viewModel.getInterAccessCodById(data.interviewId, onDataResponse = {data, response ->
            when (response) {
                200 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "getAccessCodeById: data gotted ${data?.InterviewerVideoAccesscode}")
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


    fun getInterviewDetails(accessCode:String)
    {
        showProgressDialog()
        viewModel.getVideoSessionDetails(accessCode, onDataResponse = { data,event ->

            when (event) {
                200 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "meeting data in 200 ${data}")
                    CurrentMeetingDataSaver.setData(data!!)
                    joinMeeting(accessCode)
                    CurrentMeetingDataSaver.setData(data)
                    // Log.d(TAG, "host : ${data.token}  ${data.roomName}")
                    // TwilioHelper.setTwilioCredentials(data.token.toString(), data.roomName.toString())
                    // startActivity(Intent(this@JoinMeetingActivity, VideoActivity::class.java))
                }
                400 -> {
                    dismissProgressDialog()
                    //showToast(this, "null values")
                    data?.let { CurrentMeetingDataSaver.setData(it) }
                    joinMeeting(accessCode)
                }
                404 -> {
                    dismissProgressDialog()
                    showToast(this, data?.aPIResponse?.message.toString())
                }
                401->{
                    dismissProgressDialog()
                    CurrentMeetingDataSaver.setData(data!!)
                    joinMeeting(accessCode)
                    CurrentMeetingDataSaver.setData(data)
                }
            }
        })
    }



    fun joinMeeting(accessCode: String)
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
                        Log.d("showdialogalert", "candidate: ${data.token}  ${data.roomName}")
                        TwilioHelper.setTwilioCredentials(data.token.toString(),data.roomName.toString())
                        startActivity(Intent(this, VideoActivity::class.java))
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
        setHandler().post(kotlinx.coroutines.Runnable {
            val dialog = AlertDialog.Builder(this)
            dialog.setMessage(getString(R.string.txt_not_host_alert))
            dialog.setPositiveButton(getString(R.string.txt_join_again),
                object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        getInterviewDetails(accessCode)
                    }
                })
            dialog.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                }
            })
            dialog.create()
            dialog.show()
        })
    }


}