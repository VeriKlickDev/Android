package com.domain.BaseModels

import com.twilio.video.*

data class VideoTracksBean(
    var identity: String? = null,
    var remoteParticipant: RemoteParticipant? = null,
    var videoTrack: VideoTrack? = null,
    var userName: String? = null,
    val videoSid: String? = null
)
//,val sid:String