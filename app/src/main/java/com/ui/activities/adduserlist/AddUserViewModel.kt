package com.ui.activities.adduserlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.CurrentMeetingDataSaver
import com.data.InvitationDataModel
import com.data.repositoryImpl.BaseRestRepository
import com.domain.BaseModels.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddUserViewModel @Inject constructor(val repo: BaseRestRepository) :ViewModel() {

    val TAG = "adduserviewmodel"
    fun getIsEmailAndPhoneExists(
        interviewId: Int,
        email: String,
        Phone: String,
        response: (isExists: Boolean) -> Unit
    ) {
        try {
            viewModelScope.launch {
                val result = repo.getEmailPhoneExistsDetails(
                    IsEmailPhoneExistsModel(
                        interviewId,
                        email,
                        Phone
                    )
                )

                if (result.isSuccessful) {
                    if (result.body() != null) {
                        response(result.body()!!)
                    }
                    else {
                        Log.d(TAG, "getIsEmailAndPhoneExists: null response ")
                    }
                }
                else {
                    Log.d(TAG, "getIsEmailAndPhoneExists:  response not success")
                }

            }
        } catch (e: Exception) {
            Log.d("adduserexception", "getIsEmailAndPasswordExists: exception  ")
        }
    }

    fun sendInvitationtoUsers(list:List<InvitationDataModel>,onDataResponse:(data:ResponseSendInvitation,action:Int)->Unit)
    {
        CoroutineScope(Dispatchers.IO).launch {
            try {

                val interviewList= arrayListOf<AddInterviewerList>()
                list.forEach {
                    interviewList.add(AddInterviewerList(firstName = it.firstName, lastName = it.lastName, emailId = it.email, contactNumber = it.phone, isPresenter = false))
                }

                val participantObj=AddParticipantModel(MeetingMode = "veriklick",
                    InterviewId = CurrentMeetingDataSaver.getData()?.interviewModel?.interviewId,
                    InterviewDateTime = CurrentMeetingDataSaver.getData().interviewModel?.interviewDateTime,
                    InterviewTitle = CurrentMeetingDataSaver.getData().interviewModel?.interviewTitle,
                    ClientName = CurrentMeetingDataSaver.getData().interviewModel?.clientName,
                    InterviewTimezone = CurrentMeetingDataSaver.getData().interviewTimezone,
                    EventType = "Update",
                    IsVideoRecordEnabled = false,
                    GoogleCalendarSyncEnabled = true,
                    OutlookCalendarSyncEnabled = true,
                    InterviewerList = interviewList
                    )

                val result = repo.sendInvitation(participantObj)
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
            } catch (e: Exception) {
                Log.d(TAG, "getVideoSession: exception ${e.message}")
            }
        }
    }



}