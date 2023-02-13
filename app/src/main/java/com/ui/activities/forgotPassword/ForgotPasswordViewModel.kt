package com.ui.activities.forgotPassword

import android.util.Log
import androidx.lifecycle.ViewModel
import com.data.exceptionHandler
import com.domain.BaseModels.BodyForgotPasswordBean
import com.domain.BaseModels.BodyLoginBean
import com.domain.BaseModels.LoginData
import com.domain.BaseModels.ResponseForgotPassword
import com.domain.RestApi.BaseRestApi
import com.domain.RestApi.LoginRestApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(val loginRestApi: LoginRestApi,val baseRestApi: BaseRestApi) :ViewModel() {

    val TAG="forgotpassword"

    fun sendMailForForgotPassword(email:String, response:(result:Int, data: ResponseForgotPassword, exception:String?)->Unit, actionProgress:(action:Int)->Unit) {

        try {
            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                actionProgress(1)
                val result = loginRestApi?.sendMailForforgotPassword(BodyForgotPasswordBean(  email))
                if (result?.isSuccessful!!) {
                    Log.d(TAG, "getVideoSession:  successfull result ${result.body()}")
                    if (result?.body() != null) {
                        Log.d(TAG, "getVideoSession:  not null ${result.body()}")
                        actionProgress(0)
                        if (result.body()?.success == true)
                        {
                            actionProgress(0)
                            response(200,result.body()!!,null)
                            Log.d(TAG, "getVideoSession:  success ${result.body()}")
                        }else
                        {
                            actionProgress(0)
                            response(401, ResponseForgotPassword(false,result.body()!!.message.toString(),null,result.errorBody().toString()),null)
                        }
                    }
                    else {
                        actionProgress(0)
                        response(400, ResponseForgotPassword(false,result.message(),null,result.errorBody().toString()),null)
                        Log.d(TAG, "getVideoSession: null result")
                    }
                }
                else {
                    actionProgress(0)
                    response(404,ResponseForgotPassword(false,result.message(),null,result.errorBody().toString()),null)
                    Log.d(TAG, "getVideoSession: not success")
                }
            }
        }catch (e:Exception)
        {
            Log.d(TAG, "getVideoSession: exception ${e.printStackTrace()}")
            actionProgress(0)
            response(500,ResponseForgotPassword(false,null,null,null ),e.printStackTrace().toString())
        }
    }

}