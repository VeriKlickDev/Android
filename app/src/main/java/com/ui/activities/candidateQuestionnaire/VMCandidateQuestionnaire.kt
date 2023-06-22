package com.ui.activities.candidateQuestionnaire

import android.util.Log
import androidx.lifecycle.ViewModel
import com.data.dataHolders.DataStoreHelper
import com.data.exceptionHandler
import com.domain.BaseModels.BodyQuestionnaire
import com.domain.BaseModels.ResponseCountryCode
import com.domain.BaseModels.ResponseQuestionnaire
import com.domain.BaseModels.ResponseQuestionnaireTemplate
import com.domain.BaseModels.ResponseShowQuestionnaire
import com.domain.RestApi.BaseRestApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VMCandidateQuestionnaire @Inject constructor(val baseRestApi: BaseRestApi) :ViewModel() {

    fun getQuestionnaireList(
        templateID: String,
        accessToken: String,
        respnse: (data: ResponseQuestionnaire?, isSuccess: Boolean, errorCode: Int, msg: String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO + exceptionHandler)
            .launch {
                try {
                    //val authToken= DataStoreHelper.getLoginBearerToken()
                    // Log.d("TAG", "getQuestionnaireList: token is this $authToken")
                    val response = baseRestApi.getQuestionnaireList(templateID)

                    //Log.d(TAG, "getCandidateList: token in upviewmodel $authToken")
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                respnse(response.body()!!, true, 200, "")
                            }
                            401 -> {
                                respnse(null, false, 401, "")
                            }
                            400 -> {
                                respnse(null, false, 400, "")
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


    fun postQuestionnaires(
        obj:BodyQuestionnaire,
        respnse: (data: BodyQuestionnaire?, isSuccess: Boolean, errorCode: Int, msg: String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO + exceptionHandler)
            .launch {
                try {
                    //val authToken= DataStoreHelper.getLoginBearerToken()
                    // Log.d("TAG", "getQuestionnaireList: token is this $authToken")
                    val response = baseRestApi.postQuestionnaire(obj)

                    //Log.d(TAG, "getCandidateList: token in upviewmodel $authToken")
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                respnse(response.body()!!, true, 200, response.body()?.Message!!.toString())
                            }
                            401 -> {
                                respnse(null, false, 401, response.body()?.Message!!.toString())
                            }
                            400 -> {
                                respnse(null, false, 400, response.body()?.Message!!.toString())
                            }
                            500 -> {
                                respnse(null, false, 500, response.body()?.Message!!.toString())
                            }
                            501 -> {
                                respnse(null, false, 501, response.body()?.Message!!.toString())
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
        respnse: (data: ResponseShowQuestionnaire?, isSuccess: Boolean, errorCode: Int, msg: String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO + exceptionHandler)
            .launch {
                try {
                    //val authToken= DataStoreHelper.getLoginBearerToken()
                    // Log.d("TAG", "getQuestionnaireList: token is this $authToken")

                    val response = baseRestApi.getQuestionnaireForCandidate("/api/Questionier/GetCandidateQuestionierAnswerSheet/"+candidateId)

                    //Log.d(TAG, "getCandidateList: token in upviewmodel $authToken")
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                respnse(response.body()!!, true, 200,response.body()?.Message.toString())
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