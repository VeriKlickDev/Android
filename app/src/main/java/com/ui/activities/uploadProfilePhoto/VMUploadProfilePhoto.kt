package com.ui.activities.uploadProfilePhoto

import androidx.lifecycle.ViewModel
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
                    val response = baseRestApi.updateFreshUserImageWithoutAuth(ob)
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


}