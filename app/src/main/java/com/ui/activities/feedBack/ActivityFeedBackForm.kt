package com.ui.activities.feedBack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.SpinnerAdapter
import androidx.lifecycle.ViewModelProvider
import com.data.*
import com.data.dataHolders.CurrentMeetingDataSaver
import com.data.dataHolders.DataStoreHelper
import com.domain.BaseModels.*
import com.example.twillioproject.R
import com.example.twillioproject.databinding.ActivityFeedBackFormBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import layout.SkillsListAdapter

@AndroidEntryPoint
class ActivityFeedBackForm : AppCompatActivity() {

    private val TAG = "feedbackformActi"
    private lateinit var binding: ActivityFeedBackFormBinding
    private lateinit var viewModel: FeedBackViewModel
    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private lateinit var skillsAdapter: SkillsListAdapter
    private val skillsList = mutableListOf<AssessSkills>()
    private var appliedPosition:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBackFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(FeedBackViewModel::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "onCreate: recruiterId= ${CurrentMeetingDataSaver.getData().interviewModel?.recruiterId.toString()} ${DataStoreHelper.getMeetingRecruiterid()}")
        }

        
        spinnerAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, listOf())
        binding.spinnerInterviewRemark.adapter = spinnerAdapter

        skillsAdapter = SkillsListAdapter(this, skillsList){pos, data,action ->
            when (action){
                1->{
                    // addNewItem()
                }
                2->{
                    removeItem(pos)
                }
            }
        }
        binding.rvCandidateSkills.adapter = skillsAdapter

        binding.spinnerInterviewRemark.onItemSelectedListener = spinnerItemListener


        binding.btnSubmitButton.setOnClickListener {
            if (checkInternet()){
                sendFeedBack()
            }else
            {
                Snackbar.make(it,getString(R.string.txt_no_internet_connection), Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.btnJumpBack.setOnClickListener {
            onBackPressed()
        }
        binding.tvAddMoreItem.setOnClickListener {
            addNewItem()
        }

        if (checkInternet()){
            getFeedBack()
        }else
        {
            Snackbar.make(binding.root,getString(R.string.txt_no_internet_connection), Snackbar.LENGTH_SHORT).show()
        }

        // getInterviewDetails("mkpeHcXKbF95uRiWiLzJ")
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }

    fun addNewItem()
    {
        skillsList.add(AssessSkills(value = "others"))
        skillsAdapter.notifyItemInserted(skillsList.size)
    }

    fun removeItem(pos:Int)
    {
        skillsList.removeAt(pos)
        skillsAdapter.notifyItemRemoved(pos)
    }

    private var isBlank = false
    fun sendFeedBack() {
        Log.d(TAG, "sendFeedBack: list size ${skillsAdapter.getFeedBackList().size}")
        Log.d(TAG, "sendFeedBack: list size ${skillsAdapter.getFeedBackList()}")
        isBlank=false

        skillsAdapter.getFeedBackList().forEach {
            Log.d(TAG, "sendFeedBack: cat ${it.Catagory} comment ${it.Comments}")
            if ( it.Comments.equals("")) {//it.Catagory.equals("") ||
                Log.d(TAG, "sendFeedBack:  blank data")
                isBlank = true
                // showToast(this, getString(R.string.txt_all_fields_required))
            }
            /*else {
                Log.d(TAG,"sendFeedBack: not blank data")
                isBlank = true
            }*/
        }

        if (!isBlank) {
            postFeedback()
        }
        else{
            showToast(this, getString(R.string.txt_all_fields_required))
        }
    }

    fun postFeedback() {
        if (!binding.etRemart.text.toString()
                .equals("") && !binding.etCodingRemart.text.toString()
                .equals("") && !binding.etRole.text.toString().equals("")
        ) {
            postData()
        }
        else {
            showToast(this, getString(R.string.txt_all_fields_required))
        }
    }

    fun postData()
    {
        val skillist= ArrayList<CandidateAssessmentSkills>()
        skillsAdapter.getFeedBackList().forEach {
            skillist.add(CandidateAssessmentSkills(it.CandidateAssessmentSkillsId,it.CandidateAssessmentId,it.Comments,it.Id,it.Catagory,it.ManualCatagory,it.CandiateAssessment))
        }

        viewModel.sendFeedback(this,appliedPosition,recommendationSelected,designation,interviewName,BodyFeedBack(
            CandidateAssessmentSkills=skillist,
        ), onDataResponse = {data, status ->
            finish()
            Log.d(TAG, "postData: data ${data?.jobid}  ${data?.aPIResponse?.message}")

        })
    }

    private var recommendationSelected = ""
    val spinnerItemListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            Log.d(TAG, "onItemSelected: selected ${recommendationList.get(position)}")
            recommendationSelected = recommendationList.get(position)
            binding.tvSpinnerTitle.text=recommendationList[position]
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

    }

    /**testing*/
    fun getInterviewDetails(accessCode: String) {

        viewModel.getVideoSessionDetails(accessCode, onDataResponse = { data, event ->
            showProgressDialog()
            when (event) {
                200 -> {
                    Log.d(TAG, "meeting data in 200 ${data}")
                    CurrentMeetingDataSaver.setData(data!!)
                    getFeedBack()
                    CurrentMeetingDataSaver.setData(data)
                    // Log.d(TAG, "host : ${data.token}  ${data.roomName}")
                    // TwilioHelper.setTwilioCredentials(data.token.toString(), data.roomName.toString())
                    // startActivity(Intent(this@JoinMeetingActivity, VideoActivity::class.java))
                }
                400 -> {
                    Log.d(TAG, "getInterviewDetails: 400")
                    showToast(this, data?.aPIResponse?.message!!)
                    //showToast(this, "null values")
                    data?.let { CurrentMeetingDataSaver.setData(it) }
                    getFeedBack()
                }
                404 -> {
                    Log.d(TAG, "getInterviewDetails: 404")
                    showToast(this, data?.aPIResponse?.message!!)
                    showToast(this, data?.aPIResponse?.message.toString())
                }
                401 -> {
                    Log.d(TAG, "getInterviewDetails: 401")
                    showToast(this, data?.aPIResponse?.message!!)
                    CurrentMeetingDataSaver.setData(data!!)
                    getFeedBack()

                }
            }
        })

    }


    fun getFeedBack() {
        // showProgressDialog()
        viewModel.getFeedBack(onResponse = { data, status ->

            when (status) {
                200 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "getResume: success ondata response")
                    setDataToViews(data!!)
                    //   binding.swipetorefresh.isRefreshing = false
                }
                400 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "getResume: not success ondata 400")
                    showToast(
                        this,
                        getString(com.example.twillioproject.R.string.txt_something_went_wrong)
                    )
                    // binding.swipetorefresh.isRefreshing = false
                }
                401 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "getResume: 401")
                }
                404 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "getResume: not success ondata 404")
                    showToast(
                        this,
                        getString(com.example.twillioproject.R.string.txt_something_went_wrong)
                    )
                    // binding.swipetorefresh.isRefreshing = false
                }
                500 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "getResume: not success ondata 404")
                    showToast(
                        this,
                        getString(com.example.twillioproject.R.string.txt_something_went_wrong)
                    )
                    // binding.swipetorefresh.isRefreshing = false
                }
            }
        })

    }


    private var interviewName=""
    private var designation=""

    private val recommendationList = mutableListOf<String>()
    fun setDataToViews(data: ResponseFeedBack) {

        Handler(Looper.getMainLooper()).post(kotlinx.coroutines.Runnable {

            interviewName=data.CandidateAssessmentPanelMembers.firstOrNull()?.Name.toString()
            designation=data.CandidateAssessmentPanelMembers.firstOrNull()?.Designation.toString()


            binding.tvName.text=""
            data.CandidateAssessmentPanelMembers.firstOrNull()?.Name?.let {
                binding.tvName.text=data.CandidateAssessmentPanelMembers.firstOrNull()?.Name
            }

            binding.tvJobId.text = data.jobid.toString()
            binding.tvDescription.text = data.AppliedPostion
            appliedPosition=data.AppliedPostion.toString()
            data.InterviewerRemark.forEach {
                recommendationList.add(it.Remark.toString())
            }

            spinnerAdapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_expandable_list_item_1,
                recommendationList
            )
            binding.spinnerInterviewRemark.adapter = spinnerAdapter
            spinnerAdapter.notifyDataSetChanged()

            skillsList.addAll(data.assessSkills)
            skillsAdapter.notifyDataSetChanged()


        })
    }


}