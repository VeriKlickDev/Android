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
import androidx.recyclerview.widget.LinearLayoutManager
import com.data.*
import com.data.dataHolders.DataStoreHelper
import com.domain.BaseModels.*
import com.google.gson.Gson
import com.ui.activities.upcomingMeeting.UpcomingMeetingActivity
import com.veriKlick.R
import com.veriKlick.databinding.DialogCountryCodeBinding
import com.veriKlick.databinding.FragmentCreateCandidateBinding
import com.veriKlick.databinding.LayoutChooseLanguageDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import layout.CountryCodeListAdapter

@AndroidEntryPoint
class FragmentCreateCandidate : Fragment() {

    private lateinit var binding: FragmentCreateCandidateBinding
    private var viewModel: VMCreateCandidate? = null

    private var isEmailok=false
    private var isPhoneok=false

    private lateinit var countryCodeRecyerlerAdapter:CountryCodeListAdapter
    private var iscountryCode:String?=null
    private var countryCodeList= mutableListOf<String>()
    private var countryCodeListMain= arrayListOf<ResponseCountryCode>()
    private var viewGroup:ViewGroup?=null

    private fun clearAllValues()
    {
        isEmailok=false
        isPhoneok=false
        iscountryCode=null
        binding.etEmail.setText("")
        binding.etPhoneno.setText("")
        binding.tvCountryCode.setText(getString(R.string.txt_country_code))
        binding.tvEmailError.visibility=View.INVISIBLE
        binding.tvPhoneError.visibility=View.INVISIBLE
        binding.etEmail.clearFocus()
        binding.etPhoneno.clearFocus()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateCandidateBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[VMCreateCandidate::class.java]

        this.viewGroup=container
        handleTextWatcher()
      //  requireActivity().runOnUiThread { binding.btnSubmit.isEnabled=false }
        countryCodeRecyerlerAdapter=CountryCodeListAdapter(requireContext(),countryCodeListMain){pos, data, action ->
            iscountryCode=data?.PhoneCode.toString()
            binding.tvCountryCode.setText(data?.codedisplay.toString())
            binding.tvCountryCode.setTextColor(Color.BLACK)
            countryCodeDialog.dismiss()
        }

        binding.btnBugerIcon.setOnClickListener {
            (requireActivity() as UpcomingMeetingActivity).openDrawer()
        }

        binding.btnSubmit.setOnClickListener {
            if (requireActivity().checkInternet()) {
                validateAllFields()
            } else {
                requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
            }
        }

        if (requireActivity().checkInternet()) {
            getCountryCodeList()
        } else {
            requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
        }



        binding.layoutSelectCode.setOnClickListener {
            setupCountryCodeRecyclerAdapter()
        }
        //setupCountryCodeAdapter()

        return binding.root
    }


