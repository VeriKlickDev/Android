package com.data.dataHolders

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import com.ui.activities.upcomingMeeting.UpComingFragment.UpcomingListFragment

private val doRefresh= mutableListOf<Boolean>()
private var doRefreshLive=MutableLiveData<Boolean>()
object UpcomingMeetingStatusHolder {
    private var meetingStatus=""
    fun setStatus(str:String){
        meetingStatus=str
    }
    fun getStatus()= meetingStatus

    fun setIsRefresh(sts:Boolean)
    {
        doRefresh.add(0,sts)
    }

    fun isMeetingFinished( sts:Boolean)
    {
        doRefreshLive.postValue(sts)
    }

    fun getRefereshStatus()= doRefresh.firstOrNull()

    fun getIsMeetingFinished()= doRefreshLive


    /***/
    val fragList= mutableListOf<Fragment>()
    private var fragmentManagerr: FragmentManager?=null
    fun getFragManager() = fragmentManagerr
    fun createInstanceOfFragments(fragmentManager: FragmentManager){
        fragmentManagerr=fragmentManager
        fragList.addAll(fragmentManager.fragments)
        Log.d("TAG", "createInstanceOfFragments: viewmodel fragment size ${fragList.size} ${fragmentManager.fragments.size} ")
    }
    fun setFragement(frag:UpcomingListFragment)
    {
        fragList.add(frag)
    }

    fun getFragment()=fragList.firstOrNull()



}