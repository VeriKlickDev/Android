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
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class VMUploadProfilePhoto @Inject constructor(val loginRestApi: LoginRestApi,val baseRestApi: BaseRestApi) : ViewModel() {


    fun updateUserImage(ob:BodyCandidateImageModel, respnse:(isSuccess:Boolean, errorCode:Int, msg:String)->Unit)
    {
        CoroutineScope(Dispatchers.IO + exceptionHandler)
            .launch {
                try {

                    //val authToken=DataStoreHelper.getLoginAuthToken()
                    val response = loginRestApi.updateUserImage(ob)
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                respnse(true,200,"")
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
                    //val authToken=DataStoreHelper.getLoginAuthToken()
                    val response = loginRestApi.updateUserResume(ob,imgPart)
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                respnse(true,200,"")
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