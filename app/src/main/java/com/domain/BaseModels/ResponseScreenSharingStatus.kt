package com.domain.BaseModels

import com.google.gson.annotations.SerializedName

data class ResponseScreenSharingStatus(

    @SerializedName("OwnScreenShareStatus" ) var OwnScreenShareStatus : Boolean? = null,
    @SerializedName("Message"              ) var Message              : String?  = null,
    @SerializedName("StatusCode"           ) var StatusCode           : String?  = null,
    @SerializedName("Success"              ) var Success              : Boolean? = null,
    @SerializedName("TimeTaken"            ) var TimeTaken            : String?  = null

)