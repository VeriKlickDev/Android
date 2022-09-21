package com.domain.BaseModels

import com.google.gson.annotations.SerializedName

data class BodyOtpVerification(val InterviewerEmail:String)



data class ResponseOtpVerification (
    @SerializedName("Message"    ) var Message    : String?  = null,
    @SerializedName("StatusCode" ) var StatusCode : String?  = null,
    @SerializedName("Success"    ) var Success    : Boolean? = null
)



data class BodyOtpVerificationStatus (
    @SerializedName("InterViewerId"    ) var InterViewerId    : String? = null,
    @SerializedName("InterViewerEmail" ) var InterViewerEmail : String? = null,
    @SerializedName("OTP"              ) var OTP              : String? = null,
    @SerializedName("MaxAtttempt"      ) var MaxAtttempt      : Int?    = null,
    @SerializedName("LockedUntil"      ) var LockedUntil      : String? = null
)

data class ResponseOtpVerificationStatus (
    @SerializedName("Status"           ) var Status           : String? = null,
    @SerializedName("RemainingAttempt" ) var RemainingAttempt : Int?    = null,
    @SerializedName("LockedUntil"      ) var LockedUntil      : String? = null,
    @SerializedName("recruiterid"      ) var recruiterid      : String? = null,
    @SerializedName("subscriber_id"    ) var subscriberId     : String? = null
)




