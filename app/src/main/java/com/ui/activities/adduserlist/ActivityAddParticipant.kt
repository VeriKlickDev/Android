package com.ui.activities.adduserlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.data.*
import com.data.dataHolders.*
import com.google.android.material.snackbar.Snackbar

import com.ui.listadapters.AddParticipantListAdapter
import com.veriKlick.R
import com.veriKlick.databinding.ActivityLayoutAddParticipantBinding

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.*

@AndroidEntryPoint
class ActivityAddParticipant : AppCompatActivity() {

    private lateinit var binding: ActivityLayoutAddParticipantBinding
    private lateinit var adapter: AddParticipantListAdapter
    private var TAG = "adapteraddcheck"
    private lateinit var viewModel: AddUserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLayoutAddParticipantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val randomStr = UUID.randomUUID()

       /* CoroutineScope(Dispatchers.IO).launch {
            delay(500)
            interviewList.add(InvitationDataModel(uid = randomStr.toString()))//, InterviewerTimezone = CurrentMeetingDataSaver.getData().interviewModel?.interviewTimezone.toString()))
            withContext(Dispatchers.Main)
            {
                adapter.notifyDataSetChanged()
            }
        }*/



        Log.d(
            TAG,
            "onCreate: interviewtimezone ${CurrentMeetingDataSaver.getData().interviewModel?.interviewTimezone}"
        )

        viewModel = ViewModelProvider(this).get(AddUserViewModel::class.java)

        checkTotalParticipant(1)

        binding.rvAdduserByLink.layoutManager = LinearLayoutManager(this)

        binding.btnJumpBackAddmember.setOnClickListener {
            onBackPressed()
        }

