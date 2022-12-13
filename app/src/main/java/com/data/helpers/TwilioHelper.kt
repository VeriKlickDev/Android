package com.data.helpers

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.data.dataHolders.CurrentConnectUserList
import com.data.dataHolders.UpcomingMeetingStatusHolder
import com.data.twiliochat.TwilioChatHelper
import com.domain.BaseModels.MicStatusModel
import com.domain.BaseModels.NetworkQualityModel
import com.example.twillioproject.databinding.ActivityTwilioVideoBinding
import com.twilio.video.*
import com.twilio.video.Video.connect
import com.ui.activities.twilioVideo.VideoActivity


//implement in your Main activity
private lateinit var mtwilioVideoRoomCallBack: RoomListenerCallback
private lateinit var listener: RoomParticipantListener

private val TAG = "roomConnect"
private lateinit var connectOption: ConnectOptions
private lateinit var connectOptionBuilder: ConnectOptions.Builder
private var room: Room? = null
private var participantIdentity: String? = null
private var token:String?=null
private var roomName:String?=null

object TwilioHelper {

    fun setTwilioCredentials(
        mtoken: String,
        mroomName: String,
    ) {
        token =mtoken
        roomName =mroomName
    }

    fun connectToRoom(
        context: Context,
        onRoomEvent: (action: String) -> Unit,
        twilioVideoRoomCallBack: RoomListenerCallback
        ,localAudioTrack: LocalAudioTrack,
        localVideoTrack: LocalVideoTrack,
        audioCodec: AudioCodec,
        videoCodec: VideoCodec
    ) :Room {
        listener =context as VideoActivity
        connectOptionBuilder = ConnectOptions.Builder(token!!)
            .audioTracks(listOf(localAudioTrack))
            .videoTracks(listOf(localVideoTrack))
            .preferAudioCodecs(listOf(audioCodec))
            .preferVideoCodecs(listOf(videoCodec))
            .enableAutomaticSubscription(true)
            .enableNetworkQuality(true)
            .networkQualityConfiguration(configuration)

        connectOption = connectOptionBuilder.roomName(roomName!!).build()

        room = connect(context,connectOption, roomListener)
        mtwilioVideoRoomCallBack = twilioVideoRoomCallBack
        return room!!
    }
    var configuration = NetworkQualityConfiguration(
        NetworkQualityVerbosity.NETWORK_QUALITY_VERBOSITY_MINIMAL,
        NetworkQualityVerbosity.NETWORK_QUALITY_VERBOSITY_MINIMAL
    )

    fun disConnectRoom() {
        room?.disconnect()
        room = null
        UpcomingMeetingStatusHolder.isMeetingFinished(true)

        CurrentConnectUserList.clearList()
        TwilioChatHelper.removeCallBacks()
        TwilioChatHelper.clearChatList()



    }

    fun setLocalParticipantListener(localParticipant: LocalParticipant)
    {
        localParticipant.setListener(localParticipantListener)
    }

    fun getRoomInstance()= room

    fun publishVideoTrack(localVideoTrack: LocalVideoTrack)
    {
        room?.localParticipant?.publishTrack(localVideoTrack)
    }

    fun unPublishVideoTrack(localVideoTrack: LocalVideoTrack)
    {
        room?.localParticipant?.unpublishTrack(localVideoTrack)
    }

    fun removeCallBack()
    {

    }

    fun setRemoteParticipantListener(participant: RemoteParticipant)
    {
        participant.setListener(participantListener)
    }

    fun setLocalParticipantListener(room: Room)
    {

    }

    private val micLiveData=MutableLiveData<MicStatusModel>()
    fun getMicStatusLive()= micLiveData

    private val networkStatusLiveLocal=MutableLiveData<NetworkQualityModel>()
    fun getNetWorkQualityLevelLocal()= networkStatusLiveLocal

    private val networkStatusLive=MutableLiveData<NetworkQualityModel>()
    fun getNetWorkQualityLevel()= networkStatusLive

    //val videoTrackList= mutableListOf<VideoTrackPublication>()

