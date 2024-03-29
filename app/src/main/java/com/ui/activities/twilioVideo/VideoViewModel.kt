package com.ui.activities.twilioVideo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.data.dataHolders.CurrentMeetingDataSaver
import com.data.dataHolders.DataStoreHelper
import com.data.dataHolders.VideoRecordingStatusHolder
import com.data.exceptionHandler
import com.data.helpers.TwilioHelper
import com.data.repositoryImpl.RepositoryImpl
import com.domain.BaseModels.*
import com.google.gson.Gson
import com.twilio.video.LocalVideoTrack
import com.twilio.video.RemoteParticipant
import com.twilio.video.VideoTrack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(val repositoryImpl: RepositoryImpl) : ViewModel() {

    val tlist = mutableListOf<VideoTracksBean>()

    private val remoteParticipantVideoListWithCandidate = mutableListOf<VideoTracksBean>()

    val remoteVideoLiveList=MutableLiveData<List<VideoTracksBean>>()

    fun setConnectUser(remoteParticipantVideoList : List<VideoTracksBean>,localVideoTrack:LocalVideoTrack) {
        remoteParticipantVideoListWithCandidate.clear()
        tlist.clear()
        val userDataList = CurrentMeetingDataSaver.getData()

        //current user
        val identityCurrentUser = userDataList?.identity
        Log.d("videocheck", "setConnectUser: remotlist size ${remoteParticipantVideoList.size}")

        remoteParticipantVideoList.forEach {
            Log.d("videocheck", "setConnectUser: remotlist looop ${it.remoteParticipant?.identity}")
        }

        if (CurrentMeetingDataSaver.getData()?.identity!!.contains("C")) {
            Log.d("checkUseris", "setConnectUser: i am candidate")
            Log.d(
                "checkUseris",
                "setConnectUser: i am candidate list size ${remoteParticipantVideoList.size}"
            )
            remoteParticipantVideoList.forEach {
                Log.d(
                    "checkUseris",
                    "setConnectUser: i am Interviewer list candi loop ${it.remoteParticipant?.identity}"
                )
                userDataList?.users?.forEach { user ->

                    Log.d(
                        "setconn",
                        "setConnectUser: each parti ${it.remoteParticipant?.identity}  $identityCurrentUser  ${"I" + user.id}"
                    )

                    val userIdentity = "I" + user.id

                    if (userIdentity.equals(it.remoteParticipant?.identity)) {
                        val isContain = tlist.contains(
                            VideoTracksBean(it.remoteParticipant!!.identity,
                                it.remoteParticipant,
                                it.videoTrack,
                                user.userFirstName + " " + user.userLastName,
                                it.videoSid
                            )
                        )
                        if (isContain) {
                            Log.d("iscontainsvalue", "setConnectUser: contains")
                        }
                        else {
                            Log.d("iscontainsvalue", "setConnectUser: contains not")
                        }
                        remoteParticipantVideoListWithCandidate.add(
                            VideoTracksBean(it.identity,
                                it.remoteParticipant,
                                it.videoTrack,
                                user.userFirstName,
                                it.videoSid
                            )
                        )
                        tlist.add(
                            VideoTracksBean(it.identity,
                                it.remoteParticipant,
                                it.videoTrack,
                                user.userFirstName + " " + user.userLastName,
                                it.videoSid
                            )
                        )
                        Log.d(
                            "setconn",
                            "setConnectUser: you $identityCurrentUser   ${it.remoteParticipant?.identity}"
                        )
                    }
                }
            }
            Log.d("checkUseris", "setConnectUser: i am candidate in size ${tlist.size}")
        }
        else {

            Log.d(
                "checkUseris",
                "setConnectUser: i am Interviewer list size ${remoteParticipantVideoList.size}"
            )

            remoteParticipantVideoList.forEach {
                Log.d(
                    "checkUseris",
                    "setConnectUser: i am Interviewer and id of intervi ${it.remoteParticipant?.identity}"
                )
                userDataList?.users?.forEach { user ->

                    Log.d(
                        "setconn",
                        "setConnectUser: each parti I  ${it.remoteParticipant?.identity}  $identityCurrentUser  ${"I" + user.id}"
                    )

                    val identity = user.userType + user.id

                    if (identity.trim().lowercase()
                            .equals(it.remoteParticipant?.identity?.trim()?.lowercase())
                    ) {

                        if (it.remoteParticipant?.identity!!.contains("C")) {
                            tlist.add(VideoTracksBean(TwilioHelper.getRoomInstance()?.localParticipant?.identity!!,
                                it.remoteParticipant,
                                localVideoTrack!!,
                                "You",
                                it.videoSid
                            )
                            )
                            tlist.add(VideoTracksBean(it.identity,
                                it.remoteParticipant,
                                it.videoTrack,
                                user.userFirstName,
                                it.videoSid
                            )
                            )

                            //test index 0
                            remoteParticipantVideoListWithCandidate.add(
                                VideoTracksBean(it.identity,
                                    it.remoteParticipant,
                                    it.videoTrack!!,
                                    user.userFirstName,
                                    it.videoSid
                                )
                            )
                        }
                        else {

                            tlist.add(
                                VideoTracksBean(it.identity,
                                    it.remoteParticipant,
                                    it.videoTrack!!,
                                    user.userFirstName + " " + user.userLastName,
                                    it.videoSid
                                )
                            )
                            remoteParticipantVideoListWithCandidate.add(
                                VideoTracksBean(it.identity,
                                    it.remoteParticipant,
                                    it.videoTrack!!,
                                    user.userFirstName,
                                    it.videoSid
                                )
                            )
                        }
                        Log.d(
                            "setconn",
                            "setConnectUser: candi ide 1 $identityCurrentUser   ${it.remoteParticipant?.identity}"
                        )
                    }
                }
            }
        }

        remoteVideoLiveList.postValue(tlist)
    }


    var micStatus= MutableLiveData<Boolean>()
    var videoStatus= MutableLiveData<Boolean>()

    fun setMicStatus(mic:Boolean)
    {
        micStatus.postValue(mic)
        //micAndVideoStatuslist.add(0, MicVideoStatusModel(micStatus,videoStatus))
    }

    fun setVideoStatus(video:Boolean)
    {
        videoStatus.postValue(video)
        //micAndVideoStatuslist.add(0, MicVideoStatusModel(micStatus,videoStatus))
    }


    fun getChatToken(
        identity: String,
        response: (data: ResponseChatToken?, code: Int) -> Unit
    ) {
        try {
            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                val result = repositoryImpl.getChatToken(identity)
                if (result.isSuccessful) {
                    if (result.body() != null) {
                        if (result.code() == 200) {
                            response(result.body()!!, result.code())
                        }
                    }
                    else {
                        response(result.body()!!, result.code())
                    }
                }
                else {
                    response(result.body()!!, result.code())
                }

            }

        } catch (e: Exception) {
            response(null, 500)
        }

    }

    var videoTrack=MutableLiveData<CurrentVideoUserModel?>()

    var currentlocalVideoTrack= MutableLiveData<ScreenSharingModel>()
    var currentlocalVideoTrackList= mutableListOf<ScreenSharingModel>()

    fun setLocalVideoTrack(localVideoTrack: LocalVideoTrack,isSharing:Boolean)
    {
        currentlocalVideoTrack.postValue(ScreenSharingModel(localVideoTrack,isSharing))

        currentlocalVideoTrackList.add(0,ScreenSharingModel(localVideoTrack,isSharing))
    }

    fun setCurrentVisibleUser(identity: String,mVideoTrack: VideoTrack?,username:String,type: String)
    {
        videoTrack.postValue(CurrentVideoUserModel(identity,mVideoTrack!!,username,type))
    }

    fun setMuteUnmuteStatus(
        status: Boolean,
        interviewId: String,
        onResult: (action: Int, data: ResponseMuteUmnute?) -> Unit
    ) {
        try {
            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                val result = repositoryImpl.setMuteUnmuteStatus(
                    BodyMuteUmnuteBean(
                        interviewId,
                        status,
                        "",
                        "",
                        true
                    )
                )
                if (result.isSuccessful) {
                    if (result.body() != null) {
                        onResult(200, result.body()!!)
                    }
                    else {
                        onResult(400, result.body()!!)
                    }
                }
                else {
                    onResult(404, result.body()!!)
                }
            }
        } catch (e: Exception) {
            onResult(500, null)
        }
    }



    fun getVideoSessionDetails(videoAccessCode:String,onDataResponse:(data:ResponseInterViewDetailsBean?,response:Int)->Unit)
    {
        try {
            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                val result=repositoryImpl.requestVideoSession(videoAccessCode)
                if (result.isSuccessful)
                {
                    if (result.body()!=null)
                    {
                        when(result.body()?.aPIResponse?.statusCode){
                            404->{
                                onDataResponse(result.body()!!,404)
                            }
                            200->{
                                onDataResponse(result.body(),200)
                            }
                            400->{
                                onDataResponse(result.body(),400)
                            }
                            401->{
                                onDataResponse(result.body(),401)
                            }
                        }
                        //onDataResponse(result.body()!!,200)
                        Log.d(TAG, "getVideoSession:  success ${result.body()}")

                        Log.d("videocon", "getVideoSession:  success ${result.body()?.aPIResponse?.statusCode}")
                    }
                    else{
                        onDataResponse(result.body()!!,400)
                        Log.d(TAG, "getVideoSession: null result")
                    }
                }else
                {
                    onDataResponse(null,404)
                    Log.d(TAG, "getVideoSession: not success")
                }
            }
        }catch (e:Exception)
        {
            onDataResponse(null,404)
            Log.d(TAG, "getVideoSession: not exception")
        }
    }




