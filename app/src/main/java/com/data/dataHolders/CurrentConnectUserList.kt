package com.data.dataHolders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.domain.BaseModels.VideoTracksBean

object CurrentConnectUserList {

    val listLiveData= MutableLiveData<List<VideoTracksBean>>()
    private val listLiveDataForAddParticipant= MutableLiveData<List<VideoTracksBean>>()
    private val participantList= mutableListOf<VideoTracksBean>()

    fun clearList()
    {
        participantList.clear()
        listLiveData.postValue(participantList)
    }

    fun getParticipantListSize()= participantList.size

    fun setListForVideoActivity(list:List<VideoTracksBean>)
    {
        participantList.clear()
        listLiveData.postValue(list)
        participantList.addAll(list)
        //listLiveDataForAddParticipant.postValue(list)
    }
//,onDataChanged:(pos:Int,list:List<VideoTracksBean>)->Unit
    fun setItemToParticipantList(lst :VideoTracksBean):Int
    {
        val templist= mutableListOf<VideoTracksBean>()
        templist.addAll(participantList)
        var pos =-1
        templist.mapIndexed { index, videoTracksBean ->
            if (lst.identity.equals(videoTracksBean.identity))
            {
                pos = index
                lst.userName = videoTracksBean.userName
                lst.remoteParticipant=videoTracksBean.remoteParticipant
                lst.videoTrack=videoTracksBean.videoTrack

                participantList[index] = lst
            }
        }
        //onDataChanged(pos, participantList)
        listLiveData.postValue(participantList)
        return pos
    }

    fun getListofParticipant()= participantList

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