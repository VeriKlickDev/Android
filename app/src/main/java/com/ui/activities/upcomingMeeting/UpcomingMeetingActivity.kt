package com.ui.activities.upcomingMeeting

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AbsListView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableResource
import com.data.dataHolders.DataStoreHelper
import com.data.*
import com.data.dataHolders.CurrentMeetingDataSaver
import com.data.dataHolders.WeeksDataHolder
import com.data.helpers.TwilioHelper
import com.domain.BaseModels.BodyScheduledMeetingBean
import com.domain.BaseModels.NewInterviewDetails
import com.example.twillioproject.R
import com.example.twillioproject.databinding.ActivityUpcomingMeetingBinding
import com.example.twillioproject.databinding.LayoutDescriptionDialogBinding
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
    private lateinit var accessCode: String

    var pageno=1
    var iscrolled=false
    lateinit var  layoutManager:LinearLayoutManager

    private var preUtcDate = ""
    private var preIsTDate = ""
    private var nextutcDate = ""
    private var nextIstDate = ""
    private var commonIstDate=""
    private var commonUtcDate=""
    private var isPrevClicked = false
    private var isNextClicked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpcomingMeetingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(UpComingMeetingViewModel::class.java)
        var dayOfYear = getCurrentDayOfYear()
        WeeksDataHolder.setDayToZero()
        layoutManager=LinearLayoutManager(this)
        WeeksDataHolder.getItcUtcNextDate(dateResponse = { utcDate, itcDate ->
            nextIstDate = itcDate
            nextutcDate = utcDate
        })

        WeeksDataHolder.minusWeekDay()

        WeeksDataHolder.getIstUtcPriviousDate(dateResponse = { utcDate, istDate ->
            preUtcDate = utcDate
            preIsTDate = istDate

            commonIstDate=istDate
            commonUtcDate=utcDate
        })


        if (checkInternet()) {
            handleUpcomingMeetingsList(3, null,1,9)
        }
        else {
            Snackbar.make(
                binding.root, getString(R.string.txt_no_internet_connection),
                Snackbar.LENGTH_SHORT
            ).show()
        }


        binding.btnSearchShow.setOnClickListener {
            if (binding.tvHeader.isVisible) {
                binding.layoutSearch.isVisible = true
                binding.tvHeader.isVisible = false
                binding.btnSearchShow.isVisible=false
                binding.btnCross.isVisible=true
            }
            else {
                binding.btnCross.isVisible=false
                binding.layoutSearch.isVisible = false
                binding.tvHeader.isVisible = true
                binding.btnSearchShow.isVisible=true
            }
        }

        binding.btnSearch.setOnClickListener {
            handleUpcomingMeetingsList(0, binding.etSearch.text.toString(),1,9)
        }

        binding.btnCross.setOnClickListener {

            if (!binding.tvHeader.isVisible) {
                binding.layoutSearch.isVisible = false
                binding.tvHeader.isVisible = true
                binding.btnSearchShow.isVisible=true
                binding.btnCross.isVisible=false
            }
        }


        binding.swipetorefresh.setOnRefreshListener {
            if (checkInternet()) {


                handleUpcomingMeetingsList(5, null,1,9)

            /* if (isNextClicked)
                {
                    handleUpcomingMeetingsList(5, null)
                }else
                {
                    handleUpcomingMeetingsList(3, null)
                }*/
            }
            else {
                Snackbar.make(
                    binding.root, getString(R.string.txt_no_internet_connection),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnLogout.setOnClickListener {
          val dialog=AlertDialog.Builder(this)
            dialog.setMessage(getString(R.string.txt_do_you_want_to_logout))
            dialog.setPositiveButton("ok",DialogInterface.OnClickListener { dialogInterface, i ->
                DataStoreHelper.clearData()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            })

            dialog.setNegativeButton("cancel",DialogInterface.OnClickListener { dialogInterface, i ->

            })
            dialog.show()
            dialog.create()


        }

        binding.rvUpcomingMeeting.layoutManager = layoutManager

        handleObserver()

        binding.btnLeftPrevious.setOnClickListener {
            if (isNextClicked) {
                WeeksDataHolder.minusWeekDay()
                WeeksDataHolder.minusWeekDay()
                handleUpcomingMeetingsList(1, null,1,9)
                isNextClicked = false
            }
            else {
                WeeksDataHolder.minusWeekDay()
                handleUpcomingMeetingsList(1, null,1,9)
            }
            isPrevClicked = true
        }

        binding.btnRightNext.setOnClickListener {
            if (isPrevClicked) {
                WeeksDataHolder.addWeekDay()
                WeeksDataHolder.addWeekDay()
                handleUpcomingMeetingsList(2, null,1,9)
                isPrevClicked = false
            } else {
                WeeksDataHolder.addWeekDay()
                handleUpcomingMeetingsList(2, null,1,9)
            }
            isNextClicked = true
        }

        binding.rvUpcomingMeeting.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    iscrolled=true

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val vitem=layoutManager.childCount
                val skipped=layoutManager.findFirstVisibleItemPosition()
                val totalitem=layoutManager.itemCount
                if (iscrolled && vitem+skipped==totalitem) {
                    pageno++
                    handleUpcomingMeetingsList(5, null,pageno,9)
                    Log.d(TAG, "onScrolled: "+pageno.toString())
                    iscrolled=false
                }
            }
        })
        setupAdapter()
    }


    private fun handleUpcomingMeetingsList(action: Int, searchTxt: String?,pageNumber:Int,pageSize:Int) {
        var ob: BodyScheduledMeetingBean? = null
        Log.d(TAG, "handleUpcomingMeetingsList: handleupcoming meeting list")
        var recruiterId = ""
        var userId = ""
        var recruiterEmail = ""

        CoroutineScope(Dispatchers.IO).launch {
            recruiterId = DataStoreHelper.getMeetingRecruiterid()
            userId = DataStoreHelper.getMeetingUserId()
            recruiterEmail = DataStoreHelper.getUserEmail()
        }

        ob = BodyScheduledMeetingBean(
            Isweb = true
        )

        when (action) {
            0 -> {
                /*
                * for search
                * */
                WeeksDataHolder.getIstUtcPriviousDate { utcDate, istDate ->
                    Log.d(TAG, "onCreate: previous date  utc previous ${utcDate}  itc $istDate ")

                    ob.Search = searchTxt.toString()

                    ob.fromdate = preUtcDate
                    ob.todate = nextutcDate

                    ob.from = preIsTDate
                    ob.to = nextIstDate

                }
            }

            5 -> {
                /*
                * for search
                * */
                WeeksDataHolder.getIstUtcPriviousDate{ utcDate, istDate ->
                    Log.d(TAG, "onCreate: previous date  utc previous ${utcDate}  itc $istDate ")

                 /*

                    ob.fromdate = preUtcDate
                    ob.todate = nextutcDate

                    ob.from = preIsTDate
                    ob.to = nextIstDate
*/

                    Log.d(TAG, "handleUpcomingMeetingsList: ")

                    ob.fromdate = preUtcDate
                    ob.todate = nextutcDate

                    ob.from = preIsTDate
                    ob.to = nextIstDate


                    /*   ob.fromdate = preUtcDate
                       ob.todate = nextutcDate

                       ob.from = preIsTDate
                       ob.to = nextIstDate
                       */

                  /*  if (isPrevClicked)
                    {
                        ob.fromdate = utcDate
                        ob.todate = preUtcDate

                        ob.from = istDate
                        ob.to = preIsTDate
                    }
                    else
                    {
                        ob.fromdate = utcDate
                        ob.todate = preUtcDate

                        ob.from = istDate
                        ob.to = preIsTDate
                    }*/
                }
            }

            1 -> {

                WeeksDataHolder.getIstUtcPriviousDate(dateResponse = { utcDate, istDate ->
                    Log.d(TAG,"onCreate: previous date  utc previous  from $istDate     to $preIsTDate  ")

                    if (isNextClicked)
                    {
                        ob.fromdate = utcDate
                        ob.todate = preUtcDate

                        ob.from = istDate
                        ob.to = preIsTDate
                    }
                    else
                    {
                        ob.fromdate = utcDate
                        ob.todate = commonUtcDate

                        ob.from = istDate
                        ob.to = commonIstDate
                    }

                    commonIstDate=istDate
                    commonUtcDate=utcDate

                    nextutcDate = utcDate
                    nextIstDate = istDate

                    preUtcDate = nextutcDate
                    preIsTDate = nextIstDate

                })
            }
            2 -> {

                WeeksDataHolder.getItcUtcNextDate(dateResponse = { utcDate, istDate ->
                    Log.d(TAG,"onCreate: next date is  date  utc from  $preIsTDate to $istDate ")

                    if (isPrevClicked)
                    {
                        ob.from = nextutcDate
                        ob.to = utcDate

                        ob.fromdate = nextIstDate
                        ob.todate = istDate
                    }
                    else
                    {
                        ob.from = commonUtcDate
                        ob.to = utcDate

                        ob.fromdate = commonIstDate
                        ob.todate = istDate
                    }

                    commonIstDate=istDate
                    commonUtcDate=utcDate

                    preUtcDate = utcDate
                    preIsTDate = istDate
                })
            }
            3 -> {
                WeeksDataHolder.getIstUtcPriviousDate(dateResponse = { utcDate, istDate ->
                    Log.d(TAG, "onCreate: previous date  utc previous ${utcDate}  itc $istDate ")

                /*    if (isNextClicked)
                    {
                        ob.fromdate = utcDate
                        ob.todate = nextutcDate

                        ob.from = istDate
                        ob.to = nextIstDate
                    }
                    else
                    {
                        ob.fromdate = utcDate
                        ob.todate = preUtcDate

                        ob.from = istDate
                        ob.to = preIsTDate
                    }
*/

                    isPrevClicked=true

                    ob.fromdate = utcDate
                    ob.todate = nextutcDate

                    ob.from = istDate
                    ob.to = nextIstDate

//                    commonIstDate=istDate
  //                  commonUtcDate=utcDate

                    //preUtcDate = nextutcDate
                   // preIsTDate = nextIstDate

                })
            }
        }

        binding.tvFromdateToDate.setText(
            getDateWithMonthName(
                ob.from.toString(),
                1
            ) + " to " + getDateWithMonthName(ob.to.toString(), 2)
        )

        Log.d(TAG, "handleUpcomingMeetingsList: ${ob.from} ${ob.to}")
        Log.d(TAG, "handleUpcomingMeetingsList: ${ob.fromdate} ${ob.todate}")

        nextutcDate=ob.todate.toString()
        nextIstDate=ob.to.toString()

        preIsTDate=ob.from.toString()
        preUtcDate=ob.todate.toString()

        Log.d(
            "fromDateTo",
            "handleUpcomingMeetingsList: from date to ${ob.from}  ${
                getDateWithMonthName(
                    ob.from.toString(),
                    1
                )
            }"
        )
        ob.PageNumber=pageNumber
        ob.PageSize=pageSize
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
        else {
            dismissProgressDialog()
        }
    }


    private val meetingsList= mutableListOf<NewInterviewDetails>()
    private fun handleObserver() {
        Log.d(TAG, "handleObserver: out observer method")
        binding.tvNoData.visibility = View.VISIBLE

        viewModel.scheduledMeetingLiveData.observe(this, Observer {
            if (!it.isNullOrEmpty())
            {
            meetingsList.addAll(it)
            adapter.notifyDataSetChanged()
            }
            if (meetingsList.size==0)
            {
                binding.tvNoData.visibility = View.VISIBLE
            }
            else
            {
                binding.tvNoData.visibility = View.GONE
            }
            Log.d(TAG, "handleObserver: out observer ${it.size}")


            Log.d(TAG, "handleObserver: size ${it.size}")
            //Log.d(TAG, "handleObserver: size ${it}")

            //     binding.tvNoData.visibility=View.VISIBLE
        })
    }

    fun setupAdapter()
    {
        adapter = UpcomingMeetingAdapter(this, meetingsList) { data, videoAccessCode, action ->
            when (action) {
                1 -> {
                    handleJoin(data, videoAccessCode)
                }
                2 -> {
                    showDescDialog(data)
                }
            }
        }
        binding.rvUpcomingMeeting.adapter = adapter
        //adapter.notifyDataSetChanged()
    }

    fun showDescDialog(data: NewInterviewDetails) {
        val dialog = Dialog(this)
        val dialogBinding = LayoutDescriptionDialogBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogBinding.btnCross.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.tvDescription.text = data.interviewTitle
        dialogBinding.tvUserId.text = data.interviewId.toString()
        dialog.create()
        dialog.show()
    }

    fun handleJoin(data: NewInterviewDetails, videoAccessCode: String) {
        Log.d(TAG, "handleJoin: on clicked ${data.VideoCallAccessCode}")
        //getInterviewDetails()
        if (checkInternet()) {
            accessCode = videoAccessCode
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


    fun getAccessCodeById(data: NewInterviewDetails) {
        Log.d(TAG, "getAccessCodeById: data gotted ${data?.interviewId}")
        viewModel.getInterAccessCodById(data.interviewId, onDataResponse = { data, response ->
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


    fun getInterviewDetails(accessCode: String) {
        showProgressDialog()
        viewModel.getVideoSessionDetails(accessCode, onDataResponse = { data, event ->

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
                401 -> {
                    dismissProgressDialog()
                    CurrentMeetingDataSaver.setData(data!!)
                    joinMeeting(accessCode)
                    CurrentMeetingDataSaver.setData(data)
                }
            }
        })
    }


    fun joinMeeting(accessCode: String) {
        // showProgressDialog()
        viewModel.getVideoSessionCandidate(accessCode, onDataResponse = { data, event ->

            when (event) {
                200 -> {
                    dismissProgressDialog()
                    if (data.identity!!.contains("C") && data.roomName?.trim()?.lowercase()
                            .equals("nothost")
                    ) {
                        showAlertWhenNotHost()
                        Log.d("showdialogalert", "joinMeetingCandidate: in dialog show")
                    }
                    else {
                        Log.d("showdialogalert", "candidate: ${data.token}  ${data.roomName}")
                        TwilioHelper.setTwilioCredentials(
                            data.token.toString(),
                            data.roomName.toString()
                        )
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

    fun showAlertWhenNotHost() {
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


/*

fromdate = getCurrentDate().toString(),
            todate = getIntervalMonthDate().toString(),
            from = getCurrentUtcFormatedDate(),
            to = getCurrentUtcFormatedDateIntervalofMonth(),



 */