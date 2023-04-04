package com.ui.activities.upcomingMeeting.CandidateList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.ui.activities.upcomingMeeting.UpcomingMeetingActivity
import com.veriKlick.R
import com.veriKlick.databinding.FragmentCandidateListBinding

class CandidateListFragment : Fragment() {
    lateinit var binding:FragmentCandidateListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentCandidateListBinding.inflate(layoutInflater)

        binding.tvHelloworld.setOnClickListener {
            (requireActivity() as UpcomingMeetingActivity).openDrawer()
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
    @JvmStatic
        fun getInstance(): Fragment{
            return CandidateListFragment()
    }
    }
}