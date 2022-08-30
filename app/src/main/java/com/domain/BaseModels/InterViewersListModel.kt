package com.domain.BaseModels

import com.google.gson.annotations.SerializedName


data class InterViewersListModel(

    @SerializedName("Name") var Name: String? = null,
    @SerializedName("IsPresenter") var IsPresenter: Boolean? = null,
    @SerializedName("EmailAcceptanceStatus") var EmailAcceptanceStatus: String? = null,
    @SerializedName("VideoCallAccessCode") var VideoCallAccessCode: String? = null

)