package com.ui.activities.upcomingMeeting

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
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
import com.domain.BaseModels.*
import com.domain.constant.AppConstants
import com.google.android.material.snackbar.Snackbar
import com.harvee.yourhealthmate2.ui.privacypolicy.ActivityPrivacyPolicy
import com.ui.activities.feedBack.ActivityFeedBackForm
import com.ui.activities.login.LoginActivity
import com.ui.activities.twilioVideo.VideoActivity
import com.ui.listadapters.UpcomingMeetingAdapter
import com.veriKlick.*
import com.veriKlick.databinding.ActivityUpcomingMeetingBinding
import com.veriKlick.databinding.LayoutDescriptionDialogBinding
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
    private var status = ""
    private var pageno = 1
    private var iscrolled = false
    private lateinit var layoutManager: LinearLayoutManager
    private var isOpenedFirst = false

    private var currentDateIST: String? = null
    private var currentDateUTC: String? = null

    private var isNextClicked = false
    private var isPreClicked = false


    private var searchTxt = ""
    private var isCallInProgress = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpcomingMeetingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(UpComingMeetingViewModel::class.java)

        //  WeeksDataHolder.setDayToZero()
        layoutManager = LinearLayoutManager(this)


        currentDateIST = getCurrentDate()!!
        if (checkInternet()) {
            status = "schedule"
            binding.tvHeader.setText(getString(R.string.txt_scheduled_meetings))
            handleUpcomingMeetingsList(0, 1, 9)
        } else {
            Snackbar.make(
                binding.root, getString(R.string.txt_no_internet_connection),
                Snackbar.LENGTH_SHORT
            ).show()
        }

        binding.btnSearchShow.setOnClickListener {
            if (binding.tvHeader.isVisible) {
                binding.layoutSearch.isVisible = true
                binding.tvHeader.isVisible = false
                binding.btnSearchShow.isVisible = false
                binding.btnCross.isVisible = true
            } else {
                binding.btnCross.isVisible = false
                binding.layoutSearch.isVisible = false
                binding.tvHeader.isVisible = true
                binding.btnSearchShow.isVisible = true
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
                pageno = 1
                handleUpcomingMeetingsList(7, 1, 9)
            } else {
                Snackbar.make(
                    binding.root, getString(R.string.txt_no_internet_connection),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnLogout.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setMessage(getString(R.string.txt_do_you_want_to_logout))
            dialog.setPositiveButton("ok", DialogInterface.OnClickListener { dialogInterface, i ->
                DataStoreHelper.clearData()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            })

            dialog.setNegativeButton(
                "cancel",
                DialogInterface.OnClickListener { dialogInterface, i ->

                })
            dialog.show()
            dialog.create()

        }
        binding.etSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                meetingsList.clear()
                pageno = 1
                isOpenedFirst = true
                searchTxt = binding.etSearch.text.toString()
                handleUpcomingMeetingsList(7, 1, 9)

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
            pageno = 1

            if (isNextClicked) {
                WeeksDataHolder.getPastISTandUTCDate(currentDateIST!!) { ist, utc ->
                    Log.d(TAG, "handleUpcomingMeetingsList: current date pre $ist")
                    currentDateIST = ist
                    currentDateUTC = utc
                }
                isNextClicked = false
            }
            handleUpcomingMeetingsList(1, 1, 9)
        }

        binding.btnRightNext.setOnClickListener {

            meetingsList.clear()
            pageno = 1

            if (isPreClicked) {
                WeeksDataHolder.getNextISTandUTCDate(currentDateIST!!) { ist, utc ->
                    Log.d(TAG, "handleUpcomingMeetingsList: current pres nex $ist")

                    currentDateIST = ist
                    currentDateUTC = utc
                }
                isPreClicked = false
            }
            handleUpcomingMeetingsList(2, 1, 9)

        }

        binding.btnCross.setOnClickListener {
            pageno = 1
            meetingsList.clear()
            searchTxt = ""
            handleUpcomingMeetingsList(7, 1, 9)
            binding.etSearch.setText("")
            if (binding.tvHeader.isVisible) {
                binding.layoutSearch.isVisible = true
                binding.tvHeader.isVisible = false
                binding.btnSearchShow.isVisible = false
                binding.btnCross.isVisible = true
            } else {
                binding.btnCross.isVisible = false
                binding.layoutSearch.isVisible = false
                binding.tvHeader.isVisible = true
                binding.btnSearchShow.isVisible = true
            }
        }

        binding.rvUpcomingMeeting.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    iscrolled = true
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val vitem = layoutManager.childCount
                val skipped = layoutManager.findFirstVisibleItemPosition()
                val totalitem = layoutManager.itemCount
                if (iscrolled && vitem + skipped == totalitem) {

                    if (contentLimit == meetingsList.size) {

                    } else {
                        Log.d(TAG, "onScrolled: " + pageno.toString())
                        handleUpcomingMeetingsList(7, pageno, 9)

                    }

                    Log.d(TAG, "onScrolled: " + pageno.toString())
                    iscrolled = false
                }
            }
        })
        setupAdapter()



    }

    private fun refereshPage() {
        if (checkInternet()) {
            meetingsList.clear()
            pageno = 1
            handleUpcomingMeetingsList(7, 1, 9)
        } else {
            Snackbar.make(
                binding.root, getString(R.string.txt_no_internet_connection),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }


    override fun onResume() {
        super.onResume()
        UpcomingMeetingStatusHolder.getRefereshStatus()?.let {
            if (UpcomingMeetingStatusHolder.getRefereshStatus()!!)
            {
                refereshPage()
                UpcomingMeetingStatusHolder.setIsRefresh(false)
            }
        }
    }

    private var ob: BodyScheduledMeetingBean? = null

    private fun handleUpcomingMeetingsList(action: Int, pageNumber: Int, pageSize: Int) {
        Log.d(TAG, "handleUpcomingMeetingsList: refreshed")
        UpcomingMeetingStatusHolder.setStatus(status)

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
            //first attempt
            0 -> {
                WeeksDataHolder.getISTandUTCDate(currentDateIST!!, 2) { ist, utc, istx, utcx ->
                    ob!!.from = utc
                    ob!!.to = utcx

                    ob!!.fromdate = ist
                    ob!!.todate = istx

                    currentDateIST = istx
                    currentDateUTC = utcx
                }
                isNextClicked = true
            }
            7 -> {

                try {

                    val dObject = WeeksDataHolder.getCurrentDates()
                    ob?.from = dObject?.utc
                    ob?.fromdate = dObject?.ist

                    ob?.to = dObject!!.utcx
                    ob?.todate = dObject?.istx


                } catch (e: Exception) {

                }
                binding.swipetorefresh.isRefreshing = false

            }
            //for past / previous
            1 -> {
                WeeksDataHolder.getDecreasedDate(currentDateIST!!) { ist, utc ->
                    currentDateIST = ist
                    currentDateUTC = utc
                }
                WeeksDataHolder.getISTandUTCDate(currentDateIST!!, 1) { ist, utc, istx, utcx ->
                    ob!!.from = utc
                    ob!!.to = utcx


                    ob!!.fromdate = ist
                    ob!!.todate = istx

                    currentDateIST = ist
                    currentDateUTC = utc
                }

                isPreClicked = true

            }
            //for next week date
            2 -> {
                WeeksDataHolder.getIncreasedDate(currentDateIST!!) { ist, utc ->
                    currentDateIST = ist
                    currentDateUTC = utc
                }

                WeeksDataHolder.getISTandUTCDate(currentDateIST!!, 2) { ist, utc, istx, utcx ->
                    ob!!.from = utc
                    ob!!.to = utcx


                    ob!!.fromdate = ist
                    ob!!.todate = istx

                    currentDateIST = istx
                    currentDateUTC = utcx


                }

                isNextClicked = true
            }

        }

        WeeksDataHolder.setCurrentTime(
            WeeksDataHolder.CurrentDatesHolderModel(
                ob?.fromdate!!,
                ob?.from!!,
                ob?.todate!!,
                ob?.to!!
            )
        )

        binding.tvFromdateToDate.text = getDateWithMonthName(
            ob!!.from.toString(),
            1
        ) + " to " + getDateWithMonthName(ob!!.to.toString(), 2)

        //  Log.d(TAG, "handleUpcomingMeetingsList: ${ob!!.from} ${ob!!.to}")
        Log.d(TAG, "\n\nhandleUpcomingMeetingsList: date ${ob!!.fromdate} ${ob!!.todate}")

        WeeksDataHolder.setDate(ob!!.fromdate!!, ob!!.todate!!)



        ob!!.Status = status
        ob!!.Search = searchTxt
        ob!!.PageNumber = pageNumber
        ob!!.PageSize = pageSize


        WeeksDataHolder.getPrevious1DateTimed18_30(ob!!.fromdate.toString()) { ist, utc ->
            ob!!.from = utc
        }

        WeeksDataHolder.getDateTimed18_29(ob!!.todate.toString()) { ist, utc ->
            ob!!.to = utc
        }

        //  Log.d(TAG, "handleUpcomingMeetingsList: ${ob?.Recruiter} ${ob?.Subscriber}")

        Log.d("datecheck", "\n ${ob?.fromdate}  ${ob?.todate}  \n ${ob!!.from}  ${ob!!.to} ")

        if (ob != null) {
            if (intent.getBooleanExtra(AppConstants.LOGIN_WITH_OTP, false)) {
                getDataWithOtp(ob!!)
                Log.d(TAG, "handleUpcomingMeetingsList: logged with otp")
            } else {
                Log.d(TAG, "handleUpcomingMeetingsList: logged with email")
                getDataWithoutOtp(ob!!)
            }
        } else {
            dismissProgressDialog()
        }
    }


    fun getDataWithOtp(ob: BodyScheduledMeetingBean) {
        viewModel.getScheduledMeetingListwithOtp(
            actionProgress = {
                if (it == 1) {
                    binding.swipetorefresh.isRefreshing = false
                    if (isOpenedFirst) {
                        Handler(Looper.getMainLooper()).post(Runnable {
                            binding.progressBar.isVisible = true
                        })
                    } else {
                        showProgressDialog()
                        isOpenedFirst = true
                    }
                } else {
                    binding.swipetorefresh.isRefreshing = false
                    dismissProgressDialog()

                    Handler(Looper.getMainLooper()).post(Runnable {
                        binding.progressBar.isVisible = false

                    })

                }
            },
            response = { result, exception, data ->
                try {
                    pageno++
                    contentLimit = data?.totalCount!!
                } catch (e: Exception) {

                }

                dismissProgressDialog()
                binding.swipetorefresh.isRefreshing = false
            },
            bodyScheduledMeetingBean = ob!!
        )
    }

    fun getDataWithoutOtp(ob: BodyScheduledMeetingBean) {
        viewModel.getScheduledMeetingList(
            actionProgress = {
                if (it == 1) {
                    binding.swipetorefresh.isRefreshing = false
                    if (isOpenedFirst) {
                        Handler(Looper.getMainLooper()).post(Runnable {
                            binding.progressBar.isVisible = true
                        })
                    } else {
                        showProgressDialog()
                        isOpenedFirst = true
                    }
                } else {
                    binding.swipetorefresh.isRefreshing = false
                    dismissProgressDialog()

                    Handler(Looper.getMainLooper()).post(Runnable {
                        binding.progressBar.isVisible = false

                    })

                }
            },
            response = { result, exception, data ->

                when (result) {
                    200 -> {
                        try {
                            pageno++
                            contentLimit = data?.totalCount!!
                        } catch (e: Exception) {

                        }
                    }
                    401 -> {
                        DataStoreHelper.clearData()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        finish()

                    }
                }


                dismissProgressDialog()
                binding.swipetorefresh.isRefreshing = false
            },
            bodyScheduledMeetingBean = ob!!
        )
    }


    private var contentLimit: Int = 1

    private val meetingsList = ArrayList<NewInterviewDetails>()
    private fun handleObserver() {



        CallStatusHolder.getCallStatus().observe(this) {
            isCallInProgress = it
        }


        Log.d(TAG, "handleObserver: out observer method ")
        binding.tvNoData.visibility = View.VISIBLE

        viewModel.scheduledMeetingLiveData.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                Log.d(TAG, "handleObserver: ifpart not null empty $it ")
                meetingsList.addAll(it)
                adapter.notifyDataSetChanged()
            }
            if (meetingsList.size == 0) {
                Log.d(TAG, "handleObserver: ifpart meeting size 0")
                binding.tvNoData.visibility = View.VISIBLE
            } else {
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
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }


    private fun setupAdapter() {
        adapter = UpcomingMeetingAdapter(this, meetingsList) { data, videoAccessCode, action ->
            when (action) {
                1 -> {
                    CurrentUpcomingMeetingData.setData(data)
                    handleJoin(data, videoAccessCode)
                }
                2 -> {
                    showDescDialog(data)
                }
                3 -> {
                    CurrentUpcomingMeetingData.setData(data)
                    Log.d(TAG, "setupAdapter : zone ${data.interviewTimezone}")
                    CurrentMeetingDataSaver.setData(
                        ResponseInterViewDetailsBean(
                            interviewModel = InterviewModel(
                                candidateId = data.candidateId!!,
                                candidate = Candidate(
                                    firstName = data.candidateFirstName,
                                    lastName = data.candidateLastName
                                )
                            ),
                            videoAccessCode = videoAccessCode,
                            interviewTimezone = data.interviewTimezone
                        )
                    )
                    val intent = Intent(this, ActivityFeedBackForm::class.java)
                    intent.putExtra(AppConstants.CANDIDATE_ID, data.candidateId)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                4 -> {
                   //showCustomSnackbarOnTop("Joining Meeting On MS Teams")
                    setHandler().postDelayed({
                        jumpToTeams(data.msMeetingUrl)
                    },2000)

                   // handleJoin(data, videoAccessCode)
                }
                5 -> {
                    CurrentUpcomingMeetingData.setData(data)
                    handleJoin(data, videoAccessCode)
                }
                6 -> {

                    setHandler().post(kotlinx.coroutines.Runnable {
                        val dialog = AlertDialog.Builder(this)
                        dialog.setMessage(getString(R.string.txt_do_you_want_to_cancel_meeting))
                        dialog.setPositiveButton(getString(R.string.txt_yes),
                            object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    cancelMeeting(data)
                                }
                            })
                        dialog.setNegativeButton(
                            "Cancel",
                            object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                }
                            })
                        dialog.create()
                        dialog.show()
                    })
                }

            }
        }
        binding.rvUpcomingMeeting.adapter = adapter
        //adapter.notifyDataSetChanged()
    }

    private fun jumpToTeams(link: String) {
        val sendIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(link)
        )

        if (sendIntent.resolveActivity(packageManager) != null) {
            startActivity(sendIntent)
        }else
        {
            startActivity(sendIntent)
        }
    }


    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_filter_upcominglist, menu)
        menu?.setHeaderTitle("Meetings")

        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.all_meetings -> {
                status = ""
                meetingsList.clear()
                pageno = 1
                binding.tvHeader.setText(getString(R.string.txt_all_meetings))
                handleUpcomingMeetingsList(7, 1, 9)
            }
            R.id.attended_meetings -> {
                status = "Attended"
                meetingsList.clear()
                pageno = 1
                binding.tvHeader.setText(getString(R.string.txt_attended))
                handleUpcomingMeetingsList(7, 1, 9)

            }
            R.id.scheduled_meetings -> {
                status = "schedule"
                meetingsList.clear()
                pageno = 1
                binding.tvHeader.setText(getString(R.string.txt_scheduled_meetings))
                handleUpcomingMeetingsList(7, 1, 9)
            }
            R.id.nonscheduled_meetings -> {
                status = "nonSchedule"
                meetingsList.clear()
                pageno = 1
                binding.tvHeader.setText(getString(R.string.txt_missed))
                handleUpcomingMeetingsList(7, 1, 9)
            }
            R.id.cancel_meetings -> {
                status = "cancel"
                meetingsList.clear()
                pageno = 1
                binding.tvHeader.setText(getString(R.string.txt_canceled))
                handleUpcomingMeetingsList(7, 1, 9)
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

         Log.d(TAG, "handleJoin: on clicked ${data.VideoCallAccessCode} videocode $videoAccessCode")
        //getInterviewDetails()
        if (checkInternet()) {
            accessCode = videoAccessCode
            if (data.mSMeetingMode.equals("veriklick")) {
                getInterviewDetails(videoAccessCode, false)
            } else {
                getInterviewDetails(videoAccessCode, true)
            }

            // getAccessCodeById(data)
            // showToast(this,"Under Development")
        } else {
            showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
            /*Snackbar.make(
                binding.root, getString(R.string.txt_no_internet_connection),
                Snackbar.LENGTH_SHORT
            ).show()*/
        }
    }


    fun getAccessCodeById(data: NewInterviewDetails) {
        Log.d(TAG, "getAccessCodeById: data gotted ${data?.interviewId}")
        viewModel.getInterAccessCodById(data.interviewId!!, onDataResponse = { data, response ->
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
                500 -> {
                    dismissProgressDialog()
                    showToast(this, "Interval Server Error!")
                }
            }
        })
    }


    fun getInterviewDetails(accessCode: String, isMsTeams: Boolean) {
        showProgressDialog()
        viewModel.getVideoSessionDetails(accessCode, onDataResponse = { data, event ->

            when (event) {
                200 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "meeting data in 200 ${data}")
                    data?.videoAccessCode = accessCode
                    CurrentMeetingDataSaver.setData(data!!)

                    if (isMsTeams) {
                        //jumpToTeams()
                       // data

                    } else {
                        joinMeeting(accessCode)
                        CallStatusHolder.setLastCallStatus(false)
                    }

                    CurrentMeetingDataSaver.setData(data)
                    // Log.d(TAG, "host : ${data.token}  ${data.roomName}")
                    // TwilioHelper.setTwilioCredentials(data.token.toString(), data.roomName.toString())
                    // startActivity(Intent(this@JoinMeetingActivity, VideoActivity::class.java))
                }
                400 -> {
                    dismissProgressDialog()
                    //showToast(this, "null values")
                    showCustomSnackbarOnTop(data?.aPIResponse?.message.toString())
                    //showToast(this, data?.aPIResponse?.message.toString())
                    /*data?.videoAccessCode=accessCode //remove all code
                    data?.let { CurrentMeetingDataSaver.setData(it) }
                    joinMeeting(accessCode)*/
                }
                404 -> {
                    dismissProgressDialog()
                    showCustomSnackbarOnTop(data?.aPIResponse?.message.toString())
                    //showToast(this, data?.aPIResponse?.message.toString())
                }
                401 -> {
                    dismissProgressDialog()
                    showCustomSnackbarOnTop(data?.aPIResponse?.message.toString())
                    /*  data?.videoAccessCode = accessCode //remove all code
                      CurrentMeetingDataSaver.setData(data!!)
                      joinMeeting(accessCode)
                      CurrentMeetingDataSaver.setData(data)*/
                }
            }
        })
    }

    fun cancelMeeting(data: NewInterviewDetails) {
        showProgressDialog()
        viewModel.cancelMeeting(data) { data, response ->

            when (response) {
                200 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "meeting data in 200 ${data}")
                    showCustomToast(data?.aPIResponse?.Message.toString())
                    meetingsList.clear()
                    pageno = 1
                    handleUpcomingMeetingsList(7, 1, 9)
                    // Log.d(TAG, "host : ${data.token}  ${data.roomName}")
                    // TwilioHelper.setTwilioCredentials(data.token.toString(), data.roomName.toString())
                    // startActivity(Intent(this@JoinMeetingActivity, VideoActivity::class.java))
                }
                400 -> {
                    dismissProgressDialog()
                    //showToast(this, "null values")
                    showCustomSnackbarOnTop(data?.aPIResponse?.Message.toString())
                    //showToast(this, data?.aPIResponse?.message.toString())
                    /*data?.videoAccessCode=accessCode //remove all code
                    data?.let { CurrentMeetingDataSaver.setData(it) }
                    joinMeeting(accessCode)*/
                }
                404 -> {
                    dismissProgressDialog()
                    showCustomSnackbarOnTop(getString(R.string.txt_something_went_wrong))
                    //showToast(this, data?.aPIResponse?.message.toString())
                }
                401 -> {
                    dismissProgressDialog()
                    showCustomSnackbarOnTop(data?.aPIResponse?.Message.toString())
                    /*  data?.videoAccessCode = accessCode //remove all code
                      CurrentMeetingDataSaver.setData(data!!)
                      joinMeeting(accessCode)
                      CurrentMeetingDataSaver.setData(data)*/
                }
            }
        }

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
                    } else {
                        Log.d("showdialogalert", "candidate: ${data.token}  ${data.roomName}")
                        TwilioHelper.setTwilioCredentials(
                            data.token.toString(),
                            data.roomName.toString()
                        )
                        requestVideoPermissions {
                            if (it) {
                                showPrivacyPolicy(binding.root as ViewGroup,
                                    onClicked = { it, dialog ->
                                        if (it) {
                                            if (isCallInProgress) {
                                                dialog.dismiss()
                                                showCustomSnackbarOnTop(getString(R.string.txt_call_in_progress))
                                            } else {
                                                val intent = Intent(this, VideoActivity::class.java)
                                                startActivity(intent)
                                                overridePendingTransition(
                                                    R.anim.slide_in_right,
                                                    R.anim.slide_out_left
                                                )
                                                dialog.dismiss()
                                            }

                                        } else {
                                            dialog.dismiss()
                                        }
                                    },
                                    onClickedText = { link, action ->
                                        privacyPolicy(link, action)
                                    }
                                )

                            } else {

                                showCustomSnackbarOnTop(getString(R.string.txt_permission_required))
                                //showToast(this,getString(R.string.txt_permission_required))
                            }
                        }

                        // val intent = Intent(this, VideoActivity::class.java)
                        // startActivity(intent)
                        // overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
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

    fun privacyPolicy(link: String, action: Int) {
        val intent = Intent(this, ActivityPrivacyPolicy::class.java)
        intent.putExtra(AppConstants.PRIVACY_LINK, link)
        intent.putExtra(AppConstants.PRIVACY_ACTION, action)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }


    fun showAlertWhenNotHost() {
        setHandler().post(kotlinx.coroutines.Runnable {
            val dialog = AlertDialog.Builder(this)
            dialog.setMessage(getString(R.string.txt_not_host_alert))
            dialog.setPositiveButton(getString(R.string.txt_join_again),
                object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        getInterviewDetails(accessCode, false)
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