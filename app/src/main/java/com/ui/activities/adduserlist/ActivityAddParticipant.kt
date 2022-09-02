package com.ui.activities.adduserlist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.data.*
import com.domain.BaseModels.AddInterviewerList
import com.example.twillioproject.R
import com.example.twillioproject.databinding.ActivityLayoutAddParticipantBinding
import com.ui.activities.twilioVideo.VideoActivity

import com.ui.listadapters.AddParticipantListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import java.util.*
import kotlin.math.log
import kotlin.random.Random

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
            onClick = { data: InvitationDataModel, action: Int, pos:Int,list ->
                when (action) {
                    1 -> {
                        addNewInterViewer()
                        Log.d(TAG, "onCreate: ")
                    }
                    2->{
                        removeItem(data,pos)
                    }
                }
            })
        binding.rvAdduserByLink.adapter = adapter

        binding.btnPostdata.setOnClickListener {
            handleList()
        }

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

    val invitationList= mutableListOf<InvitationDataModel>()
    val distinctinvitationList= mutableListOf<InvitationDataModel>()

    fun addFilledDataList(obj:InvitationDataModel)
    {
        invitationList.add(obj)
    }

    fun postInvitation()
    {
        if (!invitationList.isNullOrEmpty()){

            distinctinvitationList.addAll(invitationList.distinctBy { it.uid })
            if (!isEmpty)
            {
            sendInvitation()
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
        showProgressDialog()
        viewModel.sendInvitationtoUsers(distinctinvitationList, onDataResponse = {
            data, action ->

            when (action) {
                200 -> {
                    dismissProgressDialog()
                 showToast(this,getString(R.string.txt_Invitation_sent))
                }
                400 -> {
                    dismissProgressDialog()
                    showToast(this,getString(R.string.txt_failed_to_Invitation))
                }
                404 -> {

                    dismissProgressDialog()
                    showToast(this,getString(R.string.txt_failed_to_Invitation))
                }
            }


        })


    }


    private val interviewList = mutableListOf<InvitationDataModel>()

    private fun addNewInterViewer()  {
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