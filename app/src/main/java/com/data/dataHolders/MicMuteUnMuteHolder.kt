package com.data.dataHolders

import android.content.Context
import android.provider.MediaStore.Audio
import androidx.lifecycle.MutableLiveData
import com.data.helpers.TwilioHelper
import com.domain.BaseModels.AudioStatusModel
import com.domain.BaseModels.VideoStatusModel
import com.twilio.video.LocalAudioTrack
import com.twilio.video.LocalVideoTrack
import com.twilio.video.ktx.createLocalAudioTrack
import com.twilio.video.ktx.createLocalVideoTrack
import com.twilio.video.ktx.enabled
import com.twilio.video.quickstart.kotlin.CameraCapturerCompat

private var localVideoTrack:LocalVideoTrack?=null
private var localAudioTrack: LocalAudioTrack?=null
private var  isVideoByUser:Boolean?=null
private var  isAudioByUser:Boolean?=null
object MicMuteUnMuteHolder {

    var micStatus= MutableLiveData<AudioStatusModel>()
    var videoStatus= MutableLiveData<VideoStatusModel>()

    fun setMicStatus(mic:Boolean)
    {
        //micStatus.postValue(mic)
    }

    fun setVideoStatus(video:Boolean,byUser: Boolean)
    {
        videoStatus.postValue(VideoStatusModel(video,byUser))
    }

    fun setLocalVideoTrack(context: Context,cameraCapturerCompat:CameraCapturerCompat)
    {
        localVideoTrack = createLocalVideoTrack(
            context,
            true,
            cameraCapturerCompat
        )
    }

    fun getLocalVideoTrack()= localVideoTrack

    fun setVideoHideStatus(sts:Boolean,byUser: Boolean)
    {
        if (isVideoByUser!=null)
        {
            if (!sts && !byUser)
            {
                videoStatus.postValue(VideoStatusModel(sts,byUser))
                localVideoTrack?.enabled=sts
            }
        }else
        {
            videoStatus.postValue(VideoStatusModel(sts,byUser))
            localVideoTrack?.enabled=sts
        }


    }

    fun setVideoStatusByUser(sts:Boolean)
    {
        isVideoByUser=sts
    }

    fun getVideoStatusByUser()= isVideoByUser

    fun setLocalAudioTrack(context: Context)
    {
        localAudioTrack = createLocalAudioTrack(context, true)
    }
    fun getLocalAudioTrack()= localAudioTrack

    fun setLocalAudioMicStatus(sts:Boolean,byUser:Boolean)
    {
      /*  localAudioTrack?.enabled=sts
        micStatus.postValue(AudioStatusModel(sts,byUser))

        try {
            if (!sts){
                isAudioByUser?.let {
                    if (it!!)
                    {
                        localAudioTrack?.enabled=false
                        micStatus.postValue(AudioStatusModel(false,true))
                    }
                }
            }

        }catch (e:Exception)
        {

        }

        isAudioByUser=byUser*/
    }

}