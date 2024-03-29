package com.ui.activities.upcomingMeeting

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.data.dataHolders.CurrentMeetingDataSaver
import com.data.dataHolders.DataStoreHelper
import com.data.dismissProgressDialog
import com.data.dismissProgressDialogwithoutContext
import com.data.exceptionHandler
import com.data.showCustomSnackbarOnTop
import com.domain.BaseModels.*
import com.domain.RestApi.BaseRestApi
import com.domain.RestApi.LoginRestApi
import com.veriKlick.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class UpComingMeetingViewModel @Inject constructor(
    val loginRestApi: LoginRestApi,
    val baseRepoApi: BaseRestApi
) : ViewModel() {
    private val TAG = "upcomingViewModel"

    val scheduledMeetingLiveData = MutableLiveData<List<NewInterviewDetails>>()

    fun getScheduledMeetingList(
        bodyScheduledMeetingBean: BodyScheduledMeetingBean,
        response: (result: Int, exception: String?,data:ResponseScheduledMeetingBean?) -> Unit,
        actionProgress: (action: Int) -> Unit
    ) {
        try {

            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                actionProgress(1)
                Log.d(
                    TAG,
                    "getScheduledMeetingList: bearer token : ${DataStoreHelper.getLoginBearerToken()}"
                )

                bodyScheduledMeetingBean.RecruiterEmail = DataStoreHelper.getUserEmail()
                bodyScheduledMeetingBean.Recruiter = DataStoreHelper.getMeetingRecruiterid()
                bodyScheduledMeetingBean.Subscriber = DataStoreHelper.getMeetingUserId()

                val result = baseRepoApi?.getScheduledMeetingsList(
                    DataStoreHelper.getLoginBearerToken(),
                    bodyScheduledMeetingBean
                )

                Log.d("checkauth", "getScheduledMeetingList: response code ${result?.code()} ")

                if (result?.code() == 401) {
                    Log.d("checkauth", "getScheduledMeetingList: unauthorised")
                    response(401,"Unauthorised",null)
                    actionProgress(0)
                }
                if (result?.isSuccessful!!) {
                    if (result?.body() != null) {
                        actionProgress(0)

                        scheduledMeetingLiveData.postValue(result.body()?.newInterviewDetails)
                        response(200, null,result?.body()!!)
                        Log.d(TAG, "getVideoSession:  success ${result.body()}")
                    } else {
                        actionProgress(0)
                        response(400, null,result.body()!!)
                        Log.d(TAG, "getVideoSession: null result")
                    }
                } else {
                    actionProgress(0)
                    response(404, null,null)
                    Log.d(TAG, "getVideoSession: not success")
                }
            }
        } catch (e: Exception) {
            actionProgress(0)
            response(500, e.printStackTrace().toString(),null)
        }
        catch (e: HttpException) {
         //   onDataResponse(null, 404)
            Log.d(TAG, "getVideoSession: not exception https exception ${e.message} 82")
        }
        catch (e:IOException)
        {
            Log.d(TAG, "getClientForLogin: io ex ${e.message} 87")
        }
    }


    fun getScheduledMeetingListwithOtp(
        bodyScheduledMeetingBean: BodyScheduledMeetingBean,
        response: (result: Int, exception: String?,data:ResponseScheduledMeetingBean?) -> Unit,
        actionProgress: (action: Int) -> Unit
    ) {
        try {

            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                actionProgress(1)
                Log.d(
                    TAG,
                    "getScheduledMeetingList: bearer token : ${DataStoreHelper.getLoginBearerToken()}"
                )

                bodyScheduledMeetingBean.RecruiterEmail = DataStoreHelper.getUserEmail()
                bodyScheduledMeetingBean.Recruiter = DataStoreHelper.getMeetingRecruiterid()
                bodyScheduledMeetingBean.Subscriber = DataStoreHelper.getMeetingUserId()

                val result = baseRepoApi?.getScheduledMeetingsListwithOtp(
                   // DataStoreHelper.getLoginBearerToken(),
                    bodyScheduledMeetingBean
                )

                Log.d("checkauth", "getScheduledMeetingList: response code ${result?.code()} ")

                if (result?.code() == 401) {
                    Log.d("checkauth", "getScheduledMeetingList: unauthorised")
                    response(401,"Unauthorised",null)
                    actionProgress(0)
                }
                if (result?.isSuccessful!!) {
                    if (result?.body() != null) {
                        actionProgress(0)

                        scheduledMeetingLiveData.postValue(result.body()?.newInterviewDetails)
                        response(200, null,result?.body()!!)
                        Log.d(TAG, "getVideoSession:  success ${result.body()}")
                    } else {
                        actionProgress(0)
                        response(400, null,result.body()!!)
                        Log.d(TAG, "getVideoSession: null result")
                    }
                } else {
                    actionProgress(0)
                    response(404, null,null)
                    Log.d(TAG, "getVideoSession: not success")
                }
            }
        } catch (e: Exception) {
            actionProgress(0)
            response(500, e.printStackTrace().toString(),null)
        }
        catch (e: HttpException) {
         //   onDataResponse(null, 404)
            Log.d(TAG, "getVideoSession: not exception https exception 137 ${e.message}")
        }
        catch (e:IOException)
        {
            Log.d(TAG, "getClientForLogin: io ex ${e.message} 146")
        }
    }




