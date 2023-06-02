package com.ui.activities.createCandidate

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter

import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.data.*
import com.data.dataHolders.CurrentMeetingDataSaver
import com.data.dataHolders.DataStoreHelper
import com.domain.BaseModels.*
import com.google.gson.Gson
import com.ui.activities.upcomingMeeting.UpcomingMeetingActivity
import com.veriKlick.R
import com.veriKlick.databinding.FragmentCreateCandidateBinding
import com.veriKlick.databinding.LayoutChooseLanguageDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@AndroidEntryPoint
class FragmentCreateCandidate : Fragment() {

    private lateinit var binding: FragmentCreateCandidateBinding
    private var viewModel: VMCreateCandidate? = null

    private var isEmailok=false
    private var isPhoneok=false
    private var isFirstName=false
    private var isLastName=false

    private lateinit var spinnerCountryCodeAdapter:ArrayAdapter<String>
    private var iscountryCode:String?=null
    private var countryCodeList= mutableListOf<String>()
    private var countryCodeListMain= mutableListOf<ResponseCountryCode>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateCandidateBinding.inflate(layoutInflater)



        viewModel = ViewModelProvider(this)[VMCreateCandidate::class.java]

        handleTextWatcher()

        binding.btnBugerIcon.setOnClickListener {
            (requireActivity() as UpcomingMeetingActivity).openDrawer()
        }

        binding.btnSubmit.setOnClickListener {
            validateAllFields()
        }
        setupCountryCodeAdapter()
        getCountryCodeList()




