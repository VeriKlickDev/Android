package com.ui.activities.adduserlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.data.*
import com.data.dataHolders.CurrentConnectUserList
import com.data.dataHolders.CurrentMeetingDataSaver
import com.data.dataHolders.InvitationDataHolder
import com.data.dataHolders.InvitationDataModel
import com.domain.BaseModels.ResponseTotalInterviewerCount
import com.example.twillioproject.R
import com.example.twillioproject.databinding.ActivityLayoutAddParticipantBinding
import com.google.android.material.snackbar.Snackbar

import com.ui.listadapters.AddParticipantListAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ActivityAddParticipant : AppCompatActivity() {
    private lateinit var binding: ActivityLayoutAddParticipantBinding
    private lateinit var adapter: AddParticipantListAdapter
    private var TAG = "adapteraddcheck"
    private lateinit var viewModel:AddUserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLayoutAddParticipantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val randomStr=UUID.randomUUID()
        interviewList.add(InvitationDataModel(uid = randomStr.toString()))
        viewModel=ViewModelProvider(this).get(AddUserViewModel::class.java)
        binding.rvAdduserByLink.layoutManager = LinearLayoutManager(this)

        adapter = AddParticipantListAdapter(
            this,
            interviewList,
            onClick = { data: InvitationDataModel, action: Int, pos:Int, list ->
                when (action) {
                    1 -> {
                        checkTotalParticipant()
                        Log.d(TAG, "onCreate: ")
                    }
                    2->{
                        removeItem(data,pos)
                    }
                }
            }, onEditextChanged = { txt, action,position ->
                    when(action)
                    {
                        1->{
                            if (checkInternet())
                            {
                                checkEmailExists(txt,position)
                            }else
                            {
                                Snackbar.make(binding.root,getString(com.example.twillioproject.R.string.txt_no_internet_connection),
                                    Snackbar.LENGTH_SHORT).show()
                            }

                        }
                        2->{
                            if (checkInternet())
                            {
                                checkPhoneExists(txt,position)
                            }else
                            {
                                Snackbar.make(binding.root,getString(com.example.twillioproject.R.string.txt_no_internet_connection),
                                    Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
            })
        binding.rvAdduserByLink.adapter = adapter

        binding.btnPostdata.setOnClickListener {
            handleList()
        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }

    private fun checkTotalParticipant()
    {
        viewModel.getTotoalCountOfInterviewer(CurrentMeetingDataSaver.getData().videoAccessCode!!, onResponse = {action, data ->
            when(action)
            {
                200->{
                    if(interviewList.size+data?.TotalInterviewerCount!!.toInt()>6)
                    {
                        showToast(this,getString(R.string.txt_cant_add_more_participant))
                    }else
                    {
                        addNewInterViewer(data?.TotalInterviewerCount!!)
                    }
                }
                400->{
                    showToast(this,data?.Message!!)
                }
                401->{
                    Log.d(TAG, "checkTotalParticipant: null response")
                }
                404->{
                    Log.d(TAG, "checkTotalParticipant: null response")
                }
                500->{
                    Log.d(TAG, "checkTotalParticipant:exception")
                }
            }
        })


    }


    var isEmpty=false
    fun handleList()
    {
        val list=adapter.getInterviewList()
        val isEmptylist= mutableListOf<Boolean>()
        if (!list.isNullOrEmpty())
        {
            list.forEach {
                Log.d(TAG, "handleList: $it")
            }
            list.forEach {
                if (!it.firstName.toString().equals("") &&
                    !it.lastName.toString().equals("") &&
                    !it.email.toString().equals("") &&
                   !it.phone.toString().equals("")
                ){
                    addFilledDataList(it)
                    isEmptylist.add(false)
                    Log.d(TAG, "handleList: fields are filled")
                }
                else{
                    isEmptylist.add(true)
                    Log.d(TAG, "handleList: empty fields")
                    showToast(this,getString(R.string.txt_all_fields_required))
                }
            }

            val isFiledEmpty=isEmptylist.any { it==true }
            if (isFiledEmpty)
            {
                isEmpty=true
            }else
            {
            isEmpty=false
            }

            postInvitation()

        }else
        {
            Log.d(TAG, "handleList: list is empty")
        }
    }

    private fun checkEmailExists(txt: String ,position:Int)
    {
        viewModel.getIsEmailAndPhoneExists(CurrentMeetingDataSaver.getData().interviewModel?.interviewId!!,txt,"", response = {
            isExists ->
                if (!isExists)
                {
                    adapter.handleEmailIsExists(position,1)
                }else
                {
                    adapter.handleEmailIsExists(position,2)
                    showToast(this,getString(R.string.txt_email_already_exists))
                }
        })
        Log.d(TAG, "checkEmailExists: check text $txt")
    }

    private fun checkPhoneExists(txt: String,position:Int)
    {
        viewModel.getIsEmailAndPhoneExists(CurrentMeetingDataSaver.getData().interviewModel?.interviewId!!,"",txt, response = {
                isExists ->
            if (!isExists)
            {
                adapter.handlePhoneIsExists(position,1)
                Log.d(TAG, "checkPhoneExists: email false")
            }else
            {
                Log.d(TAG, "checkPhoneExists: email true")
                adapter.handlePhoneIsExists(position,2)
                showToast(this,getString(R.string.txt_phone_already_exists))
            }
        })
        Log.d(TAG, "checkEmailExists: check text $txt")
    }

    val invitationList= mutableListOf<InvitationDataModel>()
    val distinctinvitationList= mutableListOf<InvitationDataModel>()

    fun addFilledDataList(obj: InvitationDataModel)
    {
        invitationList.add(obj)
    }

    fun postInvitation()
    {
        if (!invitationList.isNullOrEmpty()){

            distinctinvitationList.addAll(invitationList.distinctBy { it.uid })
            if (!isEmpty)
            {
                if (checkInternet())
                {
                    sendInvitation()
                }else
                {
                    Snackbar.make(binding.root,getString(com.example.twillioproject.R.string.txt_no_internet_connection),
                        Snackbar.LENGTH_SHORT).show()
                }
                Log.d(TAG, "postInvitation:  posting data")
            }else
            {
                showToast(this,getString(R.string.txt_all_fields_required))
                Log.d(TAG, "postInvitation:  fill all fields first")
            }

        }else
        {
            Log.d(TAG, "postInvitation: invitation list is null")
        }
    }

    fun sendInvitation()
    {
        binding.invitationProgress.isVisible=true
        binding.btnPostdata.text=""
        viewModel.sendInvitationtoUsers(distinctinvitationList, onDataResponse = {
            data, action ->

            when (action) {
                200 -> {
                    binding.btnPostdata.text=getString(R.string.txt_invite)
                    binding.invitationProgress.isVisible=false
                     showToast(this,data?.APIResponse?.Message!!)
                    interviewList.clear()
                    onBackPressed()
                    Log.d(TAG, "sendInvitation: result $data")
                }
                400 -> {
                    binding.btnPostdata.text=getString(R.string.txt_invite)
                    binding.invitationProgress.isVisible=false
                    showToast(this,data?.APIResponse?.Message!!)
                }
                404 -> {
                    binding.btnPostdata.text=getString(R.string.txt_invite)
                    binding.invitationProgress.isVisible=false
                    showToast(this,getString(R.string.txt_failed_to_Invitation))
                }
                404 -> {
                    binding.btnPostdata.text=getString(R.string.txt_invite)
                    binding.invitationProgress.isVisible=false
                    showToast(this,getString(R.string.txt_failed_to_Invitation))
                }
            }

        })
    }

    private val interviewList = mutableListOf<InvitationDataModel>()

    private fun addNewInterViewer(interviewerCount: Int)  {
        val randomStr=UUID.randomUUID()
//        InvitationDataHolder.setItem(InvitationDataModel(uid = randomStr.toString(), index = -1))

        val element = InvitationDataModel(uid = randomStr.toString())
        interviewList.add(element)
       // InvitationDataHolder.setItemToList(InvitationDataModel(uid = randomStr.toString() , index = interviewList.size-1))
        adapter.notifyItemInserted(interviewList.indexOf(element))
        Log.d(TAG, "addNewInterViewer: ${interviewList.size}")

        adapter.dataList.forEach {
            Log.d(TAG, "addNewInterViewer: list item ${it.index}  ${it.firstName} ${it.uid}")
        }

    }

    override fun onPause() {
        interviewList.clear()
        InvitationDataHolder.getList().clear()
        super.onPause()
    }


    fun removeItem(data: InvitationDataModel, pos: Int)
    {
        try {
            interviewList.removeAt(pos)

            adapter.notifyDataSetChanged()
            Log.d(TAG, "removeItem: notify data set changed")
        }catch (e:Exception)
        {
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