package com.data.repositoryImpl


import com.domain.BaseModels.*
import com.domain.RestApi.BaseRestApi
import com.domain.RestApi.LoginRestApi
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    val baseRestApi: BaseRestApi,
    val loginRestApi: LoginRestApi
) : BaseRestRepository {

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
        return baseRestApi.getScheduledMeetingsList(token, ob)
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

    override suspend fun getResumeFileName(token: String,url: String): Response<ResponseCandidateDataForIOS> {
        return baseRestApi.getResumeFileName(authToken =token ,url)
    }

    override suspend fun getResumeFileNameWithoutAuth(url: String): Response<ResponseCandidateDataForIOS> {
        return baseRestApi.getResumeFileNameWithoutAuth(url)
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
        return baseRestApi.getFeedBackDetails("/api/ScheduleVideo/GetCandidateAssessementDetailsByIdVideoAccessCode/" + accessCode)
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
        return baseRestApi.cancelMeeting(authToken, ob)
    }

    override suspend fun getCandidateList(
        authToken: String?,
        url: String,
        ob: BodyCandidateList
    ): Response<ResponseCandidateList> {
        return baseRestApi.getCandidateList(authToken = authToken, url, ob)
    }

    override suspend fun sendSMSToCandidate(ob: BodySMSCandidate,token: String): Response<ResponseSMSCadidate> {
        return baseRestApi.sendSMSToCandidate(ob,token)
    }

    override suspend fun createCandidate(token: String,ob: BodyCreateCandidate): Response<BodyCreateCandidate> {
        return baseRestApi.createCandidate(token,ob)
    }

    override suspend fun createCandidateWithoutAuth(
        ob: BodyCreateCandidate,
        url: String
    ): Response<BodyCreateCandidate> {
        return baseRestApi.createCandidateWithoutAuth(ob,url)
    }

    override suspend fun getQuestionnaireTemplateList(recruiterId: String): Response<ResponseQuestionnaireTemplate> {
        return baseRestApi.getQuestionnaireTemplateList(recruiterId)
    }

    override suspend fun getQuestionnaireList(templateId: String): Response<ResponseQuestionnaire> {
        return baseRestApi.getQuestionnaireList(templateId)
    }

    override suspend fun postQuestionnaire(ob: BodyQuestionnaire): Response<BodyQuestionnaire> {
        return baseRestApi.postQuestionnaire(ob)
    }

    override suspend fun getRecruiterDetailsWithID(url: String): Response<ResponseRecruiterDetails> {
        return baseRestApi.getRecruiterDetailsWithID(url)
    }

    override suspend fun getCountryNamesList(): Response<List<ResponseCountryName>> {
        return baseRestApi.getCountryNamesList()
    }

    /*override suspend fun getCitizenShipList(): Response<List<ResponseNationality>> {
        return baseRestApi.getCitizenShipList()
    }*/

    override suspend fun getCountryCodeList(): Response<List<ResponseCountryCode>> {
        return baseRestApi.getCountryCodeList()
    }

    override suspend fun getCityByIDList(url:String): Response<List<ResponseCity>> {
        return baseRestApi.getCityByIDList(url)
    }

    override suspend fun getStateByIDList(url: String): Response<List<ResponseState>> {
        return baseRestApi.getStateByIDList(url)
    }

    /* override suspend fun setScreenSharingStatus(obj: BodyUpdateScreenShareStatus): Response<ResponseScreenSharingStatus> {
         return baseRestApi.setScreenSharingStatus(obj)
     }*/

    override suspend fun getResume(fileName: BodyGetResume): Response<ResponseResumeModel> {
        return loginRestApi.getResume(fileName)
    }

    override suspend fun updateFreshUserImage(token:String, ob: BodyCandidateImageModel): Response<ResponseCandidateImageModel> {
        return loginRestApi.updateFreshUserImage(token,ob)
    }

    override suspend fun updateFreshUserImageWithoutAuth(ob: BodyCandidateImageModel): Response<ResponseCandidateImageModel> {
        return loginRestApi.updateFreshUserImageWithoutAuth(ob)
    }

    override suspend fun updateUserAudio(
        file: MultipartBody.Part,
        AudioFileName: MultipartBody.Part,
        RecruiterId: MultipartBody.Part,
        Profile_Url: MultipartBody.Part,
        Token_Id: MultipartBody.Part
    ): Response<ResponseAudioRecord> {
        return baseRestApi.updateUserAudio(file,AudioFileName,RecruiterId,Profile_Url,Token_Id)
    }

    override suspend fun getExitingCandidateContact(obj: BodyExistingCandidate): Response<BodyExistingCandidate> {
        return baseRestApi.getExitingCandidateContact(obj)
    }

    override suspend fun getQuestionnaireForCandidate(url: String): Response<ResponseShowQuestionnaire> {
        return baseRestApi.getQuestionnaireForCandidate(url)
    }

    override suspend fun updateUserResume(token: String,ob: BodyCandidateResume,filePart:MultipartBody.Part): Response<ResponseCandidateResume> {
        return loginRestApi.updateUserResume(token,ob,filePart)
    }

    override suspend fun updateCandidateImage(
        auth: String,
        ob: BodyCandidateImageUpdate,
        imgPart: MultipartBody.Part
    ): Response<BodyCandidateImageUpdate> {
        return loginRestApi.updateCandidateImage(auth,ob,imgPart)
    }

    override suspend fun login(ob: BodyLoginBean): Response<LoginResponseBean> {
        return loginRestApi.login(ob)
    }

    override suspend fun sendMailForforgotPassword(ob: BodyForgotPasswordBean): Response<ResponseForgotPassword> {
        return loginRestApi.sendMailForforgotPassword(ob)
    }


}