        return binding.root
    }


    private  fun setupCountryCodeAdapter()
    {
    spinnerCountryCodeAdapter=requireActivity().getArrayAdapterOneItemSelected(countryCodeList)
    binding.spinnerCountryCode.adapter=  spinnerCountryCodeAdapter
        binding.spinnerCountryCode.onItemSelectedListener=onItemSelectListner
    }
    private val onItemSelectListner= object :OnItemSelectedListener{
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position==0)
            {
                val ob=countryCodeListMain.find { "${it.codedisplay} ${it.Name}".contains("United States") }
                iscountryCode=ob?.codedisplay
                countryCodeList.forEachIndexed { index, s ->
                    if (s == "+1 United States")
                    {
                        requireActivity().runOnUiThread {
                            binding.spinnerCountryCode.setSelection(index)
                            spinnerCountryCodeAdapter.notifyDataSetChanged()
                        }

                    }
                }

            }else
            {
                val ob=countryCodeListMain.find { "${it.codedisplay} ${it.Name}" == countryCodeList[position] }
                iscountryCode=ob?.codedisplay    
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            
        }
    }

    private fun getCountryCodeList()
    {
        viewModel?.getCountyCodeList{ data, isSuccess, _, _ ->
            if (isSuccess)
            {
                countryCodeListMain.clear()
                countryCodeList.clear()
                countryCodeList.add("Country Code")
                countryCodeListMain.addAll(data!!)
                countryCodeListMain.forEach { countryCodeList.add(it.codedisplay.toString()+" ${it.Name.toString()}") }
                requireActivity().runOnUiThread { spinnerCountryCodeAdapter.notifyDataSetChanged() }
            }
        }
    }


    private fun chooseLanguage(obj:BodySMSCandidate)
    {
        requireActivity().runOnUiThread {
            val dialog = Dialog(requireActivity())

            val dialogBinding =
                LayoutChooseLanguageDialogBinding.inflate(LayoutInflater.from(requireActivity()))
            dialog.setContentView(dialogBinding.root)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogBinding.btnCross.setOnClickListener {
                dialog.dismiss()
            }
            val language= mutableListOf<String>()
            val languageStringList= mutableListOf<ModelLanguageSelect>()


            languageStringList.add(ModelLanguageSelect(getString(R.string.txt_english),"en"))
            languageStringList.add(ModelLanguageSelect(getString(R.string.txt_spanish),"es"))
            languageStringList.add(ModelLanguageSelect(getString(R.string.txt_french),"fr"))

            language.add(getString(R.string.txt_select_language))
            language.add(getString(R.string.txt_english))
            language.add(getString(R.string.txt_spanish))
            language.add(getString(R.string.txt_french))
            var selectedLanguage:String?=null
            CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                requireActivity().runOnUiThread {

                    // dialogBinding.tvUsername.setText(data.Name)

                    val langAdapter = requireActivity().getArrayAdapterOneItemSelected(language)
                    dialogBinding.spinnerLanguage.onItemSelectedListener =
                        object : OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                if (position != 0) {
                                    languageStringList.forEach {
                                        if (it.language.equals(language[position]))
                                        {
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
                        if (selectedLanguage!=null)
                        {
                            dialog.dismiss()
                            obj.language=selectedLanguage
                            viewModel?.sendProfileLink(obj){data, isSuccess, errorCode, msg ->
                            if (isSuccess)
                            {
                                requireActivity().showCustomSnackbarOnTop(data?.ResponseMessage.toString())
                            }
                        }
                        }else
                        {
                            dialogBinding.tvError.visibility=View.VISIBLE
                            requireActivity().setHandler().postDelayed({
                                dialogBinding.tvError.visibility=View.INVISIBLE
                            },3000)
                            //showCustomSnackbarOnTop(getString(R.string.txt_please_select_language))
                        }
                    }
                }

            }

            dialog.create()
            dialog.show()

        }
        
    }

    private fun handleTextWatcher()
    {
        binding.etEmail.doOnTextChanged { text, _, _, _ ->
            emailValidator(requireActivity(), text.toString()) { isEmailOk, _, _ ->
                isEmailok=isEmailOk
                if (!isEmailOk)
                    binding.etEmail.error = "Invalid"
            }
        }

        binding.etPhoneno.doOnTextChanged { text, _, _, _ ->
            isPhoneok=requireActivity().phoneValidator(text.toString())
            if (isPhoneok)
            {
              //  checkPhoneExists(text.toString())
            }

            if (!requireActivity().phoneValidator(text.toString()))
                binding.etPhoneno.error = "Invalid"
        }

      /***2jun2023  binding.etFirstname.doOnTextChanged { text, _, _, _ ->
            val pattern="[a-zA-Z0-9]+[a-zA-Z0-9\\s]*"
            val ptrn=Pattern.compile(pattern)
            isFirstName=ptrn.matcher(text.toString()).matches()
        }

        binding.etLastname.doOnTextChanged { text, _, _, _ ->
            val pattern="[a-zA-Z0-9]+[a-zA-Z0-9\\s]*"
            val ptrn=Pattern.compile(pattern)
            isLastName=ptrn.matcher(text.toString()).matches()
        }*/

    }


    private fun checkPhoneExists(txt: String) {
        viewModel?.getIsPhoneExists(
            CurrentMeetingDataSaver.getData()?.interviewModel?.interviewId!!,
            "",
            txt,
            response = { isExists ->
                if (!isExists) {
                    binding.etPhoneno.setError("phoneNo exists")
                } else {
                    binding.etPhoneno.setError("phoneNo not exists")
                }
            })
    }

    private fun validateAllFields()
    {
        if (isFirstName && isLastName && isEmailok && isPhoneok && iscountryCode!=null)
        {
            postData()
        }
        else
        {
            requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_invalid_or_blank_data))
        }
    }

    private fun postData()
    {
        try {
            CoroutineScope(Dispatchers.IO+ exceptionHandler).launch {
                val obj=BodySMSCandidate()
                obj.Subscriberid=DataStoreHelper.getMeetingUserId()
                obj.userid=DataStoreHelper.getMeetingRecruiterid().toInt()
                //obj.FirstName=binding.etFirstname.text.toString()
                //obj.LastName=binding.etLastname.text.toString()
                obj.UserEmailid=binding.etEmail.text.toString()
                obj.email=binding.etEmail.text.toString()
                obj.language=getString(R.string.languageSelect)
                obj.MessageText="SPL"
                obj.ReceiverNumber=iscountryCode+binding.etPhoneno.text.toString()
                Log.d("TAG", "postData: sending sms is ${Gson().toJson(obj)}")
               chooseLanguage(obj)
            }

        }catch (e:Exception)
        {
            requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_something_went_wrong))
        }

    }


}