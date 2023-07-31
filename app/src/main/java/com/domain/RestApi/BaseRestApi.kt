package com.domain.RestApi

import androidx.annotation.CheckResult
import com.domain.BaseModels.*
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface BaseRestApi {

    @GET("/api/ScheduleVideo/GetVideoAccessCodeDetails/{VideoAccessCode}")
    suspend fun requestVideoSession(@Query("VideoAccessCode")videoAccessCode:String) : Response<ResponseInterViewDetailsBean>

    @GET("/api/ScheduleVideo/GetVideoTokenDetails/{VideoAccessCode}")
    suspend fun getTwilioVideoTokenHost(@Query("VideoAccessCode")videoAccessCode:String) : Response<TokenResponseBean>

    @GET("/api/ScheduleVideo/GetVideoTokenDetails/{VideoAccessCode}")
    suspend fun getTwilioVideoTokenCandidate(@Query("VideoAccessCode")videoAccessCode:String) : Response<TokenResponseBean>

    @GET
    suspend fun getQuestionnaireListNew(@Url url :String ) :Response<ResponseQuestionnaire>

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
    suspend fun getEmailPExistsDetails(@Body obj: IsEmailPhoneExistsModel):Response<Boolean>

    @POST("/api/ScheduleVideo/GetInterviewerUserByPhone")
    suspend fun getPhoneExistsDetails( @Body obj: IsEmailPhoneExistsModel):Response<Boolean>

    @POST("/api/ScheduleVideo/SaveParticipantStatus")
    suspend fun getRemoveStatus( @Body obj: BodyRemoveParticipant):Response<ResponseRemoveParticipant>

    @GET
    suspend fun getResumeFileName(@Header("Authorization") authToken: String,@Url url: String):Response<ResponseCandidateDataForIOS>

    @GET
    suspend fun getResumeFileNameWithoutAuth(@Url url: String):Response<ResponseCandidateDataForIOS>


    @POST("/api/ScheduleVideo/UpdateInterviewRecordingStatus")
    suspend fun getRecordingStatusUpdate( @Body obj: BodyUpdateRecordingStatus):Response<BodyUpdateRecordingStatus>

    @POST("/api/ScheduleVideo/SaveParticipantStatus")
    suspend fun leftUserFromMeeting( @Body obj: BodyLeftUserFromMeeting):Response<BodyLeftUserFromMeeting>

    @POST("/api/ScheduleVideo/UpdateInterviewUsersDetail")
    @CheckResult
    suspend fun sendInvitation( @Body obj: AddParticipantModel): Response<ResponseSendInvitation>

    @GET
    suspend fun getFeedBackDetails(@Url url: String):Response<ResponseFeedBack>

    @POST("/api/ScheduleVideo/CreateCandidateAssessementDetails")
    suspend fun sendFeedBack( @Body obj: BodyFeedBack):Response<ResponseBodyFeedBack>

    @POST("/api/SaveRoomStatus")
    suspend fun closeMeeting( @Body obj: BodyMeetingClose):Response<ResponseBodyFeedBack>

    @POST("/api/InterviewerLoginWithOtp")
    suspend fun sendOtpToEmailVerification(@Query("InterviewerEmail", encoded = false) email: String):Response<ResponseOtpVerification>

    @POST("/api/InterviewerLoginWithOtp/{Type}")
    suspend fun sendOtpToEmailVerificationWithType(@Query("InterviewerEmail", encoded = false) email: String,@Query("Type", encoded = false) type: String):Response<ResponseOtpVerification>


    @POST("/api/InterviewerLoginOtpStatus")
    suspend fun getOtpVerificationStatus( @Body obj: BodyOtpVerificationStatus):Response<ResponseOtpVerificationStatus>

    @GET("/api/ScheduleVideo/GetInterviewTotalInterviewerCount/{VideoAccessCode}")
    suspend fun getTotalCountOfInterViewerInMeeting( @Query("VideoAccessCode") videoAccessCode: String):Response<ResponseTotalInterviewerCount>

    @GET("/api/ScheduleVideo/GetOwnScreenShareStatus/{VideoAccessCode}")
    suspend fun getScreenSharingStatus( @Query("VideoAccessCode") videoAccessCode: String):Response<ResponseScreenSharingStatus>

    @POST("/api/ScheduleVideo/UpdateInterviewersScreenShareStatus")
    suspend fun setScreenSharingStatus(@Body obj : BodyUpdateScreenShareStatus) :Response<Gson>

    @POST("/api/ScheduleVideo/GetInterviewByRecruiterForMobile")
    suspend fun getScheduledMeetingsListwithOtp(@Body ob: BodyScheduledMeetingBean):Response<ResponseScheduledMeetingBean>

    @POST("/api/ScheduleVideo/Updatestatusofvideoscheduled")
    suspend fun setCandidateJoinMeetingStatus(@Body ob: BodyCandidateJoinedMeetingStatus):Response<ResponseCandidateJoinedMeetingStatus>

    @POST("/api/ScheduleVideo/MailInterviewersToJoin")
    suspend fun sendMailToJoinMeeting(@Body ob: BodySendMail):Response<ResponseSendMail>

    @POST("/api/ScheduleVideo/InterviewCancellationDetails")
    suspend fun cancelMeeting(@Header("Authorization")authToken:String,@Body ob: BodyCancelMeeting):Response<BodyCancelMeeting>

    @POST
    suspend fun getCandidateList(@Header("Authorization")authToken:String?,@Url url:String,@Body ob:BodyCandidateList):Response<ResponseCandidateList>

    @GET("/api/Common/GetCountry")
    suspend fun getCountryNamesList():Response<List<ResponseCountryName>>

   /* @GET("/api/Common/GetNationalityList")
    suspend fun getCitizenShipList():Response<List<ResponseNationality>>
*/
    @GET("/api/Common/GetCountryCode")
    suspend fun getCountryCodeList():Response<List<ResponseCountryCode>>

    @GET
    suspend fun getCityByIDList(@Url url: String):Response<List<ResponseCity>>

    @GET
    suspend fun getStateByIDList(@Url url:String):Response<List<ResponseState>>

    @POST("/api/Messaging/SendMessage")
    suspend fun sendSMSToCandidate(@Body ob:BodySMSCandidate,@Header("Authorization")token: String):Response<ResponseSMSCadidate>

    @POST("/api/JobSeeker")
    suspend fun createCandidate(@Header("Authorization")authToken: String,@Body ob:BodyCreateCandidate):Response<BodyCreateCandidate>

    @POST
    suspend fun createCandidateWithoutAuth(@Body ob:BodyCreateCandidate,@Url url:String):Response<BodyCreateCandidate>

    @GET("/api/Questionier/GetRecruiterTemplate/{SubscriberId}")
    suspend fun getQuestionnaireTemplateList(@Query("SubscriberId") recruiterId:String):Response<ResponseQuestionnaireTemplate>

    @GET("/api/Questionier/GetQuestionier/{templateId}")
    suspend fun getQuestionnaireList(@Query("templateId") templateId:String):Response<ResponseQuestionnaire>

    @POST("/api/Questionier/SaveQuestionierAnswers")
    suspend fun postQuestionnaire(@Body ob:BodyQuestionnaire):Response<BodyQuestionnaire>

    @GET
    suspend fun getRecruiterDetailsWithID(@Url url:String):Response<ResponseRecruiterDetails>

    @Multipart
    @POST("api/Audio/AudioFileReceive")
    suspend fun updateUserAudio(
        @Part file: MultipartBody.Part,
        @Part AudioFileName: MultipartBody.Part,
        @Part RecruiterId: MultipartBody.Part,
        @Part Profile_Url: MultipartBody.Part,
        @Part Token_Id: MultipartBody.Part,
    ):Response<ResponseAudioRecord>

    @POST("/api/JobSeeker/GetSearchCandidatePhone")
    suspend fun getExitingCandidateContact(@Body obj:BodyExistingCandidate):Response<BodyExistingCandidate>

    @GET
    suspend fun getQuestionnaireForCandidate(@Url url:String):Response<ResponseShowQuestionnaire>

    @POST("/api/ScheduleVideo/GetInterviewScheduleInfo")
    suspend fun getTimeZone(@Body timeZoneBody:BodyTimeZone):Response<String>

    @POST("/api/Messaging/GetMessageListByCandidateId")
    suspend fun getSMSchatHistory(@Header("Authorization")authToken: String,@Body obj:BodySMSchatHistory):Response<ResponseSMSchatHistory>

 /*   @Multipart
    @POST("/api/v1/Account/UpdateImageWithoutAuth")
    suspend fun updateFreshUserImageWithoutAuth(
        @Part imageName: MultipartBody.Part,
        @Part imagebyte: MultipartBody.Part,
        @Part folderName: MultipartBody.Part,
    ):Response<ResponseCandidateImageModel>
*/

}

//@GET("/api/ScheduleVideo/GetInterviewUserDetails/{VideoAccessCode}")