package com.data.dataHolders

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.createDataStore
import com.data.decodeLoginToken
import com.data.exceptionHandler
import com.domain.constant.AppConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


//var mutablepre: MutablePreferences?=null
lateinit var dataStore:DataStore<Preferences>

object DataStoreHelper {

    fun getInstance(context:Context)
    {
        dataStore =context.createDataStore(AppConstants.USER_DATA_STORE_KEY)
    }

    fun setDeepLinkData(isOpened:Boolean)
    {
            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                val tokenKey = preferencesKey<Boolean>(AppConstants.DEEPLINK_SETTING_OPEN)
                dataStore.edit {
                    it[tokenKey] = isOpened
                }
            }
    }
    suspend fun getDeeplinkIsOpenStatus():Boolean{
        return dataStore.data.first()[preferencesKey<Boolean>(AppConstants.DEEPLINK_SETTING_OPEN)]!!
    }

    fun insertValue(email:String,psswd:String)
    {
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val emailkey= preferencesKey<String>(AppConstants.USER_EMAIL_ID)
            dataStore.edit {
                it[emailkey]=email
            }
            val psswdkey= preferencesKey<String>(AppConstants.USER_PASSWORD)
            dataStore.edit {
                it[psswdkey]=psswd
            }
        }
    }

    fun setToken(token:String)
    {
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val tokenKey= preferencesKey<String>(AppConstants.USER_LOGIN_TOKEN)
            dataStore.edit {
                it[tokenKey]=token
            }
        }
    }

    fun setLoggedInWithOtp(isLoggedOtp:Boolean)
    {
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val tokenKey= preferencesKey<Boolean>(AppConstants.LOGGED_WITH_OTP)
            dataStore.edit {
                it[tokenKey]=isLoggedOtp
            }
        }
    }

    suspend fun getLoggedInStatus():Boolean{
        return dataStore.data.first()[preferencesKey<Boolean>(AppConstants.LOGGED_WITH_OTP)].toString().toBoolean()
    }



    suspend fun getLoginToken():String{
        return dataStore.data.first()[preferencesKey<String>(AppConstants.USER_LOGIN_TOKEN)].toString()
    }

    fun setAccessCode(code:String)
    {
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val accessCode= preferencesKey<String>(AppConstants.LOGGED_USER_ACCESSCODE)
            dataStore.edit {
                it[accessCode]=code
            }
        }
    }

    fun setAppLanguage(code:String)
    {
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val accessCode= preferencesKey<String>(AppConstants.APPLICATION_LANGUAGE)
            dataStore.edit {
                it[accessCode]=code
            }
        }
    }

    suspend fun getAppLanguage():String{
        return dataStore.data.first()[preferencesKey<String>(AppConstants.APPLICATION_LANGUAGE)].toString()
    }

    fun getAccessCode(code:(String)->Unit)
    {
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
         code( dataStore.data.first()[preferencesKey<String>(AppConstants.LOGGED_USER_ACCESSCODE)].toString())
        }
    }

    suspend fun getLoginBearerToken():String{
        return "Bearer "+ dataStore.data.first()[preferencesKey<String>(AppConstants.USER_LOGIN_TOKEN)].toString()
    }

    suspend fun getLoginAuthToken():String{
        return dataStore.data.first()[preferencesKey<String>(AppConstants.USER_LOGIN_TOKEN)].toString()
    }

    suspend fun getUserEmail():String{
        return dataStore.data.first()[preferencesKey<String>(AppConstants.USER_EMAIL_ID)].toString()
    }

    suspend fun getLoggedUserData(){
        decodeLoginToken(getLoginToken(), response = { response ->
            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                setMeetingRecruiterAndUserIds(response?.UserId.toString(),response?.CreatedBy.toString())
            }
        })
    }

    suspend  fun getPassword():String{
        return dataStore.data.first()[preferencesKey<String>(AppConstants.USER_PASSWORD)].toString()
    }

    fun getDataStore()= dataStore

    fun clearData()
    {
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            dataStore.edit {
                it.clear()
            }
        }
    }

    suspend fun setMeetingRecruiterAndUserIds(recruiter:String , userId:String)
    {
        val recruiterid= preferencesKey<String>(AppConstants.MEETING_RECRUITER_ID)
        val userid= preferencesKey<String>(AppConstants.MEETING_USER_ID)
        dataStore.edit {
            it[recruiterid]=recruiter
        }
        dataStore.edit {
            it[userid]=userId
        }
    }

    suspend fun getMeetingRecruiterid()= dataStore.data.first()[preferencesKey<String>(AppConstants.MEETING_RECRUITER_ID)].toString()
    suspend fun getMeetingUserId()= dataStore.data.first()[preferencesKey<String>(AppConstants.MEETING_USER_ID)].toString()

}