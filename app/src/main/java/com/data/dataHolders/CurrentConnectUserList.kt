package com.data.dataHolders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.domain.BaseModels.VideoTracksBean

object CurrentConnectUserList {

    val listLiveData= MutableLiveData<List<VideoTracksBean>>()
    val listLiveDataForAddParticipant= MutableLiveData<List<VideoTracksBean>>()

    fun setListForVideoActivity(list:List<VideoTracksBean>)
    {
        listLiveData.postValue(list)
        //listLiveDataForAddParticipant.postValue(list)
    }
    fun getListForVideoActivity():LiveData<List<VideoTracksBean>>
    {
        return listLiveData
    }

    fun setListForAddParticipantActivity(list:List<VideoTracksBean>)
    {
        listLiveDataForAddParticipant.postValue(list)
    }
    fun getListForAddParticipantActivity():LiveData<List<VideoTracksBean>>
    {
        return listLiveDataForAddParticipant
    }

}