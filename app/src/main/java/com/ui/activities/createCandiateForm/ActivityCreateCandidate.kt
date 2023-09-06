package com.ui.activities.createCandidate

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.data.*
import com.data.dataHolders.CandidateImageAndAudioHolder
import com.data.dataHolders.CreateProfileDeepLinkHolder
import com.domain.BaseModels.*
import com.google.gson.Gson
import com.veriKlick.R
import com.veriKlick.databinding.ActivityCreateCandidateBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class ActivityCreateCandidate : AppCompatActivity() {

    private lateinit var binding: ActivityCreateCandidateBinding
    private var viewModel: ViewModelCreateCandidate? = null
    private val TAG = "acitivityCreateCan"

    private var countryListMain = mutableListOf<ResponseCountryName>()
    private var countryStringList = mutableListOf<String>()
    private var countrySpinnerAdapter: ArrayAdapter<String>? = null

    private var stateListmain = mutableListOf<ResponseState>()
    private var stateStringList = mutableListOf<String>()
    private var stateSpinnerAdapter: ArrayAdapter<String>? = null

    private var countryCodeList = mutableListOf<ResponseCountryCode>()
    private var countryCodeStringList = mutableListOf<String>()
    private var countryCodeSpinnerAdapter: ArrayAdapter<String>? = null

    private var cityListmain = mutableListOf<ResponseCity>()
    private var cityStringList = mutableListOf<String>()
    private var citySpinnerAdapter: ArrayAdapter<String>? = null

    private var jobTypeListMain = mutableListOf<ModelJobType>()
    private var jobTypeStringList = mutableListOf<String>()
    private var jobTypeSpinnerAdapter: ArrayAdapter<String>? = null

    private var isEmailok = false
    private var isPhoneok = false

    private var cityStr: String? = null
    private var countryStr: String? = null
    private var countryCodeStr: String? = null
    private var stateStr: String? = null
    private var jobTypeStr: String? = null
    private var phoneCodeStr: String? = null
    private var isZipCodeOk=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCandidateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(ViewModelCreateCandidate::class.java)

        jobTypeStringList.addAll(getJobTypeList())

        Log.d(TAG,"onCreate: audio file name ${CandidateImageAndAudioHolder.getAudioFileName() + ".wav"}")

        setupCountryCodeSpinner()
        setupCountrySpinner()
        setupStateSpinner()
        setupCitySpinner()
        setupjobTypeSpinner()
        binding.spinnerCity.isEnabled = false
        binding.spinnerState.isEnabled = false

        // getCountryListFromApi(0)


        binding.etEmail.doOnTextChanged { text, start, before, count ->
            emailValidator(this, text.toString()) { isEmailOk, mEmail, error ->
                isEmailok = isEmailOk
                if (!isEmailOk)
                    binding.etEmail.setError(getString(R.string.txt_invalid))
            }
        }



        binding.etPhoneno.doOnTextChanged { text, start, before, count ->
            //isPhoneok=phoneValidator(text.toString())
            if (phoneValidatorfor9and10Digits(text.toString())) {
                Log.d(TAG, "onCreate: phone valid")
                checkPhoneExists(text.toString())
            } else {
                Log.d(TAG, "onCreate: phone invalid")
                runOnUiThread { binding.etPhoneno.setError(getString(R.string.txt_invalid)) }
            }
        }

        binding.btnJumpBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        binding.btnSubmit.setOnClickListener {
            validateAllFields()
        }

        CoroutineScope(Dispatchers.IO).launch {
            binding.etZipCode.getEditTextWithFlow().collectLatest {
                delay(1500)
                isZipCodeOk=it.length > 4 && it.length <= 10
                if (!isZipCodeOk)
                {
                    runOnUiThread {
                        binding.etZipCode.setError("${getString(R.string.txt_zip_code_error)}")
                    }
                }
            }
        }


        if (checkInternet()) {
            getCandidateDetails(CreateProfileDeepLinkHolder.getCandidateId().toString())
        } else {
            showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
        }

        binding.tvSetPreference.setOnClickListener {
            selectLangaugeDialogGlobal()
        }
    }


    private fun checkPhoneExists(txt: String) {
        viewModel?.getIsPhoneExists(txt, true,
            response = { data ->
                runOnUiThread {
                    if (data.aPIResponse?.StatusCode != null) {
                        showCustomToast(getString(R.string.txt_phone_already_exists))
                        isPhoneok = false
                        Handler(mainLooper).postDelayed({ binding.etPhoneno.setText("") }, 1000)
                    } else {
                        //  binding.etPhoneno.setError(data.aPIResponse?.Message.toString())
                        isPhoneok = true
                    }
                }
            })
    }


    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun validateAllFields() {
        runOnUiThread {
            if (binding.etEmail.text.toString().equals("")) {
                showCustomToast(
                    getString(R.string.txt_email) + " ${getString(R.string.txt_is)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
            } else if (binding.etFirstname.text.toString().equals("")) {
                showCustomToast(
                    getString(R.string.txt_firstName) + " ${getString(R.string.txt_is)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: firstname blank")
            }/* else if (binding.etMiddlename.text.toString().equals("")) {
                showCustomToast(
                    getString(R.string.txt_middleName) + " ${getString(R.string.txt_is)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: middel name blank")
            }*/

            else if (binding.etLastname.text.toString().equals("")) {
                showCustomToast(
                    getString(R.string.txt_lastName) + " ${getString(R.string.txt_is)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: last name blank")
            } else if (binding.etEmail.text.toString().equals("") || !isEmailok) {
                showCustomToast(
                    getString(R.string.txt_email) + " ${getString(R.string.txt_is)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: email blank")
            } else if (binding.etPhoneno.text.toString().equals("") || !isPhoneok) {
                showCustomToast(
                    getString(R.string.txt_phoneNo) + " ${getString(R.string.txt_is)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: phone blank")
            }
            /* else if(binding.etPhoneCode.text.toString().equals(""))
        {
            Log.d(TAG, "validateAllFields: phone blank")
        }*/
            else if (countryCodeStr == null) {
                showCustomToast(
                    getString(R.string.txt_country) + " ${getString(R.string.txt_is)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: country code str")
            }
            /*else if (binding.etStreet.text.toString().equals("")) {
                showCustomToast(
                    getString(R.string.txt_street) + " ${getString(R.string.txt_is)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: street blank")
            } */
            else if (phoneCodeStr==null) {
                showCustomToast(
                    getString(R.string.txt_country_code) + " ${getString(R.string.txt_is)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: primary skills blank")
            }

            else if (countryStr.equals("") || countryStr == null) {
                showCustomToast(
                    getString(R.string.txt_country) + " ${getString(R.string.txt_are)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: country blank")
            }

            else if (binding.etZipCode.text.toString().equals("")) {
                showCustomToast(
                    getString(R.string.txt_zip) + " ${getString(R.string.txt_is)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: zip blank")
            }

           /* else if (cityStr.equals("") || cityStr == null) {
                showCustomToast(
                    getString(R.string.txt_city) + " ${getString(R.string.txt_is)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: city blank")
            }*/ else if (stateStr.equals("") || stateStr == null) {
                showCustomToast(
                    getString(R.string.txt_state) + " ${getString(R.string.txt_is)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: state blank")
            }

            else if (!isZipCodeOk) {

                showCustomSnackbarOnTop("${getString(R.string.txt_zip_code_error)}")
            }
            else if (jobTypeStr.equals("") || jobTypeStr == null) {
                showCustomToast(
                    getString(R.string.txt_jobType) + " ${getString(R.string.txt_are)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: jobtype blank")
            }
            else if (binding.etExperience.text.toString().equals("")) {
                showCustomToast(
                    getString(R.string.txt_experience) + " ${getString(R.string.txt_is)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: secondary blank")
            }
            else if (binding.etPrimarySkills.text.toString().equals("")) {
                showCustomToast(
                    getString(R.string.txt_primary_skills) + " ${getString(R.string.txt_are)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: primary skills blank")
            } else if (binding.etSecondarySkills.text.toString().equals("")) {
                showCustomToast(
                    getString(R.string.txt_secondary_skills) + " ${getString(R.string.txt_are)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: secondary blank")
            }
            else {
                Log.d(TAG, "validateAllFields: success")
                postData()
            }
        }
       // postData()
    }

    private fun postData() {
        try {
            CoroutineScope(Dispatchers.IO + exceptionHandler)
                .launch {

                    var ob = BodyCreateCandidate()

                    withContext(Dispatchers.Main){
                        ob.Subscriberid = subscriberId
                        ob.Userid = recruiterId
                        ob.CreatedDate = getCurrentUtcFormatedDate()
                        ob.UpdatedDate = getCurrentUtcFormatedDate()
                        ob.profile?.firstName = binding.etFirstname.text.toString()
                        ob.profile?.lastName = binding.etLastname.text.toString()
                        ob.profile?.middleName = binding.etMiddlename.text.toString()
                        ob.profile?.emailId = binding.etEmail.text.toString().trim()
                        ob.profile?.phoneMobile = binding.etPhoneno.text.toString()
                        ob.profile?.countrycode = phoneCodeStr.toString()
                        ob.profile?.countrycodeview = "+$phoneCodeStr"
                        ob.profile?.country = countryCodeStr
                        ob.profile?.countryName = countryStr
                        ob.profile?.state = stateStr
                        ob.profile?.zipCode = binding.etZipCode.text.toString()
                        ob.profile?.city = cityStr
                        ob.profile?.jobType = jobTypeStr
                        ob.professional?.totalExperience = binding.etExperience.text.toString()
                        ob.professional?.primarySkills = binding.etPrimarySkills.text.toString()
                        ob.skills?.skill = binding.etSecondarySkills.text.toString()
                        ob.profile?.streetName = binding.etStreet.text.toString()
                        ob.profile?.profileImage=CandidateImageAndAudioHolder.getImageObject()?.imageName
                        ob.professional?.resume=CandidateImageAndAudioHolder.getResumeFileName()
                        //ob.profile?.GovId_Url=CandidateImageAndAudioHolde
                    }

                    if (CandidateImageAndAudioHolder.getAudioFileName()!=null)
                    {
                        ob.profile?.AudioFileName=CandidateImageAndAudioHolder.getAudioFileName()+".wav"
                    }

                    val audioFileName=CandidateImageAndAudioHolder.getAudioFileName()
                    audioFileName

                    Log.d(TAG, "postData: posting data object $audioFileName fullJson ${Gson().toJson(ob)}")

                    runOnUiThread { showProgressDialog() }

                    viewModel?.createCandidateWithoutAuth(ob,CandidateImageAndAudioHolder.getDeepLinkData()?.token_Id!!.toString()
                    ) { data, isSuccess, errorCode, msg ->
                        if (isSuccess) {
                            CandidateImageAndAudioHolder.clearAllData()
                            runOnUiThread {
                                showCustomSnackbarOnTop(msg)
                                dismissProgressDialog()
                                checkIsFormAlreadyFilled()
                            }

                        } else {
                            runOnUiThread {
                                showCustomSnackbarOnTop(getString(R.string.txt_something_went_wrong))
                                dismissProgressDialog()
                            }
                        }
                    }



                }
        } catch (_: Exception) {

        }

    }

    private fun checkIsFormAlreadyFilled()
    {
            var alertDialog = AlertDialog.Builder(this)
            alertDialog.run {
                setMessage(getString(R.string.txt_you_have_successfull_completed_form))
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


    private fun setupCountrySpinner() {
        countrySpinnerAdapter = getArrayAdapterOneItemSelected(countryStringList)
        binding.spinnerCountry.adapter = countrySpinnerAdapter
        binding.spinnerCountry.onItemSelectedListener = countrySpinnerClickListner

    }

    val countrySpinnerClickListner = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            Log.d(TAG, "onItemSelected: selected ${countryStringList[position]}")
            if (position == 0) {
                setTextColorToBlack(view!!,1)
            } else {
                try {
                    if (!countryListMain.isNullOrEmpty()) {
                        countryListMain.forEach {
                            if (it.SortName.toString().equals(countryStringList[position])) {
                                //getCityListFromApi("All",it.Id.toString())
                                cityStringList.clear()
                                cityStringList.add(getString(R.string.txt_select_city))
                                stateStringList.clear()
                                stateStringList.add(getString(R.string.txt_select_state))
                                runOnUiThread {
                                    stateSpinnerAdapter?.notifyDataSetChanged()
                                    citySpinnerAdapter?.notifyDataSetChanged()
                                }
                                countryCodeStr = it.Id.toString()
                                Log.d(TAG, "onItemSelected: 429 ${it.Id.toString()}")
                                getStateListFromApi("All", it.Id.toString(), 0)
                                countryStr = countryStringList[position]
                            }
                        }

                            setTextColorToBlack(view!!,2)

                    }

                } catch (e: Exception) {

                }

            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }

    private fun setTextColorToBlack(view:View,actionCode: Int)
    {
            (view as TextView?)?.post {
                if (actionCode==2)
                (view as TextView?)?.setTextColor(getColor(R.color.black))
                val typeface = ResourcesCompat.getFont(this, R.font.sarabun_light)
                (view as TextView?)?.typeface=typeface
            }
    }

    private fun setupStateSpinner() {
        stateSpinnerAdapter = getArrayAdapterOneItemSelected(stateStringList)
        binding.spinnerState.adapter = stateSpinnerAdapter
        binding.spinnerState.onItemSelectedListener = stateSpinnerClickListner
        stateStringList.add(getString(R.string.txt_select_state))
        stateSpinnerAdapter?.notifyDataSetChanged()
    }

    val stateSpinnerClickListner = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position == 0) {
                setTextColorToBlack(view!!,1)
                Log.d(TAG, "onItemSelected: selected ${stateStringList[position]}")
            } else {
                try {

                    if (!stateListmain.isNullOrEmpty()) {
                        stateListmain.forEach {
                            if (stateStringList[position].toString().equals(it.StateName.toString())
                            ) {
                                stateStr = it.Shortname
                                getCityListFromApi("All", it.Shortname.toString(), 0)
                            }
                        }
                        setTextColorToBlack(view!!,2)
                    }
                } catch (e: Exception) {

                }

                //stateStr=stateStringList[position]
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }


    private fun setupCitySpinner() {
        citySpinnerAdapter = getArrayAdapterOneItemSelected(cityStringList)
        binding.spinnerCity.adapter = citySpinnerAdapter
        binding.spinnerCity.onItemSelectedListener = citySpinnerClickListner
        cityStringList.add(getString(R.string.txt_select_city))
        citySpinnerAdapter?.notifyDataSetChanged()
    }

    val citySpinnerClickListner = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position == 0) {
                setTextColorToBlack(view!!,1)
                Log.d(TAG, "onItemSelected: selected ${cityStringList[position]}")
            } else {
                cityStr = cityStringList[position]
                setTextColorToBlack(view!!,2)
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }


    private fun setupjobTypeSpinner() {
        jobTypeSpinnerAdapter = getArrayAdapterOneItemSelected(jobTypeStringList)
        binding.spinnerJobtype.adapter = jobTypeSpinnerAdapter
        binding.spinnerJobtype.onItemSelectedListener = jobTypeSpinnerClickListner
    }

    private fun getJobTypeList(): List<String> {
        val list = mutableListOf<String>()

        list.add(getString(R.string.txt_select_job_type))

        try {
            val listMain = Gson().fromJson<Array<ModelJobType>>(
                "[{ \"id\": \"CORP-CORP\", name: \"CORP-CORP\", \"selected\": false },{ \"id\": \"W2 PERMANENT\", name: \"W2 PERMANENT\", \"selected\": false },{ \"id\": 'W2 CONTRACT', name: \"W2 CONTRACT\", \"selected\": false },{ \"id\": \"1099 CONTRACT\", name: \"1099 CONTRACT\", \"selected\": false },{ \"id\": \"H1B TRANSFER\", name: \"H1B TRANSFER\",\"selected\": false },{ \"id\": \"NEED H1B\", name: \"NEED H1B\", \"selected\": false},{ \"id\": \"PERMANENT\", name: \"PERMANENT\", \"selected\": false },{ \"id\": \"CONTRACT\", name: \"CONTRACT\", \"selected\": false }]",
                Array<ModelJobType>::class.java
            )
            jobTypeListMain.addAll(listMain)

        } catch (e: Exception) {
            Log.d(TAG, "getJobTypeList: exception ${e.message}")
        }

        jobTypeListMain.forEach {
            list.add(it.name.toString())
        }

        return list
    }

    val jobTypeSpinnerClickListner = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position == 0) {
                setTextColorToBlack(view!!,1)
                Log.d(TAG, "onItemSelected: selected ${jobTypeStringList[position]}")
            } else {
                jobTypeListMain.forEach {
                    if (it.name.equals(jobTypeStringList[position])) {
                        jobTypeStr = it.id
                    }
                }
                setTextColorToBlack(view!!,2)
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }


    private fun setupCountryCodeSpinner() {
        countryCodeSpinnerAdapter = getArrayAdapterOneItemSelected(countryCodeStringList)
        binding.spinnerCountryCode.adapter = countryCodeSpinnerAdapter
        binding.spinnerCountryCode.onItemSelectedListener = countryCodeSpinnerClickListner
    }

    private val countryCodeSpinnerClickListner = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position == 0) {
                setTextColorToBlack(view!!,1)
                Log.d(TAG, "onItemSelected: selected ${countryCodeStringList[position]}")
            } else {
                try {
                    if (!countryCodeList.isNullOrEmpty()) {
                        countryCodeList.forEach {
                            val countryn = it.codedisplay.toString() + " " + it.Name.toString()
                            val selectedCountryCodeis = countryn.toString().lowercase().trim()
                            val loopdata =
                                countryCodeStringList[position].toString().lowercase().trim()
                            selectedCountryCodeis
                            loopdata
                            if (countryn.toString().trim().lowercase().equals(
                                    countryCodeStringList[position].toString().trim().lowercase()
                                )
                            ) {
                                //countryCodeStr=it.codedisplay
                                phoneCodeStr = it.PhoneCode.toString()
                                Log.d(TAG, "onItemSelected: selected country phone code ${it}")
                            }
                        }
                    }
                    setTextColorToBlack(view!!,2)
                } catch (e: Exception) {
                    Log.d(TAG, "onItemSelected: exception 373 ${e.message}")
                }

            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }


    private fun getCountryListFromApi(actionCode: Int) {
        viewModel?.getCountyNameList { data, isSuccess, errorCode, msg ->
            if (isSuccess) {
                try {

                    countryStringList.clear()
                    countryListMain.clear()

                    cityStringList.clear()

                    countryStringList.add(getString(R.string.txt_select_country))
                    cityStringList.add(getString(R.string.txt_select_city))
                    stateStringList.add(getString(R.string.txt_select_state))

                    countryListMain.addAll(data!!)
                    countryListMain.forEach {
                        if (it.IsActive!!)
                            countryStringList.add(it.SortName.toString())
                    }

                    if (actionCode == 2) {
                        runOnUiThread {
                            val countryName = viewModel?.getUserProfileData()?.Country
                            countryName
                            countryListMain.forEach {
                                if (it.Id.toString().trim().equals(countryName.toString().trim())) {
                                    val item =
                                        countrySpinnerAdapter?.getPosition(it.SortName.toString())!!
                                    binding.spinnerCountry.setSelection(item)
                                    // countrySpinnerAdapter?.notifyDataSetChanged()
                                    Log.d(TAG, "getCountryListFromApi: 631 ${it.Id.toString()}")
                                    getStateListFromApi("All", it.Id.toString(), 2)
                                }
                            }
                        }
                        runOnUiThread { countrySpinnerAdapter?.notifyDataSetChanged() }

                        /* val dataobj=countrySpinnerAdapter?.getItem(countrySpinnerAdapter?.getPosition(viewModel?.getUserProfileData()?.CandidateLocation?.Country)!!)
                         countryListMain.forEach {
                             if (dataobj.equals(it.SortName))
                             {
                                 getStateListFromApi("All",it.Id.toString(),2)
                             }
                         }*/

                    } else {
                        runOnUiThread {
                            countrySpinnerAdapter?.notifyDataSetChanged()
                            stateSpinnerAdapter?.notifyDataSetChanged()
                            citySpinnerAdapter?.notifyDataSetChanged()
                        }
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "getCountryListFromApi: exception 472 ${e.message}")
                }
            }
        }
    }

    private fun getCountryCodeListFromApi(actionCode: Int) {
        viewModel?.getCountyCodeList { data, isSuccess, errorCode, msg ->
            if (isSuccess) {
                try {
                    countryCodeList.clear()
                    countryCodeStringList.clear()
                    countryCodeStringList.add(getString(R.string.txt_country_code))
                    countryCodeList.addAll(data!!)
                    countryCodeList.forEach {
                        //21jun2023 countryCodeStringList.add(it.codedisplay.toString()+" "+it.Name)
                        countryCodeStringList.add(it.codedisplay.toString() + " " + it.Name)
                    }
                    Log.d(
                        TAG,
                        "getCountryCodeListFromApi: countryCode $countryStringList ${countryCodeStringList}"
                    )
                    runOnUiThread {
                        countryCodeSpinnerAdapter?.notifyDataSetChanged()
                        /*   var countryCode=viewModel?.getUserProfileData()?.Countrycode
                           Log.d(TAG, "getCountryCodeListFromApi: countrycodeis ver first ${countryCode} ${viewModel?.getUserProfileData()} country ${viewModel?.getUserProfileData()?.Country.toString()}")
                           val appendedCode=countryCodeSpinnerAdapter?.getPosition("+"+countryCode+" "+viewModel?.getUserProfileData()?.Country.toString())

                           var selectedCountry:String?=null
                           try {

                               countryCodeList.forEachIndexed { index, responseCountryName ->
                                   if (responseCountryName.codedisplay.toString().equals("+$countryCode"))
                                   {
                                       Log.d(TAG, "getCountryCodeListFromApi: selected country is ${responseCountryName.Name} countryCode +$countryCode")
                                       selectedCountry=    "+$countryCode ${responseCountryName.Name}"
                                   }
                               }

                               countryCodeStringList.forEach {
                                   if (selectedCountry!!.lowercase().trim().equals(it.trim().lowercase()))
                                   {
                                       val appendedCode=countryCodeSpinnerAdapter?.getPosition(selectedCountry)
                                       binding.spinnerCountryCode.setSelection(appendedCode!!)
                                       countryCodeSpinnerAdapter?.notifyDataSetChanged()
                                   }
                               }

                           }catch (e:Exception)
                           {

                           }


                           Log.d(TAG, "getCountryCodeListFromApi: countrycodeis ver $appendedCode $selectedCountry")
                           //val pos=countryCodeSpinnerAdapter?.getPosition(appendedCode)
                          // binding.spinnerCountryCode.setSelection(appendedCode!!)
                          // countryCodeSpinnerAdapter?.notifyDataSetChanged()


                           */
                    }

                } catch (e: Exception) {
                    Log.d(TAG, "getCountryCodeListFromApi: exception 493 ${e.message}")
                }
                //getCandidateDetails("47422")

            } else {
                Log.d(TAG, "getCountryCodeListFromApi: code 721 $errorCode")
            }
        }
    }

    private fun getStateListFromApi(searchString: String, id: String, actionCode: Int) {
        viewModel?.getStateByidList(searchString, id) { data, isSuccess, errorCode, msg ->
            if (isSuccess) {
                runOnUiThread { binding.spinnerState.isEnabled = true }
                stateListmain.clear()
                stateStringList.clear()
                stateStringList.add(getString(R.string.txt_select_state))
                stateListmain.addAll(data!!)
                stateListmain.forEach {
                    stateStringList.add(it.StateName.toString())
                }
                Log.d(
                    TAG,
                    "getCountryCodeListFromApi: countryCode $countryStringList ${countryCodeStringList}"
                )

                if (actionCode == 2) {
                    runOnUiThread {
                        binding.spinnerState.setSelection(
                            stateSpinnerAdapter!!.getPosition(
                                viewModel?.getUserProfileData()?.StateCode.toString()
                            )
                        )
                        stateSpinnerAdapter?.notifyDataSetChanged()
                    }
                    val stateObj =
                        stateListmain.find { it.StateName!!.equals(viewModel?.getUserProfileData()?.StateCode.toString()) }
                    Log.d(TAG, "getStateListFromApi: state object $stateObj")
                    getCityListFromApi("All", stateObj?.Shortname.toString(), 2)
                } else {
                    runOnUiThread {
                        stateSpinnerAdapter?.notifyDataSetChanged()
                    }
                }

            } else {
                Log.d(TAG, "getCountryCodeListFromApi: code 761 $errorCode")
            }
        }
    }

    private fun getCityListFromApi(
        searchString: String,
        shortNameofState: String,
        actionCode: Int
    ) {
        viewModel?.getCityByidList(
            shortNameofState,
            searchString
        ) { data, isSuccess, errorCode, msg ->
            if (isSuccess) {
                runOnUiThread {
                    binding.spinnerCity.isEnabled = true
                    //binding.spinnerCity.isEnabled = true
                }

                cityListmain.clear()
                cityStringList.clear()

                cityStringList.add(getString(R.string.txt_select_city))
                cityListmain.addAll(data!!)
                cityListmain.forEach {
                    cityStringList.add(it.CityName.toString())
                }
                Log.d(TAG, "getCountryCodeListFromApi: countryCode city list is $cityStringList")
                runOnUiThread {
                    citySpinnerAdapter?.notifyDataSetChanged()
                    if (actionCode == 2)
                        binding.spinnerCity.setSelection(citySpinnerAdapter!!.getPosition(viewModel?.getUserProfileData()?.City.toString()))
                }


            } else {

                Log.d(TAG, "getCountryCodeListFromApi: code 797 $errorCode $msg")
            }
            runOnUiThread { citySpinnerAdapter?.notifyDataSetChanged() }
        }
    }

    private var recruiterId: String? = null
    private var subscriberId: String? = null
    private fun getCandidateDetails(id: String) {
        viewModel?.getCandidateDetails(
            CreateProfileDeepLinkHolder.getCandidateId().toString(),
            CreateProfileDeepLinkHolder.getTokenId().toString()
        )
        { data, isSuccess, errorCode, msg ->
            if (isSuccess) {
                try {
                    if (data?.aPIResponse?.message != null) {
                      runOnUiThread { showAlerttoFinishActivity(data?.aPIResponse?.message!!) }
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "getCandidateDetails: excpetion 738 ${e.message}")
                }
                recruiterId = data?.RecruiterId
                subscriberId = data?.Subscriberid
                setCandidateDetailsInView(data!!)
            } else {
                runOnUiThread { showCustomSnackbarOnTop(getString(R.string.txt_something_went_wrong)) }
            }
        }
    }

    private fun showAlerttoFinishActivity(msg: String) {
        var alertDialog = AlertDialog.Builder(this,R.style.custom_style_dialog)
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

    private fun setCandidateDetailsInView(dataForIOS: ResponseRecruiterDetails) {
        try {
            runOnUiThread {

                binding.etFirstname.setText(dataForIOS.FirstName)
                binding.etLastname.setText(dataForIOS.LastName)
                binding.etMiddlename.setText(dataForIOS.MiddleName)
                binding.etEmail.setText(dataForIOS?.PrimaryEmail.toString())
                binding.etEmail.isEnabled = false
                binding.etZipCode.setText(dataForIOS.ZipCode)
                binding.etStreet.setText(dataForIOS.Street)
                //binding.etPhoneno.setText(dataForIOS.PrimaryContact)
                binding.etExperience.setText(dataForIOS.Experience)
                binding.etPrimarySkills.setText(dataForIOS.primarySkills)
                binding.etSecondarySkills.setText(dataForIOS.Skills)
                if (dataForIOS.PrimaryContact.toString().isEmpty() || dataForIOS.PrimaryContact==null)
                {
                    binding.spinnerCountryCode.isVisible=true
                    binding.tvCountryCodeForMobile.isVisible=false
                }else{
                    binding.tvCountryCodeForMobile.isVisible=true
                    binding.tvCountryCodeForMobile.setText(dataForIOS.Countrycode.toString())
                    binding.tvCountryCodeForMobile.isEnabled=false
                    binding.etPhoneno.isEnabled=false
                    binding.etPhoneno.setText(dataForIOS.PrimaryContact.toString())
                    binding.etPhoneno.setTextColor(getColor(R.color.grey_disabled))
                    binding.tvCountryCodeForMobile.setTextColor(getColor(R.color.grey_disabled))
                    phoneCodeStr=dataForIOS!!.PrimaryContact?.toString()
                    binding.llMobileCode.setBackgroundResource(R.drawable.shape_rectangle_rounded_3_light_grey)
                }

                binding.spinnerJobtype.setSelection(jobTypeSpinnerAdapter!!.getPosition(viewModel?.getUserProfileData()?.DesiredJob.toString()))

                try {
                    if (dataForIOS?.aPIResponse?.message != null) {
                        runOnUiThread {
                            binding.etPhoneno.setText(dataForIOS.PrimaryContact)

                        }
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "getCandidateDetails: excpetion 738 ${e.message}")
                }


                getCountryListFromApi(2)
                getCountryCodeListFromApi(2)
            }
        } catch (e: Exception) {
            showCustomSnackbarOnTop(getString(R.string.txt_something_went_wrong))
        }
    }
}
