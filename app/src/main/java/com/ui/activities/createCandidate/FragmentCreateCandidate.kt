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
    private var isFirstName=false
    private var isLastName=false

    private lateinit var countryCodeRecyerlerAdapter:CountryCodeListAdapter
    private var iscountryCode:String?=null
    private var countryCodeList= mutableListOf<String>()
    private var countryCodeListMain= arrayListOf<ResponseCountryCode>()
    private var viewGroup:ViewGroup?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateCandidateBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[VMCreateCandidate::class.java]

        this.viewGroup=container
        handleTextWatcher()

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
            validateAllFields()
        }

        getCountryCodeList()

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
            runOnUiThread { showProgressDialog() }
        }
        viewModel?.getCountyCodeList{ data, isSuccess, _, _ ->
            if (isSuccess)
            {
                requireActivity().run {
                    runOnUiThread { dismissProgressDialog() }
                }
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
                requireActivity().run {
                    runOnUiThread { dismissProgressDialog() }
                }
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
                                    requireActivity().showCustomSnackbarOnTop(data?.ResponseMessage.toString())
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
        CoroutineScope(Dispatchers.IO).launch {
            binding.etEmail.getEditTextWithFlow().collectLatest{text->
                delay(1000)
                emailValidator(requireActivity(), text.toString()) { isEmailOk, _, _ ->
                    isEmailok=isEmailOk
                    if (!isEmailOk) {
                        requireActivity().runOnUiThread {
                            binding.etEmail.error = "Invalid"
                        }
                    }
                    else
                        checkEmailExists(text.toString())
                }
            }
        }


        binding.etPhoneno.doOnTextChanged { text, _, _, _ ->
            isPhoneok=requireActivity().phoneValidator(text.toString())
            if (isPhoneok)
            {
                checkPhoneExists(text.toString())
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

    private fun checkEmailExists(txt: String) {
        viewModel?.getIsPhoneExists(txt,false,
            response = { data ->
                requireActivity().runOnUiThread {
                    if (data.aPIResponse?.Message!=null) {
                        binding.etEmail.setError(data.aPIResponse?.Message.toString())
                        isEmailok=false
                    } else {
                       // binding.etEmail.setError(data.aPIResponse?.Message.toString())
                        isEmailok=true
                    }
                }
            })
    }

    private fun checkPhoneExists(txt: String) {
        viewModel?.getIsPhoneExists(txt,true,
            response = { data ->
                requireActivity().runOnUiThread {
                    if (data.aPIResponse?.StatusCode!=null) {
                        binding.etPhoneno.setError(data.aPIResponse?.Message.toString())
                        isPhoneok=false
                    } else {
                      //  binding.etPhoneno.setError(data.aPIResponse?.Message.toString())
                        isPhoneok=true
                    }
                }
            })
    }

    private fun validateAllFields()
    {
        if (isEmailok && isPhoneok && iscountryCode!=null)
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
                obj.ReceiverNumber="+"+iscountryCode+binding.etPhoneno.text.toString()
                Log.d("TAG", "postData: sending sms is ${Gson().toJson(obj)}")
               chooseLanguage(obj)
            }

        }catch (e:Exception)
        {
            requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_something_went_wrong))
        }

    }


}