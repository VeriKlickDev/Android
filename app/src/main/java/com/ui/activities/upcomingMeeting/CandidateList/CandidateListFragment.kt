package com.ui.activities.upcomingMeeting.CandidateList

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AbsListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.data.*
import com.data.cryptoJs.CryptoJsHelper
import com.data.dataHolders.DataStoreHelper
import com.domain.BaseModels.*
import com.domain.constant.AppConstants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.ui.activities.candidateQuestionnaire.ActivityShowCandidateQuestinnaire
import com.ui.activities.upcomingMeeting.UpComingMeetingViewModel
import com.ui.activities.upcomingMeeting.UpcomingMeetingActivity
import com.ui.listadapters.CandidateListAdapter
import com.veriKlick.R
import com.veriKlick.databinding.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CandidateListFragment() : Fragment() {
    lateinit var binding: FragmentCandidateListBinding
    private lateinit var viewModel: UpComingMeetingViewModel
    val TAG = "candidateList"
    var iscrolled = false
    var recyclerAdapter: CandidateListAdapter? = null
    var layoutManager: LinearLayoutManager? = null
    private var skipPage = 1
    private var contentLimit = 0
    private val candidateList = mutableListOf<SavedProfileDetail>()
    private var searchTxt :String?=null
    private var progressType=1

    private val jsEncryptor = CryptoJsHelper()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel=(requireActivity() as UpcomingMeetingActivity).getViewModel()
        Log.d(TAG, "onCreateView: onCreateView")
        binding = FragmentCandidateListBinding.inflate(layoutInflater)
        layoutManager = LinearLayoutManager(requireActivity())

      /*  CoroutineScope(Dispatchers.Main).launch {
            if (DataStoreHelper.getLoggedInFromStatus().equals(AppConstants.LOGGED_IN_WITH_OTP)
            ) {
                binding.btnHamburger.visibility=View.INVISIBLE
            }
        }*/

        binding.rvCandidateList.layoutManager = layoutManager
        recyclerAdapter =
            CandidateListAdapter(requireActivity(), arrayListOf(), onClick = { data, action ->
                when (action) {
                    1 -> {
                        showtemplateBottomsheet(data)
                        //handleContextMenuforItem(data)
                    }
                    2 -> {
                        if (requireActivity().checkInternet()) {
                            handleCall(data)
                        } else {
                            requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                        }

                    }
                    3 -> {

                        if (requireActivity().checkInternet()) {
                            handleSMS(1,data,null)
                        } else {
                            requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                        }

                        //showtemplateBottomsheet(data)

                    }
                    4->{
                        openQuestionnaire(data)
                    }
                }
            })
        binding.rvCandidateList.adapter = recyclerAdapter

        binding.btnHamburger.setOnClickListener {
            (requireActivity() as UpcomingMeetingActivity).openDrawer()
        }

        binding.tvHeader.setOnClickListener {
           // startActivity(Intent(requireActivity(),ActivityCandidateQuestinnaire::class.java))
        }

        setupRecyclerPagination()
        handleObserver()

        if (requireActivity().checkInternet()) {
            getCandidateList(1)
        } else {
            requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
        }


        if (requireActivity().checkInternet()) {
            CoroutineScope(Dispatchers.IO+ exceptionHandler).launch {
                getTemplateList(DataStoreHelper.getMeetingUserId())
            }
        } else {
            requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
        }


//8218090995
        binding.etSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchTxt = binding.etSearch.text.toString()
                if (requireActivity().checkInternet()) {
                    requireActivity().runOnUiThread {
                        candidateList.clear()
                        recyclerAdapter?.addList(candidateList)
                    }
                    skipPage=1
                    progressType=0
                    if (requireActivity().checkInternet()) {
                        getCandidateList(0)
                    } else {
                        requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                    }

                } else {
                    requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                }
                hideKeyboard(requireActivity())
                return@OnEditorActionListener true
            }
            false
        })

        binding.btnSearch.setOnClickListener {
            searchTxt = binding.etSearch.text.toString()
            if (requireActivity().checkInternet()) {
                requireActivity().runOnUiThread {
                    candidateList.clear()
                    recyclerAdapter?.addList(candidateList)
                }
                skipPage=1
                progressType=0
                if (requireActivity().checkInternet()) {
                    getCandidateList(0)
                } else {
                    requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                }

            } else {
                requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
            }
        }


        binding.btnSearchShow.setOnClickListener {
                if (binding.llSearchLayout.isVisible) {
                    binding.btnSearchShow.setImageResource(R.drawable.ic_search_black)
                    binding.llSearchLayout.isVisible=false
                    searchTxt=""

                    if (!binding.etSearch.text.toString().equals("")){
                        binding.etSearch.setText("")
                    if (requireActivity().checkInternet()) {
                        requireActivity().runOnUiThread {
                            candidateList.clear()
                            recyclerAdapter?.addList(candidateList)
                        }
                        progressType=0
                        if (requireActivity().checkInternet()) {
                            getCandidateList(0)
                        } else {
                            requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                        }

                    } else {
                        requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                    }}
                } else {
                    binding.llSearchLayout.isVisible=true
                    binding.btnSearchShow.setImageResource(R.drawable.ic_arrow_up_24)
                }
        }


        return binding.root
    }

    private fun openQuestionnaire(sdata: SavedProfileDetail)
    {//"48673"
        sdata
    viewModel.getQuestionnaireforCandidate(sdata.id.toString()){data, isSuccess, errorCode, msg ->
        if (data?.Answer?.size!!>0)
        {requireActivity().runOnUiThread {
            val i=Intent(requireContext(),ActivityShowCandidateQuestinnaire::class.java)
            i.putExtra(AppConstants.CANDIDATE_ID,sdata.id.toString())
            requireActivity().startActivity(i)
            requireActivity().overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }
        }else
        {
            requireActivity().showCustomSnackbarOnTop(msg)
        }
    }
    }

    private var phoneno=""
    private fun handleCall(data: SavedProfileDetail) {
        phoneno="+"+data.Countrycode+data.Phone
        checkPermissionForCall()
        //requireActivity().showCustomSnackbarOnTop("under progress")
        Log.d(TAG, "handleCall: candidate id ${data.id.toString()}")
    }

    private fun handleSMS(action:Int,data: SavedProfileDetail,templateId:Int?) {
        try {
            CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                val obj = BodySMSCandidate()
                obj.Subscriberid = DataStoreHelper.getMeetingUserId()
                obj.userid = DataStoreHelper.getMeetingRecruiterid().toInt()

                //var firstName = data.Name?.substring(0, data.Name?.indexOf(" ")!!.toInt())
                //var lastName =data.Name?.substring(data.Name?.indexOf(" ")!!.toInt() + 2, data?.Name!!.length)
                obj.UserEmailid = data.Email
                obj.email=data.Email

                obj.ReceiverNumber = "+"+data.Countrycode.toString()+data.Phone
                when(action)
                {
                    1->{
                        obj.language=getString(R.string.languageSelect)
                        showSendSmsCandidate(obj, data)
                    }
                    2->{
                        //obj.recieverId=data.Id
                        obj.MessageText="QAL"
                        obj.recieverId=data.id.toString()
                        obj.templateId=templateId.toString()
                        alertDialogForSendingSMS(obj)
                    }
                }
            }
        } catch (e: Exception) {
            requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_something_went_wrong))
        }
    }



    private fun alertDialogForSendingSMS(obj: BodySMSCandidate)
    {
       requireActivity().runOnUiThread {
           val alertDialog=AlertDialog.Builder(requireContext())
           alertDialog.setNegativeButton(getString(R.string.txt_no),object : DialogInterface.OnClickListener {
               override fun onClick(dialog: DialogInterface?, which: Int) {

               }
           })
           alertDialog.setPositiveButton(getString(R.string.txt_yes),object : DialogInterface.OnClickListener {
               override fun onClick(dialog: DialogInterface?, which: Int) {
                   sendMessage(obj)
               }
           })
           alertDialog.setTitle(getString(R.string.txt_do_you_want_to_send_template))
           alertDialog.create()
           alertDialog.show()
       }
    }


    private fun sendMessage(obj: BodySMSCandidate)
    {
        Log.d(TAG, "sendMessage: send object $obj jsonobject ${Gson().toJson(obj)}")

        viewModel?.sendProfileLink(obj) { data, isSuccess, errorCode, msg ->
            if (isSuccess) {
                requireActivity().showCustomSnackbarOnTop(data?.ResponseMessage.toString())
            }
            else
            {
                if (errorCode!=502)
                {
                    requireActivity().showCustomSnackbarOnTop(msg)
                }else{
                    requireActivity().showCustomSnackbarOnTop(getString(R.string.failed_to_send_message))
                }
            }
        }
    }

    private fun showSendSmsCandidate(obj: BodySMSCandidate, data: SavedProfileDetail) {
        requireActivity().runOnUiThread {
            val dialog = Dialog(requireActivity())

            val dialogBinding =
                LayoutSendSmsDialogBinding.inflate(LayoutInflater.from(requireContext()))
            dialog.setContentView(dialogBinding.root)
            Log.d(TAG, "showSendSmsCandidate: ")
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogBinding.btnCross.setOnClickListener {
                dialog.dismiss()
            }

            CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                requireActivity().runOnUiThread {
                    dialogBinding.tvUsername.setText(data.Name)
                }
            }
            dialogBinding.btnSend.setOnClickListener {
                if (!dialogBinding.etsms.text.toString().equals("") && dialogBinding.etsms.text.toString().trim().length>0) {
                    dialogBinding.tvErrorBlank.visibility=View.INVISIBLE
                    obj.MessageText = dialogBinding.etsms.text.toString()
                    requireActivity().runOnUiThread {
                        requireActivity().showProgressDialog()
                    }
                    dialog.dismiss()
                    viewModel?.sendProfileLink(obj) { data, isSuccess, errorCode, msg ->
                        if (isSuccess) {
                            requireActivity().runOnUiThread {
                                requireActivity().dismissProgressDialog()
                            }
                            requireActivity().showCustomSnackbarOnTop(data?.ResponseMessage.toString())
                        }else
                        {
                            requireActivity().runOnUiThread {
                                requireActivity().dismissProgressDialog()
                            }
                        }
                    }
                    Log.d("TAG", "postData: sending sms is ${Gson().toJson(obj)}")
                } else {
                    requireActivity().runOnUiThread {
                        Log.d(TAG, "showSendSmsCandidate: message blank")
                        dialogBinding.tvErrorBlank.visibility=View.VISIBLE
                        requireActivity().dismissProgressDialog()
                    }
                    Log.d(TAG, "handleSMS: blank")
                }
            }

            dialogBinding.etsms.doOnTextChanged { text, start, before, count ->
                if (text.toString().equals("") && text.toString().trim().length==0)
                {
                    dialogBinding.tvErrorBlank.visibility=View.VISIBLE
                }else
                {
                    dialogBinding.tvErrorBlank.visibility=View.INVISIBLE
                }
            }
            dialog.create()
            dialog.show()

        }
    }

    private val templateList= mutableListOf<QuestionierTemplates>()
    private var adapterTemplate:TemplatesListAdapter?=null
    private fun showtemplateBottomsheet(savedProfile: SavedProfileDetail)
    {
        val dialog= BottomSheetDialog(requireActivity(),R.style.AppBottomSheetDialogTheme)
        val dialogbinding= LayoutChooseQuestionTemplateBinding.inflate(LayoutInflater.from(requireActivity()))
        dialog.setContentView(dialogbinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogbinding.rvTemplates.layoutManager=LinearLayoutManager(requireActivity())
        adapterTemplate=TemplatesListAdapter(requireActivity(),templateList){data, action, pos ->
        when(action)
        {
            1->{
                if (requireActivity().checkInternet()) {
                    handleSMS(2,savedProfile,data.TemplateId)
                } else {
                    requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                }

                dialog.dismiss()
            }
        }
        }
        dialogbinding.rvTemplates.adapter=adapterTemplate

        dialog.create()
        dialog.show()
        dialogbinding.tvNoTemplate.isVisible=templateList.size==0
        dialogbinding.rvTemplates.isVisible=templateList.size!=0
    }

    private fun getTemplateList(recruiterId:String)
    {
        try{
            viewModel.getQuestionnaireList(recruiterId){data, isSuccess, errorCode, msg ->
            if (isSuccess)
            {
                templateList.clear()
                templateList.addAll(data?.questionierTemplates!!)
                requireActivity().runOnUiThread {
                    adapterTemplate?.notifyDataSetChanged()
                }
            }else{
               // requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_something_went_wrong))
            }
            }

        }catch (e:Exception)
        {
            Log.e(TAG, "getTemplateList: exception 210 ${e.message}", )
        }
    }


    private fun handleContextMenuforItem(data: SavedProfileDetail) {
        val dialog = Dialog(requireActivity())
        val dialogBinding =
            LayoutCandidatelistDescriptionDialogBinding.inflate(LayoutInflater.from(requireActivity()))
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogBinding.btnCross.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.tvInternalId.setText(data.id.toString())
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            var createdBy = DataStoreHelper.getMeetingRecruiterid()
            requireActivity().runOnUiThread {
                dialogBinding.tvCreatedBy.setText(createdBy)
                dialogBinding.tvLastUpdateBy.setText(createdBy)
            }
        }
        dialogBinding.tvCreatedOn.setText(getUtcDateToISTMMDDYYYY(data.CreatedDate.toString()))
        dialogBinding.tvLastUpdateOn.setText(getUtcDateToISTMMDDYYYY( data.LastInterviewDetailss.toString()))

        dialog.create()
        dialog.show()
    }


    private fun handleObserver() {

        binding.etSearch.doOnTextChanged { text, start, before, count ->
            if (text.toString().equals("") && text.toString().trim().length==0)
            {
                Log.d(TAG, "handleObserver: search blank")
                searchTxt=text.toString()
                skipPage=1
                candidateList.clear()
                recyclerAdapter?.notifyDataSetChanged()
                getCandidateList(1)
            }
        }

        Log.d(TAG, "handleObserver: ")
        viewModel.candidateListLive?.observe(requireActivity()) {
            Log.d(TAG, "handleObserver: in candidate list fragement")
            Log.d(
                TAG,
                "handleObserver: in candidate list fragement data ${it.savedProfileDetail.size}"
            )
            it?.let {
                contentLimit = it.totalCount!!.toInt()
                recyclerAdapter?.totalCount=it.totalCount!!.toInt()
                Log.d(TAG, "handleObserver: total count is ${it.totalCount}")
                Log.d(TAG, "handleObserver: candidate data $it")
                candidateList.addAll(it.savedProfileDetail)
                recyclerAdapter?.addList(candidateList)
                if (candidateList.size==0)
                {
                    binding.rvCandidateList.isVisible=false
                binding.tvNoData.isVisible=true
                }else
                {
                    binding.tvNoData.isVisible=false
                    binding.rvCandidateList.isVisible=true
                }
            }
                try {
                    recyclerAdapter?.notifyDataSetChanged()
                }catch (e:Exception)
                {
                    Log.d(TAG, "handleObserver: exception 332 ${e.message}")
                }
        }
    }

    private fun setupRecyclerPagination() {
        binding.swipetorefresh.setOnRefreshListener {
            skipPage=1
            candidateList.clear()
            recyclerAdapter?.notifyDataSetChanged()
            getCandidateList(1)
        }

        binding.rvCandidateList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                try {
                    super.onScrollStateChanged(recyclerView, newState)

                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                        iscrolled = true

                } catch (e: Exception) {
                    Log.d(TAG, "onScrollStateChanged: exception 221 ${e.message}")
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                try {
                    super.onScrolled(recyclerView, dx, dy)


                    val vitem = layoutManager?.childCount
                    val skipped = layoutManager?.findFirstVisibleItemPosition()
                    val totalitem = layoutManager?.itemCount
                    if (vitem != null) {
                        if (iscrolled && vitem + skipped!! == totalitem) {

                            if (contentLimit == candidateList.size) {

                            } else {
                                Log.d(TAG, "onScrolled: " + skipPage.toString())
                                if (requireActivity().checkInternet()) {
                                    getCandidateList(1)
                                } else {
                                    requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                                }

                            }

                            Log.d(TAG, "onScrolled: " + skipPage.toString())
                            iscrolled = false
                        }
                    }

                } catch (e: Exception) {
                    Log.d(TAG, "onScrolled: exception 228 ${e.message}")
                }

            }
        })
    }

    fun checkPermissionForCall() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                callPermission.launch(Manifest.permission.CALL_PHONE)
                /*ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    42)*/
            }
        } else {
            // Permission has already been granted
            callPhone()
        }
    }

    private val callPermission=registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            callPhone()
        } else {

        }
    }



   private fun callPhone(){
       requireActivity().runOnUiThread {
           val alertDialog=AlertDialog.Builder(requireContext())
           alertDialog.setNegativeButton(getString(R.string.txt_no),object : DialogInterface.OnClickListener {
               override fun onClick(dialog: DialogInterface?, which: Int) {

               }
           })
           alertDialog.setPositiveButton(getString(R.string.txt_yes),object : DialogInterface.OnClickListener {
               override fun onClick(dialog: DialogInterface?, which: Int) {
                   val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneno))
                   startActivity(intent)
               }
           })
           alertDialog.setTitle(getString(R.string.txt_do_you_want_to_call_the_person))
           alertDialog.create()
           alertDialog.show()
       }

    }


    private var isLoadFirst=false
    private fun getCandidateList(progressType:Int) {
        requireActivity().runOnUiThread {
            if (!isLoadFirst){
                binding.progressBar.isVisible=true
                binding.rvCandidateList.isVisible=false
            }
        }

        Log.d(TAG, "getCandidateList: ")
        CoroutineScope(Dispatchers.IO).launch {
            var reicd = DataStoreHelper.getMeetingRecruiterid()
            var subsId = DataStoreHelper.getMeetingUserId()
            // var enReic=jsEncryptor.encryptPlainTextWithRandomIV(reicd,"Synk@1234")
            // var enSubs=jsEncryptor.encryptPlainTextWithRandomIV(subsId,"Synk@1234")

            val ob = BodyCandidateList()

            Log.d(TAG, "getCandidateList: enreic ${reicd.toString()} ensubs ${subsId.toString()}")

            var top = ""

            var category = ""
            if (progressType==0)
            {
                requireActivity().runOnUiThread {
                    requireActivity().showProgressDialog()
                }
            }
            if (searchTxt==null)
            {
                searchTxt=""
            }
            ob.search=searchTxt.toString().trim()
            viewModel.getCandidateList(
                ob,
                recid = reicd,
                subsId,
                top,
                skipPage.toString(),
                searchExpression = searchTxt.toString(),
                category = category
            ) { response, errorCode, msg ->
                if (progressType==0)
                {
                    requireActivity().runOnUiThread {
                        requireActivity().dismissProgressDialog()
                    }
                }
               isLoadFirst=true
                if (response) {
                    requireActivity().runOnUiThread {
                        binding.rvCandidateList.isVisible=true
                        binding.progressBar.isVisible=false
                        binding.swipetorefresh.isRefreshing = false
                    }
                    Log.d(TAG, "getCandidateList: response sucess")
                    skipPage++
                } else {
                    requireActivity().runOnUiThread {
                        binding.rvCandidateList.isVisible=true
                        binding.progressBar.isVisible=false
                        binding.swipetorefresh.isRefreshing = false
                    }
                    Log.d(TAG, "getCandidateList: response not found/success $errorCode $msg")
                }
            }
        }
    }


    companion object {
        @JvmStatic
        /*fun getInstance(viewModel: UpComingMeetingViewModel): Fragment {
            return CandidateListFragment(viewModel)
        }*/
        fun getInstance(): Fragment {
            return CandidateListFragment()
        }
    }
}