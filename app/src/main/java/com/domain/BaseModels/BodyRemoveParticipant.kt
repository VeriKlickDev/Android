package com.domain.BaseModels

import com.google.gson.annotations.SerializedName

data class BodyRemoveParticipant(

    @SerializedName("ParticipantSid") var ParticipantSid: String? = null,
    @SerializedName("ParticipantName") var ParticipantName: String? = null,
    @SerializedName("RoomSid") var RoomSid: String? = null,
    @SerializedName("Status") var Status: String? = null

)


