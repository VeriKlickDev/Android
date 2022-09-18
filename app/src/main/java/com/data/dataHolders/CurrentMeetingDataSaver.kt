package com.data.dataHolders

import androidx.lifecycle.MutableLiveData
import com.domain.BaseModels.ResponseInterViewDetailsBean
import com.domain.BaseModels.TokenResponseBean

private val list= mutableListOf<ResponseInterViewDetailsBean>()
private val tokenResponse= mutableListOf<TokenResponseBean>()
var screenSharingStatus=false
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
    private var disconnectedStatusLiveData=MutableLiveData<Boolean>()

  /*  fun setIsRoomDisconnected(status:Boolean)
    {
        disconnectedStatusLiveData.postValue(status)
    }

    fun getIsRoomDisconnected()= disconnectedStatusLiveData
*/

    fun setScreenSharingStatus(status:Boolean)
    {
        screenSharingStatus=status
    }
    fun getScreenSharingStatus()= screenSharingStatus

}