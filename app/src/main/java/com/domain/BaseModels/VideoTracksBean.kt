package com.domain.BaseModels

import com.twilio.video.*

data class VideoTracksBean(val remoteParticipant: RemoteParticipant?,val videoTrack: VideoTrack,val userName:String)