fun setCandidateJoinedStatus(onResult: (action: Int, data: ResponseCandidateJoinedMeetingStatus?) -> Unit)
    {
        try {
            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                //val ob=BodyCandidateJoinedMeetingStatus(CurrentMeetingDataSaver.getData().videoAccessCode,"Attended",CurrentMeetingDataSaver.getData().interviewModel?.subscriberid!!.toString())
                val ob=BodyCandidateJoinedMeetingStatus(CurrentMeetingDataSaver.getData()?.videoAccessCode,"","")
                Log.d(TAG, "setCandidateJoinedStatus: ${Gson().toJson(ob)}")
                val result = repositoryImpl.setCandidateJoinMeetingStatus(ob)
                if (result.isSuccessful) {
                    if (result.body() != null) {
                        onResult(200, result.body()!!)
                    }
                    else {
                        onResult(400, result.body()!!)
                    }
                }
                else {
                    onResult(404, null)
                }
            }
        } catch (e: Exception) {
            onResult(500, null)
        }
    }







    fun getMuteStatus(
        accessCode: String,
        onResult: (action: Int, data: ResponseMuteUmnute?) -> Unit
    ) {
        try {
            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                val result = repositoryImpl.getMuteStatus(accessCode)
                if (result.isSuccessful) {
                    if (result.body() != null) {
                        when(result.body()?.StatusCode?.toInt())
                        {
                            200->{
                                onResult(200, result.body()!!)
                            }
                            400->{
                                onResult(400, result.body()!!)
                            }
                        }
                    }
                    else {
                        onResult(401, result.body()!!)
                    }
                }
                else {
                    onResult(404, result.body()!!)
                }
            }
        } catch (e: Exception) {
            onResult(500, null)
        }
    }

    fun getScreenSharingStatus(
        accessCode: String,
        onResult: (action: Int, data: ResponseScreenSharingStatus?) -> Unit
    ) {
        try {
            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                val result = repositoryImpl.getScreenSharingStatus(accessCode)
                if (result.isSuccessful) {
                    if (result.body() != null) {
                        when(result.body()?.StatusCode?.toInt())
                        {
                            200->{
                                onResult(200, result.body()!!)
                            }
                            400->{
                                onResult(400, result.body()!!)
                            }
                        }
                    }
                    else {
                        onResult(401, result.body()!!)
                    }
                }
                else {
                    onResult(404, null)
                }
            }
        } catch (e: Exception) {
            onResult(500, null)
        }
    }



    fun setScreenSharingStatus(
        status: Boolean,
        onResult: (action: Int, data: BodyUpdateScreenShareStatus?) -> Unit
    ) {
        try {
            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                val result = repositoryImpl.setScreenSharingStatus(BodyUpdateScreenShareStatus(CurrentMeetingDataSaver.getData()?.videoAccessCode!!,CurrentMeetingDataSaver.getData()?.interviewModel?.interviewId!!.toString(),status,status))
                if (result.isSuccessful) {
                    if (result.body() != null) {

                    }
                    else {
                        onResult(401, null)
                    }
                }
                else {
                    onResult(404, null)
                }
            }
        } catch (e: Exception) {
            onResult(500, null)
        }
    }

    private var isEntered=false
    fun setIsCandidateEnterMeeting(value:Boolean)
    {
        isEntered=value
    }
    fun getIsCandidateEnteredMeeting()=isEntered


    fun getRecordingStatusUpdate(
        onResult: (action: Int, data: BodyUpdateRecordingStatus?) -> Unit
    ) {
        try {
            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                val result = repositoryImpl.getRecordingStatusUpdate(
                    BodyUpdateRecordingStatus(
                        CurrentMeetingDataSaver.getData()?.interviewModel?.interviewId!!,
                        CurrentMeetingDataSaver.getRoomData().roomName!!,
                        VideoRecordingStatusHolder.setStatus(),
                        "200",
                        "recStart or not"
                    )
                )

//CurrentMeetingDataSaver.getData().videoAccessCode!!
                if (result.isSuccessful) {
                    if (result.body() != null) {
                        onResult(200, result.body()!!)
                    }
                    else {
                        onResult(400, result.body()!!)
                    }
                }
                else {
                    onResult(404, result.body()!!)
                }
            }
        } catch (e: Exception) {
            onResult(500, null)
        }
    }


    private val TAG="videoViewModelCheck"
      fun endVideoCall()
      {
          try {
              CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                  val result=repositoryImpl.closeMeeting(BodyMeetingClose(CurrentMeetingDataSaver.getRoomData().roomName, RoomStatus = "completed"))

                  if (result.isSuccessful) {
                      if (result.body() != null) {
                          Log.d(TAG, "endVideoCall: success response ")
                      }
                      else {
                          Log.d(TAG, "endVideoCall: success null ")
                      }
                  }
                  else {
                      Log.d(TAG, "endVideoCall: not success response ")
                  }
              }
          } catch (e: Exception) {
              Log.d(TAG, "endVideoCall: exception ${e.message} ")
          }


      }



}
