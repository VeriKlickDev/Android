package com.data.dataHolders

import androidx.lifecycle.MutableLiveData



object AddParticipantsErrorChecker {
    var isError=MutableLiveData<List<Boolean>>()



var firstnameError=false
    var lastnameError=false
    var emailerror=false
    var phoneerror=false
    fun setFirstNameError(sts:Boolean)
    {
        firstnameError=sts
        val list= mutableListOf<Boolean>()
        list.add(0,sts)
        isError.postValue(list)
    }

    fun setLastNameError(sts:Boolean)
    {
    lastnameError=sts
        val list= mutableListOf<Boolean>()
        list.add(1,sts)
        isError.postValue(list)
    }

    fun setEmailError(sts:Boolean)
    {
        emailerror=sts
        val list= mutableListOf<Boolean>()
        list.add(2,sts)
        isError.postValue(list)
    }

    fun setPhoneError(sts:Boolean)
    {
        phoneerror=sts
        val list= mutableListOf<Boolean>()
        list.add(3,sts)
        isError.postValue(list)
    }


}