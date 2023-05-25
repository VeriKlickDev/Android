package com.domain.BaseModels

import com.google.gson.annotations.SerializedName
import java.io.File

data class BodyCandidateImageModel (
    @SerializedName("imagebyte"  ) var imagebyte  : String? = null,
    @SerializedName("imageName"  ) var imageName  : String? = null,
    @SerializedName("folderName" ) var folderName : String? = null
)

data class CandidateDeepLinkDataModel (
     var token_Id: String?= null,
     var subscriberId: String?=null,
)


data class BodyCandidateImageAudioModel (
    @SerializedName("File"  ) var file  : File? = null,
    @SerializedName("AudioFileName"  ) var audioFileName  : String? = null,
    @SerializedName("RecruiterId" ) var recruiterId : String? = null,
    @SerializedName("Profile_Url" ) var profile_Url : String? = null,
    @SerializedName("Token_Id" ) var token_Id : String? = null,
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

data class ResponseAudioRecord (

    @SerializedName("RecruiterId"   ) var RecruiterId   : String?  = null,
    @SerializedName("AudioFileName" ) var AudioFileName : String?  = null,
    @SerializedName("File"          ) var File          : File1?    = File1(),
    @SerializedName("AudioFilePath" ) var AudioFilePath : String?  = null,
    @SerializedName("Message"       ) var Message       : String?  = null,
    @SerializedName("StatusCode"    ) var StatusCode    : String?  = null,
    @SerializedName("Success"       ) var Success       : Boolean? = null,
    @SerializedName("TimeTaken"     ) var TimeTaken     : String?  = null

)

data class File1 (

    @SerializedName("FileName"      ) var FileName      : String? = null,
    @SerializedName("ContentType"   ) var ContentType   : String? = null,
    @SerializedName("ContentLength" ) var ContentLength : Int?    = null

)

data class BodyCandidateImageUpdate (

    @SerializedName("CandidateID" ) var CandidateID : Int?     = null,
    @SerializedName("ImageName"   ) var ImageName   : String?  = null,
    @SerializedName("ResumeFile"  ) var ResumeFile  : String?  = null,
    @SerializedName("UpdatedBy"   ) var UpdatedBy   : String?  = null,
    @SerializedName("Message"     ) var Message     : String?  = null,
    @SerializedName("StatusCode"  ) var StatusCode  : String?  = null,
    @SerializedName("Success"     ) var Success     : Boolean? = null,
    @SerializedName("TimeTaken"   ) var TimeTaken   : String?  = null

)
