package com.ui.activities.feedBack

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.data.*
import com.data.dataHolders.CurrentMeetingDataSaver
import com.domain.BaseModels.*
import com.domain.constant.AppConstants
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
    private var appliedPosition: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBackFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(FeedBackViewModel::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            Log.d(
                TAG,
                "onCreate: recruiterId= ${CurrentMeetingDataSaver.getData().interviewModel?.recruiterId.toString()} "
            )
        }



        skillsAdapter =
            SkillsListAdapter(this, skillsList) { pos, data, action ->
                when (action) {
                    1 -> {
                        // addNewItem()
                    }
                    2 -> {
                        removeItem(pos)
                    }
                }
            }
        binding.rvCandidateSkills.adapter = skillsAdapter

//        binding.spinnerInterviewRemark.onItemSelectedListener =
//            spinnerItemListener


        binding.btnSubmitButton.setOnClickListener {
            if (checkInternet()) {
                binding.btnSubmitButton.isEnabled=false
                if (recommendationSelected != null) {
                    binding.recommendationError.isVisible = false
                } else {
                    binding.recommendationError.isVisible = true
                }

                setVisible()

                sendFeedBack()

            } else {
                showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
               /* Snackbar.make(
                    it,
                    getString(R.string.txt_no_internet_connection),
                    Snackbar.LENGTH_SHORT
                ).show()*/
            }
        }

        binding.btnJumpBack.setOnClickListener {
            onBackPressed()
        }
        binding.tvAddMoreItem.setOnClickListener {
            addNewItem()
        }

        if (checkInternet()) {
            getFeedBack()
        } else {
            showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
           /* Snackbar.make(
                binding.root,
                getString(R.string.txt_no_internet_connection),
                Snackbar.LENGTH_SHORT
            ).show()*/
        }

        handleViews()

        // getInterviewDetails("mkpeHcXKbF95uRiWiLzJ")
    }

    fun handleViews()
    {

        binding.etRemart.doOnTextChanged { text, start, before, count ->
           // binding.remarkError.isVisible = binding.etRemart.text.toString().equals("")
        }

        binding.etRole.doOnTextChanged { text, start, before, count ->
          //  binding.roleError.isVisible = binding.etRole.text.toString().equals("")
        }

        binding.etOverallRemark.doOnTextChanged { text, start, before, count ->
           // binding.overallRemarkError.isVisible = binding.etOverallRemark.text.toString().equals("")
        }

    }



    fun setVisible() {
        if (binding.etRemart.text.toString().trim()
                .equals("")
        ) {
           // binding.remarkError.isVisible = true
        } else {
           // binding.remarkError.isVisible = false
        }
      /*  if (binding.etOverallRemark.text.toString()
                .equals("")
        ) {
            binding.overallRemarkError.isVisible = true
        } else {
            binding.overallRemarkError.isVisible = false
        }
        if (binding.etRole.text.toString().equals("")) {
            binding.roleError.isVisible = true
        } else {
            binding.roleError.isVisible = false
        }*/
    }


    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    fun addNewItem() {
        Log.d(TAG, "addNewItem: before ${skillsList.size} ")
        skillsList.add(AssessSkills(value = "other", Comments = ""))
        skillsAdapter.notifyItemInserted(skillsList.size)
        Log.d(TAG, "addNewItem: AFTER ${skillsList.size}")
    }

    fun removeItem(pos: Int) {
        Log.d(TAG, "remove item:before ${skillsList.size} pos $pos")
        try {

            if (pos != -1)
                skillsList.removeAt(pos)
            // skillsAdapter.notifyItemRemoved(pos)
            skillsAdapter.notifyDataSetChanged()
            Log.d(TAG, "remove item:after ${skillsList.size}")
        } catch (e: Exception) {

        }
    }

    private var isBlank = false
    fun sendFeedBack() {
        Log.d(TAG, "sendFeedBack: list size ${skillsAdapter.getFeedBackList().size}")
        Log.d(TAG, "sendFeedBack: list size ${skillsAdapter.getFeedBackList()}")
        isBlank = false

        skillsAdapter.getFeedBackList().forEach {
            Log.d(TAG, "sendFeedBack: cat ${it.Catagory} comment ${it.Comments}")
            if (it.Comments.equals("")) {//it.Catagory.equals("") ||
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
            binding.skillsError.isVisible = false
            postFeedback()

        } else {
            binding.skillsError.isVisible = true
            binding.btnSubmitButton.isEnabled=true
            showCustomSnackbarOnTop(getString(R.string.txt_all_fields_required))
            //showToast(this, getString(R.string.txt_all_fields_required))
        }
    }

    fun postFeedback() {
        /*if (!binding.etRemart.text.toString()
                .equals("") && !binding.etRole.text.toString().equals("")
        )*/
        postData()/*working 30_nov
        if (!binding.etRemart.text.toString()
                .equals("") ) {

        } else {
            binding.btnSubmitButton.isEnabled=true
            showCustomSnackbarOnTop(getString(R.string.txt_all_fields_required))
            //showToast(this, getString(R.string.txt_all_fields_required))
        }*/
    }

    private var assesmentid = 0
    fun postData() {
        val skillist = ArrayList<CandidateAssessmentSkills>()
        skillsAdapter.getFeedBackList().forEach {
            skillist.add(
                CandidateAssessmentSkills(
                    it.CandidateAssessmentSkillsId,
                    it.CandidateAssessmentId,
                    it.Comments!!,
                    it.Ratings?.toInt(),
                    it.Catagory,
                    it.ManualCatagory,
                    it.CandiateAssessment
                )
            )
            Log.d(
                TAG,
                "postData: check all data  appliedpos skillist assesment Id ${it.CandidateAssessmentId}  ${it.CandiateAssessment} "
            )
        }

        Log.d(TAG, "postData: list data is $skillist")
        val candidateId = intent.getIntExtra(AppConstants.CANDIDATE_ID, 0)

        if (recommendationSelected != null) {
            binding.recommendationError.isVisible = false

            //  Log.d(TAG, "postData: check all data  appliedpos $appliedPosition recomm $recommendationSelected designation $designation intername $interviewName candidId $candidateId CANDIDATENAME ${CurrentMeetingDataSaver.getData().interviewModel?.candidate?.firstName+ CurrentMeetingDataSaver.getData().interviewModel?.candidate?.lastName} ")
            var codingRemark="null"
            if (binding.etOverallRemark.text.toString().equals(""))
            {

            }else
            {
                codingRemark=binding.etOverallRemark.text.toString()
            }

            var role=""
            if (binding.etRole.text.toString().equals(""))
            {

            }else
            {
                role=binding.etRole.text.toString()
            }


            var remark=""
            if (binding.etRemart.text.toString().equals(""))
            {

            }else
            {
                remark=binding.etRemart.text.toString()
            }




            viewModel.sendFeedback(this,
                assesmentid,
                role,
                appliedPosition,
                recommendationSelected!!,
                role,
                interviewName,
                candidateId,
                remark,
                codingRemark,
                BodyFeedBack(
                    CandidateAssessmentSkills = skillist,
                ),
                skillsListres,
                remarkList,
                onDataResponse = { data, status ->
                    when (status) {
                        404 -> {
                            binding.btnSubmitButton.isEnabled=true
                        }
                        200 -> {
                            showCustomToast(data?.aPIResponse?.message!!.toString())
                            //showCustomSnackbarOnTop(data?.aPIResponse?.message!!)
                            //showToast(this, data?.aPIResponse?.message!!)
                            Handler(Looper.getMainLooper()).postDelayed({
                                finish()
                            },1000)
                        }
                        400 -> {
                            binding.btnSubmitButton.isEnabled=true
                        }
                        401 -> {
                            binding.btnSubmitButton.isEnabled=true
                        }
                        500 -> {
                            binding.btnSubmitButton.isEnabled=true
                            showCustomSnackbarOnTop(getString(R.string.txt_something_went_wrong))
                            //showToast(this, getString(R.string.txt_something_went_wrong))
                        }
                    }

                    Log.d(
                        TAG,
                        "postData: data status $status ${data?.jobid}  ${data?.aPIResponse?.message}"
                    )
                   // finish()
                })

        } else {
            binding.recommendationError.isVisible = true
            binding.btnSubmitButton.isEnabled=true
            showCustomSnackbarOnTop(getString(R.string.txt_all_fields_required))
            //showToast(this, getString(R.string.txt_all_fields_required))
        }

    }

    private var recommendationSelected: String? = null
    private var isOpenedFirst = false

    private val spinnerItemListener = object : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            Log.d(TAG, "onItemSelected: selected ${recommendationList.get(position)}")
            if (position==0){
              //  ( view as TextView).setTextColor(ContextCompat.getColor(this@ActivityFeedBackForm,R.color.grey))
            }else{
              //  ( view as TextView).setTextColor(ContextCompat.getColor(this@ActivityFeedBackForm,R.color.black))
                recommendationSelected=recommendationList[position].toString()
            }

        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            Log.d(TAG, "onNothingSelected: notthing selected")
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
                    showCustomSnackbarOnTop(data?.aPIResponse?.message!!)
                    //showToast(this, data?.aPIResponse?.message!!)
                    //showToast(this, "null values")
                    data?.let { CurrentMeetingDataSaver.setData(it) }
                    getFeedBack()
                }
                404 -> {
                    Log.d(TAG, "getInterviewDetails: 404")
                    showCustomSnackbarOnTop(data?.aPIResponse?.message!!)
                    //showToast(this, data?.aPIResponse?.message!!)
                    //showToast(this, data?.aPIResponse?.message.toString())
                }
                401 -> {
                    Log.d(TAG, "getInterviewDetails: 401")
                    showCustomSnackbarOnTop(data?.aPIResponse?.message!!)
                    //showToast(this, data?.aPIResponse?.message!!)
                    CurrentMeetingDataSaver.setData(data!!)
                    getFeedBack()
                }
            }
        })
    }

    private val remarkList = arrayListOf<InterviewerRemark>()
    private val skillsListres = arrayListOf<AssessSkills>()
    fun getFeedBack() {
        // showProgressDialog()
        viewModel.getFeedBack(onResponse = { data, status ->

            when (status) {
                200 -> {
                    dismissProgressDialog()
                    remarkList.addAll(data?.InterviewerRemark!!)
                    skillsListres.addAll(data.assessSkills)
                    Log.d(TAG, "getResume: success ondata response")
                    setDataToViews(data!!)
                    assesmentid = data.AssessmentId!!
                    //   binding.swipetorefresh.isRefreshing = false
                }
                400 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "getResume: not success ondata 400")
                    showCustomSnackbarOnTop(getString(com.example.twillioproject.R.string.txt_something_went_wrong))
                   /* showToast(
                        this,
                        getString(com.example.twillioproject.R.string.txt_something_went_wrong)
                    )*/
                    // binding.swipetorefresh.isRefreshing = false
                }
                401 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "getResume: 401")
                }
                404 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "getResume: not success ondata 404")
                    showCustomSnackbarOnTop(getString(com.example.twillioproject.R.string.txt_something_went_wrong))
                    /*showToast(
                        this,
                        getString(com.example.twillioproject.R.string.txt_something_went_wrong)
                    )*/
                    // binding.swipetorefresh.isRefreshing = false
                }
                500 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "getResume: not success ondata 404")
                    showCustomSnackbarOnTop(getString(com.example.twillioproject.R.string.txt_something_went_wrong))
                    /*showToast(
                        this,
                        getString(com.example.twillioproject.R.string.txt_something_went_wrong)
                    )*/
                    // binding.swipetorefresh.isRefreshing = false
                }
            }
        })

    }


    private var interviewName = ""
    private var designation = ""



    private val recommendationList = mutableListOf<String>()
    fun setDataToViews(data: ResponseFeedBack) {

        Handler(Looper.getMainLooper()).post(kotlinx.coroutines.Runnable {

            try {
                interviewName = data.CandidateAssessmentPanelMembers.firstOrNull()?.Name.toString()
                designation = data.CandidateAssessmentPanelMembers.firstOrNull()?.Designation.toString()

                binding.tvName.text = ""
                data.CandidateAssessmentPanelMembers.firstOrNull()?.Name?.let {
                    binding.tvName.text = data.CandidateAssessmentPanelMembers.firstOrNull()?.Name
                }

                binding.tvJobId.text = data.jobid.toString()

                binding.tvDescription.text = data.AppliedPostion

                appliedPosition = data.AppliedPostion.toString()

                if (!data.Recommendation.toString().equals("") || data.Recommendation!=null)
                {
                    binding.etRemart.setText(data.Recommendation)
                  //  binding.remarkError.isVisible=false
                }else
                {
                    binding.etRemart.setText("")
                   // binding.remarkError.isVisible=true
                }


                try {
                    if (!data.CandidateAssessmentPanelMembers[0].Designation.toString().equals("null") || data.CandidateAssessmentPanelMembers[0].Designation!=null) {
                            binding.etRole.setText(data.CandidateAssessmentPanelMembers[0].Designation!!.toString())

                       // binding.roleError.isVisible=false
                    }else
                    {
                       // binding.roleError.isVisible=true
                    }

                }catch (e:Exception)
                {
                    Log.d(
                        TAG,
                        "setDataToViews: exception candidate designation ${e.printStackTrace()}")
                }

                recommendationList.add(0, "Select Recommendation")

                data.InterviewerRemark.forEach {
                    recommendationList.add(it.Remark.toString())
                }
                Log.e(TAG, "setDataToViews: " + (recommendationList.size ?: 0).toString())
                spinnerAdapter =
                    object :
                        ArrayAdapter<String>(
                            this, android.R.layout.simple_spinner_dropdown_item,
                            recommendationList
                        ) {
                        override fun getDropDownView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {

                            var v: View? = null

                            // If this is the initial dummy entry, make it hidden

                            // If this is the initial dummy entry, make it hidden
                            if (position === 0) {
                                val tv = TextView(context)
                                tv.height = 0
                                //  tv.visibility = View.GONE
                                v = tv
                            } else {
                                // Pass convertView as null to prevent reuse of special case views
                                v = super.getDropDownView(
                                    position,
                                    null,
                                    parent
                                )
                            }

                            // Hide scroll bar because it appears sometimes unnecessarily, this does not prevent scrolling

                            // Hide scroll bar because it appears sometimes unnecessarily, this does not prevent scrolling

                            return v!!

                        }
                    }
                binding.spinnerInterviewRemark.adapter = spinnerAdapter
                binding.spinnerInterviewRemark.onItemSelectedListener = spinnerItemListener

                spinnerAdapter.notifyDataSetChanged()

                //  spinnerAdapter.notifyDataSetChanged()

                if (data.CodingTestRemarksForVideo.toString().equals("null"))
                {
                    binding.etOverallRemark.setText("")
                }else
                {
                    binding.etOverallRemark.setText(data.CodingTestRemarksForVideo.toString())
                }



                if (!data.Comments.equals("null") || data.Comments!=null) {
                    val spinnerPos = spinnerAdapter.getPosition(data.Comments.toString())
                    binding.spinnerInterviewRemark.setSelection(spinnerPos)
                    binding.recommendationError.isVisible=false
                }else
                {
                    binding.recommendationError.isVisible=true
                }


                if (!data.CandidateAssessmentSkills.isNullOrEmpty())
                    if (data.CandidateAssessmentSkills[0].Catagory != null && !data.CandidateAssessmentSkills[0].Catagory.equals(
                            "null"
                        )
                    ) {
                        binding.btnSubmitButton.setText("Update")
                        skillsList.addAll(data.CandidateAssessmentSkills)
                    } else {
                        binding.btnSubmitButton.setText("Submit")
                        skillsList.addAll(data.assessSkills)
                    }
                Log.d(TAG, "setDataToViews: listdata $skillsList")
                skillsAdapter.notifyDataSetChanged()

            }catch (e:Exception)
            {
                Log.d(TAG, "setDataToViews: on data res exception ${e.message}")
            }


        })
    }


}