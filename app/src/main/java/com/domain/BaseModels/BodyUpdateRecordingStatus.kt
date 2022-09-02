package com.domain.BaseModels

import com.google.gson.annotations.SerializedName



data class BodyUpdateRecordingStatus (

    @SerializedName("InterviewId"     ) var InterviewId     : Int?    = null,
    @SerializedName("RoomSid"         ) var RoomSid         : String? = null,
    @SerializedName("RecordingStatus" ) var RecordingStatus : String? = null,
    @SerializedName("StatusCode"      ) var StatusCode      : String? = null,
    @SerializedName("Message"         ) var Message         : String? = null

)
