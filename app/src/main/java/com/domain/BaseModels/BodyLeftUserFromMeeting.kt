package com.domain.BaseModels

import com.google.gson.annotations.SerializedName

data class BodyLeftUserFromMeeting(

    @SerializedName("ParticipantName") var ParticipantName: String? = null,
    @SerializedName("ParticipantSid") var ParticipantSid: String? = null,
    @SerializedName("RoomSid") var RoomSid: String? = null,
    @SerializedName("Status") var Status: String? = null,
    @SerializedName("Message") var Message: String? = null,
    @SerializedName("StatusCode") var StatusCode: String? = null,
    @SerializedName("Success") var Success: Boolean? = null

)