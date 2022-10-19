package com.data.dataHolders

object UpcomingMeetingStatusHolder {
    private var meetingStatus=""
    fun setStatus(str:String){
        meetingStatus=str
    }
    fun getStatus()= meetingStatus

}