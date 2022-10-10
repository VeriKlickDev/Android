package com.domain.BaseModels

import com.twilio.video.NetworkQualityLevel

data class MicStatusModel(var micStatus:Boolean, var identity:String)
data class NetworkQualityModel(var identity:String, var network:NetworkQualityLevel)