    fun addRemoteParticipant(remoteParticipant: RemoteParticipant,
                             binding: ActivityTwilioVideoBinding,onAddParticipant:(remoteVideoTrack:RemoteVideoTrack)->Unit
    )
    {
        Log.d("joinuser", "addRemoteParticipant method: method helper ")
        /*
         * This app only displays video for one additional participant per Room
         */
        /* if (thumbnailVideoView.visibility == View.VISIBLE) {
             Log.d("joinuser", "addRemoteParticipant method: multiple not allowed ")
             return
         }*/
        participantIdentity = remoteParticipant.identity
        binding.videoStatusTextview.text = "Participant $participantIdentity joined"
        Log.d("joinuser", "addRemoteParticipant method: Participant ${remoteParticipant.remoteVideoTracks.size} joined")


        /*
        * Add participant renderer
        */
        remoteParticipant.remoteVideoTracks.firstOrNull()?.let { remoteVideoTrackPublication ->
            if (remoteVideoTrackPublication.isTrackSubscribed) {

                Log.d("joinuser", "addRemoteParticipant method: track subscribed ")
                remoteVideoTrackPublication.remoteVideoTrack?.let {
                  //  videoTrackList.add(remoteVideoTrackPublication)
                    onAddParticipant(it)

                    Log.d("joinuser", "addRemoteParticipant method: add participant ")
                }
            }
        }

        remoteParticipant.setListener(participantListener)
    }


    fun removeRemoteParticipant(remoteParticipant: RemoteParticipant,binding:ActivityTwilioVideoBinding,onRemoveParticipant:(videoTrack: VideoTrack)->Unit) {
        binding.videoStatusTextview.text = "Participant $remoteParticipant.identity left."
        if (remoteParticipant.identity != participantIdentity) {
            return
        }

        /*
         * Remove participant renderer
         */
        remoteParticipant.remoteVideoTracks.firstOrNull()?.let { remoteVideoTrackPublication ->
            if (remoteVideoTrackPublication.isTrackSubscribed) {
                remoteVideoTrackPublication.remoteVideoTrack?.let { onRemoveParticipant(it) }
            }
        }
    }


    private val localParticipantListener=object: LocalParticipant.Listener{
        override fun onAudioTrackPublished(
            localParticipant: LocalParticipant,
            localAudioTrackPublication: LocalAudioTrackPublication
        ) {
            
        }

        override fun onAudioTrackPublicationFailed(
            localParticipant: LocalParticipant,
            localAudioTrack: LocalAudioTrack,
            twilioException: TwilioException
        ) {
            
        }

        override fun onVideoTrackPublished(
            localParticipant: LocalParticipant,
            localVideoTrackPublication: LocalVideoTrackPublication
        ) {
            
        }

        override fun onVideoTrackPublicationFailed(
            localParticipant: LocalParticipant,
            localVideoTrack: LocalVideoTrack,
            twilioException: TwilioException
        ) {
            
        }

        override fun onDataTrackPublished(
            localParticipant: LocalParticipant,
            localDataTrackPublication: LocalDataTrackPublication
        ) {
            
        }

        override fun onDataTrackPublicationFailed(
            localParticipant: LocalParticipant,
            localDataTrack: LocalDataTrack,
            twilioException: TwilioException
        ) {
            
        }

        override fun onNetworkQualityLevelChanged(
            localParticipant: LocalParticipant,
            networkQualityLevel: NetworkQualityLevel
        ) {
            super.onNetworkQualityLevelChanged(localParticipant, networkQualityLevel)
            networkStatusLiveLocal.postValue(NetworkQualityModel(localParticipant.identity,networkQualityLevel))
        }
    }



