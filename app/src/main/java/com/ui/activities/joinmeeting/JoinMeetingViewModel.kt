package com.ui.activities.joinmeeting

import android.util.Log
import androidx.lifecycle.ViewModel
import com.data.repositoryImpl.BaseRestRepository
import com.domain.BaseModels.ResponseInterViewDetailsBean
import com.domain.BaseModels.TokenResponseBean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinMeetingViewModel @Inject constructor(val repo: BaseRestRepository) :ViewModel() {

    val TAG="token"

    fun getVideoSessionHost(videoAccessCode:String, onDataResponse:(data:TokenResponseBean, response:Int)->Unit)
    {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val result=repo.getTwilioVideoTokenHost(videoAccessCode)
                if (result.isSuccessful)
                {
                    if (result.body()!=null)
                    {
                        onDataResponse(result.body()!!,200)
                        Log.d(TAG, "getVideoSession:  success ${result.body()}")
                    }
                    else{
                        onDataResponse(result.body()!!,400)
                        Log.d(TAG, "getVideoSession: null result")
                    }
                }else
                {
                    onDataResponse(result.body()!!,404)
                    Log.d(TAG, "getVideoSession: not success")
                }
            }
        }catch (e:Exception)
        {
            Log.d(TAG, "getVideoSession: not exception")
        }
    }


    fun getVideoSessionCandidate(videoAccessCode:String, onDataResponse:(data:TokenResponseBean, response:Int)->Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {

                val result = repo.getTwilioVideoTokenCandidate(videoAccessCode)
                if (result.isSuccessful) {
                    if (result.body() != null) {
                        onDataResponse(result.body()!!, 200)
                        Log.d(TAG, "getVideoSession:  success ${result.body()}")
                    }
                    else {
                        onDataResponse(result.body()!!, 400)
                        Log.d(TAG, "getVideoSession: null result")
                    }
                }
                else {
                    onDataResponse(result.body()!!, 404)
                    Log.d(TAG, "getVideoSession: not success")
                }
            } catch (e: Exception) {
                Log.d(TAG, "getVideoSession: exception ${e.message}")
            }
        }
    }


    fun getVideoSessionDetails(videoAccessCode:String,onDataResponse:(data:ResponseInterViewDetailsBean?,response:Int)->Unit)
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
                    onDataResponse(result.body()!!,404)
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

/* flow<Response<TokenResponseBean>>{
            repo.getTwilioVideoTokenCandidate(videoAccessCode)
        }.map {result1->
            if (result1.isSuccessful)
            {
                ""
            }
            ""
        }.map {result2->
            repo.getTwilioVideoTokenCandidate(result2)
        } .map {
                repo.getTwilioVideoTokenCandidate(result2)
            }
       */