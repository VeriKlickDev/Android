package com.domain.BaseModels

import com.google.gson.annotations.SerializedName


data class BodyMeetingClose (
    @SerializedName("RoomName"          ) var RoomName          : String?  = null,
    @SerializedName("RoomSid"           ) var RoomSid           : String?  = null,
    @SerializedName("RoomStatus"        ) var RoomStatus        : String?  = null,
    @SerializedName("TotalParticipants" ) var TotalParticipants : Int?     = null,
    @SerializedName("Message"           ) var Message           : String?  = null,
    @SerializedName("StatusCode"        ) var StatusCode        : String?  = null,
    @SerializedName("Success"           ) var Success           : Boolean? = null,
    @SerializedName("TimeTaken"         ) var TimeTaken         : String?  = null

)