package com.domain.RestApi

import com.app.wavespodcast.api.model.response.ApiResponse
import com.domain.BaseModels.*

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LoginRestApi {

    @POST("/api/v1/Authentication/Token")
    suspend fun login(@Body ob: BodyLoginBean) : Response<LoginResponseBean>

    // /api/v1/Account/ForgotPassword

    @POST("/api/v1/Account/ForgotPassword")
    suspend fun sendMailForforgotPassword(@Body ob: BodyForgotPasswordBean) : Response<ResponseForgotPassword>

    @POST("/api/v1/Account/GetVKUserProfileResumeWithoutAuth")
    suspend fun getResume( @Body fileName: BodyGetResume):Response<ResponseResumeModel>

}