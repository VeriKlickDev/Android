package com.ui.activities.twilioVideo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.TwilioChatHelper
import com.data.repositoryImpl.RepositoryImpl
import com.domain.BaseModels.BodyMuteUmnuteBean
import com.domain.BaseModels.BodyUpdateRecordingStatus
import com.domain.BaseModels.ResponseChatToken
import com.domain.BaseModels.ResponseMuteUmnute
import com.twilio.conversations.CallbackListener
import com.twilio.conversations.Conversation
import com.twilio.conversations.ConversationsClient
import com.twilio.conversations.ErrorInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(val repositoryImpl: RepositoryImpl) : ViewModel() {

    fun getChatToken(identity:String,response:(data: ResponseChatToken?,code:Int)->Unit) {
        try {
            viewModelScope.launch {
            val result= repositoryImpl.getChatToken(identity)
                if (result.isSuccessful)
                {
                    if (result.body()!=null)
                    {
                        if (result.code()==200)
                        {
                            response(result.body()!!,result.code())
                        }
                    }else
                    {
                        response(result.body()!!,result.code())
                    }
                }else{
                    response(result.body()!!,result.code())
                }

            }

        }catch (e:Exception)
        {
            response(null,500)
        }

    }


    fun  setMuteUnmuteStatus(status:Boolean,interviewId:String,onResult:(action:Int,data: ResponseMuteUmnute?)->Unit)
    {
        try {
            viewModelScope.launch {
                val result =repositoryImpl.setMuteUnmuteStatus(BodyMuteUmnuteBean(interviewId,status,"","",true))
                if (result.isSuccessful)
                {
                    if(result.body()!=null){
                        onResult(200,result.body()!!)
                    }else
                    {
                        onResult(400,result.body()!!)
                    }
                }else
                {
                    onResult(404,result.body()!!)
                }
            }
        }catch (e:Exception)
        {
            onResult(500,null)
        }
    }

    fun  getMuteStatus(accessCode:String,onResult:(action:Int,data: ResponseMuteUmnute?)->Unit)
    {
        try {
            viewModelScope.launch {
                val result =repositoryImpl.getMuteStatus(accessCode)
                if (result.isSuccessful)
                {
                    if(result.body()!=null){
                        onResult(200,result.body()!!)
                    }else
                    {
                        onResult(400,result.body()!!)
                    }
                }else
                {
                    onResult(404,result.body()!!)
                }
            }
        }catch (e:Exception)
        {
            onResult(500,null)
        }
    }


    fun  getRecordingStatusUpdate(interviewId: Int,roomSid:String,recStatus:String,statusCode:String,message:String,accessCode:String,onResult:(action:Int,data: BodyUpdateRecordingStatus?)->Unit)
    {
        try {
            viewModelScope.launch {
                val result =repositoryImpl.getRecordingStatusUpdate(BodyUpdateRecordingStatus(interviewId,roomSid,recStatus,statusCode,message))
                if (result.isSuccessful)
                {
                    if(result.body()!=null){
                        onResult(200,result.body()!!)
                    }else
                    {
                        onResult(400,result.body()!!)
                    }
                }else
                {
                    onResult(404,result.body()!!)
                }
            }
        }catch (e:Exception)
        {
            onResult(500,null)
        }
    }







}