        adapter = AddParticipantListAdapter(
            viewModel,
            this,
            interviewList,
            onClick = { data: InvitationDataModel, action: Int, pos: Int, list ->
                when (action) {
                    1 -> {
                        checkTotalParticipant(2)
                        Log.d(TAG, "onCreate: ")
                    }
                    2 -> {
                        removeItem(data, pos)
                    }
                }
            }, onEditextChanged = { txt, action, position ->
                when (action) {
                    1 -> {
                        if (checkInternet()) {
                            checkEmailExists(txt, position)
                        } else {
                            Snackbar.make(
                                binding.root,
                                getString(R.string.txt_no_internet_connection),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                    2 -> {
                        if (checkInternet()) {
                            checkPhoneExists(txt, position)
                        } else {
                            Snackbar.make(
                                binding.root,
                                getString(R.string.txt_no_internet_connection),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            })
        binding.rvAdduserByLink.adapter = adapter

        binding.btnPostdata.setOnClickListener {
            hideKeyboard(this)
            handleList()
        }
        handleObserver()
    }

    private fun handleObserver() {

        CallStatusHolder.getCallStatus().observe(this) {
            if (it) {
                CallStatusHolder.setLastCallStatus(true)
                finish()
                Log.d(TAG, "handleObserver: true part")
            } else {
                Log.d(TAG, "handleObserver: false part")
            }
        }

        UpcomingMeetingStatusHolder.getIsMeetingFinished().observe(this){
            if (it)
            {
                UpcomingMeetingStatusHolder.setIsRefresh(false)
                finish()
            }
        }


    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun checkTotalParticipant(actionPerformed: Int) {
        viewModel.getTotoalCountOfInterviewer(
            CurrentMeetingDataSaver.getData().videoAccessCode!!,
            onResponse = { action, data ->
                when (action) {
                    200 -> {
                        if (interviewList.size + data?.TotalInterviewerCount!!.toInt() > 6) {
                            showCustomToast(getString(R.string.txt_cant_add_more_participant))
                           // Handler(Looper.getMainLooper()).postDelayed({finish()},1000)
                            if (actionPerformed==1)
                            {
                                binding.btnPostdata.visibility=View.GONE
                            }else
                            {

                            }
                        } else {
                            addNewInterViewer(actionPerformed)
                        }
                    }
                    400 -> {
                        showToast(this, data?.Message!!)
                    }
                    401 -> {
                        Log.d(TAG, "checkTotalParticipant: null response")
                    }
                    404 -> {
                        Log.d(TAG, "checkTotalParticipant: null response")
                    }
                    500 -> {
                        Log.d(TAG, "checkTotalParticipant:exception")
                    }
                }
            })
    }

    var isEmpty = false
    fun handleList() {
        val list = adapter.getInterviewList()
        val isEmptylist = mutableListOf<Boolean>()
        if (!list.isNullOrEmpty()) {
            list.forEach {
                Log.d(TAG, "handleList: $it")
            }
            list.forEach {
                if (!it.firstName.toString().equals("") &&
                    !it.lastName.toString().equals("") &&
                    !it.email.toString().equals("") &&
                    !it.phone.toString().equals("")
                ) {
                    addFilledDataList(it)
                    isEmptylist.add(false)
                    Log.d(TAG, "handleList: fields are filled")
                } else {
                    isEmptylist.add(true)
                    Log.d(TAG, "handleList: empty fields")
                    showToast(this, getString(R.string.txt_all_fields_required))
                }
            }

            val isFiledEmpty = isEmptylist.any { it == true }
            if (isFiledEmpty) {
                isEmpty = true
            } else {
                isEmpty = false
            }

            postInvitation()

        } else {
            Log.d(TAG, "handleList: list is empty")
        }
    }

    private fun checkEmailExists(txt: String, position: Int) {
        viewModel.getIsEmailExists(
            CurrentMeetingDataSaver.getData().interviewModel?.interviewId!!,
            txt,
            "",
            response = { isExists ->
                if (!isExists) {
                    adapter.handleEmailIsExists(position, 1, isExists)
                } else {
                    adapter.handleEmailIsExists(position, 2, isExists)
                    showCustomToast(getString(R.string.txt_email_already_exists))
                    //showToast(this,getString(R.string.txt_email_already_exists))
                }
            })
        Log.d(TAG, "checkEmailExists: check text $txt")
    }

    private fun checkPhoneExists(txt: String, position: Int) {
        viewModel.getIsPhoneExists(
            CurrentMeetingDataSaver.getData().interviewModel?.interviewId!!,
            "",
            txt,
            response = { isExists ->
                if (!isExists) {
                    adapter.handlePhoneIsExists(position, 1, isExists)
                    Log.d(TAG, "checkPhoneExists: email false")
                } else {
                    Log.d(TAG, "checkPhoneExists: email true")
                    adapter.handlePhoneIsExists(position, 2, isExists)
                    showCustomToast(getString(R.string.txt_phone_already_exists))
                    //showToast(this,getString(R.string.txt_phone_already_exists))
                }
            })
        Log.d(TAG, "checkEmailExists: check text $txt")
    }

    val invitationList = mutableListOf<InvitationDataModel>()
    val distinctinvitationList = mutableListOf<InvitationDataModel>()

    fun addFilledDataList(obj: InvitationDataModel) {
        //Log.d(TAG, "addFilledDataList:  timezone is ${obj.InterviewerTimezone}")
        invitationList.add(obj)
    }

    fun postInvitation() {
        if (!invitationList.isNullOrEmpty()) {

            Log.d(TAG, "postInvitation:  tim zone $invitationList")
            distinctinvitationList.addAll(invitationList.distinctBy { it.uid })
            var isErrorOccur = false
            if (!distinctinvitationList.isNullOrEmpty()) {

                distinctinvitationList.forEachIndexed { index, invitationDataModel ->
                    if (!invitationDataModel.isFirstNameError && !invitationDataModel.isLastNameError && !invitationDataModel.isEmailError && !invitationDataModel.isPhoneError) {
                        isErrorOccur = true
                    }else
                    {
                        isErrorOccur = false
                    }
                }
            }

            if (isErrorOccur){

                if (!isEmpty) {
                    if (checkInternet()) {
                        // checkIsErrorExists()
                        //showCustomToast("posted")
                        sendInvitation()
                    } else {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.txt_no_internet_connection),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    Log.d(TAG, "postInvitation:  posting data")
                } else {
                    showCustomToast(getString(R.string.txt_all_fields_required))
                    //showToast(this,getString(R.string.txt_all_fields_required))
                    Log.d(TAG, "postInvitation:  fill all fields first")
                }

            }else
            {
                showCustomToast(getString(R.string.txt_please_enter_valid_info))
            }




        } else {
            Log.d(TAG, "postInvitation: invitation list is null")
        }
    }

    fun sendInvitation() {
        binding.invitationProgress.visibility = View.VISIBLE
        binding.btnPostdata.text = ""
        binding.btnPostdata.isEnabled = false
        val tem=CurrentMeetingDataSaver.getData()
        tem
        distinctinvitationList
        viewModel.sendInvitationtoUsers(distinctinvitationList, onDataResponse = { data, action ->
            var isSuccess=false
            when (action) {
                200 -> {
                    Handler(Looper.getMainLooper()).post({

                        binding.btnPostdata.text = getString(R.string.txt_invite)
                        binding.invitationProgress.visibility = View.INVISIBLE
                        //Toast.makeText(this,data?.APIResponse?.Message!!,Toast.LENGTH_LONG).show()
                        //showToast(this,data?.APIResponse?.Message!!)
                        //Snackbar.make(binding.root,data?.APIResponse?.Message!!,Snackbar.LENGTH_LONG).show()
                        //showCustomSnackbarOnTop(data?.APIResponse?.Message!!)
                        showCustomToast(data?.APIResponse?.Message!!)
                        isSuccess=true
                        interviewList.clear()
                        adapter.notifyDataSetChanged()
                        Log.d(TAG, "sendInvitation: result $data")
                        binding.btnPostdata.isEnabled = true
                    })
                    Handler(Looper.getMainLooper()).postDelayed({ finish() }, 1000)
                }
                400 -> {
                    Handler(Looper.getMainLooper()).post({

                        binding.btnPostdata.text = getString(R.string.txt_invite)
                        binding.invitationProgress.visibility = INVISIBLE
                        showCustomToast(data?.APIResponse?.Message!!)
                        //showCustomSnackbarOnTop(data?.APIResponse?.Message!!)
                        //showToast(this,data?.APIResponse?.Message!!)
                        binding.btnPostdata.isEnabled = true

                    })

                }
                404 -> {
                    Handler(Looper.getMainLooper()).post({

                        binding.btnPostdata.text = getString(R.string.txt_invite)
                        binding.invitationProgress.visibility = INVISIBLE
                        binding.btnPostdata.isEnabled = true

                    })

                    //  showToast(this,getString(R.string.txt_failed_to_Invitation))
                }
                404 -> {
                    Handler(Looper.getMainLooper()).post({

                        binding.btnPostdata.text = getString(R.string.txt_invite)
                        binding.invitationProgress.visibility = INVISIBLE
                        binding.btnPostdata.isEnabled = true

                    })

                    //  showToast(this,getString(R.string.txt_failed_to_Invitation))
                }
                404 -> {
                    Handler(Looper.getMainLooper()).post({

                        binding.btnPostdata.text = getString(R.string.txt_invite)
                        binding.invitationProgress.visibility = INVISIBLE
                        binding.btnPostdata.isEnabled = true

                    })
                }
                500 -> {
                  //  Log.d(TAG, "sendInvitation: ${data!!.APIResponse?.Message}")
                    // showToast(this,getString(R.string.txt_something_went_wrong))
                    Handler(Looper.getMainLooper()).post({
                        binding.btnPostdata.text = getString(R.string.txt_invite)
                        binding.invitationProgress.visibility = INVISIBLE
                        binding.btnPostdata.isEnabled = true
                        if (!isSuccess)
                        {
                            showCustomToast(getString(R.string.txt_something_went_wrong))
                            Handler(Looper.getMainLooper()).postDelayed({finish()},1000)
                        }else
                        {

                        }
                    })
                   // Handler(Looper.getMainLooper()).postDelayed({finish()},1000)

                }
            }
        })
    }

    private val interviewList = mutableListOf<InvitationDataModel>()

    private fun checkIsErrorExists()
    {
        if (!interviewList.isNullOrEmpty()){
            var isErrorOccur=false
            interviewList.forEachIndexed { index, invitationDataModel ->
                if (invitationDataModel.isFirstNameError) {
                    isErrorOccur=true
                }
            }
            if (isErrorOccur)
            {
                setSubmitButtonEnable(false)
                showCustomToast(getString(R.string.txt_data_already_exists))
            }else
            {
               // addParticipantItem()
                setSubmitButtonEnable(true)
            }
        }
    }


    private fun addNewInterViewer(action: Int) {


        if (action==2){
        if (!interviewList.isNullOrEmpty()){
            var isErrorOccur=false
            interviewList.forEachIndexed { index, invitationDataModel ->
                if (invitationDataModel.isFirstNameError && invitationDataModel.isLastNameError && invitationDataModel.isEmailError && invitationDataModel.isPhoneError) {
                    isErrorOccur=true
                }
            }

            if (isErrorOccur)
            {
               // setSubmitButtonEnable(false)
//                showCustomToast(getString(R.string.txt_data_already_exists))
            }else
            {
                addParticipantItem()
               // setSubmitButtonEnable(true)
            }
        }
        }else{
            addParticipantItem()
        }

    }

    private fun setSubmitButtonEnable(isEnable:Boolean)
    {
        binding.btnPostdata.isEnabled=isEnable
        if (!isEnable)
        {
            binding.btnPostdata.alpha=0.6f
        }else
        {
            binding.btnPostdata.alpha=1f
        }
    }

    override fun onResume() {
        super.onResume()
        if (CallStatusHolder.checkCallOnResume())
        {
            finish()
        }
    }

    private fun addParticipantItem()
    {
        val randomStr = UUID.randomUUID()
//        InvitationDataHolder.setItem(InvitationDataModel(uid = randomStr.toString(), index = -1))
        //, InterviewerTimezone =CurrentMeetingDataSaver.getData().interviewModel?.interviewTimezone.toString()
        val element = InvitationDataModel(uid = randomStr.toString())
        interviewList.add(element)
        val pos = interviewList.indexOf(element)
        // InvitationDataHolder.setItemToList(InvitationDataModel(uid = randomStr.toString() , index = interviewList.size-1))
        //uncomment adapter.notifyItemInserted(pos)
         adapter.notifyDataSetChanged()
        //adapter.notifyDataSetChanged()
        Log.d(TAG, "addNewInterViewer: ${interviewList.size}")

        adapter.dataList.forEach {
            Log.d(TAG, "addNewInterViewer: list item ${it.index}  ${it.firstName} ${it.uid}")
        }
        binding.scrollview.post {
            binding.scrollview.fullScroll(View.FOCUS_DOWN);
        }
    }


    override fun onPause() {
        interviewList.clear()
        InvitationDataHolder.getList().clear()
        super.onPause()
    }


    fun removeItem(data: InvitationDataModel, pos: Int) {
        try {
            interviewList.removeAt(pos)

            adapter.notifyDataSetChanged()
            Log.d(TAG, "removeItem: notify data set changed")
        } catch (e: Exception) {
            Log.d(TAG, "removeItem:exception ${e.printStackTrace()} ")
        }
    }
}
/*
  if (interviewList.size==2)
            {
                interviewList.removeAt(pos)
                adapter.notifyDataSetChanged()
                Log.d(TAG, "removeItem: notify data set changed")
            }else
            {
                interviewList.removeAt(pos)
                adapter.notifyDataSetChanged()
                Log.d(TAG, "removeItem: notify data set changed")
            }

*/