package com.ui.activities.documentviewer

import android.util.Log
import androidx.lifecycle.ViewModel
import com.data.dataHolders.CurrentMeetingDataSaver
import com.data.repositoryImpl.BaseRestRepository
import com.domain.BaseModels.BodyGetResume
import com.domain.BaseModels.ResponseCandidateDataForIOS
import com.domain.BaseModels.ResponseResumeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocumentViewerViewModel @Inject constructor(val baseRepo: BaseRestRepository) : ViewModel() {
val TAG="docuviewmodelcheck"
    fun getDocument(onDataResponse:(obj:ResponseCandidateDataForIOS,action:Int)->Unit,onResumeResponse:(obj:ResponseResumeModel?,fileName:String,action:Int)->Unit)
    {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val result=baseRepo.getResumeFileName(CurrentMeetingDataSaver.getData().interviewModel?.candidateId!!.toString())
                if (result.isSuccessful)
                {
                    if (result.body()!=null)
                    {
                        onDataResponse(result.body()!!,200)
                        getResume(result.body()?.Candidate?.ResumeFile.toString(),{obj,fileName, action ->
                            when(action)
                            {
                                200->{
                                    onResumeResponse(obj,fileName,200)
                                }
                                400->{
                                    onResumeResponse(obj,fileName,400)
                                }
                                404->
                                {
                                    onResumeResponse(null,fileName,404)
                                }
                            }

                        })
                        Log.d(TAG, "getVideoSession:  success ${result.body()}")
                    }
                    else{
                        onDataResponse(result.body()!!,400)
                        Log.d(TAG, "getVideoSession: null result")
                    }
                }else
                {
                    onDataResponse(result.body()!!,404)
                    Log.d(TAG, "getVideoSession: not success")
                }
            }
        }catch (e:Exception)
        {
            Log.d(TAG, "getVideoSession: not exception")
        }
    }

    fun getResume(fileName:String,onResumeResponse:(obj: ResponseResumeModel?,fileName:String, action:Int)->Unit)
    {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val result=baseRepo.getResume(BodyGetResume(fileName))
                if (result.isSuccessful)
                {
                    if (result.body()!=null)
                    {
                        onResumeResponse(result.body()!!,fileName,200)
                        Log.d(TAG, "getVideoSession:  success ${result.body()}")
                    }
                    else{
                        onResumeResponse(result.body()!!,fileName,400)
                        Log.d(TAG, "getVideoSession: null result")
                    }
                }else
                {
                    onResumeResponse(result.body()!!,fileName,404)
                    Log.d(TAG, "getVideoSession: not success")
                }
            }
        }catch (e:Exception)
        {
            Log.d(TAG, "getVideoSession: not exception")
        }

    }

}