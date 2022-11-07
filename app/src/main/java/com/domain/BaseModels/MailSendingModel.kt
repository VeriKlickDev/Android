package com.domain.BaseModels

import com.google.gson.annotations.SerializedName


data class BodySendMail (

    @SerializedName("UserSeqId"    ) var UserSeqId    : Int?    = null,
    @SerializedName("SubscriberId" ) var SubscriberId : String? = null,
    @SerializedName("logedInUser"  ) var logedInUser  : String? = null

)


data class ResponseSendMail (

    @SerializedName("Message"    ) var Message    : String?  = null,
    @SerializedName("StatusCode" ) var StatusCode : String?  = null,
    @SerializedName("Success"    ) var Success    : Boolean? = null

)