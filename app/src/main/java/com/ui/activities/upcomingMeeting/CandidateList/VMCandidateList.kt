package com.ui.activities.upcomingMeeting.CandidateList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.data.dataHolders.DataStoreHelper
import com.data.exceptionHandler
import com.domain.BaseModels.*
import com.domain.RestApi.BaseRestApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class VMCandidateList @Inject constructor(val baseRestApi: BaseRestApi) : ViewModel() {

    private var candidateListMutable = MutableLiveData<ResponseCandidateList>()
    var candidateListLive: LiveData<ResponseCandidateList>? = null

    fun getCandidateList(ob: BodyCandidateList, recid: String, subsId: String,top:String,skip:String,searchExpression:String,category:String,error:(isError:Boolean,errorCode:Int,msg:String)->Unit) {
        CoroutineScope(Dispatchers.IO + exceptionHandler)
            .launch {
                try {
                    var endPoint = "/api/Recruiter/GetSavedProfilesListByUserId/"
                    var url = endPoint + recid + "/" + subsId + "?Top=" + top + "" + "&" + "skip=" + skip + "&" + "SearchExpression=" + searchExpression + "&" + "Category=" + category
                    val authToken=DataStoreHelper.getLoginAuthToken()
                    val response = baseRestApi.getCandidateList(authToken,url,ob)
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                error(false,200,"")
                                candidateListMutable.postValue(response.body())
                            }
                            401 -> {
                                error(true,401,"")
                            }
                            400 -> {
                                error(true,400,"")
                            }
                            500 -> {
                                error(true,500,"")
                            }
                        }
                    } else {
                        error(true,501,"")
                    }
                } catch (e: Exception) {
                    error(true,502,e.message.toString())
                }
            }
    }




    fun getQuestionnaireTemplate(
        recruiterId: String,
        respnse: (data: ResponseQuestionnaireTemplate?, isSuccess: Boolean, errorCode: Int, msg: String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO + exceptionHandler)
            .launch {
                try {
                    //val authToken= DataStoreHelper.getLoginBearerToken()
                    // Log.d("TAG", "getQuestionnaireList: token is this $authToken")
                    val response = baseRestApi.getQuestionnaireTemplateList(recruiterId)

                    //Log.d(TAG, "getCandidateList: token in upviewmodel $authToken")
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                respnse(response.body()!!, true, 200,"")
                            }
                            401 -> {
                                respnse(null, false, 401,"")
                            }
                            400 -> {
                                respnse(null, false, 400,"")
                            }
                            500 -> {
                                respnse(null, false, 500, "")
                            }
                            501 -> {
                                respnse(null, false, 501, "")
                            }
                        }
                    } else {
                        respnse(null, false, 502, "Response not success")
                    }
                } catch (e: Exception) {
                    respnse(null, false, 503, e.message.toString())
                }
            }
    }


    fun getQuestionnaireforCandidate(
        candidateId: String,
        respnse: (data: ResponseQuestionnaireTemplate?, isSuccess: Boolean, errorCode: Int, msg: String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO + exceptionHandler)
            .launch {
                try {
                    //val authToken= DataStoreHelper.getLoginBearerToken()
                    // Log.d("TAG", "getQuestionnaireList: token is this $authToken")

                    val response = baseRestApi.getQuestionnaireTemplateList("/api/Questionier/GetCandidateQuestionierAnswerSheet/"+candidateId)

                    //Log.d(TAG, "getCandidateList: token in upviewmodel $authToken")
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                respnse(response.body()!!, true, 200,"")
                            }
                            401 -> {
                                respnse(null, false, 401,"")
                            }
                            400 -> {
                                respnse(null, false, 400,"")
                            }
                            500 -> {
                                respnse(null, false, 500, "")
                            }
                            501 -> {
                                respnse(null, false, 501, "")
                            }
                        }
                    } else {
                        respnse(null, false, 502, "Response not success")
                    }
                } catch (e: Exception) {
                    respnse(null, false, 503, e.message.toString())
                }
            }
    }





}