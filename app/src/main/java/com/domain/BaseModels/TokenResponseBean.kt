package com.domain.BaseModels

data class TokenResponseBean(
    val token:String?=null,
    val identity:String?=null,
    val roomName:String?=null,
    val VoiceCallPassCode:String?=null,
    val VoiceCallPhoneNumber:String?=null,
)