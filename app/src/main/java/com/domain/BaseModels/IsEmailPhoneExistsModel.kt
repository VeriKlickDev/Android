package com.domain.BaseModels

import com.google.gson.annotations.SerializedName


data class IsEmailPhoneExistsModel(
    @SerializedName("InterviewId") var InterviewId: Int? = null,
    @SerializedName("Email") var Email: String? = null,
    @SerializedName("Phone") var Phone: String? = null
)






