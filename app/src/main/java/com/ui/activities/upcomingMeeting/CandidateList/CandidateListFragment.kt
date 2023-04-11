package com.ui.activities.upcomingMeeting.CandidateList

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.data.dataHolders.CurrentMeetingDataSaver
import com.data.dataHolders.DataStoreHelper
import com.domain.BaseModels.BodyCandidateList
import com.domain.BaseModels.CurrentVideoUserModel
import com.ui.activities.upcomingMeeting.UpComingMeetingViewModel
import com.ui.activities.upcomingMeeting.UpcomingMeetingActivity
import com.ui.activities.upcomingMeeting.audioRecord.AudioMainActivity
import com.ui.activities.upcomingMeeting.audioRecord.PlayActivity
import com.ui.activities.uploadProfilePhoto.UploadProfilePhoto
import com.veriKlick.R
import com.veriKlick.databinding.FragmentCandidateListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CandidateListFragment(val viewModel: UpComingMeetingViewModel) : Fragment() {
    lateinit var binding:FragmentCandidateListBinding
    val TAG ="candidateList"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: candidateListFragment")
        
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: onCreateView")
        binding=FragmentCandidateListBinding.inflate(layoutInflater)

        binding.tvHelloworld.setOnClickListener {
            //(requireActivity() as UpcomingMeetingActivity).openDrawer()
            //startActivity(Intent( requireActivity(),UploadProfilePhoto::class.java))
            startActivity(Intent( requireActivity(),AudioMainActivity::class.java))
        }
        handleObserver()
        getCandidateList()
        return binding.root
    }

    fun handleObserver()
    {
        Log.d(TAG, "handleObserver: ")
     viewModel.candidateListLive?.observe(requireActivity()){
         it?.let {
             Log.d(TAG, "handleObserver: candidate data $it")
         }
     }
    }

    fun getCandidateList()
    {
        Log.d(TAG, "getCandidateList: ")
        CoroutineScope(Dispatchers.IO).launch {
            var reicd=DataStoreHelper.getMeetingRecruiterid()
            var subsId=DataStoreHelper.getMeetingUserId()
            val ob=BodyCandidateList()
            viewModel.getCandidateList(ob, recid = reicd,subsId){response, errorCode, msg ->
                if (response)
                {
                    Log.d(TAG, "getCandidateList: response sucess")
                }else
                {
                    Log.d(TAG, "getCandidateList: response not found/success $errorCode $msg")
                }
            }
        }
    }


    companion object {
    @JvmStatic
        fun getInstance(viewModel:UpComingMeetingViewModel): Fragment{
            return CandidateListFragment(viewModel)
    }
    }
}