package com.ui.activities.meetingmemberslist

import android.util.Log
import androidx.lifecycle.ViewModel
import com.data.exceptionHandler

import com.data.repositoryImpl.BaseRestRepository
import com.domain.BaseModels.BodyLeftUserFromMeeting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemberListViewModel @Inject constructor(val repo: BaseRestRepository): ViewModel() {
val TAG="memberlistviewmodelcheck"

    fun leftUser(participantSid:String,roomSid:String,onDataResponse:(data:BodyLeftUserFromMeeting,status:Int)->Unit)
    {
        try {
            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                val result = repo.leftUserFromMeeting(
                    BodyLeftUserFromMeeting(
                        ParticipantSid = participantSid,
                        RoomSid = roomSid,
                        Status = "disconnected"
                    )
                )
                if (result.isSuccessful) {
                    if (result.body() != null) {
                        onDataResponse(result.body()!!, 200)
                        Log.d(TAG, "getVideoSession:  success ${result.body()}")
                    }
                    else {
                        onDataResponse(result.body()!!, 400)
                        Log.d(TAG, "getVideoSession: null result")
                    }
                }
                else {
                    onDataResponse(result.body()!!, 404)
                    Log.d(TAG, "getVideoSession: not success")
                }
            }
        }catch (e: Exception) {
            Log.d(TAG, "getVideoSession: exception ${e.message}")
        }
    }
}