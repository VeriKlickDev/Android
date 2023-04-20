package com.ui.activities.candidateQuestionnaire

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.data.showCustomSnackbarOnTop
import com.domain.BaseModels.CandidateQuestionnaireModel
import com.domain.BaseModels.SavedProfileDetail
import com.ui.listadapters.CandidateQuestionnaireListAdapter
import com.veriKlick.R
import com.veriKlick.databinding.ActivityCandidateQuestinnaireBinding

class ActivityCandidateQuestinnaire : AppCompatActivity() {
    private var binding:ActivityCandidateQuestinnaireBinding?=null
    private var viewModel:VMCandidateQuestionnaire?=null
    private var questionAdapter:CandidateQuestionnaireListAdapter?=null
    private var questionList= mutableListOf<CandidateQuestionnaireModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityCandidateQuestinnaireBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        viewModel=ViewModelProvider(this).get(VMCandidateQuestionnaire::class.java)
        questionAdapter= CandidateQuestionnaireListAdapter(this,questionList!!){data: CandidateQuestionnaireModel, action: Int ->


        }
        binding?.rvQuestions?.layoutManager=LinearLayoutManager(this)
        binding?.rvQuestions?.adapter=questionAdapter
        addQuestion()
        binding?.btnSubmit?.setOnClickListener {
            getAllQuestionsList()
        }
    }
    private var isAnswer=false
    fun getAllQuestionsList(){
        if (!questionAdapter?.list.isNullOrEmpty()){
            questionAdapter?.list
            isAnswer= questionAdapter?.list?.any { it.answer==null } == false
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
        for (i in 0..5)
        questionList.add(CandidateQuestionnaireModel("what is $i in range",null))
        questionAdapter?.notifyDataSetChanged()
    }

}