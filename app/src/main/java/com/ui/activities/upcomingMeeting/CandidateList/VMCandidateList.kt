package com.ui.activities.upcomingMeeting.CandidateList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.data.exceptionHandler
import com.domain.BaseModels.BodyCandidateList
import com.domain.BaseModels.ResponseCandidateList
import com.domain.RestApi.BaseRestApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class VMCandidateList @Inject constructor(val baseRestApi: BaseRestApi) : ViewModel() {

    private var candidateListMutable = MutableLiveData<ResponseCandidateList>()
    var candidateListLive: LiveData<ResponseCandidateList>? = null

    fun getCandidateList(ob: BodyCandidateList, recid: String, subsId: String,error:(isError:Boolean,errorCode:Int,msg:String)->Unit) {
        CoroutineScope(Dispatchers.IO + exceptionHandler)
            .launch {
                try {
                    val response = baseRestApi.getCandidateList(ob, recid, subsId)
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                error(false,200,"")
                                candidateListMutable.postValue(response.body())
                            }
                            401 -> {
                                error(true,401,"")
                            }
                            400 -> {
                                error(true,400,"")
                            }
                            500 -> {
                                error(true,500,"")
                            }
                        }
                    } else {
                        error(true,501,"")
                    }
                } catch (e: Exception) {
                    error(true,502,e.message.toString())
                }
            }
    }


}