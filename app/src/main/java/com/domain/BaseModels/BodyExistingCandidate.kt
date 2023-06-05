package com.domain.BaseModels

import com.google.gson.annotations.SerializedName

data class BodyExistingCandidate (

    @SerializedName("CandidateId"   ) var CandidateId   : String?         = null,
    @SerializedName("CurrentUserId" ) var CurrentUserId : String?      = null,
    @SerializedName("SubscriberId"  ) var SubscriberId  : String?      = null,
    @SerializedName("SearchString"  ) var SearchString  : String?      = null,
    @SerializedName("aPIResponse"   ) var aPIResponse   : APIResponse12? = APIResponse12()

)
data class APIResponse12 (

    @SerializedName("Message"    ) var Message    : String?  = null,
    @SerializedName("StatusCode" ) var StatusCode : String?  = null,
    @SerializedName("Success"    ) var Success    : Boolean? = null,
    @SerializedName("TimeTaken"  ) var TimeTaken  : String?  = null

)