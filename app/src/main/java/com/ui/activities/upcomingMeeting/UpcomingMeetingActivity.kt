package com.ui.activities.upcomingMeeting

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AbsListView
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.data.*
import com.data.dataHolders.*
import com.data.helpers.TwilioHelper
import com.domain.BaseModels.BodyScheduledMeetingBean
import com.domain.BaseModels.NewInterviewDetails
import com.domain.BaseModels.ResponseInterViewDetailsBean
import com.domain.constant.AppConstants
import com.example.twillioproject.R
import com.example.twillioproject.databinding.ActivityUpcomingMeetingBinding
import com.example.twillioproject.databinding.LayoutDescriptionDialogBinding
import com.google.android.material.snackbar.Snackbar
import com.ui.activities.feedBack.ActivityFeedBackForm
import com.ui.activities.login.LoginActivity
import com.ui.activities.twilioVideo.VideoActivity
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
    private var status=""
    private var pageno=1
    private var iscrolled=false
    private  lateinit var  layoutManager:LinearLayoutManager
    private var isOpenedFirst=false
    private var preUtcDate = ""
    private var preIsTDate = ""
    private var nextutcDate = ""
    private var nextIstDate = ""
    private var commonIstDate=""
    private var commonUtcDate=""
    private var isPrevClicked = false
    private var isNextClicked = false

    private var searchTxt=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpcomingMeetingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(UpComingMeetingViewModel::class.java)

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
            handleUpcomingMeetingsList(3, 1,9)
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
/*
        binding.btnSearch.setOnClickListener {
            meetingsList.clear()
            handleUpcomingMeetingsList(0, binding.etSearch.text.toString(),1,9)
        }
*/



        binding.swipetorefresh.setOnRefreshListener {
            if (checkInternet()) {
                meetingsList.clear()
                pageno=1
                handleUpcomingMeetingsList(5, 1,9)

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
        binding.etSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                meetingsList.clear()
                pageno=1
                isOpenedFirst=true
                searchTxt=binding.etSearch.text.toString()
                handleUpcomingMeetingsList(0,1,9)

                hideKeyboard(this)
                return@OnEditorActionListener true
            }
            false
        })
        binding.rvUpcomingMeeting.layoutManager = layoutManager

        handleObserver()

        binding.btnEllipsize.setOnClickListener {
            registerForContextMenu(it)
            this.openContextMenu(it)
        }

        binding.btnLeftPrevious.setOnClickListener {
           meetingsList.clear()
            pageno=1
            if (isNextClicked) {
                WeeksDataHolder.minusWeekDay()
                WeeksDataHolder.minusWeekDay()
                handleUpcomingMeetingsList(1, 1,9)
                isNextClicked = false
            }
            else {
                WeeksDataHolder.minusWeekDay()
                handleUpcomingMeetingsList(1, 1,9)
            }
            isPrevClicked = true
        }

        binding.btnRightNext.setOnClickListener {
            meetingsList.clear()
            pageno=1
            if (isPrevClicked) {
                WeeksDataHolder.addWeekDay()
                WeeksDataHolder.addWeekDay()
                handleUpcomingMeetingsList(2, 1,9)
                isPrevClicked = false
            } else {
                WeeksDataHolder.addWeekDay()
                handleUpcomingMeetingsList(2, 1,9)
            }
            isNextClicked = true
        }

        binding.btnCross.setOnClickListener {
            pageno=1
            meetingsList.clear()
            searchTxt=""
            handleUpcomingMeetingsList(5, 1,9)
            binding.etSearch.setText("")
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

                    if (contentLimit==meetingsList.size)
                    {

                    }else
                    {
                        Log.d(TAG, "onScrolled: "+pageno.toString())
                        handleUpcomingMeetingsList(5, pageno,9)

                    }

                    Log.d(TAG, "onScrolled: "+pageno.toString())
                    iscrolled=false
                }
            }
        })
        setupAdapter()
    }


    private fun handleUpcomingMeetingsList(action: Int,pageNumber:Int,pageSize:Int) {
        UpcomingMeetingStatusHolder.setStatus(status)
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

                    ob.Search = searchTxt
                    ob.Status=status
                    ob.fromdate = preUtcDate
                    ob.todate = nextutcDate

                    ob.from = preIsTDate
                    ob.to = nextIstDate

                }
            }
            7 -> {
                /*
                * for search
                * */
                WeeksDataHolder.getIstUtcPriviousDate { utcDate, istDate ->
                    Log.d(TAG, "onCreate: previous date  utc previous ${utcDate}  itc $istDate ")
                    ob.Search = searchTxt
                    ob?.Status = status

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
                    ob.Search = searchTxt
                    ob.Status=status
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
                    ob.Search = searchTxt
                    ob.Status=status
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
                    ob.Search = searchTxt
                    ob.Status=status
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
                    ob.Search = searchTxt
                    isPrevClicked=true
                    ob.Status=status

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
            if (intent.getBooleanExtra(AppConstants.LOGIN_WITH_OTP,false))
            {
                getDataWithOtp(ob)
                Log.d(TAG, "handleUpcomingMeetingsList: logged with otp")
            }
            else{
                Log.d(TAG, "handleUpcomingMeetingsList: logged with email")
                getDataWithoutOtp(ob)
            }
        }
        else {
            dismissProgressDialog()
        }
    }


    fun getDataWithOtp(ob:BodyScheduledMeetingBean)
    {
        viewModel.getScheduledMeetingListwithOtp(
            actionProgress = {
                if (it == 1) {
                    binding.swipetorefresh.isRefreshing = false
                    if (isOpenedFirst)
                    {
                        Handler(Looper.getMainLooper()).post(Runnable {
                            binding.progressBar.isVisible=true
                        })
                    }else
                    {
                        showProgressDialog()
                        isOpenedFirst=true
                    }
                }
                else {
                    binding.swipetorefresh.isRefreshing = false
                    dismissProgressDialog()

                    Handler(Looper.getMainLooper()).post(Runnable {
                        binding.progressBar.isVisible=false

                    })

                }
            },
            response = { result, exception,data ->
                try {
                    pageno++
                    contentLimit=data?.totalCount!!
                }catch (e:Exception)
                {

                }

                dismissProgressDialog()
                binding.swipetorefresh.isRefreshing = false
            },
            bodyScheduledMeetingBean = ob!!
        )
    }
    fun getDataWithoutOtp(ob:BodyScheduledMeetingBean)
    {
        viewModel.getScheduledMeetingList(
            actionProgress = {
                if (it == 1) {
                    binding.swipetorefresh.isRefreshing = false
                    if (isOpenedFirst)
                    {
                        Handler(Looper.getMainLooper()).post(Runnable {
                            binding.progressBar.isVisible=true
                        })
                    }else
                    {
                        showProgressDialog()
                        isOpenedFirst=true
                    }
                }
                else {
                    binding.swipetorefresh.isRefreshing = false
                    dismissProgressDialog()

                    Handler(Looper.getMainLooper()).post(Runnable {
                        binding.progressBar.isVisible=false

                    })

                }
            },
            response = { result, exception,data ->
                try {
                    pageno++
                    contentLimit=data?.totalCount!!
                }catch (e:Exception)
                {

                }

                dismissProgressDialog()
                binding.swipetorefresh.isRefreshing = false
            },
            bodyScheduledMeetingBean = ob!!
        )
    }


    private var contentLimit:Int=1

    private val meetingsList= mutableListOf<NewInterviewDetails>()
    private fun handleObserver() {
        Log.d(TAG, "handleObserver: out observer method ")
        binding.tvNoData.visibility = View.VISIBLE

        viewModel.scheduledMeetingLiveData.observe(this, Observer {
            if (!it.isNullOrEmpty())
            {
                Log.d(TAG, "handleObserver: ifpart not null empty $it ")
            meetingsList.addAll(it)
            adapter.notifyDataSetChanged()
            }
            if (meetingsList.size==0)
            {
                Log.d(TAG, "handleObserver: ifpart meeting size 0")
                binding.tvNoData.visibility = View.VISIBLE
            }
            else
            {

                Log.d(TAG, "handleObserver: elsepart meeting size not size")
                binding.tvNoData.visibility = View.GONE
            }
            Log.d(TAG, "handleObserver: out observer ${it.size}")

            Log.d(TAG, "handleObserver: size ${it.size}")
            //Log.d(TAG, "handleObserver: size ${it}")

            //     binding.tvNoData.visibility=View.VISIBLE
        })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }

    fun setupAdapter()
    {
        adapter = UpcomingMeetingAdapter(this, meetingsList) { data, videoAccessCode, action ->
            when (action) {
                1 -> {
                    CurrentUpcomingMeetingData.setData(data)
                    handleJoin(data, videoAccessCode)
                }
                2 -> {
                    showDescDialog(data)
                }
                3->{
                    CurrentUpcomingMeetingData.setData(data)
                    Log.d(TAG, "setupAdapter : zone ${data.interviewTimezone}")
                    CurrentMeetingDataSaver.setData(ResponseInterViewDetailsBean(videoAccessCode = videoAccessCode, interviewTimezone = data.interviewTimezone))
                    val intent=Intent(this,ActivityFeedBackForm::class.java)
                    startActivity(intent)
                }
            }
        }
        binding.rvUpcomingMeeting.adapter = adapter
        //adapter.notifyDataSetChanged()
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        val inflater = menuInflater
        inflater.inflate(com.example.twillioproject.R.menu.menu_filter_upcominglist, menu)
        menu!!.setHeaderTitle("Meetings")

        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.all_meetings->{
                status=""
                meetingsList.clear()
                pageno=1
                handleUpcomingMeetingsList(7,1,9)
            }
            R.id.attended_meetings->{
                status="Attended"
                meetingsList.clear()
                pageno=1
                handleUpcomingMeetingsList(7,1,9)
            }
            R.id.scheduled_meetings->{
                status="schedule"
                meetingsList.clear()
                pageno=1
                handleUpcomingMeetingsList(7,1,9)
            }
            R.id.nonscheduled_meetings->{
                status="nonSchedule"
                meetingsList.clear()
                pageno=1
                handleUpcomingMeetingsList(7,1,9)
            }
            R.id.cancel_meetings->{
                status="cancel"
                meetingsList.clear()
                pageno=1
                handleUpcomingMeetingsList(7,1,9)
            }
        }

        return super.onContextItemSelected(item)
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
        dialogBinding.tvUserId.text = data.jobid.toString()
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
                500->{
                    dismissProgressDialog()
                    showToast(this, "Interval Server Error!")
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
                    data?.videoAccessCode=accessCode
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
                    showToast(this, data?.aPIResponse?.message.toString())
                    /*data?.videoAccessCode=accessCode //remove all code
                    data?.let { CurrentMeetingDataSaver.setData(it) }
                    joinMeeting(accessCode)*/
                }
                404 -> {
                    dismissProgressDialog()
                    showToast(this, data?.aPIResponse?.message.toString())
                }
                401 -> {
                    dismissProgressDialog()
                    /*data?.videoAccessCode=accessCode //remove all code
                    CurrentMeetingDataSaver.setData(data!!)
                    joinMeeting(accessCode)
                    CurrentMeetingDataSaver.setData(data)*/
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
                        val intent=Intent(this, VideoActivity::class.java)
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