package com.domain.BaseModels

import com.google.gson.annotations.SerializedName

data class BodyCancelMeeting(
    @SerializedName("Cuid"               ) var Cuid               : String?      = "3bc30ebc-93a7-4c06-8fca-47bfded3e6b8",
    @SerializedName("CancellationReason" ) var CancellationReason : String?      = "no reason",
    @SerializedName("InterviewSeqId"     ) var InterviewSeqId     : Int?         = null,
    @SerializedName("aPIResponse"        ) var aPIResponse        : APIResponse1? = APIResponse1(),
    @SerializedName("SubscriberId"       ) var SubscriberId       : String?      = null,
    @SerializedName("logedInUser"        ) var logedInUser        : String?      = null
)


data class APIResponse1 (

    @SerializedName("Message"    ) var Message    : String?  = null,
    @SerializedName("StatusCode" ) var StatusCode : String?  = null,
    @SerializedName("Success"    ) var Success    : Boolean? = null,
    @SerializedName("TimeTaken"  ) var TimeTaken  : String?  = null

)
