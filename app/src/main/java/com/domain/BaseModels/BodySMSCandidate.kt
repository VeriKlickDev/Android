package com.domain.BaseModels

import com.google.gson.annotations.SerializedName

data class BodySMSCandidate (

    @SerializedName("userid"         ) var userid         : Int?    = null,
    @SerializedName("ReceiverNumber" ) var ReceiverNumber : String? = null,
    @SerializedName("Subscriberid"   ) var Subscriberid   : String? = null,
    @SerializedName("UserEmailid"          ) var UserEmailid      : String? = null,
    @SerializedName("MessageText"    ) var MessageText    : String? = null,
    @SerializedName("FirstName"      ) var FirstName      : String? = null,
    @SerializedName("LastName"       ) var LastName       : String? = null

)

data class ResponseSMSCadidate (

    @SerializedName("MsgID"           ) var MsgID           : Int?     = null,
    @SerializedName("IsActive"        ) var IsActive        : Boolean? = null,
    @SerializedName("IsDeleted"       ) var IsDeleted       : Boolean? = null,
    @SerializedName("CreatedBy"       ) var CreatedBy       : String?  = null,
    @SerializedName("CreatedOn"       ) var CreatedOn       : String?  = null,
    @SerializedName("ModifiedOn"      ) var ModifiedOn      : String?  = null,
    @SerializedName("ModifiedBy"      ) var ModifiedBy      : String?  = null,
    @SerializedName("SenderType"      ) var SenderType      : String?  = null,
    @SerializedName("SenderID"        ) var SenderID        : Int?     = null,
    @SerializedName("SenderName"      ) var SenderName      : String?  = null,
    @SerializedName("SenderNumber"    ) var SenderNumber    : String?  = null,
    @SerializedName("ReceiverType"    ) var ReceiverType    : String?  = null,
    @SerializedName("ReceiverID"      ) var ReceiverID      : Int?     = null,
    @SerializedName("ReceiverName"    ) var ReceiverName    : String?  = null,
    @SerializedName("ReceiverNumber"  ) var ReceiverNumber  : String?  = null,
    @SerializedName("MessageType"     ) var MessageType     : String?  = null,
    @SerializedName("MessageText"     ) var MessageText     : String?  = null,
    @SerializedName("ResponseID"      ) var ResponseID      : String?  = null,
    @SerializedName("ResponseMessage" ) var ResponseMessage : String?  = null,
    @SerializedName("ErrorCode"       ) var ErrorCode       : String?  = null,
    @SerializedName("ErrorMessage"    ) var ErrorMessage    : String?  = null,
    @SerializedName("IsRead"          ) var IsRead          : Boolean? = null,
    @SerializedName("emailid"         ) var emailid         : String?  = null,
    @SerializedName("userid"          ) var userid          : String?  = null,
    @SerializedName("Subscriberid"    ) var Subscriberid    : String?  = null,
    @SerializedName("FirstName"       ) var FirstName       : String?  = null,
    @SerializedName("LastName"        ) var LastName        : String?  = null,
    @SerializedName("UserEmailid"     ) var UserEmailid     : String?  = null,
    @SerializedName("Id"              ) var Id              : Int?     = null,
    @SerializedName("Name"            ) var Name            : String?  = null

)