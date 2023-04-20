package com.domain.RestApi

import com.app.wavespodcast.api.model.response.ApiResponse
import com.domain.BaseModels.*
import okhttp3.MultipartBody

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface LoginRestApi {

    @POST("/api/v1/Authentication/Token")
    suspend fun login(@Body ob: BodyLoginBean) : Response<LoginResponseBean>

    // /api/v1/Account/ForgotPassword

    @POST("/api/v1/Account/ForgotPassword")
    suspend fun sendMailForforgotPassword(@Body ob: BodyForgotPasswordBean) : Response<ResponseForgotPassword>

    @POST("/api/v1/Account/GetVKUserProfileResumeWithoutAuth")
    suspend fun getResume( @Body fileName: BodyGetResume):Response<ResponseResumeModel>


    @POST("/api/v1/Account/UpdateImage")
    suspend fun updateUserImage( @Body ob: BodyCandidateImageModel):Response<ResponseCandidateImageModel>

    @Multipart
    @POST("v1/Account/GetVKUserProfileImage")
    suspend fun updateUserResume( @Body ob: BodyCandidateResume,@Part img:MultipartBody.Part):Response<ResponseCandidateResume>

}