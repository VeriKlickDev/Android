package com.domain.BaseModels

data class BodyCandidateJoinedMeetingStatus(
    val VideoAccessCode:String?=null,
    val Status:String?=null,
    val SubscriberId:String?=null
)


data class ResponseCandidateJoinedMeetingStatus(
    val Message:String?=null,
    val StatusCode:String?=null,
    val Success:Boolean?=null
)
