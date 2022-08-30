package com.data

import com.domain.BaseModels.InterviewModel
import com.domain.BaseModels.ResponseInterViewDetailsBean

private val list= mutableListOf<ResponseInterViewDetailsBean>()

object CurrentMeetingDataSaver {

    fun setData(ob: ResponseInterViewDetailsBean)
    {
        list.add(0,ob)
    }

    fun getData():ResponseInterViewDetailsBean
    {
        return list[0]
    }

}