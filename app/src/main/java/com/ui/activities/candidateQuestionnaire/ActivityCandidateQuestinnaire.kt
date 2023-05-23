package com.ui.activities.candidateQuestionnaire

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TimePicker
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.data.*
import com.domain.BaseModels.*
import com.google.gson.Gson
import com.ui.listadapters.CandidateQuestionnaireListAdapter
import com.veriKlick.R
import com.veriKlick.databinding.ActivityCandidateQuestinnaireBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ActivityCandidateQuestinnaire : AppCompatActivity() {
    private var binding:ActivityCandidateQuestinnaireBinding?=null
    private var viewModel:VMCandidateQuestionnaire?=null
    private var questionAdapter:CandidateQuestionnaireListAdapter?=null
    private var questionList= arrayListOf<Question>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityCandidateQuestinnaireBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        viewModel=ViewModelProvider(this).get(VMCandidateQuestionnaire::class.java)
        questionAdapter = CandidateQuestionnaireListAdapter(this,questionList!!){data: CandidateQuestionnaireModel, action: Int ->

        }
        binding?.rvQuestions?.layoutManager=LinearLayoutManager(this)
        binding?.rvQuestions?.adapter=questionAdapter

        //addQuestion()
        getQuestionnaireList()
        binding?.btnSubmit?.setOnClickListener {
            getAllQuestionsList()
        }
        binding?.btnJumpBack?.setOnClickListener {
        onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun getQuestionnaireList()
    {
        try {
            val deepLinkingIntent = intent
            val pathstr=deepLinkingIntent.data!!.path

            val pathlist=pathstr?.split("/")
            val accessToken=pathlist?.get(pathlist!!.size-1)
            val candidateId=pathlist?.get(3)
            val templateId=pathlist?.get(4)
            runOnUiThread { showProgressDialog() }
            viewModel?.getQuestionnaireList(templateId.toString(),accessToken.toString()){data, isSuccess, errorCode, msg ->
                if (isSuccess)
                {
                    //val list=data?.QuestionList
                    runOnUiThread { dismissProgressDialog() }
                    runOnUiThread {
                        questionList.addAll(data?.QuestionList?.get(0)?.Question!!)
                        questionAdapter?.notifyDataSetChanged()
                    }
                }else
                {
                    runOnUiThread {
                        runOnUiThread { dismissProgressDialog() }
                        showCustomSnackbarOnTop(getString(R.string.txt_something_went_wrong))
                    }
                }
            }
        }catch (e:Exception)
        {
            Log.d("TAG", "getQuestionnaireList: question exception ${e.message}")
        }
    }


    private var isAnswer=false
    private fun getAllQuestionsList(){
        if (!questionAdapter?.list.isNullOrEmpty()){
            isAnswer= questionAdapter?.list?.any { it.Answer==null || it.Answer?.OptionDesc.equals("") } == false
        }
        if (isAnswer)
        {
            buildQuestionnaireData()
        }else
        {
            showCustomSnackbarOnTop(getString(R.string.txt_all_quesiton_required))
        }

    }

    private fun scheduleMeetingDate(bodyQuestionnaire: BodyQuestionnaire)
    {
        runOnUiThread {
            val alertDialog= AlertDialog.Builder(this)
            alertDialog.setNegativeButton(getString(R.string.txt_skip),object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    runOnUiThread {
                        bodyQuestionnaire.Availability_For_Interview=getCurrentUtcFormatedDate()
                        postQuestionnaireData(bodyQuestionnaire)
                    }

                }
            })
            alertDialog.setPositiveButton(getString(R.string.txt_yes),object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                   // bodyQuestionnaire.Availability_For_Interview=
                   // postQuestionnaireData(bodyQuestionnaire)
                    runOnUiThread {
                        selectDate {
                            bodyQuestionnaire.Availability_For_Interview=it
                            postQuestionnaireData(bodyQuestionnaire)
                        }
                    }
                }
            })
            alertDialog.setTitle(getString(R.string.txt_do_you_want_to_send_sms))
            alertDialog.create()
            alertDialog.show()
        }


    }

    private val TAG="timeselectorcheck"
    fun selectDate(timeSelected: (time: String) -> Unit) {
        Log.d(TAG, "selectTime: method")

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            // Display Selected date in textbox
            val scheduleDate="$year-$monthOfYear-${dayOfMonth}"
            var scheduleFullDate=""
            var isTimeSelected=false
            selectTime {
                isTimeSelected=true
                scheduleFullDate="${scheduleDate}T${it}:00Z"
                Log.d(TAG, "selectDate: time selected $scheduleFullDate")
                timeSelected(scheduleFullDate)
            }
        }, year, month, day)

        dpd.show()

       /* */
    }


    private fun selectTime(timeSelected: (time: String?) -> Unit)
    {
        val timePicker = TimePickerDialog(
            this,
            R.style.TimePickerTheme,
            object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    Log.d(TAG, "onTimeSet: hourofday $hourOfDay min ${minute}")
                    if (minute == 0) {
                        if (hourOfDay > 12) {
                            timeSelected("$hourOfDay:" + minute + "0" + "PM")
                        }
                        else {
                            timeSelected("$hourOfDay:" + minute + "0" + "AM")
                        }
                    }
                    else {
                        timeSelected("$hourOfDay:$minute")

                        if (hourOfDay > 12) {
                            //timeSelected("$hourOfDay:$minute" + "PM")
                            timeSelected("$hourOfDay:$minute")
                        }
                        else {
                            //timeSelected("$hourOfDay:$minute" + "AM")
                            timeSelected("$hourOfDay:$minute")
                        }
                    }
                }
            },
            12,
            60,
            false
        )
        timePicker
        //timePicker.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        timePicker.create()
        timePicker.show()
    }



    private fun postQuestionnaireData(bodyQuestionnaire: BodyQuestionnaire)
    {
        runOnUiThread { showProgressDialog() }
        viewModel?.postQuestionnaires(bodyQuestionnaire){data, isSuccess, errorCode, msg ->
            runOnUiThread {
                if (isSuccess)
                {
                    runOnUiThread { dismissProgressDialog() }
                    setHandler().postDelayed({
                        showCustomSnackbarOnTop(msg)
                    },500)
                    if (isSuccess){
                        setHandler().postDelayed(Runnable {
                            finish()
                        },2000)
                    }
                }
            }
        }

    }

    private fun buildQuestionnaireData()
    {
        val deepLinkingIntent = intent
        val pathstr=deepLinkingIntent.data!!.path

        val pathlist=pathstr?.split("/")
        val accessToken=pathlist?.get(pathlist!!.size-1)
        val candidateId=pathlist?.get(3)
        val templateId=pathlist?.get(4)

       // questionAdapter?.list
        try {

            val answerList= mutableListOf<AnswerMasterModels>()
            val bodyQuestionnaire=BodyQuestionnaire()
            if (!questionAdapter?.list.isNullOrEmpty())
            {
                questionAdapter?.list?.forEach {
                    var obj=AnswerMasterModels()
                    obj.CandidateId=candidateId.toString().toInt()
                    obj.QuestionId=it.QuestionId
                    obj.AnswerDesc=it.Answer?.OptionDesc.toString()
                    obj.OptionId=it.Answer?.OptionId.toString().toInt()
                   // obj.AnswerId=it.Answer?.OptionId?.toInt()
                    obj.TemplateId=templateId.toString().toInt()
                    obj.AnswerGivenOn=getCurrentUtcFormatedDate()
                    answerList.add(obj)
                }
            }else
            {
                Log.d("TAG", "postQuestionnaire: ")
            }

            Log.d("TAG", "postQuestionnaire: final model ${Gson().toJson(answerList)}")

            bodyQuestionnaire.answerMasterModels.addAll(answerList)
           // runOnUiThread { showProgressDialog() }
            scheduleMeetingDate(bodyQuestionnaire)
            /**may23_2023*/
            /*viewModel?.postQuestionnaires(bodyQuestionnaire){data, isSuccess, errorCode, msg ->
               runOnUiThread {
                   if (isSuccess)
                   {
                       showCustomSnackbarOnTop(msg)

                       runOnUiThread { dismissProgressDialog() }
                       if (isSuccess){
                           setHandler().postDelayed(Runnable {
                               finish()
                           },2000)
                       }
                   }
               }
            }*/

        }catch (e:Exception)
        {
            Log.d("TAG", "postQuestionnaire: exception ${e.message}")
        }

    }


}