    private val roomListener = object : Room.Listener {



        override fun onConnected(room: Room) {
            try {
                Log.d(TAG, "onConnected: connect room ")
                mtwilioVideoRoomCallBack.onParticipantConnect(room)
                // localParticipant = room.localParticipant
                // binding.videoStatusTextview.text = "Connected to ${room.name}"
                // title = room.name

                // Only one participant is supported
                // room.remoteParticipants.firstOrNull()?.let { addRemoteParticipant(it) }
            } catch (e: Exception) {
                Log.d(TAG, "onConnected: exception on connected listener ${e.printStackTrace()} ")
            }
        }

        override fun onReconnected(room: Room) {
            mtwilioVideoRoomCallBack.onParticipantReconnect(room)
            //  binding.videoStatusTextview.text = "Connected to ${room.name}"
            //reconnectingProgressBar.visibility = View.GONE
        }

        override fun onReconnecting(room: Room, twilioException: TwilioException) {
            mtwilioVideoRoomCallBack.onParticipantReconnecting(room)
            // binding.videoStatusTextview.text = "Reconnecting to ${room.name}"
            //reconnectingProgressBar.visibility = View.VISIBLE
        }

        override fun onConnectFailure(room: Room, e: TwilioException) {
            mtwilioVideoRoomCallBack.onConnectFailure(room, e)
            /* binding.videoStatusTextview.text = "Failed to connect"
             audioSwitch.deactivate()
             initializeUI()*/
        }

        override fun onDisconnected(room: Room, e: TwilioException?) {
            mtwilioVideoRoomCallBack.onDisconnected(room, e)
            disConnectRoom()
            // Only reinitialize the UI if disconnect was not called from onDestroy()


            /* localParticipant = null
                binding.videoStatusTextview.text = "Disconnected from ${room.name}"
                // reconnectingProgressBar.visibility = View.GONE
                this@VideoActivity.room = null
                // Only reinitialize the UI if disconnect was not called from onDestroy()
                if (!disconnectedFromOnDestroy) {
                    audioSwitch.deactivate()
                    initializeUI()
                    moveLocalVideoToPrimaryView()
                }*/
        }

        override fun onParticipantConnected(room: Room, participant: RemoteParticipant) {
            mtwilioVideoRoomCallBack.onParticipantConnected(room, participant)
            //addRemoteParticipant(participant)
            Log.d(TAG, "onParticipantConnected: ")

        }

        override fun onParticipantDisconnected(room: Room, participant: RemoteParticipant) {
            mtwilioVideoRoomCallBack.onParticipantDisconnected(room, participant)
            // removeRemoteParticipant(participant)
            Log.d(TAG, "onParticipantDisconnected: ")
        }

        override fun onRecordingStarted(room: Room) {
            mtwilioVideoRoomCallBack.onRecordingStarted(room)
            /*
             * Indicates when media shared to a Room is being recorded. Note that
             * recording is only available in our Group Rooms developer preview.
             */
            Log.d(TAG, "onRecordingStarted")
        }

        override fun onRecordingStopped(room: Room) {
            mtwilioVideoRoomCallBack.onRecordingStopped(room)
            /*
             * Indicates when media shared to a Room is no longer being recorded. Note that
             * recording is only available in our Group Rooms developer preview.
             */
            Log.d(TAG, "onRecordingStopped")
        }
    }


