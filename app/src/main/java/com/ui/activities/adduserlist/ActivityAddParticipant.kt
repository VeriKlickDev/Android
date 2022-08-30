package com.ui.activities.adduserlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.data.InvitationDataHolder
import com.data.InvitationDataModel
import com.domain.BaseModels.AddInterviewerList
import com.example.twillioproject.databinding.ActivityLayoutAddParticipantBinding

import com.ui.listadapters.AddParticipantListAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.math.log
import kotlin.random.Random

@AndroidEntryPoint
class ActivityAddParticipant : AppCompatActivity() {
    private lateinit var binding: ActivityLayoutAddParticipantBinding
    private lateinit var adapter: AddParticipantListAdapter
    private var TAG = "adapteraddcheck"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLayoutAddParticipantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try{
            val randomStr=UUID.randomUUID().toString()
            Log.d(TAG, "onCreate: random $randomStr")
            interviewList.add(InvitationDataModel(uid = randomStr, index = -1))
        }catch (e:Exception)
        {
            Log.d(TAG, "onCreate: exception ${e.printStackTrace()}")
        }

        binding.rvAdduserByLink.layoutManager = LinearLayoutManager(this)
        adapter = AddParticipantListAdapter(
            this,
            interviewList,
            onClick = { data: InvitationDataModel, action: Int, pos:Int ->
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
    }

    private val interviewList = mutableListOf<InvitationDataModel>()

    private fun addNewInterViewer()  {
        val randomStr=UUID.randomUUID()
        InvitationDataHolder.setItem(InvitationDataModel(uid = randomStr.toString(), index = -1))
        interviewList.add(InvitationDataModel(uid = randomStr.toString(), index = -1))
        adapter.notifyDataSetChanged()
        Log.d(TAG, "addNewInterViewer: ${interviewList.size}")

        InvitationDataHolder.getList().forEach {
            Log.d(TAG, "addNewInterViewer: ${it.uid} ${it.index}")

        }


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