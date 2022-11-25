package com.data.dataHolders

import androidx.lifecycle.MutableLiveData


private  var navigateFrom=""
private  var isCallExists=false
private var isCallInProgress=MutableLiveData<Boolean>()
object CallStatusHolder {

    fun setCallInprogressStatus(status:Boolean)
    {
        isCallInProgress.postValue(status)
    }

    fun getCallStatus()= isCallInProgress


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