    private val participantListener = object : RemoteParticipant.Listener {
        override fun onAudioTrackPublished(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication
        ) {
            listener.onAudioTrackPublished(remoteParticipant, remoteAudioTrackPublication)
            Log.i(
                TAG, "onAudioTrackPublished: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteAudioTrackPublication: sid=${remoteAudioTrackPublication.trackSid}, " +
                        "enabled=${remoteAudioTrackPublication.isTrackEnabled}, " +
                        "subscribed=${remoteAudioTrackPublication.isTrackSubscribed}, " +
                        "name=${remoteAudioTrackPublication.trackName}]"
            )
            // binding.videoStatusTextview.text = "onAudioTrackAdded"
        }

        override fun onNetworkQualityLevelChanged(
            remoteParticipant: RemoteParticipant,
            networkQualityLevel: NetworkQualityLevel
        ) {
            super.onNetworkQualityLevelChanged(remoteParticipant, networkQualityLevel)
            listener.onNetworkQualityLevelChanged(remoteParticipant,networkQualityLevel)
            networkStatusLive.postValue(NetworkQualityModel(remoteParticipant.identity,networkQualityLevel))
        }

        override fun onAudioTrackUnpublished(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication
        ) {

            listener.onAudioTrackPublished(remoteParticipant, remoteAudioTrackPublication)
            Log.i(
                TAG, "onAudioTrackUnpublished: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteAudioTrackPublication: sid=${remoteAudioTrackPublication.trackSid}, " +
                        "enabled=${remoteAudioTrackPublication.isTrackEnabled}, " +
                        "subscribed=${remoteAudioTrackPublication.isTrackSubscribed}, " +
                        "name=${remoteAudioTrackPublication.trackName}]"
            )
            // binding.videoStatusTextview.text = "onAudioTrackRemoved"
        }

        override fun onDataTrackPublished(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication
        ) {
            listener.onDataTrackPublished(remoteParticipant, remoteDataTrackPublication)
            Log.i(
                TAG, "onDataTrackPublished: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteDataTrackPublication: sid=${remoteDataTrackPublication.trackSid}, " +
                        "enabled=${remoteDataTrackPublication.isTrackEnabled}, " +
                        "subscribed=${remoteDataTrackPublication.isTrackSubscribed}, " +
                        "name=${remoteDataTrackPublication.trackName}]"
            )
            //  binding.videoStatusTextview.text = "onDataTrackPublished"
        }

        override fun onDataTrackUnpublished(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication
        ) {
            listener.onDataTrackUnpublished(remoteParticipant, remoteDataTrackPublication)
            Log.i(
                TAG, "onDataTrackUnpublished: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteDataTrackPublication: sid=${remoteDataTrackPublication.trackSid}, " +
                        "enabled=${remoteDataTrackPublication.isTrackEnabled}, " +
                        "subscribed=${remoteDataTrackPublication.isTrackSubscribed}, " +
                        "name=${remoteDataTrackPublication.trackName}]"
            )
            // binding.videoStatusTextview.text = "onDataTrackUnpublished"
        }

        override fun onVideoTrackPublished(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication
        ) {
            listener.onVideoTrackPublished(remoteParticipant, remoteVideoTrackPublication)
            Log.i(
                TAG, "onVideoTrackPublished: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteVideoTrackPublication: sid=${remoteVideoTrackPublication.trackSid}, " +
                        "enabled=${remoteVideoTrackPublication.isTrackEnabled}, " +
                        "subscribed=${remoteVideoTrackPublication.isTrackSubscribed}, " +
                        "name=${remoteVideoTrackPublication.trackName}]"
            )
            // binding.videoStatusTextview.text = "onVideoTrackPublished"
        }

        override fun onVideoTrackUnpublished(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication
        ) {
            listener.onVideoTrackUnpublished(remoteParticipant, remoteVideoTrackPublication)
            Log.i(
                TAG, "onVideoTrackUnpublished: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteVideoTrackPublication: sid=${remoteVideoTrackPublication.trackSid}, " +
                        "enabled=${remoteVideoTrackPublication.isTrackEnabled}, " +
                        "subscribed=${remoteVideoTrackPublication.isTrackSubscribed}, " +
                        "name=${remoteVideoTrackPublication.trackName}]"
            )
            // binding.videoStatusTextview.text = "onVideoTrackUnpublished"
        }

        override fun onAudioTrackSubscribed(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication,
            remoteAudioTrack: RemoteAudioTrack
        ) {
            listener.onAudioTrackSubscribed(
                remoteParticipant,
                remoteAudioTrackPublication,
                remoteAudioTrack
            )
            Log.i(
                TAG, "onAudioTrackSubscribed: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteAudioTrack: enabled=${remoteAudioTrack.isEnabled}, " +
                        "playbackEnabled=${remoteAudioTrack.isPlaybackEnabled}, " +
                        "name=${remoteAudioTrack.name}]"
            )
            //  binding.videoStatusTextview.text = "onAudioTrackSubscribed"
        }

        override fun onAudioTrackUnsubscribed(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication,
            remoteAudioTrack: RemoteAudioTrack
        ) {
            listener.onAudioTrackUnsubscribed(
                remoteParticipant,
                remoteAudioTrackPublication,
                remoteAudioTrack
            )
            Log.i(
                TAG, "onAudioTrackUnsubscribed: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteAudioTrack: enabled=${remoteAudioTrack.isEnabled}, " +
                        "playbackEnabled=${remoteAudioTrack.isPlaybackEnabled}, " +
                        "name=${remoteAudioTrack.name}]"
            )
            //  binding.videoStatusTextview.text = "onAudioTrackUnsubscribed"
        }

        override fun onAudioTrackSubscriptionFailed(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication,
            twilioException: TwilioException
        ) {
            listener.onAudioTrackSubscriptionFailed(
                remoteParticipant,
                remoteAudioTrackPublication,
                twilioException
            )
            Log.i(
                TAG, "onAudioTrackSubscriptionFailed: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteAudioTrackPublication: sid=${remoteAudioTrackPublication.trackSid}, " +
                        "name=${remoteAudioTrackPublication.trackName}]" +
                        "[TwilioException: code=${twilioException.code}, " +
                        "message=${twilioException.message}]"
            )
            // binding.videoStatusTextview.text = "onAudioTrackSubscriptionFailed"
        }

        override fun onDataTrackSubscribed(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication,
            remoteDataTrack: RemoteDataTrack
        ) {
            listener.onDataTrackSubscribed(
                remoteParticipant,
                remoteDataTrackPublication,
                remoteDataTrack
            )
            Log.i(
                TAG, "onDataTrackSubscribed: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteDataTrack: enabled=${remoteDataTrack.isEnabled}, " +
                        "name=${remoteDataTrack.name}]"
            )
            //  binding.videoStatusTextview.text = "onDataTrackSubscribed"
        }

        override fun onDataTrackUnsubscribed(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication,
            remoteDataTrack: RemoteDataTrack
        ) {
            listener.onDataTrackUnsubscribed(
                remoteParticipant,
                remoteDataTrackPublication,
                remoteDataTrack
            )
            Log.i(
                TAG, "onDataTrackUnsubscribed: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteDataTrack: enabled=${remoteDataTrack.isEnabled}, " +
                        "name=${remoteDataTrack.name}]"
            )
            // binding.videoStatusTextview.text = "onDataTrackUnsubscribed"
        }

        override fun onDataTrackSubscriptionFailed(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication,
            twilioException: TwilioException
        ) {
            listener.onDataTrackSubscriptionFailed(
                remoteParticipant,
                remoteDataTrackPublication,
                twilioException
            )
            Log.i(
                TAG, "onDataTrackSubscriptionFailed: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteDataTrackPublication: sid=${remoteDataTrackPublication.trackSid}, " +
                        "name=${remoteDataTrackPublication.trackName}]" +
                        "[TwilioException: code=${twilioException.code}, " +
                        "message=${twilioException.message}]"
            )
            // binding.videoStatusTextview.text = "onDataTrackSubscriptionFailed"
        }

        override fun onVideoTrackSubscribed(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication,
            remoteVideoTrack: RemoteVideoTrack
        ) {
            listener.onVideoTrackSubscribed(
                remoteParticipant,
                remoteVideoTrackPublication,
                remoteVideoTrack
            )
            Log.i(
                TAG, "onVideoTrackSubscribed: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteVideoTrack: enabled=${remoteVideoTrack.isEnabled}, " +
                        "name=${remoteVideoTrack.name}]"
            )
            //  binding.videoStatusTextview.text = "onVideoTrackSubscribed"
            //  addRemoteParticipantVideo(remoteVideoTrack)
        }

        override fun onVideoTrackUnsubscribed(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication,
            remoteVideoTrack: RemoteVideoTrack
        ) {
            listener.onVideoTrackUnsubscribed(
                remoteParticipant,
                remoteVideoTrackPublication,
                remoteVideoTrack
            )
            Log.i(
                TAG, "onVideoTrackUnsubscribed: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteVideoTrack: enabled=${remoteVideoTrack.isEnabled}, " +
                        "name=${remoteVideoTrack.name}]"
            )
            //  binding.videoStatusTextview.text = "onVideoTrackUnsubscribed"
            // removeParticipantVideo(remoteVideoTrack)
        }

        override fun onVideoTrackSubscriptionFailed(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication,
            twilioException: TwilioException
        ) {
            listener.onVideoTrackSubscriptionFailed(
                remoteParticipant,
                remoteVideoTrackPublication,
                twilioException
            )
            Log.i(
                TAG, "onVideoTrackSubscriptionFailed: " +
                        "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                        "[RemoteVideoTrackPublication: sid=${remoteVideoTrackPublication.trackSid}, " +
                        "name=${remoteVideoTrackPublication.trackName}]" +
                        "[TwilioException: code=${twilioException.code}, " +
                        "message=${twilioException.message}]"
            )
            //  binding.videoStatusTextview.text = "onVideoTrackSubscriptionFailed"
            /*Snackbar.make(
                binding.connectActionFab,
                "Failed to subscribe to ${remoteParticipant.identity}",
                Snackbar.LENGTH_LONG
            )
                .show()*/
        }

        override fun onAudioTrackEnabled(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication
        ) {
            listener.onAudioTrackEnabled(remoteParticipant, remoteAudioTrackPublication)
            micLiveData.postValue(MicStatusModel(true,remoteParticipant.identity))
            Log.d(TAG, "onAudioTrackEnabled: ")
        }

        override fun onVideoTrackEnabled(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication
        ) {
            listener.onVideoTrackEnabled(remoteParticipant, remoteVideoTrackPublication)
            Log.d(TAG, "onVideoTrackEnabled: ")
        }

        override fun onVideoTrackDisabled(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication
        ) {
            listener.onVideoTrackDisabled(remoteParticipant, remoteVideoTrackPublication)
            Log.d(TAG, "onVideoTrackDisabled: ")
        }

        override fun onAudioTrackDisabled(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication
        ) {
            listener.onAudioTrackDisabled(remoteParticipant, remoteAudioTrackPublication)
            micLiveData.postValue(MicStatusModel(false,remoteParticipant.identity))
            Log.d(TAG, "onAudioTrackDisabled: ")
        }
    }

}



interface RoomParticipantListener {
    fun onNetworkQualityLevelChanged(
    remoteParticipant: RemoteParticipant,
    networkQualityLevel: NetworkQualityLevel
    )

