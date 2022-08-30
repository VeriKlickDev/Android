package com.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.twilio.video.LocalParticipant

object LocalConfrenseMic {
  private  lateinit var localParticipant: LocalParticipant
  private var isMuted=MutableLiveData<Boolean>()
    fun setLocalParticipantMic(isMutedm : Boolean)
    {
        isMuted.postValue(isMutedm)
    }
    fun getLocalParticipant():LiveData<Boolean>
    {
        return isMuted
    }

}