package com.ui.activities.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.data.exceptionHandler
import com.data.repositoryImpl.BaseRestRepository
import com.domain.BaseModels.*
import com.domain.RestApi.LoginRestApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val baserepo: BaseRestRepository, val loginRestApi: LoginRestApi) :ViewModel() {

    val TAG="token"

    fun getVideoSession(videoAccessCode:String)
    {
        try {
            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                val result = baserepo.getTwilioVideoTokenHost(videoAccessCode)
                if (result.isSuccessful) {
                    if (result.body() != null) {

                        Log.d(TAG, "getVideoSession:  success ${result.body()}")
                    }
                    else {
                        Log.d(TAG, "getVideoSession: null result")
                    }
                }
                else {
                    Log.d(TAG, "getVideoSession: not success")
                }
            }
        }catch (e:Exception)
        {

        }
    }

    fun login(email:String,password:String,response:(result:Int,data: LoginResponseBean,exception:String?)->Unit,actionProgress:(action:Int)->Unit) {
        try {
            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                actionProgress(1)
                val result = loginRestApi?.login(BodyLoginBean( userName = email, password = password, RecruiterEmail = email))
                if (result?.isSuccessful!!) {
                    Log.d(TAG, "getVideoSession:  successfull result ${result.body()}")
                    if (result?.body() != null) {
                        Log.d(TAG, "getVideoSession:  not null ${result.body()}")
                        actionProgress(0)
                        if (result.body()?.success == true)
                        {
                            actionProgress(0)
                            response(200, result.body()!!,null)
                            Log.d(TAG, "getVideoSession:  success ${result.body()}")
                        }else
                        {
                            actionProgress(0)
                            response(401, result.body()!!,null)
                        }
                        if (result.code()==500)
                        {
                            response(500, result.body()!!,null)
                        }
                    }
                    else {
                        actionProgress(0)
                        response(400, result.body()!!,null)
                        Log.d(TAG, "getVideoSession: null result")
                    }
                }
                else {
                    actionProgress(0)
                    response(404, LoginResponseBean(),null)
                    Log.d(TAG, "getVideoSession: not success")
                }
            }
        }catch (e:Exception)
        {
            Log.d(TAG, "getVideoSession: exception ${e.printStackTrace()}")
            actionProgress(0)
            response(500, LoginResponseBean(),e.printStackTrace().toString())
        }
    }



}