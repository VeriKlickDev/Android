package com.domain.BaseModels

import com.twilio.video.VideoTrack

data class CurrentVideoUserModel(val videoTrack: VideoTrack, val username:String, val type:String)