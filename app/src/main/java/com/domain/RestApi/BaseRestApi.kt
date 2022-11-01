package com.domain.RestApi

import com.domain.BaseModels.*
import com.google.gson.Gson
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

    @GET
    suspend fun getFeedBackDetails(@Url url: String):Response<ResponseFeedBack>

    @POST("/api/ScheduleVideo/CreateCandidateAssessementDetails")
    suspend fun sendFeedBack( @Body obj: BodyFeedBack):Response<ResponseBodyFeedBack>

    @POST("/api/SaveRoomStatus")
    suspend fun closeMeeting( @Body obj: BodyMeetingClose):Response<ResponseBodyFeedBack>

    @POST("/api/InterviewerLoginWithOtp")
    suspend fun sendOtpToEmailVerification(@Query("InterviewerEmail", encoded = false) email: String):Response<ResponseOtpVerification>

    @POST("/api/InterviewerLoginOtpStatus")
    suspend fun getOtpVerificationStatus( @Body obj: BodyOtpVerificationStatus):Response<ResponseOtpVerificationStatus>

    @GET("/api/ScheduleVideo/GetInterviewTotalInterviewerCount/{VideoAccessCode}")
    suspend fun getTotalCountOfInterViewerInMeeting( @Query("VideoAccessCode") videoAccessCode: String):Response<ResponseTotalInterviewerCount>

    @GET("/api/ScheduleVideo/GetOwnScreenShareStatus/{VideoAccessCode}")
    suspend fun getScreenSharingStatus( @Query("VideoAccessCode") videoAccessCode: String):Response<ResponseScreenSharingStatus>

    @POST("/api/ScheduleVideo/UpdateInterviewersScreenShareStatus")
    suspend fun setScreenSharingStatus(@Body obj : BodyUpdateScreenShareStatus) :Response<Gson>
    //GetInterviewByRecruiterForMobile

    @POST("/api/ScheduleVideo/GetInterviewByRecruiterForMobile")
    suspend fun getScheduledMeetingsListwithOtp(@Body ob: BodyScheduledMeetingBean):Response<ResponseScheduledMeetingBean>

    @POST("/api/ScheduleVideo/Updatestatusofvideoscheduled")
    suspend fun setCandidateJoinMeetingStatus(@Body ob: BodyCandidateJoinedMeetingStatus):Response<ResponseCandidateJoinedMeetingStatus>

}


//@GET("/api/ScheduleVideo/GetInterviewUserDetails/{VideoAccessCode}")