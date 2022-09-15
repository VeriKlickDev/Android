package com.ui.activities.feedBack

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.data.dataHolders.CurrentMeetingDataSaver
import com.data.getCurrentUtcFormatedDate
import com.data.repositoryImpl.RepositoryImpl
import com.domain.BaseModels.*
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
                val result=repo.getFeedBackDetails(CurrentMeetingDataSaver.getData().videoAccessCode.toString())
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

    fun sendFeedback(context: Context,appliedPosition:String,recommendation:String,designation:String,interviewName:String, obj:BodyFeedBack, onDataResponse:(data: ResponseBodyFeedBack?, status:Int)->Unit)
    {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                //val candidateId=CurrentMeetingDataSaver.getData().users?.filter { it.userType.contains("C") }

                val memberList= arrayListOf<CandidateAssessmentPanelMembers>(CandidateAssessmentPanelMembers(
                    Name = interviewName,
                    Designation = designation
                ))

                obj.CandidateAssessment?.Recommendation=recommendation
                obj.RecruiterId=CurrentMeetingDataSaver.getData().interviewModel?.recruiterId!!.toString()
                obj.CandidateAssessment?.CandidateName=interviewName
                obj.CandidateAssessment?.Date= context.getCurrentUtcFormatedDate()
                obj.CandidateAssessment?.AppliedPostion=appliedPosition
                obj.CandidateAssessment?.VIDEOCALLACCESSCODE=CurrentMeetingDataSaver.getData().videoAccessCode
                obj.CandidateAssessment?.jobid=CurrentMeetingDataSaver.getData().interviewModel?.jobid
                obj.CandidateAssessmentPanelMembers= memberList




                val result=repo.sendFeedBack(obj)
                Log.d(TAG, "sendFeedback: code ${result.code()}")
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
                            500->{
                                onDataResponse(null,401)
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
            onDataResponse(null,501)
            Log.d(TAG, "getVideoSession: not exception")
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
