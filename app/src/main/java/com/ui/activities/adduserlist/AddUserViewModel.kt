package com.ui.activities.adduserlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.repositoryImpl.BaseRestRepository
import com.domain.BaseModels.IsEmailPhoneExistsModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddUserViewModel @Inject constructor(val baseRepo: BaseRestRepository) :ViewModel() {

    val TAG="adduserviewmodel"
    fun getIsEmailAndPhoneExists(interviewId:Int,email:String ,Phone:String,response:(isExists:Boolean)->Unit)
    {
        try {
            viewModelScope.launch {
                val result = baseRepo.getEmailPhoneExistsDetails(IsEmailPhoneExistsModel(interviewId,email,Phone))

             if (result.isSuccessful)
             {
                 if(result.body()!=null)
                 {
                     response(result.body()!!)
                 }else
                 {
                     Log.d(TAG, "getIsEmailAndPhoneExists: null response ")
                 }
             }else
             {
                 Log.d(TAG, "getIsEmailAndPhoneExists:  response not success")
             }

            }
        }catch (e:Exception)
        {
            Log.d("adduserexception", "getIsEmailAndPasswordExists: exception  ")
        }
    }



}