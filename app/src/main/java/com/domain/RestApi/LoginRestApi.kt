package com.domain.RestApi

import com.domain.BaseModels.*
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

interface LoginRestApi {
//created by userid
    //last updated by updated by
    //created on data.createdDate
    //internal id id
    //createby userid
    //mmddyyyy

    @POST("/api/v1/Authentication/Token")
    suspend fun login(@Body ob: BodyLoginBean) : Response<LoginResponseBean>

    // /api/v1/Account/ForgotPassword

    @POST("/api/v1/Account/ForgotPassword")
    suspend fun sendMailForforgotPassword(@Body ob: BodyForgotPasswordBean) : Response<ResponseForgotPassword>

    @POST("/api/v1/Account/GetVKUserProfileResumeWithoutAuth")
    suspend fun getResume( @Body fileName: BodyGetResume):Response<ResponseResumeModel>

    @Multipart
    @POST("/api/v1/Account/UpdateImage")
    suspend fun updateFreshUserImage(@Header ("Authorization")auth:String, @Body ob: BodyCandidateImageModel):Response<ResponseCandidateImageModel>

    //@Multipart
    @POST("/api/v1/Account/UpdateImageWithoutAuth")
    suspend fun updateFreshUserImageWithoutAuth( @Body ob : BodyCandidateImageModel):Response<ResponseCandidateImageModel>
//@Header ("Authorization")auth:String,

  /*  @Multipart
    @POST("/api/v1/Account/UpdateImageWithoutAuth")
    suspend fun updateFreshUserImageWithoutAuth(
        @Part imageName: MultipartBody.Part,
        @Part imagebyte: MultipartBody.Part,
        @Part folderName: MultipartBody.Part,
    ):Response<ResponseCandidateImageModel>
*/


    @Multipart
    @POST("v1/Account/GetVKUserProfileImage")
    suspend fun updateUserResume(@Header("Authorization")auth:String, @Body ob: BodyCandidateResume,@Part resumePart:MultipartBody.Part):Response<ResponseCandidateResume>

    @Multipart
    @POST("/api/Candidate/UpdateCandidateImage")
    suspend fun updateCandidateImage(@Header("Authorization")auth:String, @Body ob: BodyCandidateImageUpdate,@Part imgPart:MultipartBody.Part):Response<BodyCandidateImageUpdate>
    @Multipart
    @POST("/api/v1/Account/UpdateResumeWithoutAuth")
    suspend fun updateResumeWithoutAuth(
        @Part filePath: MultipartBody.Part,
        @Part NewFileName: MultipartBody.Part,
        @Part subscriberid: MultipartBody.Part,
        ):Response<ResponseUpdateResumeWithoutAuth>

}