  /*  private  fun setupCountryCodeAdapter()
    {
    countryCodeRecyerlerAdapter=requireActivity().getArrayAdapterOneItemSelected(countryCodeList)
    binding.spinnerCountryCode.adapter=  countryCodeRecyerlerAdapter
        binding.spinnerCountryCode.onItemSelectedListener=onItemSelectListner
    }
    private val onItemSelectListner= object :OnItemSelectedListener{
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position==0)
            {
              /***5jun  val ob=countryCodeListMain.find { "${it.codedisplay} ${it.Name}".contains("United States") }
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
            */
            }else
            {
                val ob=countryCodeListMain.find { "${it.codedisplay} ${it.Name}" == countryCodeList[position] }
                iscountryCode=ob?.codedisplay    
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            
        }
    }*/

    private lateinit var countryCodeDialog:Dialog

    private fun setupCountryCodeRecyclerAdapter()
    {
        Log.d("TAG", "setupCountryCodeRecyclerAdapter: setup adapter")
        countryCodeDialog=Dialog(requireContext())
        val dialogBinding=DialogCountryCodeBinding.inflate(layoutInflater,viewGroup,false)
        countryCodeDialog.setContentView(dialogBinding.root)
        countryCodeDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogBinding.rvCountry.layoutManager=LinearLayoutManager(requireContext())

        val templist= arrayListOf<ResponseCountryCode>()
        templist.addAll(countryCodeListMain)

        dialogBinding.btnCross.setOnClickListener { countryCodeDialog.dismiss() }

        CoroutineScope(Dispatchers.IO).launch {
            dialogBinding.etSearch.getEditTextWithFlow().collectLatest {text->
                Log.d("TAG", "setupCountryCodeRecyclerAdapter: for each contains $text")
                    val searchedlist=templist.filter { it.Name.toString().lowercase().trim().contains(text.toString().lowercase().trim()) }.toMutableList()

                Log.d("TAG", "setupCountryCodeRecyclerAdapter: for each contains $text list size ${searchedlist.size}")
                      requireActivity().runOnUiThread {
                          countryCodeRecyerlerAdapter.search(searchedlist.distinct())
                      }

            }
        }

        dialogBinding.rvCountry.adapter=countryCodeRecyerlerAdapter
        countryCodeDialog.create()
        countryCodeDialog.show()
    }
  /*
    requireActivity().run {runOnUiThread {countryCodeRecyerlerAdapter.notifyDataSetChanged()}
    }*/


    private fun getCountryCodeList()
    {
        requireActivity().run {
            //runOnUiThread { showProgressDialog() }
        }
        viewModel?.getCountyCodeList{ data, isSuccess, _, _ ->
            if (isSuccess)
            {
               /* requireActivity().run {
                    runOnUiThread { dismissProgressDialog() }
                }*/
                countryCodeListMain.clear()
                countryCodeList.clear()
                countryCodeList.add(getString(R.string.txt_select_code))
                countryCodeListMain.addAll(data!!)
                countryCodeListMain.forEach { countryCodeList.add(it.codedisplay.toString()+" ${it.Name.toString()}") }

                requireActivity().runOnUiThread {
                    countryCodeRecyerlerAdapter.swapList(countryCodeListMain)
                }
            }
            else
            {
              /*  requireActivity().run {
                    runOnUiThread { dismissProgressDialog() }
                }*/
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
            dialogBinding.btnSubmitButton.setText(getString(R.string.txt_send))
            val language= mutableListOf<String>()
            val languageStringList= mutableListOf<ModelLanguageSelect>()

            languageStringList.add(ModelLanguageSelect(getString(R.string.txt_english),"en-US"))
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
                            requireActivity().runOnUiThread {
                                requireActivity().showProgressDialog()
                            }

                            viewModel?.sendProfileLink(obj){data, isSuccess, errorCode, msg ->
                            if (isSuccess)
                            {
                                requireActivity().runOnUiThread {
                                    requireActivity().dismissProgressDialog()
                                    requireActivity().showCustomToast(data?.ResponseMessage.toString())
                                    //requireActivity().showCustomSnackbarOnTop(data?.ResponseMessage.toString())
                                    clearAllValues()
                                }
                            }
                                else
                            {
                                requireActivity().runOnUiThread {
                                    requireActivity().dismissProgressDialog()
                                    requireActivity().showCustomSnackbarOnTop(data?.ResponseMessage.toString())
                                }
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
        CoroutineScope(Dispatchers.IO).launch    {
            binding.etEmail.getEditTextWithFlow().collectLatest{text->
                delay(1000)
                emailValidator(requireActivity(), text.toString()) { isEmailOk, _, _ ->
                    isEmailok=isEmailOk
                    if (!isEmailOk) {
                        if (!binding.etEmail.text.toString().equals("")) {
                        requireActivity().runOnUiThread {
                            binding.tvEmailError.visibility=View.VISIBLE
                            binding.tvEmailError.setText(getString(R.string.txt_invalid)+" ${getString(R.string.txt_email)}")
                            emailErrorstr=getString(R.string.txt_invalid)+" ${getString(R.string.txt_email)}"
                        }}
                    }
                    else{
                        if (requireActivity().checkInternet()) {
                            checkEmailExists(text.toString())
                        } else {
                            requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                        }

                    }
                }
            }
        }


        binding.etPhoneno.doOnTextChanged { text, _, _, _ ->
            isPhoneok=requireActivity().phoneValidatorfor9and10Digits(text.toString())
            if (isPhoneok)
            {
                binding.tvPhoneError.visibility=View.INVISIBLE
                if (requireActivity().checkInternet()) {
                    checkPhoneExists(text.toString())
                } else {
                    requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                }
            }
            if (!requireActivity().phoneValidatorfor9and10Digits(text.toString())){
                if (!binding.etPhoneno.text.toString().equals("")) {
                    binding.tvPhoneError.setText(getString(R.string.txt_invalid) + " ${getString(R.string.txt_phoneNo)}")
                    phoneErrorstr =
                        getString(R.string.txt_invalid) + " ${getString(R.string.txt_phoneNo)}"
                    binding.tvPhoneError.visibility = View.VISIBLE
                }
            }

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

    private fun checkEmailExists(txt: String) {
        requireActivity().runOnUiThread { binding.btnSubmit.isEnabled=false }
        viewModel?.getIsPhoneExists(txt,false,
            response = { data ->
                requireActivity().runOnUiThread {
                    if (data.aPIResponse?.Message!=null) {
                        requireActivity().runOnUiThread { binding.btnSubmit.isEnabled=true }
                        binding.tvEmailError.visibility=View.VISIBLE
                        binding.tvEmailError.setText(data.aPIResponse?.Message.toString())
                        emailErrorstr=data.aPIResponse?.Message.toString()
                        isEmailok=false
                    } else {
                        requireActivity().runOnUiThread { binding.btnSubmit.isEnabled=true }
                        binding.tvEmailError.visibility=View.INVISIBLE
                       // binding.etEmail.setError(data.aPIResponse?.Message.toString())
                        isEmailok=true
                    }
                }
            })
    }

    private fun checkPhoneExists(txt: String) {
        requireActivity().runOnUiThread { binding .btnSubmit.isEnabled=false}
        viewModel?.getIsPhoneExists(txt,true,
            response = { data ->
                requireActivity().runOnUiThread {
                    if (data.aPIResponse?.StatusCode!=null) {
                        requireActivity().runOnUiThread { binding .btnSubmit.isEnabled=true}
                        binding.tvPhoneError.visibility=View.VISIBLE
                        binding.tvPhoneError.setText(data.aPIResponse?.Message.toString())
                        phoneErrorstr=data.aPIResponse?.Message.toString()
                        isPhoneok=false
                    } else {
                        requireActivity().runOnUiThread { binding .btnSubmit.isEnabled=true}
                       // binding.tvEmailError.visibility=View.INVISIBLE
                      //  binding.etPhoneno.setError(data.aPIResponse?.Message.toString())
                        isPhoneok=true
                    }
                }
            })
    }
    private var emailErrorstr:String?=null
    private var phoneErrorstr:String?=null
    private fun validateAllFields()
    {
        if (isEmailok && isPhoneok && iscountryCode!=null)
        {
            postData()
        }
        else
        {
            if (iscountryCode==null) {
                requireActivity().showCustomSnackbarOnTop(
                    getString(R.string.txt_country_code) + " ${
                        getString(
                            R.string.txt_isNotSelected
                        )
                    }"
                )
            }
            if (binding.etEmail.text.toString().trim().equals("") && binding.etPhoneno.text.toString().trim().equals(""))
            {
                requireActivity().showCustomSnackbarOnTop("${getString(R.string.txt_email)} ${getString(R.string.txt_and)} ${getString(R.string.txt_phoneNo)} ${getString(R.string.txt_required)}")
            }else
            {

                if (!isPhoneok)
                {
                    Log.d("TAG", "validateAllFields: phone error")
                    phoneErrorstr.let {
                        requireActivity().showCustomSnackbarOnTop(it.toString())
                    }
                }
                if (!isEmailok)
                {
                    Log.d("TAG", "validateAllFields: email error")
                    emailErrorstr.let {
                        requireActivity().showCustomSnackbarOnTop(it.toString())
                    }
                }
            }

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
                obj.ReceiverNumber="+"+iscountryCode+binding.etPhoneno.text.toString()
                Log.d("TAG", "postData: sending sms is ${Gson().toJson(obj)}")
                if (requireActivity().checkInternet()) {
                    chooseLanguage(obj)
                } else {
                    requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                }


            }

        }catch (e:Exception)
        {
            requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_something_went_wrong))
        }

    }


}