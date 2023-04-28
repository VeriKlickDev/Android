package com.ui.activities.createCandidate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
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
import com.data.dataHolders.DataStoreHelper
import com.domain.BaseModels.*
import com.google.gson.Gson
import com.veriKlick.R
import com.veriKlick.databinding.ActivityCreateCandidateBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@AndroidEntryPoint
class ActivityCreateCandidate : AppCompatActivity() {

    private lateinit var binding: ActivityCreateCandidateBinding
    private var viewModel: VMCreateCandidate? = null
    private val TAG = "acitivityCreateCan"

    private var isEmailok=false
    private var isPhoneok=false
    private var isFirstName=false
    private var isLastName=false

    private lateinit var spinnerCountryCodeAdapter:ArrayAdapter<String>
    private var iscountryCode:String?=null
    private var countryCodeList= mutableListOf<String>()
    private var countryCodeListMain= mutableListOf<ResponseCountryCode>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCandidateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(VMCreateCandidate::class.java)

        handleTextWatcher()

        binding.btnJumpBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSubmit.setOnClickListener {
            validateAllFields()
        }
        setupCountryCodeAdapter()
        getCountryCodeList()
    }

    private  fun setupCountryCodeAdapter()
    {
    spinnerCountryCodeAdapter=getArrayAdapterOneItemSelected(countryCodeList)
    binding.spinnerCountryCode.adapter=  spinnerCountryCodeAdapter
        binding.spinnerCountryCode.onItemSelectedListener=onItemSelectListner
    }
    private val onItemSelectListner= object :OnItemSelectedListener{
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position==0)
            {

            }else
            {
                val ob=countryCodeListMain.find { "${it.codedisplay} ${it.Name}".equals(countryCodeList[position]) }
                iscountryCode=ob?.codedisplay    
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            
        }

    }

    private fun getCountryCodeList()
    {
        viewModel?.getCountyCodeList{data, isSuccess, errorCode, msg ->
            if (isSuccess)
            {
                countryCodeListMain.clear()
                countryCodeList.clear()
                countryCodeList.add("Country Code")
                countryCodeListMain.addAll(data!!)
                countryCodeListMain.forEach { countryCodeList.add(it.codedisplay.toString()+" ${it.Name.toString()}") }
                runOnUiThread { spinnerCountryCodeAdapter.notifyDataSetChanged() }
            }
        }
    }



    private fun handleTextWatcher()
    {
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

        binding.etFirstname.doOnTextChanged { text, start, before, count ->
            val pattern="[a-zA-Z0-9]+[a-zA-Z0-9\\s]*"
            val ptrn=Pattern.compile(pattern)
            isFirstName=ptrn.matcher(text.toString()).matches()

        }

        binding.etLastname.doOnTextChanged { text, start, before, count ->
            val pattern="[a-zA-Z0-9]+[a-zA-Z0-9\\s]*"
            val ptrn=Pattern.compile(pattern)
            isLastName=ptrn.matcher(text.toString()).matches()
        }

    }

    private fun validateAllFields()
    {
        if (isFirstName && isLastName && isEmailok && isPhoneok && iscountryCode!=null)
        {
            postData()
        }
        else
        {
            showCustomSnackbarOnTop(getString(R.string.txt_invalid_or_blank_data))
        }
    }

    private fun postData()
    {
        Log.d(TAG, "postData: success")
        try {
            CoroutineScope(Dispatchers.IO+ exceptionHandler).launch {
                val obj=BodySMSCandidate()
                obj.Subscriberid=DataStoreHelper.getMeetingUserId()
                obj.userid=DataStoreHelper.getMeetingRecruiterid().toInt()
                obj.FirstName=binding.etFirstname.text.toString()
                obj.LastName=binding.etLastname.text.toString()
                obj.email=binding.etEmail.text.toString()
                obj.MessageText="SPL"
                obj.ReceiverNumber=iscountryCode+binding.etPhoneno.text.toString()
                Log.d(TAG, "postData: sending sms is ${Gson().toJson(obj)}")
                viewModel?.sendProfileLink(obj){data, isSuccess, errorCode, msg ->
                    if (isSuccess)
                    {
                        showCustomSnackbarOnTop(data?.ResponseMessage.toString())
                    }
                }
            }

        }catch (e:Exception)
        {
        showCustomSnackbarOnTop(getString(R.string.txt_something_went_wrong))
        }

    }


}