    fun onAudioTrackPublished(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    )
    fun onAudioTrackUnpublished(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    )

    fun onDataTrackPublished(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication
    )

    fun onDataTrackUnpublished(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication
    )
    fun onVideoTrackPublished(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    )

    fun onVideoTrackUnpublished(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    )

    fun onAudioTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication,
        remoteAudioTrack: RemoteAudioTrack
    )

    fun onAudioTrackUnsubscribed(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication,
        remoteAudioTrack: RemoteAudioTrack
    )

    fun onAudioTrackSubscriptionFailed(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication,
        twilioException: TwilioException
    )

    fun onDataTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication,
        remoteDataTrack: RemoteDataTrack
    )

    fun onDataTrackUnsubscribed(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication,
        remoteDataTrack: RemoteDataTrack
    )

    fun onDataTrackSubscriptionFailed(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication,
        twilioException: TwilioException
    )

    fun onVideoTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        remoteVideoTrack: RemoteVideoTrack
    )

    fun onVideoTrackUnsubscribed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        remoteVideoTrack: RemoteVideoTrack
    )


    fun onVideoTrackSubscriptionFailed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        twilioException: TwilioException
    )


    fun onAudioTrackEnabled(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    )

    fun onVideoTrackEnabled(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    )

    fun onVideoTrackDisabled(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    )
    fun onAudioTrackDisabled(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    )
}


interface RoomListenerCallback {
    fun onParticipantConnect(room: Room)
    fun onParticipantDisconnect(room: Room)
    fun onParticipantReconnect(room: Room)
    fun onParticipantReconnecting(room: Room)
    fun onConnectFailure(room: Room, e: TwilioException)
    fun onDisconnected(room: Room, e: TwilioException?)
    fun onParticipantConnected(room: Room, participant: RemoteParticipant)
    fun onParticipantDisconnected(room: Room, participant: RemoteParticipant)
    fun onRecordingStarted(room: Room)
    fun onRecordingStopped(room: Room)

}







