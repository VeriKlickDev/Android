package com.domain.BaseModels

import com.google.gson.annotations.SerializedName


data class ResponseResumeModel(

    @SerializedName("success") var success: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: DataU? = DataU(),
    @SerializedName("errorMessage") var errorMessage: String? = null

)


data class DataU(

    @SerializedName("fileName") var fileName: String? = null,
    @SerializedName("isReturn") var isReturn: Boolean? = null

)