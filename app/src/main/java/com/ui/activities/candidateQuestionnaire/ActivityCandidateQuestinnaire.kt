package com.ui.activities.candidateQuestionnaire

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.data.showCustomSnackbarOnTop
import com.domain.BaseModels.CandidateQuestionnaireModel
import com.domain.BaseModels.Options
import com.domain.BaseModels.Question
import com.domain.BaseModels.SavedProfileDetail
import com.ui.listadapters.CandidateQuestionnaireListAdapter
import com.veriKlick.R
import com.veriKlick.databinding.ActivityCandidateQuestinnaireBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityCandidateQuestinnaire : AppCompatActivity() {
    private var binding:ActivityCandidateQuestinnaireBinding?=null
    private var viewModel:VMCandidateQuestionnaire?=null
    private var questionAdapter:CandidateQuestionnaireListAdapter?=null
    private var questionList= mutableListOf<Question>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityCandidateQuestinnaireBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        viewModel=ViewModelProvider(this).get(VMCandidateQuestionnaire::class.java)
        questionAdapter = CandidateQuestionnaireListAdapter(this,questionList!!){data: CandidateQuestionnaireModel, action: Int ->

        }
        binding?.rvQuestions?.layoutManager=LinearLayoutManager(this)
        binding?.rvQuestions?.adapter=questionAdapter

        val deepLinkingIntent = intent
        val schemestr=deepLinkingIntent.scheme
        val pathstr=deepLinkingIntent.data!!.path

        Log.d("TAG", "onCreate: deeplink $deepLinkingIntent $schemestr $pathstr")


        addQuestion()
        binding?.btnSubmit?.setOnClickListener {
            getAllQuestionsList()
        }
        binding?.btnJumpBack?.setOnClickListener {
        onBackPressedDispatcher.onBackPressed()
        }
    }

    private var isAnswer=false
    fun getAllQuestionsList(){
        if (!questionAdapter?.list.isNullOrEmpty()){
            questionAdapter?.list
            isAnswer= questionAdapter?.list?.any { it.Answer==null || it.Answer?.OptionDesc.equals("") } == false
        }

        if (isAnswer)
        {
            showCustomSnackbarOnTop("Success")
        }else
        {
            showCustomSnackbarOnTop(getString(R.string.txt_all_quesiton_required))
        }

    }

    private fun addQuestion()
    {
        var quesType="M"
        for (i in 0..10)
        {
            if (i%2==1)
            {
                questionList.add(Question(i,"what is $i in range","D",getOptions()))
            }else
            {
                questionList.add(Question(i,"what is $i in range","M",getOptions()))
            }
        }

        questionAdapter?.notifyDataSetChanged()
    }

    private fun getOptions():ArrayList<Options>
    {
        val listt= arrayListOf<Options>()
        for (i in 0..3)
            listt.add(Options(i,"option $i"))
        return listt
    }

}