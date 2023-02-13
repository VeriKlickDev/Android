package com.data.daggermodule

import android.content.Context
import androidx.lifecycle.ViewModel
import com.data.dismissProgressDialog
import com.data.showCustomSnackbarOnTop
import com.veriKlick.R
import kotlinx.coroutines.CoroutineExceptionHandler


open class BaseViewModel constructor(val context: Context) : ViewModel(){
    protected val exceptionHandler = CoroutineExceptionHandler{_, exception->
       // Timber.tag("API_CALL_ERROR").e(exception)
        context.dismissProgressDialog()
        context.showCustomSnackbarOnTop(exception.message?:context.getString(R.string.txt_something_went_wrong))
        //context.showToast(exception.message?:context.getString(R.string.msg_something_went_wrong))
    }
}