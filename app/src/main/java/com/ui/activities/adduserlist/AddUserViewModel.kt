package com.ui.activities.adduserlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.dataHolders.CurrentMeetingDataSaver
import com.data.dataHolders.CurrentUpcomingMeetingData
import com.data.dataHolders.InvitationDataModel
import com.data.repositoryImpl.BaseRestRepository
import com.domain.BaseModels.*
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun sendInvitationtoUsers(list:List<InvitationDataModel>, onDataResponse:(data:ResponseSendInvitation?, action:Int)->Unit)
    {
        viewModelScope.launch {
            try {
                val interviewList= arrayListOf<AddInterviewerList>()
                list.forEach {
                    interviewList.add(AddInterviewerList(firstName = it.firstName, lastName = it.lastName, emailId = it.email, contactNumber = it.phone, isPresenter = false))//, InterviewerTimezone = it.InterviewerTimezone))
                }

                Log.d(TAG, "sendInvitationtoUsers: viewmodel interview list timezone $interviewList")

                val participantObj=AddParticipantModel(MeetingMode = "veriklick",
                    InterviewId = CurrentMeetingDataSaver.getData()?.interviewModel?.interviewId,
                    InterviewDateTime = CurrentMeetingDataSaver.getData().interviewModel?.interviewDateTime,
                    InterviewTitle = CurrentMeetingDataSaver.getData().interviewModel?.interviewTitle,
                    ClientName = CurrentMeetingDataSaver.getData().interviewModel?.clientName,
                    InterviewTimezone = CurrentMeetingDataSaver.getData().interviewModel?.interviewTimezone,
                    EventType = "Update",
                    CandidateId = 0,
                    IsVideoRecordEnabled = false,
                    GoogleCalendarSyncEnabled = true,
                    OutlookCalendarSyncEnabled = true,
                    InterviewerList = interviewList
                    )

             /*   if (CurrentMeetingDataSaver.getData().interviewModel?.interviewTimezone!=null)
                {
                    participantObj.InterviewTimezone= CurrentMeetingDataSaver.getData().interviewModel?.interviewTimezone
                }else
                {
                    participantObj.InterviewTimezone=CurrentUpcomingMeetingData.getData().interviewTimezone
                }
*/

                Log.d(TAG, "sendInvitationtoUsers: data here $participantObj")

                val result = repo.sendInvitation(participantObj)
                Log.d(TAG, "sendInvitationtoUsers: result ${result.body()}  code ${result.code()}   error ${result.errorBody()}")

                if (result.isSuccessful) {
                    if (result.body() != null) {
                        onDataResponse(result.body()!!, 200)
                        Log.d(TAG, "getVideoSession:  success ${result.body()}   message ${result.body()?.APIResponse?.Message}")
                    }
                    else {
                        onDataResponse(result.body()!!, 400)
                        Log.d(TAG, "getVideoSession: null result")
                    }
                    if (result.code()==200)
                    {
                        onDataResponse(result.body()!!, 200)
                    }
                }
                if (result.code()==500)
                {
                    onDataResponse(result.body()!!, 500)
                }
                else {
                    if (result.code()==500)
                    {
                        onDataResponse(result.body()!!, 500)
                    }

                    onDataResponse(result.body()!!, 404)
                    Log.d(TAG, "getVideoSession: not success")
                }
            } catch (e: Exception) {
                onDataResponse(null, 500)
                Log.d(TAG, "getVideoSession: exception ${e.message}")
            }
        }
    }


    fun getTotoalCountOfInterviewer(videoAccessCode:String,onResponse:(action:Int,data:ResponseTotalInterviewerCount?)->Unit)
    {
        try {
            viewModelScope.launch {
                val result = repo.getTotalCountOfInterViewerInMeeting(videoAccessCode = videoAccessCode)

                if (result.isSuccessful) {
                    if (result.body() != null) {
                        onResponse(200,result.body()!!)
                    }
                    else {
                        onResponse(400,result.body()!!)
                        Log.d(TAG, "getIsEmailAndPhoneExists: null response ")
                    }
                }
                else {
                    onResponse(404,result.body()!!)
                    Log.d(TAG, "getIsEmailAndPhoneExists:  response not success")
                }
            }
        } catch (e: Exception) {
            onResponse(500,null)
            Log.d("adduserexception", "getIsEmailAndPasswordExists: exception  ")
        }

    }



}