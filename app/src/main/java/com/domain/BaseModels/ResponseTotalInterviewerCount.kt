package com.domain.BaseModels

import com.google.gson.annotations.SerializedName

data class ResponseTotalInterviewerCount(
    @SerializedName("TotalInterviewerCount" ) var TotalInterviewerCount : Int?     = null,
    @SerializedName("Message"               ) var Message               : String?  = null,
    @SerializedName("StatusCode"            ) var StatusCode            : String?  = null,
    @SerializedName("Success"               ) var Success               : Boolean? = null,
    @SerializedName("TimeTaken"             ) var TimeTaken             : String?  = null

)
