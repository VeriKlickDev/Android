package com.data.dataHolders

import androidx.lifecycle.MutableLiveData


private var isCallInProgress=MutableLiveData<Boolean>()
object CallStatusHolder {

    fun setCallInprogressStatus(status:Boolean)
    {
        isCallInProgress.postValue(status)
    }

    fun getCallStatus()= isCallInProgress


}