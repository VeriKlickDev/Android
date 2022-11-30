package com.data.dataHolders

import androidx.lifecycle.MutableLiveData


private  var navigateFrom=""
private  var isCallExists=false
private var isCallInProgress=MutableLiveData<Boolean>()
private var isCalloccur=false
object CallStatusHolder {

    fun setCallInprogressStatus(status:Boolean)
    {
        isCallInProgress.postValue(status)
        if (status)
        {
            isCalloccur=true
        }
    }

    fun getCallStatus()= isCallInProgress

    fun setCallonResumeFalse()
    {
        isCalloccur=false
    }

    fun checkCallOnResume()= isCalloccur


    fun setNavigateData(str:String)
    {
        navigateFrom=str
    }
    fun getNavigateData()= navigateFrom

    fun setLastCallStatus(s:Boolean)
    {
        isCallExists= s
    }
    fun getLastCallStatus()= isCallExists


}

