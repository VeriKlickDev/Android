package com.ui.activities.upcomingMeeting.CandidateList

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.data.*
import com.data.cryptoJs.CryptoJsHelper
import com.data.dataHolders.CurrentMeetingDataSaver
import com.data.dataHolders.DataStoreHelper
import com.domain.BaseModels.BodyCandidateList
import com.domain.BaseModels.BodySMSCandidate
import com.domain.BaseModels.CurrentVideoUserModel
import com.domain.BaseModels.SavedProfileDetail
import com.google.gson.Gson
import com.ui.activities.candidateQuestionnaire.ActivityCandidateQuestinnaire
import com.ui.activities.login.LoginActivity
import com.ui.activities.upcomingMeeting.UpComingMeetingViewModel
import com.ui.activities.upcomingMeeting.UpcomingMeetingActivity
import com.ui.activities.upcomingMeeting.audioRecord.AudioMainActivity
import com.ui.activities.upcomingMeeting.audioRecord.PlayActivity
import com.ui.listadapters.CandidateListAdapter
import com.veriKlick.R
import com.veriKlick.databinding.FragmentCandidateListBinding
import com.veriKlick.databinding.LayoutCandidatelistDescriptionDialogBinding
import com.veriKlick.databinding.LayoutDescriptionDialogBinding
import com.veriKlick.databinding.LayoutItemSenderChatBinding
import com.veriKlick.databinding.LayoutSendSmsDialogBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CandidateListFragment(val viewModel: UpComingMeetingViewModel) : Fragment() {
    lateinit var binding: FragmentCandidateListBinding
    val TAG = "candidateList"
    var iscrolled = false
    var recyclerAdapter: CandidateListAdapter? = null
    var layoutManager: LinearLayoutManager? = null
    private var skipPage = 1
    private var contentLimit = 0
    private val candidateList = mutableListOf<SavedProfileDetail>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: candidateListFragment")


    }

    private val jsEncryptor = CryptoJsHelper()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: onCreateView")
        binding = FragmentCandidateListBinding.inflate(layoutInflater)
        layoutManager = LinearLayoutManager(requireActivity())
        binding.rvCandidateList.layoutManager = layoutManager
        recyclerAdapter =
            CandidateListAdapter(requireActivity(), arrayListOf(), onClick = { data, action ->
                when (action) {
                    1 -> {
                        handleContextMenuforItem(data)
                    }
                    2 -> {
                        handleCall(data)
                    }
                    3 -> {
                        handleSMS(data)
                    }
                }
            })
        binding.rvCandidateList.adapter = recyclerAdapter

        binding.btnHamburger.setOnClickListener {
            (requireActivity() as UpcomingMeetingActivity).openDrawer()
        }

        binding.tvHeader.setOnClickListener {
            startActivity(Intent(requireActivity(),ActivityCandidateQuestinnaire::class.java))
        }
        setupRecyclerPagination()
        handleObserver()
        getCandidateList()
        return binding.root
    }

    private fun handleCall(data: SavedProfileDetail) {
        data.id.toString()
        Log.d(TAG, "handleCall: candidate id ${data.id.toString()}")
    }

    private fun handleSMS(data: SavedProfileDetail) {
        try {
            CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                val obj = BodySMSCandidate()
                obj.Subscriberid = DataStoreHelper.getMeetingUserId()
                obj.userid = DataStoreHelper.getMeetingRecruiterid().toInt()
                var firstName = data.Name?.substring(0, data.Name?.indexOf(" ")!!.toInt())
                var lastName =
                    data.Name?.substring(data.Name?.indexOf(" ")!!.toInt() + 2, data?.Name!!.length)
                obj.FirstName = firstName
                obj.LastName = lastName
                obj.UserEmailid = data.Email
                obj.ReceiverNumber = data.Phone
                showSendSmsCandidate(obj, data)


            }
        } catch (e: Exception) {
            requireActivity().showCustomSnackbarOnTop(getString(R.string.txt_something_went_wrong))
        }
    }

    fun showSendSmsCandidate(obj: BodySMSCandidate, data: SavedProfileDetail) {
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
                if (!dialogBinding.etsms.text.toString().equals("")) {
                    obj.MessageText = dialogBinding.etsms.text.toString()
                    viewModel?.sendProfileLink(obj) { data, isSuccess, errorCode, msg ->
                        if (isSuccess) {
                            requireActivity().showCustomSnackbarOnTop(data?.ResponseMessage.toString())
                        }
                    }
                    Log.d("TAG", "postData: sending sms is ${Gson().toJson(obj)}")
                } else {
                    Log.d(TAG, "handleSMS: blank")
                }
            }
            dialog.create()
            dialog.show()

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
                recyclerAdapter?.notifyDataSetChanged()
            }
        }
    }

    private fun setupRecyclerPagination() {
        binding.swipetorefresh.setOnRefreshListener {
            candidateList.clear()
            recyclerAdapter?.notifyDataSetChanged()
            getCandidateList()
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
                                    getCandidateList()
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


    private fun getCandidateList() {

        Log.d(TAG, "getCandidateList: ")
        CoroutineScope(Dispatchers.IO).launch {
            var reicd = DataStoreHelper.getMeetingRecruiterid()
            var subsId = DataStoreHelper.getMeetingUserId()
            // var enReic=jsEncryptor.encryptPlainTextWithRandomIV(reicd,"Synk@1234")
            // var enSubs=jsEncryptor.encryptPlainTextWithRandomIV(subsId,"Synk@1234")

            val ob = BodyCandidateList()

            Log.d(TAG, "getCandidateList: enreic ${reicd.toString()} ensubs ${subsId.toString()}")


            var top = ""
            var searchexp = ""
            var category = ""
            viewModel.getCandidateList(
                ob,
                recid = reicd,
                subsId,
                top,
                skipPage.toString(),
                searchExpression = searchexp,
                category = category
            ) { response, errorCode, msg ->
                if (response) {
                    binding.swipetorefresh.isRefreshing = false
                    Log.d(TAG, "getCandidateList: response sucess")
                    skipPage++
                } else {
                    binding.swipetorefresh.isRefreshing = false
                    Log.d(TAG, "getCandidateList: response not found/success $errorCode $msg")
                }
            }
        }
    }


    companion object {
        @JvmStatic
        fun getInstance(viewModel: UpComingMeetingViewModel): Fragment {
            return CandidateListFragment(viewModel)
        }
    }
}