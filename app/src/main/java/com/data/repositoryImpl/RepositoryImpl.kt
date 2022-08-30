package com.data.repositoryImpl




import com.domain.BaseModels.*
import com.domain.RestApi.BaseRestApi
import com.domain.RestApi.LoginRestApi
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

    override suspend fun getEmailPhoneExistsDetails(obj: IsEmailPhoneExistsModel): Response<Boolean> {
        return baseRestApi.getEmailPhoneExistsDetails(obj)
    }

    override suspend fun login(ob:BodyLoginBean): Response<LoginResponseBean> {
        return loginRestApi.login(ob)
    }

    override suspend fun sendMailForforgotPassword(ob: BodyForgotPasswordBean): Response<ResponseForgotPassword> {
        return loginRestApi.sendMailForforgotPassword(ob)
    }

}