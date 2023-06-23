package com.ui.activities.createCandidate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.data.*
import com.data.dataHolders.CandidateImageAndAudioHolder
import com.data.dataHolders.CreateProfileDeepLinkHolder
import com.data.dataHolders.DataStoreHelper
import com.domain.BaseModels.*
import com.domain.constant.AppConstants
import com.google.gson.Gson
import com.veriKlick.R
import com.veriKlick.databinding.ActivityCreateCandidateBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    private var isEmailok=false
    private var isPhoneok=false

    private var cityStr:String?=null
    private var countryStr:String?=null
    private var countryCodeStr:String?=null
    private var stateStr:String?=null
    private var jobTypeStr:String?=null
    private var phoneCodeStr:Int?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCandidateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(ViewModelCreateCandidate::class.java)

        jobTypeStringList.addAll(getJobTypeList())


        setupCountryCodeSpinner()
        setupCountrySpinner()
        setupStateSpinner()
        setupCitySpinner()
        setupjobTypeSpinner()

       // getCountryListFromApi(0)


        binding.etEmail.doOnTextChanged { text, start, before, count ->
            emailValidator(this, text.toString()) { isEmailOk, mEmail, error ->
                isEmailok=isEmailOk
                if (!isEmailOk)
                    binding.etEmail.setError("Invalid")

            }
        }

        binding.etPhoneno.doOnTextChanged { text, start, before, count ->
            isPhoneok=phoneValidator(text.toString())
            if (phoneValidator(text.toString()))
            {
                Log.d(TAG, "onCreate: phone valid")
            }else
            {
                Log.d(TAG, "onCreate: phone invalid")
                binding.etPhoneno.setError("Invalid")
            }

        }

        binding.btnJumpBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSubmit.setOnClickListener {
            validateAllFields()
        }
        if (checkInternet()) {
            getCandidateDetails(CreateProfileDeepLinkHolder.getCandidateId().toString())
        } else {
            showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
        }


    }
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
    private fun validateAllFields()
    {
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
            } else if (binding.etMiddlename.text.toString().equals("")) {
                showCustomToast(
                    getString(R.string.txt_middleName) + " ${getString(R.string.txt_is)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: middel name blank")
            } else if (binding.etLastname.text.toString().equals("")) {
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
                    getString(R.string.hint_password) + " ${getString(R.string.txt_is)} ${
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
                    getString(R.string.txt_country_code) + " ${getString(R.string.txt_is)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: country code str")
            } else if (binding.etZipCode.text.toString().equals("")) {
                showCustomToast(
                    getString(R.string.txt_zip) + " ${getString(R.string.txt_is)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: zip blank")
            } else if (binding.etStreet.text.toString().equals("")) {
                showCustomToast(
                    getString(R.string.txt_street) + " ${getString(R.string.txt_is)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: street blank")
            } else if (binding.etPrimarySkills.text.toString().equals("")) {
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
            } else if (binding.etExperience.text.toString().equals("")) {
                showCustomToast(
                    getString(R.string.txt_experience) + " ${getString(R.string.txt_is)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: secondary blank")
            } else if (cityStr.equals("") || cityStr == null) {
                showCustomToast(
                    getString(R.string.txt_city) + " ${getString(R.string.txt_is)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: city blank")
            } else if (stateStr.equals("") || stateStr == null) {
                showCustomToast(
                    getString(R.string.txt_state) + " ${getString(R.string.txt_is)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: state blank")
            } else if (countryStr.equals("") || countryStr == null) {
                showCustomToast(
                    getString(R.string.txt_country) + " ${getString(R.string.txt_are)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: country blank")
            } else if (jobTypeStr.equals("") || jobTypeStr == null) {
                showCustomToast(
                    getString(R.string.txt_jobType) + " ${getString(R.string.txt_are)} ${
                        getString(
                            R.string.txt_invalid_or_blank_data
                        )
                    }"
                )
                Log.d(TAG, "validateAllFields: jobtype blank")
            } else {
                Log.d(TAG, "validateAllFields: success")
                postData()
            }
        }
    }

    private fun postData()
    {
        try {
            CoroutineScope(Dispatchers.IO+ exceptionHandler)
                .launch {

                    var ob=BodyCreateCandidate()

                    ob.Subscriberid=DataStoreHelper.getMeetingUserId()
                    ob.Userid=DataStoreHelper.getMeetingRecruiterid()
                    ob.CreatedDate=getCurrentUtcFormatedDate()
                    ob.UpdatedDate=getCurrentUtcFormatedDate()
                    ob.profile?.firstName=binding.etFirstname.text.toString()
                    ob.profile?.lastName=binding.etLastname.text.toString()
                    ob.profile?.middleName=binding.etMiddlename.text.toString()
                    ob.profile?.emailId=binding.etEmail.text.toString()
                    ob.profile?.phoneMobile=binding.etPhoneno.text.toString()
                    ob.profile?.countrycode=phoneCodeStr
                    ob.profile?.countrycodeview="+$phoneCodeStr"
                    ob.profile?.country=countryCodeStr
                    ob.profile?.countryName=countryStr
                    ob.profile?.state=stateStr
                    ob.profile?.zipCode=binding.etZipCode.text.toString()
                    ob.profile?.city=cityStr
                    ob.profile?.jobType=jobTypeStr
                    ob.professional?.totalExperience=binding.etExperience.text.toString()
                    ob.professional?.primarySkills=binding.etPrimarySkills.text.toString()
                    ob.skills?.skill=binding.etSecondarySkills.text.toString()
                    ob.profile?.streetName=binding.etStreet.text.toString()
                    Log.d(TAG, "postData: posting data object ${Gson().toJson(ob)}")
                    runOnUiThread { showProgressDialog() }
                    viewModel?.createCandidateWithoutAuth(ob,
                        CandidateImageAndAudioHolder.getDeepLinkData()?.token_Id!!.toString()
                    ){ data, isSuccess, errorCode, msg ->
                    if (isSuccess)
                    {
                        runOnUiThread { dismissProgressDialog() }
                      showCustomSnackbarOnTop(msg)
                    }else
                    {
                        runOnUiThread { dismissProgressDialog() }
                        showCustomSnackbarOnTop(getString(R.string.txt_something_went_wrong))
                    }

                    }
                }
        }catch (_:Exception)
        {

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

            }else
            {
                try {
                    if (!countryListMain.isNullOrEmpty()){
                        countryListMain.forEach {
                            if (it.SortName.toString().equals(countryStringList[position])){
                                //getCityListFromApi("All",it.Id.toString())
                                cityStringList.clear()
                                cityStringList.add("Select City")
                                stateStringList.clear()
                                stateStringList.add("Select State")
                                runOnUiThread {
                                    stateSpinnerAdapter?.notifyDataSetChanged()
                                }

                                getStateListFromApi("All",it.Id.toString(),0)
                                countryStr=countryStringList[position]
                            }
                        }
                    }

                }catch (e:Exception)
                {

                }

            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }


    private fun setupStateSpinner() {
        stateSpinnerAdapter = getArrayAdapterOneItemSelected(stateStringList)
        binding.spinnerState.adapter = stateSpinnerAdapter
        binding.spinnerState.onItemSelectedListener = stateSpinnerClickListner
    }

    val stateSpinnerClickListner = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position == 0) {
                Log.d(TAG, "onItemSelected: selected ${stateStringList[position]}")
            }else
            {
                try {

                    if (!stateListmain.isNullOrEmpty()) {
                        stateListmain.forEach {
                            if (stateStringList[position].toString().equals(it.StateName.toString())
                            ) {
                                stateStr=stateStringList[position]
                                getCityListFromApi("All",it.Shortname.toString(),0)
                            }
                        }
                    }
                }catch (e:Exception)
                {

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
    }

    val citySpinnerClickListner = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position == 0) {
                Log.d(TAG, "onItemSelected: selected ${cityStringList[position]}")
            }else
            {
                cityStr=cityStringList[position]
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

        list.add("Select JobType")
        
        try {
            val listMain=Gson().fromJson<Array<ModelJobType>>("[{ \"id\": \"CORP-CORP\", name: \"CORP-CORP\", \"selected\": false },{ \"id\": \"W2 PERMANENT\", name: \"W2 PERMANENT\", \"selected\": false },{ \"id\": 'W2 CONTRACT', name: \"W2 CONTRACT\", \"selected\": false },{ \"id\": \"1099 CONTRACT\", name: \"1099 CONTRACT\", \"selected\": false },{ \"id\": \"H1B TRANSFER\", name: \"H1B TRANSFER\",\"selected\": false },{ \"id\": \"NEED H1B\", name: \"NEED H1B\", \"selected\": false},{ \"id\": \"PERMANENT\", name: \"PERMANENT\", \"selected\": false },{ \"id\": \"CONTRACT\", name: \"CONTRACT\", \"selected\": false }]",
                Array<ModelJobType>::class.java)
            jobTypeListMain.addAll(listMain)

        }catch (e:Exception)
        {
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
                Log.d(TAG, "onItemSelected: selected ${jobTypeStringList[position]}")
            }else
            {
                jobTypeListMain.forEach {
                    if (it.name.equals(jobTypeStringList[position]))
                    {
                    jobTypeStr=it.id
                    }
                }
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }


    private fun setupCountryCodeSpinner() {
        countryCodeSpinnerAdapter =getArrayAdapterOneItemSelected(countryCodeStringList)
        binding.spinnerCountryCode.adapter = countryCodeSpinnerAdapter
        binding.spinnerCountryCode.onItemSelectedListener = countryCodeSpinnerClickListner
    }

    private val countryCodeSpinnerClickListner = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position == 0) {
                Log.d(TAG, "onItemSelected: selected ${countryCodeStringList[position]}")
            }else
            {
                try
                {
                    if (!countryCodeList.isNullOrEmpty()){
                        countryCodeList.forEach {
                            val countryn=it.codedisplay.toString()+" "+it.Name.toString()
                            val selectedCountryCodeis=countryn.toString().lowercase().trim()
                            val loopdata=countryCodeStringList[position].toString().lowercase().trim()
                            selectedCountryCodeis
                            loopdata
                            if (countryn.toString().trim().lowercase().equals(countryCodeStringList[position].toString().trim().lowercase()))
                            {
                                countryCodeStr=it.Name
                                phoneCodeStr=it.PhoneCode
                                Log.d(TAG, "onItemSelected: selected country phone code ${it}")
                            }
                        }
                    }
                }catch (e:Exception)
                {
                    Log.d(TAG, "onItemSelected: exception 373 ${e.message}")
                }

            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }




    private fun getCountryListFromApi(actionCode:Int){
        viewModel?.getCountyNameList { data, isSuccess, errorCode, msg ->
            if (isSuccess)
            {
                try {

                    countryStringList.clear()
                    countryListMain.clear()

                    cityStringList.clear()

                    countryStringList.add("Select Country")
                    cityStringList.add("Select City")
                    stateStringList.add("Select State")

                    countryListMain.addAll(data!!)
                    countryListMain.forEach {
                        countryStringList.add(it.SortName.toString())
                    }

                    if (actionCode==2)
                    {
                        runOnUiThread {
                            val countryName=viewModel?.getUserProfileData()?.CandidateLocation?.Country
                            countryName
                            countryListMain.forEach {
                                if (it.Id.toString().trim().equals(countryName.toString().trim()))
                                {
                                    val item=countrySpinnerAdapter?.getPosition(it.SortName.toString())!!
                                    binding.spinnerCountry.setSelection(item)
                                    countrySpinnerAdapter?.notifyDataSetChanged()
                                    getStateListFromApi("All",it.Id.toString(),2)
                                }
                            }
                        }
                       /* val dataobj=countrySpinnerAdapter?.getItem(countrySpinnerAdapter?.getPosition(viewModel?.getUserProfileData()?.CandidateLocation?.Country)!!)
                        countryListMain.forEach {
                            if (dataobj.equals(it.SortName))
                            {
                                getStateListFromApi("All",it.Id.toString(),2)
                            }
                        }*/

                    }else
                    {
                        runOnUiThread {
                            countrySpinnerAdapter?.notifyDataSetChanged()
                            stateSpinnerAdapter?.notifyDataSetChanged()
                            citySpinnerAdapter?.notifyDataSetChanged()
                        }
                    }
                }catch (e:Exception)
                {
                    Log.d(TAG, "getCountryListFromApi: exception 472 ${e.message}")
                }
            }
        }
    }

    private fun getCountryCodeListFromApi(actionCode:Int){
        viewModel?.getCountyCodeList { data, isSuccess, errorCode, msg ->
            if (isSuccess)
            {
                try {
                    countryCodeList.clear()
                    countryCodeStringList.clear()
                    countryCodeStringList.add("Code")
                    countryCodeList.addAll(data!!)
                    countryCodeList.forEach {
                        //21jun2023 countryCodeStringList.add(it.codedisplay.toString()+" "+it.Name)
                        countryCodeStringList.add(it.codedisplay.toString()+" "+it.Name)
                    }
                    Log.d(TAG, "getCountryCodeListFromApi: countryCode $countryStringList ${countryCodeStringList}")
                    runOnUiThread {
                        countryCodeSpinnerAdapter?.notifyDataSetChanged()
                        var countryCode=viewModel?.getUserProfileData()?.Candidate?.Countrycode
                        Log.d(TAG, "getCountryCodeListFromApi: countrycodeis ver first ${countryCode} ${viewModel?.getUserProfileData()} country ${viewModel?.getUserProfileData()?.CandidateLocation?.Country.toString()}")
                        val appendedCode=countryCodeSpinnerAdapter?.getPosition("+"+countryCode+" "+viewModel?.getUserProfileData()?.CandidateLocation?.Country.toString())

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
                    }

                }catch (e:Exception)
                {
                    Log.d(TAG, "getCountryCodeListFromApi: exception 493 ${e.message}")
                }
              //getCandidateDetails("47422")

            }else
            {
                Log.d(TAG, "getCountryCodeListFromApi: code $errorCode")
            }
        }
    }

    private fun getStateListFromApi(searchString: String,id: String,actionCode: Int){
        viewModel?.getStateByidList(searchString,id) { data, isSuccess, errorCode, msg ->
            if (isSuccess)
            {
                stateListmain.clear()
                stateStringList.clear()
                stateStringList.add("Select State")
                stateListmain.addAll(data!!)
                stateListmain.forEach {
                    stateStringList.add(it.StateName.toString())
                }
                Log.d(TAG, "getCountryCodeListFromApi: countryCode $countryStringList ${countryCodeStringList}")

                if (actionCode==2)
                {
                    runOnUiThread {
                        binding.spinnerState.setSelection(stateSpinnerAdapter!!.getPosition(viewModel?.getUserProfileData()?.CandidateLocation?.State.toString()))
                        stateSpinnerAdapter?.notifyDataSetChanged()
                    }
                    val stateObj=stateListmain.find { it.StateName!!.equals(viewModel?.getUserProfileData()?.CandidateLocation?.State.toString()) }
                    getCityListFromApi("All",stateObj?.Shortname.toString(),2)
                }else{
                    runOnUiThread {
                        stateSpinnerAdapter?.notifyDataSetChanged()
                    }
                }

            }else
            {
                Log.d(TAG, "getCountryCodeListFromApi: code $errorCode")
            }
        }
    }

    private fun getCityListFromApi(searchString:String,shortNameofState:String,actionCode:Int){
        viewModel?.getCityByidList(shortNameofState,searchString) { data, isSuccess, errorCode, msg ->
            if (isSuccess)
            {
                cityListmain.clear()
                cityStringList.clear()

                cityStringList.add("Select City")
                cityListmain.addAll(data!!)
                cityListmain.forEach {
                    cityStringList.add(it.CityName.toString())
                }
                Log.d(TAG, "getCountryCodeListFromApi: countryCode $cityStringList")
                runOnUiThread{
                    citySpinnerAdapter?.notifyDataSetChanged()
                    if (actionCode==2)
                    binding.spinnerCity.setSelection(citySpinnerAdapter!!.getPosition(viewModel?.getUserProfileData()?.CandidateLocation?.City.toString()))

                }



            }else
            {

                Log.d(TAG, "getCountryCodeListFromApi: code $errorCode")
            }
        }
    }

    private fun getCandidateDetails(id:String)
    {
        viewModel?.getCandidateDetails(id)
        {data, isSuccess, errorCode, msg ->
            if (isSuccess)
            {
               setCandidateDetailsInView(data!!)
            }
        }
    }

    private fun setCandidateDetailsInView(dataForIOS: ResponseCandidateDataForIOS)
    {
        try {
            runOnUiThread {
                binding.etFirstname.setText(dataForIOS.Candidate?.Firstname)
                binding.etLastname.setText(dataForIOS.Candidate?.LastName)
                binding.etMiddlename.setText(dataForIOS.Candidate?.MiddleName)
                binding.etEmail.setText(dataForIOS?.Candidate?.PrimaryEmail.toString())
                binding.etZipCode.setText(dataForIOS.CandidateLocation?.Zip)
                binding.etStreet.setText(dataForIOS.CandidateLocation?.Street)
                binding.etPhoneno.setText(dataForIOS.Candidate?.PrimaryContact)
                binding.etExperience.setText(dataForIOS.Candidate?.Experience)
                binding.etPrimarySkills.setText(dataForIOS.Candidate?.primarySkills)
                binding.etSecondarySkills.setText(dataForIOS.Candidate?.Skills)
                binding.spinnerJobtype.setSelection(jobTypeSpinnerAdapter!!.getPosition(viewModel?.getUserProfileData()?.Candidate?.DesiredJob.toString()))


                getCountryListFromApi(2)
                getCountryCodeListFromApi(2)
            }
        }catch (e:Exception)
        {
            showCustomSnackbarOnTop(getString(R.string.txt_something_went_wrong))
        }
    }
}