package com.ui.activities.createCandidate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.data.*
import com.domain.BaseModels.*
import com.google.gson.Gson
import com.veriKlick.databinding.ActivityCreateCandidateBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityCreateCandidate : AppCompatActivity() {

    private lateinit var binding: ActivityCreateCandidateBinding
    private var viewModel: VMCreateCandidate? = null
    private var fragementNo = 0
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
    private var stateStr:String?=null
    private var jobTypeStr:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCandidateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(VMCreateCandidate::class.java)


        jobTypeStringList.addAll(getJobTypeList())


        setupCountryCodeSpinner()
        setupCountrySpinner()
        setupStateSpinner()
        setupCitySpinner()
        setupjobTypeSpinner()

        getCountryListFromApi()
        getCountryCodeListFromApi()


        binding.etEmail.doOnTextChanged { text, start, before, count ->
            emailValidator(this, text.toString()) { isEmailOk, mEmail, error ->
                isEmailok=isEmailOk
                if (isEmailOk)
                    Log.d(TAG, "onCreate: email valid $mEmail")
                else
                    binding.etEmail.setError("Invalid")
                    Log.d(TAG, "onCreate: email not valid $mEmail")
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

    }

    private fun validateAllFields()
    {
        if (binding.etEmail.text.toString().equals(""))
        {
            Log.d(TAG, "validateAllFields: email blank")
        }
        else if(binding.etFirstname.text.toString().equals(""))
        {
            Log.d(TAG, "validateAllFields: firstname blank")
        }
        else if(binding.etMiddlename.text.toString().equals(""))
        {
            Log.d(TAG, "validateAllFields: middel name blank")
        }
        else if(binding.etLastname.text.toString().equals(""))
        {
            Log.d(TAG, "validateAllFields: last name blank")
        }
        else if(binding.etEmail.text.toString().equals("") || !isEmailok)
        {
            Log.d(TAG, "validateAllFields: email blank")
        }
        else if(binding.etPhoneno.text.toString().equals("") || !isPhoneok)
        {
            Log.d(TAG, "validateAllFields: phone blank")
        }
       /* else if(binding.etPhoneCode.text.toString().equals(""))
        {
            Log.d(TAG, "validateAllFields: phone blank")
        }*/
        else if(binding.etZipCode.text.toString().equals(""))
        {
            Log.d(TAG, "validateAllFields: zip blank")
        }
        else if(binding.etStreet.text.toString().equals(""))
        {
            Log.d(TAG, "validateAllFields: street blank")
        }
        else if(binding.etPrimarySkills.text.toString().equals(""))
        {
            Log.d(TAG, "validateAllFields: primary skills blank")
        }
        else if(binding.etSecondarySkills.text.toString().equals(""))
        {
            Log.d(TAG, "validateAllFields: secondary blank")
        }
        else if(binding.etExperience.text.toString().equals(""))
        {
            Log.d(TAG, "validateAllFields: secondary blank")
        }
        else if (cityStr.equals("") || cityStr==null)
        {
            Log.d(TAG, "validateAllFields: city blank")
        }
        else if (stateStr.equals("") || stateStr==null)
        {
            Log.d(TAG, "validateAllFields: state blank")
        }
        else if (countryStr.equals("") || countryStr==null){
            Log.d(TAG, "validateAllFields: country blank")
        }
        else if (jobTypeStr.equals("") || jobTypeStr==null){
            Log.d(TAG, "validateAllFields: jobtype blank")
        }else
        {
            Log.d(TAG, "validateAllFields: success")
            postData()
        }
    }

    private fun postData()
    {

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
                                getCityListFromApi("All",it.Id.toString())
                                getStateListFromApi(it.SortName.toString(),"All")
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
                            if (stateStringList[position].toString()
                                    .equals(it.Shortname.toString())
                            ) {

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
            val listMain=Gson().fromJson<Array<ModelJobType>>("[{ \"id\": \"CORP-CORP\", name: \"CORP-CORP\", \"selected\": false },{ \"id\": \"W2 PERMANENT\", name: \"W2PERMANENT\", \"selected\": false },{ \"id\": 'W2 CONTRACT', name: \"W2 CONTRACT\", \"selected\": false },{ \"id\": \"1099 CONTRACT\", name: \"1099 CONTRACT\", \"selected\": false },{ \"id\": \"H1B TRANSFER\", name: \"H1B TRANSFER\",\"selected\": false },{ \"id\": \"NEED H1B\", name: \"NEED H1B\", \"selected\": false},{ \"id\": \"PERMANENT\", name: \"PERMANENT\", \"selected\": false },{ \"id\": \"CONTRACT\", name: \"CONTRACT\", \"selected\": false }]",
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
                        Log.d(TAG, "onItemSelected: selected ${it}")
                    }
                }
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }


    private fun setupCountryCodeSpinner() {
        countryCodeSpinnerAdapter =object :
            ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item,
                countryCodeStringList
            ) {
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                var v: View? = null
                if (position === 0) {
                    val tv = TextView(context)
                    tv.height = 0
                    v = tv
                } else {
                    v = super.getDropDownView(
                        position,
                        null,
                        parent
                    )
                }
                return v!!
            }
            }

        binding.spinnerCountryCode.adapter = countryCodeSpinnerAdapter
        binding.spinnerCountryCode.onItemSelectedListener = countryCodeSpinnerClickListner
    }

    private val countryCodeSpinnerClickListner = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position == 0) {
                Log.d(TAG, "onItemSelected: selected ${jobTypeStringList[position]}")
            }else
            {
                try
                {
                    if (!jobTypeListMain.isNullOrEmpty()){
                        jobTypeListMain.forEach {
                            if (it.name.equals(jobTypeStringList[position]))
                            {
                                jobTypeStr=it.id
                                Log.d(TAG, "onItemSelected: selected ${it}")
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




    private fun getCountryListFromApi(){
        viewModel?.getCountyNameList { data, isSuccess, errorCode, msg ->
            if (isSuccess)
            {
                try {
                    countryStringList.clear()
                    countryListMain.clear()

                    countryStringList.add("Select Country")
                    countryListMain.addAll(data!!)
                    countryListMain.forEach {
                        countryStringList.add(it.SortName.toString())
                    }
                    runOnUiThread {
                        countrySpinnerAdapter?.notifyDataSetChanged()
                    }

                }catch (e:Exception)
                {

                }
            }
        }
    }

    private fun getCountryCodeListFromApi(){
        viewModel?.getCountyCodeList { data, isSuccess, errorCode, msg ->
            if (isSuccess)
            {
                countryCodeList.clear()
                countryCodeStringList.clear()
                countryCodeStringList.add("Code")
                countryCodeList.addAll(data!!)
                countryCodeList.forEach {
                    countryCodeStringList.add(it.codedisplay.toString()+" "+it.Name)
                }
                Log.d(TAG, "getCountryCodeListFromApi: countryCode $countryStringList ${countryCodeStringList}")
                runOnUiThread {
                    countryCodeSpinnerAdapter?.notifyDataSetChanged()
                }

            }else
            {
                Log.d(TAG, "getCountryCodeListFromApi: code $errorCode")
            }
        }
    }

    private fun getStateListFromApi(stateShortName:String,searchString: String){
        viewModel?.getStateByidList(stateShortName,searchString) { data, isSuccess, errorCode, msg ->
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
                runOnUiThread {
                    stateSpinnerAdapter?.notifyDataSetChanged()
                }

            }else
            {
                Log.d(TAG, "getCountryCodeListFromApi: code $errorCode")
            }
        }
    }

    private fun getCityListFromApi(searchString:String,id:String){
        viewModel?.getCityByidList(searchString,id) { data, isSuccess, errorCode, msg ->
            if (isSuccess)
            {
                cityListmain.clear()
                cityStringList.clear()
                cityListmain.addAll(data!!)
                cityListmain.forEach {
                    cityStringList.add(it.CityName.toString())
                }
                Log.d(TAG, "getCountryCodeListFromApi: countryCode $countryStringList ${countryCodeStringList}")
                runOnUiThread{
                    citySpinnerAdapter?.notifyDataSetChanged()
                }

            }else
            {
                /*cityListmain.clear()
                cityStringList.clear()
                runOnUiThread{
                    citySpinnerAdapter?.notifyDataSetChanged()
                }*/
                Log.d(TAG, "getCountryCodeListFromApi: code $errorCode")
            }
        }
    }





}