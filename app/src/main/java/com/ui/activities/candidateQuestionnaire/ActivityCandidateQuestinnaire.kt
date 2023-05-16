package com.ui.activities.candidateQuestionnaire

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.data.getCurrentUtcFormatedDate
import com.data.showCustomSnackbarOnTop
import com.data.showCustomToast
import com.domain.BaseModels.*
import com.google.gson.Gson
import com.ui.listadapters.CandidateQuestionnaireListAdapter
import com.veriKlick.R
import com.veriKlick.databinding.ActivityCandidateQuestinnaireBinding
import dagger.hilt.android.AndroidEntryPoint

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

            viewModel?.getQuestionnaireList(templateId.toString(),accessToken.toString()){data, isSuccess, errorCode, msg ->
                if (isSuccess)
                {
                    //val list=data?.QuestionList
                    runOnUiThread {
                        questionList.addAll(data?.QuestionList?.get(0)?.Question!!)
                        questionAdapter?.notifyDataSetChanged()
                    }

                }else
                {
                    runOnUiThread {
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
            postQuestionnaire()
        }else
        {
            showCustomSnackbarOnTop(getString(R.string.txt_all_quesiton_required))
        }

    }

    private fun postQuestionnaire()
    {
        showCustomSnackbarOnTop("Success")
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
            viewModel?.postQuestionnaires(bodyQuestionnaire){data, isSuccess, errorCode, msg ->
               runOnUiThread {
                   showCustomSnackbarOnTop(msg)
               }
            }

        }catch (e:Exception)
        {
            Log.d("TAG", "postQuestionnaire: exception ${e.message}")
        }

    }


}