package com.data.application

import android.app.Application
import com.data.dataHolders.DataStoreHelper
//import com.data.twiliochattemp.ChatClientWrapper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication :Application() {
    override fun onCreate() {
        super.onCreate()
        DataStoreHelper.getInstance(this)
        //ChatClientWrapper.createInstance()
    }
}