package com.data.dataHolders

import androidx.lifecycle.MutableLiveData

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

}