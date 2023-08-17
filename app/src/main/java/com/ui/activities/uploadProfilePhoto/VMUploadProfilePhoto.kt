package com.ui.activities.uploadProfilePhoto

import androidx.lifecycle.ViewModel
import com.data.dataHolders.CandidateImageAndAudioHolder
import com.data.dataHolders.DataStoreHelper
import com.data.exceptionHandler
import com.domain.BaseModels.BodyCandidateImageModel
import com.domain.BaseModels.BodyCandidateResume
import com.domain.BaseModels.ResponseRecruiterDetails
import com.domain.RestApi.BaseRestApi
import com.domain.RestApi.LoginRestApi
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject

@HiltViewModel
class VMUploadProfilePhoto @Inject constructor(val loginRestApi: LoginRestApi,val baseRestApi: BaseRestApi) : ViewModel() {


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


    fun updateUserImageWithoutAuth(ob:BodyCandidateImageModel, respnse:(isSuccess:Boolean, code:Int, msg:String)->Unit)
    {
        CoroutineScope(Dispatchers.IO + exceptionHandler)
            .launch {
                try {

                    val authToken=DataStoreHelper.getLoginBearerToken()

                    val response = loginRestApi.updateFreshUserImageWithoutAuth(ob)
                    if (response.isSuccessful)
                    {
                        when (response.code()) {
                            200 -> {
                                respnse(true,200,response.body()!!.message.toString())
                            }
                            401 -> {
                                respnse(false,401,response.body()!!.message.toString())
                            }
                            400 -> {
                                respnse(false,400,response.body()!!.message.toString())
                            }
                            500 -> {
                                respnse(false,500,response.body()!!.message.toString())
                            }
                            501 -> {
                                respnse(false,501,response.body()!!.message.toString())
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

    /*                    var filee= File(finalUserImage?.path)
         var imgPart=MultipartBody.Part.createFormData(
             DataStoreHelper.getMeetingUserId()+"/"+filee.name,
             DataStoreHelper.getMeetingUserId()+"IMG${System.currentTimeMillis()}.png",filee.asRequestBody())
*/

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

    fun getRecruiterDetails(id:String,token:String, respnse:(data:ResponseRecruiterDetails?,isSuccess:Boolean, errorCode:Int, msg:String)->Unit)
    {
        CoroutineScope(Dispatchers.IO + exceptionHandler)
            .launch {
                try {
                    //val authToken= DataStoreHelper.getLoginBearerToken()
                    val url="/api/Candidate/$id/$token"
                    val response = baseRestApi.getRecruiterDetailsWithID(url)
                    if (response.isSuccessful)
                    {
                        when (response.code()) {
                            200 -> {
                                respnse(response.body(),true,200,"")
                            }
                            401 -> {
                                respnse(null,false,401,"")
                            }
                            400 -> {
                                respnse(null,false,400,"")
                            }
                            500 -> {
                                respnse(null,false,500,"")
                            }
                            501 -> {
                                respnse(null,false,501,"")
                            }
                        }
                    } else {
                        respnse(null,false,503,"Response not success")
                    }
                } catch (e: Exception) {
                    respnse(null,false,502,e.message.toString())
                }
            }
    }

    fun getCandidateDetails( candidateId:String,tokenId: String,respnse:(data:ResponseRecruiterDetails?,isSuccess:Boolean, errorCode:Int, msg:String?)->Unit) {
        CoroutineScope(Dispatchers.IO + exceptionHandler)
            .launch {
                try {

                    val url="/api/Candidate/$candidateId/$tokenId"
                    val response = baseRestApi.getRecruiterDetailsWithID(url)
                    //val response=baseRestApi.getResumeFileNameWithoutAuth("/api/CandidateDataForIOS/" + candidateId)
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {//response.body()!!
                                try {

                                }catch (e:Exception)
                                {

                                }
                                respnse(response.body()!!,true,200,null)
                            }
                            401 -> {
                                respnse(null,false,401,response.body()?.aPIResponse?.message!!)
                            }
                            400 -> {
                                respnse(null,false,400,response.body()?.aPIResponse?.message!!)
                            }
                            500 -> {
                                respnse(null,false,500,"")
                            }
                            501 -> {
                                respnse(null,false,501,"")
                            }
                        }
                    } else {
                        respnse(null,false,502,null)
                    }
                } catch (e: Exception) {
                    respnse(null,false,503,e.message.toString())
                    if (e.message.equals("null"))
                    {

                    }
                }
            }
    }



}