/*
    fun getVideoSessionDetails(videoAccessCode:String,onDataResponse:(data: ResponseInterViewDetailsBean?, response:Int)->Unit)
    {
        try {
            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                val result=baseRepoApi.requestVideoSession(videoAccessCode)
                if (result.isSuccessful)
                {
                    if (result.body()!=null)
                    {
                        when(result.body()?.aPIResponse?.statusCode){
                            404->{
                                onDataResponse(null,404)
                            }
                            200->{
                                onDataResponse(result.body(),200)
                            }
                            400->{
                                onDataResponse(result.body(),400)
                            }
                        }

                        //onDataResponse(result.body()!!,200)
                        Log.d(TAG, "getVideoSession:  success ${result.body()}")

                        Log.d("videocon", "getVideoSession:  success ${result.body()?.aPIResponse?.statusCode}")
                    }
                    else{
                        onDataResponse(result.body()!!,400)
                        Log.d(TAG, "getVideoSession: null result")
                    }
                }else
                {
                    onDataResponse(result.body()!!,404)
                    Log.d(TAG, "getVideoSession: not success")
                }
            }
        }catch (e:Exception)
        {
            onDataResponse(null,404)
            Log.d(TAG, "getVideoSession: not exception")
        }
    }
*/


    fun getVideoSessionHost(
        videoAccessCode: String,
        onDataResponse: (data: TokenResponseBean, response: Int) -> Unit
    ) {
        try {
            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                val result = baseRepoApi.getTwilioVideoTokenHost(videoAccessCode)
                if (result.isSuccessful) {
                    if (result.body() != null) {
                        onDataResponse(result.body()!!, 200)
                        Log.d(TAG, "getVideoSession:  success ${result.body()}")
                    } else {
                        onDataResponse(result.body()!!, 400)
                        Log.d(TAG, "getVideoSession: null result")
                    }
                } else {
                    onDataResponse(result.body()!!, 404)
                    Log.d(TAG, "getVideoSession: not success")
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "getVideoSession: 207 not exception")
        }
        catch (e: HttpException) {
         //   onDataResponse(null, 404)
            Log.d(TAG, "getVideoSession: 211 not exception https exception ${e.message}")
        }
        catch (e:IOException)
        {
            Log.d(TAG, "getClientForLogin: io ex ${e.message} 224")
        }
    }

    fun getInterAccessCodById(
        id: Int,
        onDataResponse: (data: ResponseInterviewerAccessCodeByID?, response: Int) -> Unit
    ) {
        try {
            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                val result = baseRepoApi.getInterviewAccessCodeById(id)
                if (result.isSuccessful) {
                    if (result.body() != null) {
                        onDataResponse(result.body()!!, 200)
                        Log.d(TAG, "getVideoSession:  success ${result.body()}")
                    } else {
                        onDataResponse(result.body()!!, 400)
                        when(result.code())
                        {
                            401->{
                                onDataResponse(result.body()!!, 400)
                            }
                            400->{
                                onDataResponse(result.body()!!, 400)
                            }
                            500->{
                                onDataResponse(result.body()!!, 500)
                            }
                        }
                        Log.d(TAG, "getVideoSession: null result")
                    }
                } else {
                    onDataResponse(null, 404)
                    Log.d(TAG, "getVideoSession: not success")
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "getVideoSession: not exception")
        } catch (e: HttpException) {
         //   onDataResponse(null, 404)
            Log.d(TAG, "getVideoSession: 247 not exception https exception ${e.message}")
        }
        catch (e:IOException)
        {
            Log.d(TAG, "getClientForLogin: io ex ${e.message} 264")
        }
    }

    fun setMuteUnmuteStatus(
        status: Boolean,
        interviewId: String,
        onResult: (action: Int, data: ResponseMuteUmnute?) -> Unit
    ) {
        try {
            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                val result = baseRepoApi.setMuteUnmuteStatus(
                    BodyMuteUmnuteBean(
                        interviewId,
                        true,
                        "",
                        "",
                        true
                    )
                )
                if (result.isSuccessful) {
                    if (result.body() != null) {
                        onResult(200, result.body()!!)
                    } else {
                        onResult(400, result.body()!!)
                    }
                } else {
                    onResult(404, result.body()!!)
                }
            }
        } catch (e: Exception) {
            onResult(500, null)
        }
        catch (e: HttpException) {
         //   onDataResponse(null, 404)
            Log.d(TAG, "getVideoSession: 294not exception https exception ${e.message}")
        }
        catch (e:IOException)
        {
            Log.d(TAG, "getClientForLogin: io ex ${e.message} 299")
        }
    }


    fun getVideoSessionCandidate(
        videoAccessCode: String,
        onDataResponse: (data: TokenResponseBean, response: Int) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {

            /* flow<Response<TokenResponseBean>>{
                repo.getTwilioVideoTokenCandidate(videoAccessCode)
            }.map {result1->
                if (result1.isSuccessful)
                {
                    ""
                }
                ""
            }.map {result2->
                repo.getTwilioVideoTokenCandidate(result2)
            } .map {
                    repo.getTwilioVideoTokenCandidate(result2)
                }
           */

            try {

                val result = baseRepoApi.getTwilioVideoTokenCandidate(videoAccessCode)
                if (result.isSuccessful) {
                    if (result.body() != null) {
                        onDataResponse(result.body()!!, 200)
                        Log.d(TAG, "getVideoSession:  success ${result.body()}")
                    } else {
                        onDataResponse(result.body()!!, 400)
                        Log.d(TAG, "getVideoSession: null result")
                    }
                } else {
                    onDataResponse(result.body()!!, 404)
                    Log.d(TAG, "getVideoSession: not success")
                }
            } catch (e: Exception) {
                Log.d(TAG, "getVideoSession: exception ${e.message}")
            }
            catch (e: HttpException) {
               // onDataResponse(null, 404)
                Log.d(TAG, "getVideoSession: not exception https exception 321 ${e.message}")
            }
            catch (e:IOException)
            {
                Log.d(TAG, "getClientForLogin: io ex ${e.message} 345")
            }
        }
    }


    fun getVideoSessionDetails(
        videoAccessCode: String,
        onDataResponse: (data: ResponseInterViewDetailsBean?, response: Int) -> Unit
    ) {
        try {

            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                val result = baseRepoApi.requestVideoSession(videoAccessCode)
                if (result.isSuccessful) {
                    if (result.body() != null) {
                        when (result.body()?.aPIResponse?.statusCode) {
                            404 -> {
                                onDataResponse(result.body()!!, 404)
                            }
                            200 -> {
                                onDataResponse(result.body(), 200)
                            }
                            400 -> {
                                onDataResponse(result.body(), 400)
                            }
                            401 -> {
                                onDataResponse(result.body(), 401)
                            }
                        }
                        //onDataResponse(result.body()!!,200)
                        Log.d(TAG, "getVideoSession:  success ${result.body()}")

                        Log.d(
                            "videocon",
                            "getVideoSession:  success ${result.body()?.aPIResponse?.statusCode}"
                        )
                    } else {
                        onDataResponse(result.body()!!, 400)
                        Log.d(TAG, "getVideoSession: null result")
                    }
                } else {
                    onDataResponse(result.body()!!, 404)
                    Log.d(TAG, "getVideoSession: not success")
                }
            }
        } catch (e: Exception) {
            onDataResponse(null, 404)
            Log.d(TAG, "getVideoSession: not exception")
        }
        catch (e:HttpException){
            Log.d(TAG, "getVideoSession: not exception https 391 ${e.message}")
            onDataResponse(null, 404)
        }
        catch (e:IOException)
        {
            Log.d(TAG, "getClientForLogin: io ex ${e.message} 397")
        }
    }


