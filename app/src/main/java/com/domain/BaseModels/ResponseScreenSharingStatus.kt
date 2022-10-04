package com.domain.BaseModels

import com.google.gson.annotations.SerializedName

data class ResponseScreenSharingStatus(

    @SerializedName("InterviewerList"          ) var InterviewerList          : ArrayList<InterviewerList1> = arrayListOf(),
    @SerializedName("ScreenShareCurrentStatus" ) var ScreenShareCurrentStatus : Boolean?                   = null,
    @SerializedName("Message"                  ) var Message                  : String?                    = null,
    @SerializedName("StatusCode"               ) var StatusCode               : String?                    = null,
    @SerializedName("Success"                  ) var Success                  : Boolean?                   = null,
    @SerializedName("TimeTaken"                ) var TimeTaken                : String?                    = null

)

data class InterviewerList1 (

    @SerializedName("Status"                   ) var Status                   : String?  = null,
    @SerializedName("ScreenShareCurrentStatus" ) var ScreenShareCurrentStatus : Boolean? = null

)