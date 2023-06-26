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
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelCreateCandidate @Inject constructor(val baseRestApi: BaseRestApi) : ViewModel() {

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



    fun createCandidate(creatingObject:BodyCreateCandidate, respnse:(data:BodyCreateCandidate?,isSuccess:Boolean, errorCode:Int, msg:String)->Unit) {
        CoroutineScope(Dispatchers.IO + exceptionHandler)
            .launch {
                try {
                    creatingObject.Subscriberid=getUserProfileData()?.Subscriberid
                    creatingObject.Userid=getUserProfileData()?.RecruiterId
                    creatingObject.CandidateId=getUserProfileData()?.Id
                    creatingObject.Subscriberid
                    creatingObject.Userid
                    creatingObject.CandidateId
                    Log.d(TAG, "createCandidate: gson data of create profile ${Gson().toJson(creatingObject)}")
                    val authToken= DataStoreHelper.getLoginAuthToken()
                    val response=baseRestApi.createCandidate(authToken,creatingObject)

                    //Log.d(TAG, "getCandidateList: token in upviewmodel $authToken")
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                respnse(response.body()!!,true,200,response.body()?.aPIResponse?.message!!)
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

    fun createCandidateWithoutAuth(creatingObject:BodyCreateCandidate,tokenId:String, respnse:(data:BodyCreateCandidate?,isSuccess:Boolean, errorCode:Int, msg:String)->Unit) {
        CoroutineScope(Dispatchers.IO + exceptionHandler)
            .launch {
                try {
                    creatingObject.CandidateId=getUserProfileData()?.Id
                    Log.d(TAG, "createCandidate: gson data of create profile ${Gson().toJson(creatingObject)}")
                    val response=baseRestApi.createCandidateWithoutAuth(creatingObject,"/api/JobSeekerProfile/$tokenId")

                    //Log.d(TAG, "getCandidateList: token in upviewmodel $authToken")
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                respnse(response.body()!!,true,200,response.body()?.aPIResponse?.message!!)
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


    private val userProfileDataList= mutableListOf<ResponseRecruiterDetails?>()
    fun getUserProfileData()=
     if (userProfileDataList.isNullOrEmpty())
     {
         null
     }else
     {
         userProfileDataList[0]
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
                                    userProfileDataList.clear()
                                    userProfileDataList.add(0,response.body())
                                }catch (e:Exception)
                                {
                                    Log.d(TAG, "getCandidateDetails: exception ${e.message}")
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
                    Log.d(TAG, "getCandidateDetails: exception 298 p ${e.message}")
                }
            }
    }





}