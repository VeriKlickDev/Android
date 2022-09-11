package com.domain.RestApi

import com.domain.BaseModels.*
import retrofit2.Response
import retrofit2.http.*

interface BaseRestApi {

    @GET("/api/ScheduleVideo/GetVideoAccessCodeDetails/{VideoAccessCode}")
    suspend fun requestVideoSession(@Query("VideoAccessCode")videoAccessCode:String) : Response<ResponseInterViewDetailsBean>

    @GET("/api/ScheduleVideo/GetVideoTokenDetails/{VideoAccessCode}")
    suspend fun getTwilioVideoTokenHost(@Query("VideoAccessCode")videoAccessCode:String) : Response<TokenResponseBean>

    @GET("/api/ScheduleVideo/GetVideoTokenDetails/{VideoAccessCode}")
    suspend fun getTwilioVideoTokenCandidate(@Query("VideoAccessCode")videoAccessCode:String) : Response<TokenResponseBean>

    @POST("/api/ScheduleVideo/GetInterviewByRecruiter")
    suspend fun getScheduledMeetingsList(@Header("Authorization")token:String, @Body ob: BodyScheduledMeetingBean):Response<ResponseScheduledMeetingBean>

    @GET("/api/ScheduleVideo/GetSaveInterviewDetails/{Id}")
    suspend fun getInterviewAccessCodeById( @Query("Id") id: Int):Response<ResponseInterviewerAccessCodeByID>

    @GET("/api/ScheduleVideo/GetChatTokenDetails/{identity}")
    suspend fun getChatToken( @Query("identity") identity: String):Response<ResponseChatToken>

    @POST("/api/ScheduleVideo/UpdateInterviewAllowMuteStatus")
    suspend fun setMuteUnmuteStatus( @Body bodyobj: BodyMuteUmnuteBean):Response<ResponseMuteUmnute>

    @GET("/api/ScheduleVideo/GetInterviewUserDetails/{VideoAccessCode}")
    suspend fun getMuteStatus( @Query("VideoAccessCode") accessCode: String):Response<ResponseMuteUmnute>

    @POST("/api/ScheduleVideo/GetInterviewerUserByEmail")
    suspend fun getEmailPhoneExistsDetails( @Body obj: IsEmailPhoneExistsModel):Response<Boolean>

    @POST("/api/ScheduleVideo/SaveParticipantStatus")
    suspend fun getRemoveStatus( @Body obj: BodyRemoveParticipant):Response<ResponseRemoveParticipant>

    @GET
    suspend fun getResumeFileName(@Url url: String):Response<ResponseCandidateDataForIOS>

    @POST("/api/ScheduleVideo/UpdateInterviewRecordingStatus")
    suspend fun getRecordingStatusUpdate( @Body obj: BodyUpdateRecordingStatus):Response<BodyUpdateRecordingStatus>

    @POST("/api/ScheduleVideo/SaveParticipantStatus")
    suspend fun leftUserFromMeeting( @Body obj: BodyLeftUserFromMeeting):Response<BodyLeftUserFromMeeting>

    @POST("/api/ScheduleVideo/UpdateInterviewUsersDetail")
    suspend fun sendInvitation( @Body obj: AddParticipantModel):Response<ResponseSendInvitation>

}