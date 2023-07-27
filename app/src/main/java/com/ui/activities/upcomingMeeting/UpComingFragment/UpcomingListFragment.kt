package com.ui.activities.upcomingMeeting.UpComingFragment

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.AbsListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.data.*
import com.data.dataHolders.*
import com.data.helpers.TwilioHelper
import com.domain.BaseModels.*
import com.domain.constant.AppConstants
import com.google.gson.Gson
import com.harvee.yourhealthmate2.ui.privacypolicy.ActivityPrivacyPolicy
import com.ui.activities.feedBack.ActivityFeedBackForm
import com.ui.activities.login.LoginActivity
import com.ui.activities.twilioVideo.VideoActivity
import com.ui.activities.upcomingMeeting.UpcomingMeetingActivity
import com.ui.listadapters.UpcomingMeetingAdapter
import com.veriKlick.R
import com.veriKlick.databinding.FragmentUpcomingListBinding
import com.veriKlick.databinding.LayoutDescriptionDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import kotlin.concurrent.thread

@AndroidEntryPoint
class UpcomingListFragment(val from: String) : Fragment() {

    lateinit var binding: FragmentUpcomingListBinding
    lateinit var viewModel: VMUpcomingFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentUpcomingListBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this).get(VMUpcomingFragment::class.java)

        requireActivity().dismissProgressDialog()
        setupAdapter()
        meetingsList.clear()


        //  WeeksDataHolder.setDayToZero()
        layoutManager = LinearLayoutManagerWrapper(requireActivity())
        binding.rvUpcomingMeeting.layoutManager = layoutManager


        currentDateIST = requireActivity().getCurrentDate()!!


        handleMeetingFilter()


       /* if (requireActivity().checkInternet()) {
            status = "schedule"
            binding.tvHeader.setText(getString(R.string.txt_scheduled_meetings))
            handleUpcomingMeetingsList(0, 1, 9)
            //2jun2023 handleUpcomingMeetingsList(0, 1, 9)
        } else {
            requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
        }*/

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
            if (requireActivity().checkInternet()) {

                clearList()
                pageno = 1
                //handleMeetingFilter()
                handleUpcomingMeetingsList(7, 1, 9)
                //2jun2023 handleUpcomingMeetingsList(7, 1, 9)
            } else {
                binding.swipetorefresh.isRefreshing = false
                requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            Log.d(TAG, "onCreateView: upcoming data logged ${DataStoreHelper.getLoggedInStatus()}")
        }



        CoroutineScope(Dispatchers.Main).launch {
       if (DataStoreHelper.getLoggedInStatus()) {
                binding.btnBugerIcon.setImageDrawable(requireActivity().getDrawable(R.drawable.img_logout))
            }else
       {
           binding.btnBugerIcon.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_hamburger_menu))
       }
        }

        binding.btnBugerIcon.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                if (DataStoreHelper.getLoggedInStatus())
                {
                    val dialog = AlertDialog.Builder(requireActivity())
                    dialog.setMessage(getString(R.string.txt_do_you_want_to_logout))
                    dialog.setPositiveButton(getString(R.string.txt_yes), DialogInterface.OnClickListener { dialogInterface, i ->
                        DataStoreHelper.clearData()
                        startActivity(Intent(requireActivity(), LoginActivity::class.java))
                        requireActivity().finish()
                    })
                    dialog.setNegativeButton(
                        getString(R.string.txt_cancel),
                        DialogInterface.OnClickListener { dialogInterface, i ->

                        })
                    dialog.show()
                    dialog.create()
                }else
                {
                    (requireActivity() as UpcomingMeetingActivity).openDrawer()
                }
            }

        }


        binding.etSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                clearList()
                pageno = 1
                isOpenedFirst = true
                searchTxt = binding.etSearch.text.toString()
                if (requireActivity().checkInternet()) {
                    isOpenedFirst = false
                    handleUpcomingMeetingsList(7, 1, 9)
                } else {
                    requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                }
                hideKeyboard(requireActivity())
                return@OnEditorActionListener true
            }
            false
        })


        // handleObserver()

        binding.btnEllipsize.setOnClickListener {
            registerForContextMenu(it)
            requireActivity().openContextMenu(it)
        }


        binding.btnLeftPrevious.setOnClickListener {
            isOpenedFirst = false
            clearList()
            pageno = 1

            if (isNextClicked) {
                WeeksDataHolder.getPastISTandUTCDate(currentDateIST!!) { ist, utc ->
                    Log.d(TAG, "handleUpcomingMeetingsList: current date pre $ist")
                    currentDateIST = ist
                    currentDateUTC = utc
                }
                isNextClicked = false
            }
            if (requireActivity().checkInternet()) {
                handleUpcomingMeetingsList(1, 1, 9)
            } else {
                requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
            }
        }

        binding.btnRightNext.setOnClickListener {
            isOpenedFirst = false
            clearList()
            pageno = 1

            if (isPreClicked) {
                WeeksDataHolder.getNextISTandUTCDate(currentDateIST!!) { ist, utc ->
                    Log.d(TAG, "handleUpcomingMeetingsList: current pres nex $ist")

                    currentDateIST = ist
                    currentDateUTC = utc
                }
                isPreClicked = false
            }
            if (requireActivity().checkInternet()) {
                handleUpcomingMeetingsList(2, 1, 9)
            } else {
                requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
            }

        }

        binding.btnCross.setOnClickListener {
            isOpenedFirst = false
            pageno = 1
            clearList()
            searchTxt = ""
            if (requireActivity().checkInternet()) {
                handleUpcomingMeetingsList(7, 1, 9)
            } else {
                requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
            }


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
                try {
                    super.onScrollStateChanged(recyclerView, newState)

                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                        iscrolled = true

                } catch (e: Exception) {
                    Log.d(TAG, "onScrollStateChanged: exception 221 ${e.message}")
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                try {
                    super.onScrolled(recyclerView, dx, dy)


                    val vitem = layoutManager.childCount
                    val skipped = layoutManager.findFirstVisibleItemPosition()
                    val totalitem = layoutManager.itemCount
                    if (iscrolled && vitem + skipped == totalitem) {

                        if (contentLimit == meetingsList.size) {

                        } else {
                            Log.d(TAG, "onScrolled: " + pageno.toString())
                            if (requireActivity().checkInternet()) {
                                handleUpcomingMeetingsList(7, pageno, 9)
                            } else {
                                requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                            }

                        }

                        Log.d(TAG, "onScrolled: " + pageno.toString())
                        iscrolled = false
                    }

                } catch (e: Exception) {
                    Log.d(TAG, "onScrolled: exception 228 ${e.message}")
                }

            }
        })
       //2jun2023 setupAdapter()
        handleObserver()
       /* requireActivity().requestNearByPermissions() {
            Log.d(TAG, "onCreate: onNearbyPermission $it")
            thread {
                Thread.sleep(1000)
                try {
                    requireActivity().requestNotificationPermission {

                    }
                } catch (e: Exception) {
                    Log.d(TAG, "onCreateView: exception 285 ${e.message}")
                }

            }

        }*/

        setupDrawer()

        return binding.root
    }


    private fun checkDeepLinkIsOpenFirst(){
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "checkDeepLinkIsOpenFirst: check in coroutine")
            try {
                if (DataStoreHelper.getDeeplinkIsOpenStatus() != null && !DataStoreHelper.getDeeplinkIsOpenStatus()) {
                    requireActivity().runOnUiThread { getDeepLinkPermission {  } }

                } else {
                    requireActivity().runOnUiThread {  }

                }
            } catch (e: Exception) {
                requireActivity().runOnUiThread { getDeepLinkPermission {  } }

            }
        }

    }


    private fun getDeepLinkPermission(onFinish: () -> Unit) {
        Log.d(TAG, "getDeepLinkPermission: on deeplink dialog")
        val dialog = AlertDialog.Builder(requireActivity())
        dialog.setTitle(getString(R.string.txt_please_enable_the_deeplink))
        dialog.setPositiveButton(getString(R.string.txt_ok),object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                Log.d(TAG, "onClick: ${Build.BRAND.toString()}")
                 CoroutineScope(Dispatchers.IO).launch {
                     DataStoreHelper.setDeepLinkData(true)
                 }
                 if (Build.BRAND.toString().trim().lowercase().contains("SAMSUNG".trim().lowercase()))
                 {
                     requireActivity().showCustomSnackbarOnTop("you have to add deeplink manually")
                 }else
                 {
                     val intent = Intent(
                         Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                         Uri.parse("package:${requireActivity().packageName}")
                     )
                     startActivity(intent)

                 }
            }
        })
        dialog.setOnDismissListener {
            onFinish()
        }
        dialog.create()
        dialog.show()

    }

    private fun checkAllPermisions() {
        if (Build.VERSION.SDK_INT== Build.VERSION_CODES.TIRAMISU){
            Log.d(TAG, "checkAllPermisions: up tiramishu")

            requireActivity().requestAllPermissionForApp {
              /*  requireActivity().runOnUiThread {
                   // checkDeepLinkIsOpenFirst()
                }*/
                Log.d(TAG, "checkAllPermisions: all permission for app  tiramishu $it")
            }
        }else
        {
           try {
               requireActivity().requestAllPermissionForApp {
                   requireActivity().runOnUiThread {
                     //  checkDeepLinkIsOpenFirst()
                   }}
           }catch (e:Exception){}
            Log.d(TAG, "checkAllPermisions: below below12")
        }

    }



    private fun handleMeetingFilter() {
        Log.d(TAG, "handleMeetingFilter: come from $from")

       // viewModel.clearObserverList()

        when (from) {
            getString(R.string.txt_scheduled_meetings) -> {
                clearList()
                status = "schedule"
                binding.tvHeader.setText(getString(R.string.txt_scheduled_meetings))
                handleUpcomingMeetingsList(0, 1, 9)
            }
            getString(R.string.txt_all_meetings) -> {
                getMeetingList(0)
            }
            getString(R.string.txt_scheduled) -> {
                getMeetingList(2)
            }
            getString(R.string.txt_attended) -> {
                getMeetingList(1)
            }
            getString(R.string.txt_missed) -> {
                getMeetingList(3)
            }
            getString(R.string.txt_cancelled) -> {
                getMeetingList(4)
            }
        }

    }

    private fun clearList() {
        meetingsList
        requireActivity().runOnUiThread {
            meetingsList.clear()
            adapter.swapList(meetingsList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: upcoming list fragment")
    }

    private fun logout() {
        val dialog = AlertDialog.Builder(requireActivity())
        dialog.setMessage(getString(R.string.txt_do_you_want_to_logout))
        dialog.setPositiveButton("ok", DialogInterface.OnClickListener { dialogInterface, i ->
            DataStoreHelper.clearData()
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
            requireActivity().finish()
        })
        dialog.setNegativeButton(
            "cancel",
            DialogInterface.OnClickListener { dialogInterface, i ->

            })
        dialog.show()
        dialog.create()
    }


    fun getMeetingList(action: Int) {
        //binding=FragmentUpcomingListBinding.inflate(layoutInflater)

        when (action) {
            0 -> {
                try {
                    status = ""

                    clearList()
                    pageno = 1
                    requireActivity().runOnUiThread {
                        binding.tvHeader.setText(getString(R.string.txt_all_meetings))
                    }


                    if (requireActivity().checkInternet()) {
                        handleUpcomingMeetingsList(7, 1, 9)
                    } else {
                        requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                    }
                }catch (e:Exception)
                {
                    Log.d(TAG, "getMeetingList: exception ${e.message}")
                }

            }

            1 -> {
                status = "Attended"
                clearList()
                pageno = 1
                binding.tvHeader.setText(getString(R.string.txt_attended))
                if (requireActivity().checkInternet()) {
                    handleUpcomingMeetingsList(7, 1, 9)
                } else {
                    requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                }

            }

            2 -> {
                status = "schedule"
                clearList()
                pageno = 1
                binding.tvHeader.setText(getString(R.string.txt_scheduled))
                if (requireActivity().checkInternet()) {
                    handleUpcomingMeetingsList(7, 1, 9)
                } else {
                    requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                }
            }

            3 -> {
                status = "nonSchedule"
                clearList()
                pageno = 1
                binding.tvHeader.setText(getString(R.string.txt_missed))
                if (requireActivity().checkInternet()) {
                    handleUpcomingMeetingsList(7, 1, 9)
                } else {
                    requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                }
            }

            4 -> {
                status = "cancel"
                clearList()
                pageno = 1
                binding.tvHeader.setText(getString(R.string.txt_cancelled))
                if (requireActivity().checkInternet()) {
                    handleUpcomingMeetingsList(7, 1, 9)
                } else {
                    requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                }
            }
            /*R.id.navdrawerCandidateId->{
                Log.d(TAG, "onContextItemSelected: candidate list clicked")
            }*/
        }
    }


    companion object {

        var instance: UpcomingListFragment? = null
        fun getInstance(from: String): Fragment {
            Log.d("TAG", "getInstance: from meeting $from")
            return UpcomingListFragment(from)
        }
    }

    private val TAG = "upcomingMeeting"
    private lateinit var adapter: UpcomingMeetingAdapter
    private lateinit var accessCode: String
    private var status = ""
    private var pageno = 1
    private var iscrolled = false
    private lateinit var layoutManager: LinearLayoutManagerWrapper
    private var isOpenedFirst = false

    private var currentDateIST: String? = null
    private var currentDateUTC: String? = null

    private var isNextClicked = false
    private var isPreClicked = false


    private var searchTxt = ""
    private var isCallInProgress = false


    private fun setupDrawer() {
        //binding.drawerLayout
    }

    override fun onDestroy() {
        requireActivity().dismissProgressDialog()
        super.onDestroy()
    }

    private fun refereshPage() {
        if (requireActivity().checkInternet()) {
            clearList()
            pageno = 1
            handleUpcomingMeetingsList(7, 1, 9)
        } else {
            requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
        }
    }


    override fun onResume() {
        super.onResume()
        UpcomingMeetingStatusHolder.getRefereshStatus()?.let {
            if (UpcomingMeetingStatusHolder.getRefereshStatus()!!) {
                refereshPage()
                UpcomingMeetingStatusHolder.setIsRefresh(false)
            }
        }
    }

    private var ob: BodyScheduledMeetingBean? = null

    private fun handleUpcomingMeetingsList(action: Int, pageNumber: Int, pageSize: Int) {

        try {

            Log.d(TAG, "handleUpcomingMeetingsList: refreshed")
            UpcomingMeetingStatusHolder.setStatus(status)

            var recruiterId = ""
            var userId = ""
            var recruiterEmail = ""

            CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                recruiterId = DataStoreHelper.getMeetingRecruiterid()
                userId = DataStoreHelper.getMeetingUserId()
                recruiterEmail = DataStoreHelper.getUserEmail()
            }

            ob = BodyScheduledMeetingBean(
                Isweb = true
            )


        } catch (e: Exception) {
            requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_something_went_wrong))
        }



        try {

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
                    if (isPreClicked == true) {
                        WeeksDataHolder.getDecreasedDate(currentDateIST!!) { ist, utc ->
                            currentDateIST = ist
                            currentDateUTC = utc
                        }
                    }
                    /* WeeksDataHolder.getDecreasedDate(currentDateIST!!) { ist, utc ->
                         currentDateIST = ist
                         currentDateUTC = utc
                     }*/
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
                    if (isNextClicked == true) {
                        WeeksDataHolder.getIncreasedDate(currentDateIST!!) { ist, utc ->
                            currentDateIST = ist
                            currentDateUTC = utc
                        }
                    }
                    /* WeeksDataHolder.getIncreasedDate(currentDateIST!!) { ist, utc ->
                         currentDateIST = ist
                         currentDateUTC = utc
                     }*/

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


        } catch (e: Exception) {
            requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_something_went_wrong))
        }

        try {
            WeeksDataHolder.setCurrentTime(
                WeeksDataHolder.CurrentDatesHolderModel(
                    ob?.fromdate!!,
                    ob?.from!!,
                    ob?.todate!!,
                    ob?.to!!
                )
            )

            //from here


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

            CoroutineScope(Dispatchers.IO).launch {
                if (ob != null) {
                    Log.d(TAG, "handleUpcomingMeetingsList: logged in from ${DataStoreHelper.getLoggedInStatus()}")

                    if (DataStoreHelper.getLoggedInStatus()) {
                        if (requireActivity().checkInternet()) {
                            getDataWithOtp(ob!!)
                        } else {
                            requireActivity().runOnUiThread {
                                requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                            }
                        }

                        Log.d(TAG, "handleUpcomingMeetingsList: logged with otp")
                    } else {
                        Log.d(TAG, "handleUpcomingMeetingsList: logged with email")
                        if (requireActivity().checkInternet()) {
                            getDataWithoutOtp(ob!!)
                        } else {
                            requireActivity().run {
                            runOnUiThread { showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection)) }}
                        }
                    }
                } else {
                    requireActivity().dismissProgressDialog()
                }

            }   //to here
        } catch (e: Exception) {
            requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_something_went_wrong))
            Log.d(TAG, "handleUpcomingMeetingsList: exception 433 ${e.message}")
        }

    }

    fun getDataWithOtp(ob: BodyScheduledMeetingBean) {
        if (!isOpenedFirst)
            visibleProgressBar()
        viewModel.getScheduledMeetingListwithOtp(
            actionProgress = {
                if (it == 1) {
                    binding.swipetorefresh.isRefreshing = false
                    if (isOpenedFirst) {
                        visibleProgressBar()
                        Handler(Looper.getMainLooper()).post(Runnable {
                            //may 3 2023   binding.progressBar.isVisible = true
                        })
                    } else {
                        //requireActivity().showProgressDialog()
                        visibleRecyclerView()
                        isOpenedFirst = true
                    }
                } else {
                    binding.swipetorefresh.isRefreshing = false
                    //requireActivity().dismissProgressDialog()
                    visibleRecyclerView()
                    Handler(Looper.getMainLooper()).post(Runnable {
                        //may 3 2023  binding.progressBar.isVisible = false

                    })

                }
            },
            response = { result, exception, data ->
                try {
                    pageno++
                    contentLimit = data?.totalCount!!
                } catch (e: Exception) {

                }

                requireActivity().dismissProgressDialog()
                binding.swipetorefresh.isRefreshing = false
            },
            bodyScheduledMeetingBean = ob!!
        )
    }

    fun getDataWithoutOtp(ob: BodyScheduledMeetingBean) {
        if (!isOpenedFirst)
            visibleProgressBar()
        viewModel.getScheduledMeetingList(
            actionProgress = {
                if (it == 1) {
                    binding.swipetorefresh.isRefreshing = false
                    if (isOpenedFirst) {

                        Handler(Looper.getMainLooper()).post(Runnable {
                            //may 3 2023   binding.progressBar.isVisible = true
                        })
                    } else {
                        //25may requireActivity().showProgressDialog()
                        visibleProgressBar()
                        isOpenedFirst = true
                    }
                } else {
                    binding.swipetorefresh.isRefreshing = false
                    // requireActivity().dismissProgressDialog()
                    visibleRecyclerView()
                    Handler(Looper.getMainLooper()).post(Runnable {
                        //may 3 2023  binding.progressBar.isVisible = false

                    })

                }
            },
            response = { result, exception, data, totalSize ->

                when (result) {
                    200 -> {
                        try {
                            pageno++
                            contentLimit = data?.totalCount!!
                            adapter.totalSize = totalSize
                        } catch (e: Exception) {

                        }
                    }

                    401 -> {
                        DataStoreHelper.clearData()
                        val intent = Intent(requireActivity(), LoginActivity::class.java)
                        startActivity(intent)
                        requireActivity().overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        requireActivity().finish()

                    }
                }


                //25may2023 requireActivity().dismissProgressDialog()
                binding.swipetorefresh.isRefreshing = false
            },
            bodyScheduledMeetingBean = ob!!
        )
    }


    private var contentLimit: Int = 1

    private val meetingsList = ArrayList<NewInterviewDetails>()
    private fun handleObserver() {

        CallStatusHolder.getCallStatus().observe(requireActivity()) {
            isCallInProgress = it
        }


        Log.d(TAG, "handleObserver: out observer method ")
        //25may2023binding.tvNoData.visibility = View.VISIBLE
        //visibleProgressBar()
        viewModel.scheduledMeetingLiveData.observe(requireActivity(), Observer {
            if (it.size>0)
            {
                meetingsList.addAll(it)
                adapter.swapList(meetingsList)
                requireActivity().runOnUiThread {
                    binding.rvUpcomingMeeting.isVisible=true
                binding.tvNoData.isVisible=false
                }
            }
            try {
                if (it.size==0 && it.get(0).totalCount==0)
                {
                    meetingsList.clear()
                    adapter.notifyDataSetChanged()
                    requireActivity().runOnUiThread {
                        binding.rvUpcomingMeeting.isVisible=false
                        binding.tvNoData.isVisible=true
                    }

                }   else
                {
                    Log.d(TAG, "handleObserver: ")
                }
            }catch (e:Exception)
            {
                Log.d(TAG, "handleObserver: excl ${e.message}")
            }
            try {
                if (meetingsList.size==0)
                {
                    requireActivity().runOnUiThread {
                        binding.rvUpcomingMeeting.isVisible=false
                        binding.tvNoData.isVisible=true
                    }
                }
             /*   contentLimit
                if (contentLimit==0)
                {
                    clearList()
                    binding.tvNoData.visibility = View.VISIBLE
                }else
                {
                    binding.tvNoData.visibility = View.INVISIBLE
                }*/
                Handler(Looper.getMainLooper()).postDelayed({
                    checkAllPermisions()
                },500)

            }catch (e:Exception)
            {
                Log.d(TAG, "handleObserver: exxcep ${e.message}")
            }


           /* if (it.firstOrNull() == meetingsList.firstOrNull()) {

                try {
                    meetingsList.clear()
                    adapter.swapList(meetingsList)
                }catch (e:Exception)
                {
                    Log.d(TAG, "handleObserver: excp ${e.message}")
                }
            }
            if (!it.isNullOrEmpty()) {
                Log.d(TAG, "handleObserver: ifpart not null empty ${it.size} ")
                //3 may 2023
                meetingsList.addAll(it)
                adapter.swapList(meetingsList)
                Handler(Looper.getMainLooper()).postDelayed({
                    //adapter.notifyDataSetChanged()
                }, 500)
            }
            if (meetingsList.size == 0) {
                Log.d(TAG, "handleObserver: ifpart meeting size 0")
                try {
                    meetingsList.clear()
                    adapter.swapList(meetingsList)
                }catch (e:Exception)
                {
                    Log.d(TAG, "handleObserver: excp 851${e.message}")
                }
                binding.tvNoData.visibility = View.VISIBLE
            } else {
                Log.d(TAG, "handleObserver: elsepart meeting size not size")
                binding.tvNoData.visibility = View.GONE
            }
           */
        })
    }

    /*override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }*/


    private fun setupAdapter() {
        adapter = UpcomingMeetingAdapter(
            requireActivity(),
            meetingsList
        ) { data, videoAccessCode, action ->
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
                    val intent = Intent(requireActivity(), ActivityFeedBackForm::class.java)
                    intent.putExtra(AppConstants.CANDIDATE_ID, data.candidateId)
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )
                }

                4 -> {
                    //showCustomSnackbarOnTop("Joining Meeting On MS Teams")
                    requireActivity().setHandler().postDelayed({
                        jumpToTeams(data.msMeetingUrl)
                    }, 2000)

                    // handleJoin(data, videoAccessCode)
                }

                5 -> {
                    CurrentUpcomingMeetingData.setData(data)
                    handleJoin(data, videoAccessCode)
                }

                6 -> {

                    requireActivity().setHandler().post(kotlinx.coroutines.Runnable {
                        val dialog = AlertDialog.Builder(requireActivity())
                        dialog.setMessage(getString(R.string.txt_do_you_want_to_cancel_meeting))
                        dialog.setPositiveButton(getString(R.string.txt_yes),
                            object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    if (requireActivity().checkInternet()) {
                                        cancelMeeting(data)
                                    } else {
                                        requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                                    }

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
        requireActivity().runOnUiThread {
            binding.rvUpcomingMeeting.adapter = adapter
            binding.rvUpcomingMeeting.itemAnimator = null
        }
        //adapter.notifyDataSetChanged()
    }

    private fun jumpToTeams(link: String) {
        val sendIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(link)
        )

        if (sendIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(sendIntent)
        } else {
            startActivity(sendIntent)
        }
    }


    fun showDescDialog(data: NewInterviewDetails) {
        val dialog = Dialog(requireActivity())
        val dialogBinding = LayoutDescriptionDialogBinding.inflate(requireActivity().layoutInflater)


        /*val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(20,0,20,0)
        dialogBinding.root.setLayoutParams(params)
*/

        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogBinding.btnCross.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.tvDescription.text = data.interviewTitle
        dialogBinding.tvUserId.text = data.jobid.toString()
        dialog.create()
        dialog.show()

        /*val displaySize: Point = requireActivity()
        val width: Int = displaySize.x - 10 - 10
        dialog.window?.setLayout(width, 0)*/
    }

    fun handleJoin(data: NewInterviewDetails, videoAccessCode: String) {
        data.isVideoRecordEnabled=false
        Log.d(TAG, "handleJoin: on clicked data is ${Gson().toJson(data)}  ${data.VideoCallAccessCode} videocode $videoAccessCode")
        //getInterviewDetails()
        if (requireActivity().checkInternet()) {
            accessCode = videoAccessCode
            if (data.mSMeetingMode.equals("veriklick")) {
                getInterviewDetails(videoAccessCode, false)
            } else {
                getInterviewDetails(videoAccessCode, true)
            }

            // getAccessCodeById(data)
            // showToast(this,"Under Development")
        } else {
            requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
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
                    requireActivity().dismissProgressDialog()
                    Log.d(TAG, "getAccessCodeById: data gotted ${data?.InterviewerVideoAccesscode}")
                }

                400 -> {
                    requireActivity().dismissProgressDialog()
                    requireActivity().showToast(requireActivity(), "null values")
                }

                404 -> {
                    requireActivity().dismissProgressDialog()
                    requireActivity().showToast(requireActivity(), "response not success")
                }

                500 -> {
                    requireActivity().dismissProgressDialog()
                    requireActivity().showToast(requireActivity(), "Interval Server Error!")
                }
            }
        })
    }

    private fun visibleNotdataText() {
        requireActivity().runOnUiThread {
            binding.tvNoData.isVisible = true
            binding.rvUpcomingMeeting.isVisible = false
            binding.progressbarUpcomingList.isVisible = false
        }
    }

    private fun visibleProgressBar() {
        requireActivity().runOnUiThread {
            binding.tvNoData.isVisible = false
            binding.rvUpcomingMeeting.isVisible = false
            binding.progressbarUpcomingList.isVisible = true
        }
    }

    private fun visibleRecyclerView() {
        requireActivity().runOnUiThread {
            binding.tvNoData.isVisible = false
            binding.rvUpcomingMeeting.isVisible = true
            binding.progressbarUpcomingList.isVisible = false
        }
    }

    fun getInterviewDetails(accessCode: String, isMsTeams: Boolean) {
        //requireActivity().showProgressDialog()
        // visibleProgressBar()
        viewModel.getVideoSessionDetails(accessCode, onDataResponse = { data, event ->

            when (event) {
                200 -> {
                    requireActivity().dismissProgressDialog()
                    Log.d(TAG, "meeting data in 200 ${data}")
                    data?.videoAccessCode = accessCode
                    CurrentMeetingDataSaver.setData(data!!)
                    visibleRecyclerView()
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
                    requireActivity().dismissProgressDialog()
                    //showToast(this, "null values")
                    requireActivity().showCustomSnackbarOnTop(data?.aPIResponse?.message.toString())
                    //showToast(this, data?.aPIResponse?.message.toString())
                    /*data?.videoAccessCode=accessCode //remove all code
                    data?.let { CurrentMeetingDataSaver.setData(it) }
                    joinMeeting(accessCode)*/
                }

                404 -> {
                    requireActivity().dismissProgressDialog()
                    requireActivity().showCustomSnackbarOnTop(data?.aPIResponse?.message.toString())
                    //showToast(this, data?.aPIResponse?.message.toString())
                }

                401 -> {
                    requireActivity().dismissProgressDialog()
                    requireActivity().showCustomSnackbarOnTop(data?.aPIResponse?.message.toString())
                    /*  data?.videoAccessCode = accessCode //remove all code
                      CurrentMeetingDataSaver.setData(data!!)
                      joinMeeting(accessCode)
                      CurrentMeetingDataSaver.setData(data)*/
                }
            }
        })
    }

    fun cancelMeeting(data: NewInterviewDetails) {
        requireActivity().showProgressDialog()
        viewModel.cancelMeeting(data) { data, response ->

            when (response) {
                200 -> {
                    requireActivity().dismissProgressDialog()
                    Log.d(TAG, "meeting data in 200 ${data}")
                    requireActivity().showCustomToast(data?.aPIResponse?.Message.toString())
                    clearList()
                    pageno = 1
                    if (requireActivity().checkInternet()) {
                        handleUpcomingMeetingsList(7, 1, 9)
                    } else {
                        requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                    }
                    // Log.d(TAG, "host : ${data.token}  ${data.roomName}")
                    // TwilioHelper.setTwilioCredentials(data.token.toString(), data.roomName.toString())
                    // startActivity(Intent(this@JoinMeetingActivity, VideoActivity::class.java))
                }

                400 -> {
                    requireActivity().dismissProgressDialog()
                    //showToast(this, "null values")
                    requireActivity().showCustomSnackbarOnTop(data?.aPIResponse?.Message.toString())
                    //showToast(this, data?.aPIResponse?.message.toString())
                    /*data?.videoAccessCode=accessCode //remove all code
                    data?.let { CurrentMeetingDataSaver.setData(it) }
                    joinMeeting(accessCode)*/
                }

                404 -> {
                    requireActivity().dismissProgressDialog()
                    requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_something_went_wrong))
                    //showToast(this, data?.aPIResponse?.message.toString())
                }

                401 -> {
                    requireActivity().dismissProgressDialog()
                    requireActivity().showCustomSnackbarOnTop(data?.aPIResponse?.Message.toString())
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
                    requireActivity().dismissProgressDialog()
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
                        requireActivity().requestCameraAndMicPermissions {
                            if (it) {
                                requireActivity().showPrivacyPolicy(binding.root as ViewGroup,
                                    onClicked = { it, dialog ->
                                        if (it) {
                                            if (isCallInProgress) {
                                                dialog.dismiss()
                                                requireActivity().showCustomSnackbarOnTop(
                                                    getString(
                                                        R.string.txt_call_in_progress
                                                    )
                                                )
                                            } else {
                                                val intent = Intent(
                                                    requireActivity(),
                                                    VideoActivity::class.java
                                                )
                                                startActivity(intent)
                                                /* overridePendingTransition(
                                                     R.anim.slide_in_right,
                                                     R.anim.slide_out_left
                                                 )*/
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

                                requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_permission_required))
                                //showToast(this,getString(R.string.txt_permission_required))
                            }
                        }

                        // val intent = Intent(this, VideoActivity::class.java)
                        // startActivity(intent)
                        // overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                }

                400 -> {
                    requireActivity().dismissProgressDialog()
                    requireActivity().showToast(requireActivity(), "null values")
                }

                404 -> {
                    requireActivity().dismissProgressDialog()
                    requireActivity().showToast(requireActivity(), "response not success")
                }
            }
        })
    }

    fun privacyPolicy(link: String, action: Int) {
        val intent = Intent(requireActivity(), ActivityPrivacyPolicy::class.java)
        intent.putExtra(AppConstants.PRIVACY_LINK, link)
        intent.putExtra(AppConstants.PRIVACY_ACTION, action)
        startActivity(intent)
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }


    fun showAlertWhenNotHost() {
        requireActivity().setHandler().post(kotlinx.coroutines.Runnable {
            val dialog = AlertDialog.Builder(requireActivity())
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

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        val inflater = requireActivity().menuInflater
        inflater.inflate(R.menu.menu_filter_upcominglist, menu)
        menu?.setHeaderTitle(getString(R.string.txt_meetings))

        super.onCreateContextMenu(menu, v, menuInfo)
    }


    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.all_meetings -> {
                status = ""
                clearList()
                pageno = 1
                binding.tvHeader.setText(getString(R.string.txt_all_meetings))
                if (requireActivity().checkInternet()) {
                    handleUpcomingMeetingsList(7, 1, 9)
                } else {
                    requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                }
            }

            R.id.attended_meetings -> {
                status = "Attended"
                clearList()
                pageno = 1
                binding.tvHeader.setText(getString(R.string.txt_attended))
                if (requireActivity().checkInternet()) {
                    handleUpcomingMeetingsList(7, 1, 9)
                } else {
                    requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                }

            }

            R.id.scheduled_meetings -> {
                status = "schedule"
                clearList()
                pageno = 1
                binding.tvHeader.setText(getString(R.string.txt_scheduled_meetings))
                if (requireActivity().checkInternet()) {
                    handleUpcomingMeetingsList(7, 1, 9)
                } else {
                    requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                }
            }

            R.id.nonscheduled_meetings -> {
                status = "nonSchedule"

                clearList()
                pageno = 1
                binding.tvHeader.setText(getString(R.string.txt_missed))
                if (requireActivity().checkInternet()) {
                    handleUpcomingMeetingsList(7, 1, 9)
                } else {
                    requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                }
            }

            R.id.cancel_meetings -> {
                status = "cancel"
                clearList()
                pageno = 1
                binding.tvHeader.setText(getString(R.string.txt_cancelled))
                if (requireActivity().checkInternet()) {
                    handleUpcomingMeetingsList(7, 1, 9)
                } else {
                    requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                }
            }
            /*R.id.navdrawerCandidateId->{
                Log.d(TAG, "onContextItemSelected: candidate list clicked")
            }*/
        }

        return super.onContextItemSelected(item)
    }


}
