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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.data.*
import com.data.dataHolders.CreateProfileDeepLinkHolder
import com.data.dataHolders.DataStoreHelper
import com.domain.BaseModels.*
import com.google.gson.Gson
import com.ui.listadapters.CandidateQuestionnaireListAdapter
import com.veriKlick.R
import com.veriKlick.databinding.ActivityCandidateQuestinnaireBinding
import com.veriKlick.databinding.LayoutChooseLanguageDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        questionAdapter = CandidateQuestionnaireListAdapter(this,questionList!!,true){data: CandidateQuestionnaireModel, action: Int ->

        }
        handleObserver()
        getTimeZoneFromApi()
      //  getAppLanguage()
        binding?.rvQuestions?.layoutManager=LinearLayoutManager(this)
        binding?.rvQuestions?.adapter=questionAdapter

        //addQuestion()
        if (checkInternet()) {
            getQuestionnaireList()
        } else {
            showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
        }

        binding?.btnSubmit?.setOnClickListener {
            getAllQuestionsList()
        }
        binding?.btnJumpBack?.setOnClickListener {
        onBackPressedDispatcher.onBackPressed()
        }
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

    private fun getTimeZoneFromApi()
    {
        val pathstr=CreateProfileDeepLinkHolder.getQuestionString()
        val pathlist=pathstr?.split("/")
        val candidateId=pathlist?.get(3)


        Log.d(TAG, "getTimeZoneFromApi: ${candidateId} full path is $pathstr")
        candidateId.let {
            viewModel?.getTimeZone(candidateId = candidateId!!){data, isSuccess, errorCode, msg ->
                Log.d(TAG, "getTimeZoneFromApi: api resonse $isSuccess $errorCode excp ${msg}")
                //Log.d(TAG, "getTimeZoneFromApi: data from api ")

            }
        }

        Log.d(TAG, "getTimeZoneFromApi: api resonse after")
    }

    private fun handleObserver()
    {
        viewModel?.timeZoneLiveData?.observe(this){
            Log.d(TAG, "getTimeZoneFromApi: data from api $it")
            if (!it.isNullOrEmpty()){
                timeZoneList.clear()
                timeZoneList.addAll(it)
            }
        }
    }


    private var selectedTimeZone: String? = null
    private var timeZoneList= mutableListOf<TimeZone21>()
    private fun selectTimeZoneDialog(obj:BodyQuestionnaire) {
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

            val timeZoneListString = mutableListOf<String>()
            timeZoneListString.add(getString(R.string.txt_select_timezone).toString())

            try {
                timeZoneList.forEach{
                    timeZoneListString.add(it.TimeZoneDescription.toString())
                }
            }catch (e:Exception)
            {

            }


            CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                runOnUiThread {

                    // dialogBinding.tvUsername.setText(data.Name)

                    val timeZoneAdapter = getArrayAdapterOneItemSelected(timeZoneListString)

                    dialogBinding.spinnerLanguage.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                if (position != 0) {
                                    timeZoneList.forEach {
                                        if (it.TimeZoneDescription.equals(timeZoneListString[position])) {
                                            selectedTimeZone = it.TimeZone
                                        }
                                    }

                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                        }

                    dialogBinding.spinnerLanguage.adapter = timeZoneAdapter
                    dialogBinding.btnSubmitButton.setText(getString(R.string.txt_submit))
                    dialogBinding.btnSubmitButton.setOnClickListener {
                        if (selectedTimeZone != null) {
                            dialog.dismiss()
                            obj.candidateTimeZone=selectedTimeZone
                            scheduleMeetingDate(obj)
                           // setLanguagetoApp(selectedLanguage.toString())
                        } else {
                            dialogBinding.tvError.setText(getString(R.string.txt_timezone_not_selected))
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


    private fun getAppLanguage()
    {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (DataStoreHelper.getAppLanguage()!=null){
                    setLanguagetoApp(intent, DataStoreHelper.getAppLanguage(),false)
                }
                else{

                }
            }catch (e:Exception)
            {

            }
        }
    }

    private fun getQuestionnaireList()
    {
        try {
            val deepLinkingIntent = intent
            val pathstr= CreateProfileDeepLinkHolder.getQuestionString()
            Log.d(TAG, "getQuestionnaireList: path full $pathstr")

            val pathlist=pathstr?.split("/")
            val accessToken=pathlist?.get(pathlist!!.size-1)
            val candidateId=pathlist?.get(3)
            val templateId=pathlist?.get(4)

            val queryString="$candidateId"+"|"+"$accessToken"
            runOnUiThread { showProgressDialog() }
            //viewModel?.getQuestionnaireListNew(candidateId.toString(),templateId.toString(),accessToken.toString()){data, isSuccess, errorCode, msg ->
                viewModel?.getQuestionnaireListNew(
                    templateId.toString(),
                    queryString
                ){ data, isSuccess, errorCode, msg ->
                if (isSuccess)
                {
                    //val list=data?.QuestionList
                    runOnUiThread { dismissProgressDialog() }
                    runOnUiThread {
                        try {
                            if (data?.Success==false)
                            {
                                runOnUiThread { showAlerttoFinishActivity(data?.Message.toString()) }
                            }else
                            {
                                questionList.addAll(data?.QuestionList?.get(0)?.Question!!)
                                runOnUiThread { questionAdapter?.notifyDataSetChanged() }
                            }
                        }catch (e:Exception)
                        {
                            e.message
                        }
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

    private fun showAlerttoFinishActivity(msg: String) {
        var alertDialog = androidx.appcompat.app.AlertDialog.Builder(this)
        alertDialog.run {
            setMessage(msg)
            setPositiveButton(getString(R.string.txt_ok),
                object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        finishAffinity()
                    }
                })
            setCancelable(false)
            create()
            show()
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
            showCustomSnackbarOnTop(getString(R.string.txt_all_answers_required))
        }

    }

    private fun scheduleMeetingDate(bodyQuestionnaire: BodyQuestionnaire)
    {
        runOnUiThread {
            val alertDialog= AlertDialog.Builder(this)
            alertDialog.setNegativeButton(getString(R.string.txt_skip),object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    runOnUiThread {
                       // bodyQuestionnaire.Availability_For_Interview=getCurrentUtcFormatedDate()
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
            alertDialog.setTitle(getString(R.string.txt_provide_your_availability_for_meeting))
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

        val date=Date(Calendar.getInstance().timeInMillis)

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
        dpd.datePicker.minDate=date.time
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
        //timePicker.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        timePicker.create()
        timePicker.show()
        timePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.skyblue_light1))
        timePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.skyblue_light1))
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
        val pathstr=CreateProfileDeepLinkHolder.getQuestionString()

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
            selectTimeZoneDialog(bodyQuestionnaire)
            //05july2023 scheduleMeetingDate(bodyQuestionnaire)
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
                           },2000)33333333
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