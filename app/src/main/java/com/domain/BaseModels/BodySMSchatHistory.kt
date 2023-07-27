package com.domain.BaseModels

import com.google.gson.annotations.SerializedName

data class BodySMSchatHistory (

    @SerializedName("candidateId" ) var candidateId : Int?    = null,
    @SerializedName("MessageType" ) var MessageType : String? = "",
    @SerializedName("SenderID"    ) var SenderID    : Int?    = 0,
    @SerializedName("MessageText" ) var MessageText : String? = ""

)


data class ResponseSMSchatHistory(
    @SerializedName("CreatedBy"    ) var CreatedBy    : String? = null,
    @SerializedName("CreatedOn"    ) var CreatedOn    : String? = null,
    @SerializedName("CreatedDate"  ) var CreatedDate  : String? = null,
    @SerializedName("CreatedTime"  ) var CreatedTime  : String? = null,
    @SerializedName("SenderID"     ) var SenderID     : Int?    = null,
    @SerializedName("SenderName"   ) var SenderName   : String? = null,
    @SerializedName("ReceiverID"   ) var ReceiverID   : Int?    = null,
    @SerializedName("ReceiverName" ) var ReceiverName : String? = null,
    @SerializedName("MessageText"  ) var MessageText  : String? = null,
    @SerializedName("MessageType"  ) var messageType  : String? = null,

)