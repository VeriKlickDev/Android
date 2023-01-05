package com.data.repositoryImpl




import com.domain.BaseModels.*
import com.domain.RestApi.BaseRestApi
import com.domain.RestApi.LoginRestApi
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

class RepositoryImpl  @Inject constructor(val baseRestApi: BaseRestApi,val loginRestApi: LoginRestApi): BaseRestRepository {

    override suspend fun requestVideoSession(videoAccessCode: String): Response<ResponseInterViewDetailsBean> {
    return baseRestApi.requestVideoSession(videoAccessCode)
    }


    override suspend fun getTwilioVideoTokenHost(videoAccessCode: String): Response<TokenResponseBean> {
        return baseRestApi.getTwilioVideoTokenHost(videoAccessCode)
    }

    override suspend fun getTwilioVideoTokenCandidate(videoAccessCode: String): Response<TokenResponseBean> {
        return baseRestApi.getTwilioVideoTokenCandidate(videoAccessCode)
    }

    override suspend fun getScheduledMeetingsList(
        token: String,
        ob: BodyScheduledMeetingBean
    ): Response<ResponseScheduledMeetingBean> {
        return baseRestApi.getScheduledMeetingsList(token,ob)
    }

    override suspend fun getInterviewAccessCodeById(id: Int): Response<ResponseInterviewerAccessCodeByID> {
        return baseRestApi.getInterviewAccessCodeById(id)
    }

    override suspend fun getChatToken(identity: String): Response<ResponseChatToken> {
        return baseRestApi.getChatToken(identity)
    }

    override suspend fun setMuteUnmuteStatus(bodyobj: BodyMuteUmnuteBean): Response<ResponseMuteUmnute> {
        return baseRestApi.setMuteUnmuteStatus(bodyobj)
    }

    override suspend fun getMuteStatus(accessCode: String): Response<ResponseMuteUmnute> {
        return baseRestApi.getMuteStatus(accessCode)
    }

    override suspend fun getEmailPExistsDetails(obj: IsEmailPhoneExistsModel): Response<Boolean> {
        return baseRestApi.getEmailPExistsDetails(obj)
    }

    override suspend fun getPhoneExistsDetails(obj: IsEmailPhoneExistsModel): Response<Boolean> {
        return baseRestApi.getPhoneExistsDetails(obj)
    }

    override suspend fun getRemoveStatus(obj: BodyRemoveParticipant): Response<ResponseRemoveParticipant> {
        return baseRestApi.getRemoveStatus(obj)
    }

    override suspend fun getResumeFileName(id: String): Response<ResponseCandidateDataForIOS> {
        return baseRestApi.getResumeFileName("/api/CandidateDataForIOS/"+id)
    }

    override suspend fun getRecordingStatusUpdate(obj: BodyUpdateRecordingStatus): Response<BodyUpdateRecordingStatus> {
        return baseRestApi.getRecordingStatusUpdate(obj)
    }

    override suspend fun leftUserFromMeeting(obj: BodyLeftUserFromMeeting): Response<BodyLeftUserFromMeeting> {
        return baseRestApi.leftUserFromMeeting(obj)
    }

    override suspend fun sendInvitation(obj: AddParticipantModel): Response<ResponseSendInvitation> {
        return baseRestApi.sendInvitation(obj)
    }

    override suspend fun getFeedBackDetails(accessCode: String): Response<ResponseFeedBack> {
        return baseRestApi.getFeedBackDetails("/api/ScheduleVideo/GetCandidateAssessementDetailsByIdVideoAccessCode/"+accessCode)
    }

    override suspend fun sendFeedBack(obj: BodyFeedBack): Response<ResponseBodyFeedBack> {
        return baseRestApi.sendFeedBack(obj)
    }

    override suspend fun closeMeeting(obj: BodyMeetingClose): Response<ResponseBodyFeedBack> {
        return baseRestApi.closeMeeting(obj)
    }

    override suspend fun sendOtpToEmailVerification(email: String): Response<ResponseOtpVerification> {
        return baseRestApi.sendOtpToEmailVerification(email)
    }

    override suspend fun getOtpVerificationStatus(obj: BodyOtpVerificationStatus): Response<ResponseOtpVerificationStatus> {
        return baseRestApi.getOtpVerificationStatus(obj)
    }

    override suspend fun getTotalCountOfInterViewerInMeeting(videoAccessCode: String): Response<ResponseTotalInterviewerCount> {
        return baseRestApi.getTotalCountOfInterViewerInMeeting(videoAccessCode)
    }

    override suspend fun getScreenSharingStatus(videoAccessCode: String): Response<ResponseScreenSharingStatus> {
        return baseRestApi.getScreenSharingStatus(videoAccessCode)
    }

    override suspend fun setScreenSharingStatus(obj: BodyUpdateScreenShareStatus): Response<Gson> {
        return baseRestApi.setScreenSharingStatus(obj)
    }


    override suspend fun getScheduledMeetingsListwithOtp(ob: BodyScheduledMeetingBean): Response<ResponseScheduledMeetingBean> {
        return baseRestApi.getScheduledMeetingsListwithOtp(ob)
    }

    override suspend fun setCandidateJoinMeetingStatus(ob: BodyCandidateJoinedMeetingStatus): Response<ResponseCandidateJoinedMeetingStatus> {
        return baseRestApi.setCandidateJoinMeetingStatus(ob)
    }

    override suspend fun sendMailToJoinMeeting(ob: BodySendMail): Response<ResponseSendMail> {
        return baseRestApi.sendMailToJoinMeeting(ob)
    }

    override suspend fun cancelMeeting(
        authToken: String,
        ob: BodyCancelMeeting
    ): Response<BodyCancelMeeting> {
        return baseRestApi.cancelMeeting(authToken,ob)
    }

    /* override suspend fun setScreenSharingStatus(obj: BodyUpdateScreenShareStatus): Response<ResponseScreenSharingStatus> {
         return baseRestApi.setScreenSharingStatus(obj)
     }*/

    override suspend fun getResume(fileName: BodyGetResume): Response<ResponseResumeModel> {
       return loginRestApi.getResume(fileName)
    }

    override suspend fun login(ob:BodyLoginBean): Response<LoginResponseBean> {
        return loginRestApi.login(ob)
    }

    override suspend fun sendMailForforgotPassword(ob: BodyForgotPasswordBean): Response<ResponseForgotPassword> {
        return loginRestApi.sendMailForforgotPassword(ob)
    }

}