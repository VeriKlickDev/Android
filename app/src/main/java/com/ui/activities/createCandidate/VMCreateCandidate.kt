package com.ui.activities.createCandidate

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import com.data.dataHolders.DataStoreHelper
import com.data.exceptionHandler
import com.domain.BaseModels.*
import com.domain.RestApi.BaseRestApi
import com.domain.RestApi.LoginRestApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VMCreateCandidate @Inject constructor(val baseRestApi: BaseRestApi) : ViewModel() {

    private val TAG="createcandidateVM"

    fun getCountyNameList( respnse:(data:List<ResponseCountryName>?,isSuccess:Boolean, errorCode:Int, msg:String)->Unit) {
        CoroutineScope(Dispatchers.IO + exceptionHandler)
            .launch {
                try {
                    val response=baseRestApi.getCountryNamesList()
                    //val authToken= DataStoreHelper.getLoginAuthToken()
                    //Log.d(TAG, "getCandidateList: token in upviewmodel $authToken")
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                respnse(response.body()!!,true,200,"")
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
                        respnse(null,false,502,"Response not success")
                    }
                } catch (e: Exception) {
                    respnse(null,false,503,e.message.toString())
                }
            }
    }
    fun getCountyCodeList( respnse:(data:List<ResponseCountryCode>?,isSuccess:Boolean, errorCode:Int, msg:String)->Unit) {
        CoroutineScope(Dispatchers.IO + exceptionHandler)
            .launch {
                try {

                    val response=baseRestApi.getCountryCodeList()
                    //val authToken= DataStoreHelper.getLoginAuthToken()
                    //Log.d(TAG, "getCandidateList: token in upviewmodel $authToken")
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                respnse(response.body()!!,true,200,"")
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
                        respnse(null,false,502,"Response not success")
                    }
                } catch (e: Exception) {
                    respnse(null,false,503,e.message.toString())
                }
            }
    }



    fun getStateByidList(searchString: String,id:String, respnse:(data:List<ResponseState>?,isSuccess:Boolean, errorCode:Int, msg:String)->Unit) {
        CoroutineScope(Dispatchers.IO + exceptionHandler)
            .launch {
                try {
                    val url="/api/Common/GetStateListById/$searchString/${id}"
                    val response=baseRestApi.getStateByIDList(url)
                    //val authToken= DataStoreHelper.getLoginAuthToken()
                    //Log.d(TAG, "getCandidateList: token in upviewmodel $authToken")
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                respnse(response.body()!!,true,200,"")
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
                        respnse(null,false,502,"Response not success")
                    }
                } catch (e: Exception) {
                    respnse(null,false,503,e.message.toString())
                }
            }
    }


    fun getCityByidList(shortName:String,searchString:String, respnse:(data:List<ResponseCity>?,isSuccess:Boolean, errorCode:Int, msg:String)->Unit) {
        CoroutineScope(Dispatchers.IO + exceptionHandler)
            .launch {
                try {
                    val url="/api/Common/GetCityListById/$shortName/$searchString"
                    val response=baseRestApi.getCityByIDList(url)
                    //val authToken= DataStoreHelper.getLoginAuthToken()
                    //Log.d(TAG, "getCandidateList: token in upviewmodel $authToken")
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                respnse(response.body()!!,true,200,"")
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
                        respnse(null,false,502,"Response not success")
                    }
                } catch (e: Exception) {
                    respnse(null,false,503,e.message.toString())
                }
            }
    }



    fun createCandidate(creatingObject:BodyCreateCandidate, respnse:(data:String?,isSuccess:Boolean, errorCode:Int, msg:String)->Unit) {
        CoroutineScope(Dispatchers.IO + exceptionHandler)
            .launch {
                try {
                    val response=baseRestApi.createCandidate(creatingObject)
                    //val authToken= DataStoreHelper.getLoginAuthToken()
                    //Log.d(TAG, "getCandidateList: token in upviewmodel $authToken")
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                respnse(response.body()!!,true,200,"")
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
                        respnse(null,false,502,"Response not success")
                    }
                } catch (e: Exception) {
                    respnse(null,false,503,e.message.toString())
                }
            }
    }

    private val userProfileDataList= mutableListOf<ResponseCandidateDataForIOS?>()
    fun getUserProfileData()=
     if (userProfileDataList.isNullOrEmpty())
     {
         null
     }else
     {
         userProfileDataList[0]
     }

    fun getCandidateDetails( candidateId:String,respnse:(data:ResponseCandidateDataForIOS?,isSuccess:Boolean, errorCode:Int, msg:String)->Unit) {
        CoroutineScope(Dispatchers.IO + exceptionHandler)
            .launch {
                try {

                    val response=baseRestApi.getResumeFileName(DataStoreHelper.getLoginBearerToken(),"/api/CandidateDataForIOS/" + candidateId)
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {//response.body()!!
                                userProfileDataList.clear()
                                userProfileDataList.add(response.body()!!)
                                userProfileDataList
                                respnse(response.body()!!,true,200,"")
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
                        respnse(null,false,502,"Response not success")
                    }
                } catch (e: Exception) {
                    respnse(null,false,503,e.message.toString())
                }
            }
    }


    fun sendProfileLink(bodySMSCandidate: BodySMSCandidate,respnse:(data:ResponseSMSCadidate?,isSuccess:Boolean, errorCode:Int, msg:String)->Unit) {
        CoroutineScope(Dispatchers.IO + exceptionHandler)
            .launch {
                try {
                    val authToken= DataStoreHelper.getLoginBearerToken()
                    val response=baseRestApi.sendSMSToCandidate(bodySMSCandidate,authToken)
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {//response.body()!!
                                respnse(response.body()!!,true,200,response?.body()!!.ResponseMessage.toString())
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
                        respnse(null,false,502,"Response not success")
                    }
                } catch (e: Exception) {
                    respnse(null,false,503,e.message.toString())
                }
            }
    }


    fun getIsPhoneExists(
        interviewId: Int,
        email: String,
        Phone: String,
        response: (isExists: Boolean) -> Unit
    ) {
        try {
            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                val result = baseRestApi.getPhoneExistsDetails(
                    IsEmailPhoneExistsModel(
                        interviewId,
                        email,
                        Phone
                    )
                )

                if (result.isSuccessful) {
                    if (result.body() != null) {
                        response(result.body()!!)
                    }
                    else {
                        Log.d(TAG, "getIsEmailAndPhoneExists: null response ")
                    }
                }
                else {
                    Log.d(TAG, "getIsEmailAndPhoneExists:  response not success")
                }
            }
        } catch (e: Exception) {
            Log.d("adduserexception", "getIsEmailAndPasswordExists: exception  ")
        }
    }




}