/**cancel meeting*/

    fun cancelMeeting(data: NewInterviewDetails,
                onDataResponse: (data: BodyCancelMeeting?, response: Int) -> Unit
    ) {
        try {

            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {

                val token=DataStoreHelper.getLoginBearerToken()
                token
                val email=DataStoreHelper.getUserEmail()
                email

                val result = baseRepoApi.cancelMeeting(DataStoreHelper.getLoginBearerToken(),BodyCancelMeeting(InterviewSeqId=data.interviewId, SubscriberId = data.subscriberid.toString(), logedInUser = DataStoreHelper.getUserEmail()))
                if (result.isSuccessful) {
                    if (result.body() != null) {
                        when (result.code()) {
                            404 -> {
                                onDataResponse(result.body()!!, 404)
                            }
                            200 -> {
                                onDataResponse(result.body(), 200)
                            }
                            400 -> {
                                onDataResponse(result.body(), 400)
                            }
                            401 -> {
                                onDataResponse(result.body(), 401)
                            }
                        }
                        //onDataResponse(result.body()!!,200)
                        Log.d(TAG, "getVideoSession:  success ${result.body()}")

                        Log.d(
                            "videocon",
                            "getVideoSession:  success ${result.body()?.aPIResponse?.Message}"
                        )
                    } else {
                        onDataResponse(result.body()!!, 400)
                        Log.d(TAG, "getVideoSession: null result")
                    }
                } else {
                    onDataResponse(null, 404)
                    Log.d(TAG, "getVideoSession: not success")
                }
            }
        } catch (e: Exception) {
            onDataResponse(null, 404)
            Log.d(TAG, "getVideoSession: not exception")
        }
        catch (e: HttpException) {
            onDataResponse(null, 404)
            Log.d(TAG, "getVideoSession: not exception https exception 450 ${e.message}")
        }
        catch (e: IOException)
        {
            Log.d(TAG, "getClientForLogin: io ex ${e.message} 455")
        }
    }








}