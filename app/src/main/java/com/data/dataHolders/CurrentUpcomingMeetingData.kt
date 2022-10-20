package com.data.dataHolders

import com.domain.BaseModels.NewInterviewDetails

object CurrentUpcomingMeetingData {
    private val list= mutableListOf<NewInterviewDetails>()
    fun setData(ob:NewInterviewDetails)
    {
        list.add(0,ob)
    }
    fun getData()=list[0]
}