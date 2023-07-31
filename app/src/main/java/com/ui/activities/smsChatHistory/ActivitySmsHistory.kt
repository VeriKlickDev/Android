package com.ui.activities.smsChatHistory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.data.dismissProgressDialog
import com.data.showProgressDialog
import com.domain.BaseModels.BodySMSchatHistory
import com.domain.BaseModels.ResponseSMSchatHistory
import com.domain.BaseModels.SMSchatHistoryModel
import com.domain.constant.AppConstants
import com.ui.listadapters.SMSchatHistoryListAdapter
import com.veriKlick.R
import com.veriKlick.databinding.ActivitySmsHistoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivitySmsHistory : AppCompatActivity() {

    private lateinit var viewModel:SmsHistoryViewModel
    private lateinit var binding:ActivitySmsHistoryBinding
    private var chatHistoryAdapter:SMSchatHistoryListAdapter?=null
    private  var smsList= mutableListOf<SMSchatHistoryModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySmsHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel= ViewModelProvider(this)[SmsHistoryViewModel::class.java]
        setupListAdapter()
        getSMSList()
        binding.btnJumpBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }


    private fun getSMSList()
    {
        runOnUiThread { showProgressDialog() }
        viewModel?.getSmsHistoryList(
            intent.getStringExtra(AppConstants.CANDIDATE_ID).toString()
        ){data, isSuccess, errorCode, msg ->
       // viewModel?.getSmsHistoryList("9993".toString()){data, isSuccess, errorCode, msg ->

            Log.d("TAG", "getChatList: sms array $data")
            runOnUiThread { dismissProgressDialog() }
            if (!data.isNullOrEmpty())
            {
                smsList.clear()
                smsList.addAll(data)
                runOnUiThread {
                    chatHistoryAdapter?.notifyDataSetChanged()
                    binding.tvNoData.isVisible=false
                    binding.rvSmsHistory.isVisible=true
                }
            }else
            {
                runOnUiThread {
                    binding.rvSmsHistory.isVisible=false
                    binding.tvNoData.isVisible=true
                }

            }
        }
    }

    private fun setupListAdapter()
    {
        binding.rvSmsHistory.layoutManager=LinearLayoutManager(this)
        chatHistoryAdapter= SMSchatHistoryListAdapter(this,smsList){data, action ->

        }
        binding.rvSmsHistory.adapter=chatHistoryAdapter
    }

}