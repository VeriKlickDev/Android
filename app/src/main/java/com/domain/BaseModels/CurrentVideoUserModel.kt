package com.domain.BaseModels

import com.twilio.video.LocalVideoTrack
import com.twilio.video.RemoteParticipant
import com.twilio.video.VideoTrack

data class MicMuteStatusModel(val removeParticipant: RemoteParticipant,val status:Boolean=false)
data class CurrentVideoUserModel(val identity:String,val videoTrack: VideoTrack, val username:String, val type:String)
data class ScreenSharingModel(val localVideoTrack: LocalVideoTrack,val isSharing:Boolean)