package com.data

import com.domain.BaseModels.InterviewModel
import com.domain.BaseModels.ResponseInterViewDetailsBean
import com.domain.BaseModels.TokenResponseBean

private val list= mutableListOf<ResponseInterViewDetailsBean>()
private val tokenResponse= mutableListOf<TokenResponseBean>()
object CurrentMeetingDataSaver {

    fun setData(ob: ResponseInterViewDetailsBean)
    {
        list.add(0,ob)
    }

    fun getData():ResponseInterViewDetailsBean
    {
        return list[0]
    }

    fun setRoomData(obj:TokenResponseBean)
    {
        tokenResponse.add(0,obj)
    }
    fun getRoomData():List<TokenResponseBean>
    {
        return tokenResponse
    }


}