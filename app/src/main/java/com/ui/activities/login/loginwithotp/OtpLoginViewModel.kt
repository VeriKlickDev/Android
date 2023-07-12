package com.ui.activities.login.loginwithotp

import android.util.Log
import androidx.lifecycle.ViewModel
import com.data.exceptionHandler
import com.data.repositoryImpl.RepositoryImpl
import com.domain.BaseModels.BodyOtpVerification
import com.domain.BaseModels.BodyOtpVerificationStatus
import com.domain.BaseModels.ResponseOtpVerification
import com.domain.BaseModels.ResponseOtpVerificationStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpLoginViewModel @Inject constructor(val repo: RepositoryImpl) : ViewModel() {

private val TAG="otpviewModelCheck"

    fun sendOtp(email:String, response:(result:Int, data: ResponseOtpVerification, exception:String?)->Unit, actionProgress:(action:Int)->Unit) {
        try {
            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                actionProgress(1)

                val result = repo?.sendOtpToEmailVerificationWithType(email,"Login")

                if (result?.isSuccessful!!) {
                    actionProgress(0)
                    Log.d(TAG, "getVideoSession:  successfull result ${result.body()}")
                    Log.d(TAG, "getVideoSession:  successfull result ${result.body()?.Message}")
                    if (result?.body() != null) {
                        Log.d(TAG, "getVideoSession:  not null ${result.body()}")

                        if (result.body()?.Success == true)
                        {
                            when(result.body()?.StatusCode!!.toInt())
                            {
                                200->{
                                    response(200, result.body()!!,null)
                                }
                                400->{
                                    response(400, result.body()!!,null)
                                }
                            }
                            actionProgress(0)

                            Log.d(TAG, "getVideoSession:  success ${result.body()}")
                        }else
                        {
                            actionProgress(0)
                            response(401, result.body()!!,null)
                        }
                    }
                    else {actionProgress(0)
                        response(401, result.body()!!,null)
                        Log.d(TAG, "getVideoSession: null result ${result.body()}")
                    }
                }
                else {
                    actionProgress(0)
                    response(404, ResponseOtpVerification(),null)
                    Log.d(TAG, "getVideoSession: not success ${result.body()?.Message}")
                }
            }
        }catch (e:Exception)
        {
            Log.d(TAG, "getVideoSession: exception ${e.printStackTrace()}")
            actionProgress(0)
            response(500,  ResponseOtpVerification(),e.printStackTrace().toString())
        }
    }


    fun verifyOtp(email:String, otp:String, response:(result:Int, data: ResponseOtpVerificationStatus, exception:String?)->Unit, actionProgress:(action:Int)->Unit) {
        try {
            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                actionProgress(1)


                val result = repo?.getOtpVerificationStatus(BodyOtpVerificationStatus(InterViewerEmail = email, OTP = otp))
                if (result?.isSuccessful!!) {
                    Log.d(TAG, "getVideoSession:  successfull result ${result.body()}")
                    if (result?.body() != null) {
                        Log.d(TAG, "getVideoSession:  not null ${result.body()}")
                        actionProgress(0)

                            actionProgress(0)
                            response(200, result.body()!!,null)
                            Log.d(TAG, "getVideoSession:  success ${result.body()}")
                    }
                    else {
                        actionProgress(0)
                        response(400, result.body()!!,null)
                        Log.d(TAG, "getVideoSession: null result ${result.body()}")
                    }
                }
                else {
                    actionProgress(0)
                    response(404, ResponseOtpVerificationStatus(),null)
                    Log.d(TAG, "getVideoSession: not success")
                }
            }
        }catch (e:Exception)
        {
            Log.d(TAG, "getVideoSession: exception ${e.printStackTrace()}")
            actionProgress(0)
            response(500,  ResponseOtpVerificationStatus(),e.printStackTrace().toString())
        }
    }





}