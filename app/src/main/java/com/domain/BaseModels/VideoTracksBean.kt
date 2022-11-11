package com.domain.BaseModels

import com.twilio.video.*

data class VideoTracksBean(var identity:String,var remoteParticipant: RemoteParticipant?, var videoTrack: VideoTrack?, var userName:String)
//,val sid:String