package com.data.dataHolders

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.domain.BaseModels.VideoTracksBean

object CurrentConnectUserList {

    val listLiveData= MutableLiveData<List<VideoTracksBean>>()
    private val listLiveDataForAddParticipant= MutableLiveData<List<VideoTracksBean>>()
    private val participantList= mutableListOf<VideoTracksBean>()

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
        templist.forEach {

            var pos=templist.indexOf(it)

        }
        templist.mapIndexed { index, videoTracksBean ->
            Log.d("checkMicStatusIntObject", "setItemToParticipantList:out of if mic Status name ${lst.userName} pos $pos mic ${videoTracksBean.remoteParticipant?.remoteAudioTracks?.firstOrNull()?.isTrackEnabled} ")
            if (lst.identity.equals(videoTracksBean.identity))
            {
                pos = index
                lst.userName = videoTracksBean.userName
                participantList[index] = lst
                Log.d("checkMicStatusIntObject", "setItemToParticipantList: in if condition mic Status name ${lst.userName} pos $pos mic ${videoTracksBean.remoteParticipant?.remoteAudioTracks?.firstOrNull()?.isTrackEnabled} ")
            }
        }
        //onDataChanged(pos, participantList)
        listLiveData.postValue(participantList)
        return pos
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