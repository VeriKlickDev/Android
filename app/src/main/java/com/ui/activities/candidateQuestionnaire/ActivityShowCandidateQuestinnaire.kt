package com.ui.activities.candidateQuestionnaire

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.TimePicker
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.data.*
import com.data.dataHolders.CreateProfileDeepLinkHolder
import com.data.dataHolders.DataStoreHelper
import com.domain.BaseModels.*
import com.domain.constant.AppConstants
import com.google.gson.Gson
import com.ui.listadapters.CandidateQuestionnaireListAdapter
import com.ui.listadapters.ShowCandidateQuestionnaireListAdapter
import com.veriKlick.R
import com.veriKlick.databinding.ActivityCandidateQuestinnaireBinding
import com.veriKlick.databinding.LayoutChooseLanguageDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class ActivityShowCandidateQuestinnaire : AppCompatActivity() {
    private var binding:ActivityCandidateQuestinnaireBinding?=null
    private var viewModel:VMCandidateQuestionnaire?=null
    private var questionAdapter:ShowCandidateQuestionnaireListAdapter?=null
    private var questionList= arrayListOf<Answer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCandidateQuestinnaireBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        viewModel=ViewModelProvider(this).get(VMCandidateQuestionnaire::class.java)
        questionAdapter = ShowCandidateQuestionnaireListAdapter(this,questionList!!){data: CandidateQuestionnaireModel, action: Int ->

        }
      //  getAppLanguage()
        binding?.rvQuestions?.layoutManager=LinearLayoutManager(this)
        binding?.rvQuestions?.adapter=questionAdapter

        //addQuestion()
        if (checkInternet()) {
            getQuestionnaireList()
        } else {
            showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
        }
        binding?.btnSubmit?.isVisible=false
        binding?.btnSubmit?.setOnClickListener {
            //getAllQuestionsList()
        }
        binding?.btnJumpBack?.setOnClickListener {
        onBackPressedDispatcher.onBackPressed()
        }
        binding?.tvSetPreference?.isVisible=false
        binding?.tvSetPreference?.setOnClickListener { selectLangaugeDialog() }

    }


    private fun selectLangaugeDialog() {
        runOnUiThread {
            val dialog = Dialog(this)

            val dialogBinding =
                LayoutChooseLanguageDialogBinding.inflate(LayoutInflater.from(this))
            dialog.setContentView(dialogBinding.root)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogBinding.btnCross.setOnClickListener {
                dialog.dismiss()
            }
            dialogBinding.btnSubmitButton.setText(getString(R.string.txt_submit))
            val language = mutableListOf<String>()
            val languageStringList = mutableListOf<ModelLanguageSelect>()


            languageStringList.add(ModelLanguageSelect(getString(R.string.txt_english), "en-US"))
            languageStringList.add(ModelLanguageSelect(getString(R.string.txt_spanish), "es"))
            languageStringList.add(ModelLanguageSelect(getString(R.string.txt_french), "fr"))

            language.add(getString(R.string.txt_select_language))
            language.add(getString(R.string.txt_english))
            language.add(getString(R.string.txt_spanish))
            language.add(getString(R.string.txt_french))
            var selectedLanguage: String? = null
            CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                runOnUiThread {

                    // dialogBinding.tvUsername.setText(data.Name)

                    val langAdapter = getArrayAdapterOneItemSelected(language)
                    dialogBinding.spinnerLanguage.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                if (position != 0) {
                                    languageStringList.forEach {
                                        if (it.language.equals(language[position])) {
                                            selectedLanguage = it.langCode
                                        }
                                    }
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                        }

                    dialogBinding.spinnerLanguage.adapter = langAdapter

                    dialogBinding.btnSubmitButton.setOnClickListener {
                        if (selectedLanguage != null) {
                            dialog.dismiss()
                            setLanguagetoApp(intent,selectedLanguage.toString(),false)
                            finish()
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        } else {
                            dialogBinding.tvError.visibility = View.VISIBLE
                            setHandler().postDelayed({
                                dialogBinding.tvError.visibility = View.INVISIBLE
                            }, 3000)
                            //showCustomSnackbarOnTop(getString(R.string.txt_please_select_language))
                        }
                    }
                }

            }

            dialog.create()
            dialog.show()

        }
    }


    private fun getQuestionnaireList()
    {
        try {
            //"48673"
            runOnUiThread { showProgressDialog() }
            viewModel?.getQuestionnaireforCandidate(intent.getStringExtra(AppConstants.CANDIDATE_ID).toString()){data, isSuccess, errorCode, msg ->
                if (isSuccess)
                {
                    //val list=data?.QuestionList
                    runOnUiThread { dismissProgressDialog() }
                    runOnUiThread {
                        questionList.addAll(data?.Answer!!)
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







}