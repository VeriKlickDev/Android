package com.ui.activities.upcomingMeeting.audioRecord.player

import android.util.Log
import androidx.lifecycle.ViewModel
import com.data.dataHolders.CandidateImageAndAudioHolder
import com.data.dataHolders.DataStoreHelper
import com.data.exceptionHandler
import com.domain.BaseModels.BodyCandidateImageModel
import com.domain.BaseModels.BodyCandidateResume
import com.domain.RestApi.BaseRestApi
import com.domain.RestApi.LoginRestApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject


@HiltViewModel
class VMPlayAudio @Inject constructor(val loginRestApi: LoginRestApi, val baseRestApi: BaseRestApi) : ViewModel() {


    fun updateUserImageFresh(ob:BodyCandidateImageModel, respnse:(isSuccess:Boolean, code:Int, msg:String)->Unit)
    {
        CoroutineScope(Dispatchers.IO + exceptionHandler)
            .launch {
                try {

                    val authToken=DataStoreHelper.getLoginBearerToken()
                    val response = loginRestApi.updateFreshUserImage(authToken,ob)
                    if (response.isSuccessful)
                    {
                        when (response.code()) {
                            200 -> {
                                respnse(true,200,response.body()!!.message.toString())
                            }
                            401 -> {
                                respnse(false,401,"")
                            }
                            400 -> {
                                respnse(false,400,"")
                            }
                            500 -> {
                                respnse(false,500,"")
                            }
                            501 -> {
                                respnse(false,501,"")
                            }
                        }
                    } else {
                        respnse(false,503,"Response not success")
                    }
                } catch (e: Exception) {
                    respnse(false,502,e.message.toString())
                }
            }
    }


    fun updateUserAudioWithoutAuth(respnse:(isSuccess:Boolean, code:Int, msg:String)->Unit)
    {
        CoroutineScope(Dispatchers.IO + exceptionHandler)
            .launch {
                try {

                    val audioFileNameStr="rec${System.currentTimeMillis()}"+CandidateImageAndAudioHolder.getAudioObject()?.name.toString()

                    val audioFile = MultipartBody.Part.createFormData("File", "${CandidateImageAndAudioHolder.getAudioObject()?.getName()}", CandidateImageAndAudioHolder.getAudioObject()!!.asRequestBody())
                    val audioFileName=MultipartBody.Part.createFormData("AudioFileName",audioFileNameStr)
                    val recruiterId=MultipartBody.Part.createFormData("RecruiterId",CandidateImageAndAudioHolder.getDeepLinkData()?.subscriberId.toString())
                    val profileUrl=MultipartBody.Part.createFormData("Profile_Url",CandidateImageAndAudioHolder.getImageObject()?.imageName.toString())
                    val tokenId=MultipartBody.Part.createFormData("Token_Id", CandidateImageAndAudioHolder.getDeepLinkData()?.token_Id.toString())

                    CandidateImageAndAudioHolder.setAudioName(audioFileNameStr)
                    Log.d("TAG", "updateUserAudioWithoutAuth: audiofilename ${audioFileNameStr}")
                   // val authToken=DataStoreHelper.getLoginBearerToken()
                    val response = baseRestApi.updateUserAudio(audioFile,audioFileName,recruiterId,profileUrl,tokenId)
                    if (response.isSuccessful)
                    {
                        when (response.code()) {
                            200 -> {
                                respnse(true,200,response.body()!!.Message.toString())
                            }
                            401 -> {
                                respnse(false,401,response.body()!!.Message.toString())
                            }
                            400 -> {
                                respnse(false,400,response.body()!!.Message.toString())
                            }
                            500 -> {
                                respnse(false,500,response.body()!!.Message.toString())
                            }
                            501 -> {
                                respnse(false,501,response.body()!!.Message.toString())
                            }
                        }
                    } else {
                        respnse(false,503,"Response not success")
                    }
                } catch (e: Exception) {
                    respnse(false,502,e.message.toString())
                }
            }
    }

    fun updateUserResume(ob:BodyCandidateResume,imgPart:MultipartBody.Part, respnse:(isSuccess:Boolean, errorCode:Int, msg:String)->Unit)
    {
        CoroutineScope(Dispatchers.IO + exceptionHandler)
            .launch {
                try {
                    val authToken= DataStoreHelper.getLoginBearerToken()
                    val response = loginRestApi.updateUserResume(authToken,ob,imgPart)
                    if (response.isSuccessful)
                    {
                        when (response.code()) {
                            200 -> {
                                respnse(true,200,response.body()?.message.toString())
                            }
                            401 -> {
                                respnse(false,401,"")
                            }
                            400 -> {
                                respnse(false,400,"")
                            }
                            500 -> {
                                respnse(false,500,"")
                            }
                            501 -> {
                                respnse(false,501,"")
                            }
                        }
                    } else {
                        respnse(false,503,"Response not success")
                    }
                } catch (e: Exception) {
                    respnse(false,502,e.message.toString())
                }
            }
    }


}