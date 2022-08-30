package com.app.wavespodcast.api.model.response

import com.data.Status


/**
 * Helper Data Class for handling Api's Output
 * @param status Status of output
 * @param data Data value of output
 * @param message Status message of output*/
data class ResultApi<out T>(
    val success:Boolean,
    val message :String?,
    val data:T?,
    val errorMessage: String?,
    val status: Status?
) {
    companion object {
        fun <T> success(data:T):ResultApi<T> = ResultApi(status = Status.SUCCESS,data = data, message=null, success = true, errorMessage = "")
        fun <T> failed(data:T?, msg: String?):ResultApi<T> = ResultApi(status = Status.FAILED, data = data, message = msg,success = false, errorMessage = "")
        fun <T> processing(data:T?):ResultApi<T> = ResultApi(status = Status.PROCESSING, data = data, message = null,success = false, errorMessage = "")
    }
}