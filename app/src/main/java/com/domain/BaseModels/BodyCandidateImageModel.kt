package com.domain.BaseModels

import com.google.gson.annotations.SerializedName

data class BodyCandidateImageModel (
    @SerializedName("imagebyte"  ) var imagebyte  : String? = null,
    @SerializedName("imageName"  ) var imageName  : String? = null,
    @SerializedName("folderName" ) var folderName : String? = null
)

data class ResponseCandidateImageModel(

    @SerializedName("success"      ) var success      : Boolean? = null,
    @SerializedName("message"      ) var message      : String?  = null,
    @SerializedName("data"         ) var data         : String?  = null,
    @SerializedName("errorMessage" ) var errorMessage : String?  = null

)

data class BodyCandidateResume (

    @SerializedName("fileName" ) var fileName : String?  = null,
    @SerializedName("isReturn" ) var isReturn : Boolean? = null

)

data class ResponseCandidateResume (

    @SerializedName("success"      ) var success      : Boolean? = null,
    @SerializedName("message"      ) var message      : String?  = null,
    @SerializedName("data"         ) var data         : String?  = null,
    @SerializedName("errorMessage" ) var errorMessage : String?  = null

)