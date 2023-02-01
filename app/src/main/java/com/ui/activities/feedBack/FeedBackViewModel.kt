package com.ui.activities.feedBack

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.dataHolders.CurrentMeetingDataSaver
import com.data.dataHolders.CurrentUpcomingMeetingData
import com.data.dataHolders.DataStoreHelper
import com.data.getCurrentUtcFormatedDate
import com.data.repositoryImpl.RepositoryImpl
import com.domain.BaseModels.*
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedBackViewModel @Inject constructor(val repo: RepositoryImpl) :ViewModel() {

    private val TAG="feedbackVIewModel"

    fun getFeedBack(onResponse:(data: ResponseFeedBack?,status:Int)->Unit)
    {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val result=repo.getFeedBackDetails(CurrentMeetingDataSaver.getData()?.videoAccessCode.toString())
                if (result.isSuccessful)
                {
                    if (result.body()!=null)
                    {
                        when(result.code())
                        {
                            200->{
                                onResponse(result.body()!!,200)
                            }
                            401->{
                                onResponse(result.body()!!,401)
                            }
                        }
                        Log.d(TAG, "getVideoSession:  success ${result.body()}")
                    }
                    else{
                        onResponse(result.body()!!,400)
                        Log.d(TAG, "getVideoSession: null result")
                    }
                }else
                {
                    onResponse(result.body()!!,404)
                    Log.d(TAG, "getVideoSession: not success")
                }
            }
        }catch (e:Exception)
        {
            onResponse(null,500)
            Log.d(TAG, "getVideoSession: not exception")
        }

    }

    fun sendFeedback(context: Context,assementId:Int,role:String,appliedPosition:String,recommendation:String,designation:String,interviewName:String,candidateId:Int,remarktxt:String,codingRemarktxt:String, obj:BodyFeedBack,skillsListRes:ArrayList<AssessSkills>,remarkList:ArrayList<InterviewerRemark>, onDataResponse:(data: ResponseBodyFeedBack?, status:Int)->Unit) {
        viewModelScope.launch {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    //val candidateId=CurrentMeetingDataSaver.getData()?.users?.filter { it.userType.contains("C") }

                    val memberList = arrayListOf<CandidateAssessmentPanelMembers>()
                    memberList.add( CandidateAssessmentPanelMembers(
                        Name = interviewName,
                        Designation = designation,
                        CandiateAssessment = "null",
                        CandidateAssesmentId = 0,
                        PanelMemberId = 0
                    ))

                    //obj.CandidateAssessment?.Recommendation = recommendation

                    obj?.CandidateAssessment?.Remark=remarkList

                    obj.CandidateAssessment?.Recommendation=remarktxt
                    obj.CandidateAssessment?.Comments=recommendation

                    obj?.CandidateAssessment?.Skills=skillsListRes
                    obj?.CandidateAssessment?.AssessmentId=assementId
                    obj?.CandidateAssessment?.CodingTestRemarksForVideo=codingRemarktxt

                    obj?.CandidateAssessmentPanelMembers?.add(0,memberList[0])

                    /*obj?.CandidateAssessmentPanelMembers?.get(0)!!.CandidateAssesmentId=assementId
                    obj?.CandidateAssessmentPanelMembers?.get(0)!!.Designation=role
                    obj?.CandidateAssessmentPanelMembers?.get(0)!!.Name=interviewName
                    obj?.CandidateAssessmentPanelMembers?.get(0)!!.CandiateAssessment="null"
                    obj?.CandidateAssessmentPanelMembers?.get(0)!!.PanelMemberId=0
                    */

                            /* CurrentUpcomingMeetingData.getData()?.let {
                        obj.RecruiterId=it.interviewId.toString()
                    }*/

                        if (DataStoreHelper.getMeetingRecruiterid().equals("") || DataStoreHelper.getMeetingRecruiterid().equals("null"))
                        {
                            obj.RecruiterId=CurrentMeetingDataSaver.getData()?.interviewModel?.recruiterId.toString()
                        }else
                        {
                            obj.RecruiterId=DataStoreHelper.getMeetingRecruiterid()
                        }


                    /*
                    CurrentMeetingDataSaver.getData()?.interviewModel?.interviewId?.let {
                        obj.RecruiterId=it.toString()
                    }*/

                    //
                    obj.CandidateAssessment?.CandidateName = "${CurrentMeetingDataSaver.getData()?.interviewModel?.candidate?.firstName} ${CurrentMeetingDataSaver.getData()?.interviewModel?.candidate?.lastName}"
                    obj.CandidateAssessment?.Date = context.getCurrentUtcFormatedDate()
                    obj.CandidateAssessment?.AppliedPostion = appliedPosition
                    obj.CandidateAssessment?.VIDEOCALLACCESSCODE =
                        CurrentMeetingDataSaver.getData()?.videoAccessCode
                    obj.CandidateAssessment?.jobid =
                        CurrentMeetingDataSaver.getData()?.interviewModel?.jobid
                    //uncomment obj.CandidateAssessmentPanelMembers = memberList[0]
                    obj.CandidateAssessment?.RecommendationId=3


                    CurrentMeetingDataSaver.getData()?.let {
                        Log.d(TAG, "sendFeedback: current meetin og canid ${it.interviewModel?.candidateId}")
                        obj.CandidateAssessment?.CandidateId=it.interviewModel?.candidateId
                    }

                    Log.d(TAG, "sendFeedback: after candidate id  ${candidateId}")

                    Log.d(TAG, "sendFeedback: ${Gson().toJson(obj)}")

                    val result = repo.sendFeedBack(obj)
                   Log.d(TAG, "sendFeedback: code ${result.code()}")
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
                                500 -> {
                                    onDataResponse(null, 401)
                                }
                            }
                            //onDataResponse(result.body()!!,200)
                            Log.d(TAG, "getVideoSession:  success ${result.body()}")

                            Log.d(
                                "videocon",
                                "getVideoSession:  success ${result.body()?.aPIResponse?.statusCode}"
                            )

                            if (result.code()==200)
                            {
                                onDataResponse(result.body(), 200)
                            }


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
                onDataResponse(null, 501)
                Log.d(TAG, "getVideoSession: not exception")
            }
        }

    }


    fun getVideoSessionDetails(videoAccessCode:String,onDataResponse:(data: ResponseInterViewDetailsBean?, response:Int)->Unit)
    {
        try {

            CoroutineScope(Dispatchers.IO).launch {
                val result=repo.requestVideoSession(videoAccessCode)
                if (result.isSuccessful)
                {
                    if (result.body()!=null)
                    {
                        when(result.body()?.aPIResponse?.statusCode){
                            404->{
                                onDataResponse(result.body()!!,404)
                            }
                            200->{
                                onDataResponse(result.body(),200)
                            }
                            400->{
                                onDataResponse(result.body(),400)
                            }
                            401->{
                                onDataResponse(result.body(),401)
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
                    onDataResponse(null,404)
                    Log.d(TAG, "getVideoSession: not success")
                }
            }
        }catch (e:Exception)
        {
            onDataResponse(null,404)
            Log.d(TAG, "getVideoSession: not exception")
        }
    }
}
