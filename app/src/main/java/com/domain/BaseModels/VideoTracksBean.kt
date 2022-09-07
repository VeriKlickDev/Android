package com.domain.BaseModels

import com.twilio.video.*

data class VideoTracksBean(var remoteParticipant: RemoteParticipant?, var videoTrack: VideoTrack, var userName:String)
