package com.ui.activities.twilioVideo

import android.util.Log
import com.twilio.video.*
import tvi.webrtc.DataChannel

class MicStatusListener(val onStatusChanged:(status:Int)->Unit) : RemoteParticipant.Listener {

    val TAG="checkmicstatus"

    override fun onAudioTrackPublished(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
        
    }

    override fun onAudioTrackUnpublished(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
        
    }

    override fun onAudioTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication,
        remoteAudioTrack: RemoteAudioTrack
    ) {
        
    }

    override fun onAudioTrackSubscriptionFailed(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication,
        twilioException: TwilioException
    ) {
        
    }

    override fun onAudioTrackUnsubscribed(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication,
        remoteAudioTrack: RemoteAudioTrack
    ) {
        
    }

    override fun onVideoTrackPublished(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
        
    }

    override fun onVideoTrackUnpublished(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
        
    }

    override fun onVideoTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        remoteVideoTrack: RemoteVideoTrack
    ) {
        
    }

    override fun onVideoTrackSubscriptionFailed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        twilioException: TwilioException
    ) {
        
    }

    override fun onVideoTrackUnsubscribed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        remoteVideoTrack: RemoteVideoTrack
    ) {
        
    }

    override fun onDataTrackPublished(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication
    ) {
        
    }

    override fun onDataTrackUnpublished(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication
    ) {
        
    }

    override fun onDataTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication,
        remoteDataTrack: RemoteDataTrack
    ) {
        
    }

    override fun onDataTrackSubscriptionFailed(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication,
        twilioException: TwilioException
    ) {
        
    }

    override fun onDataTrackUnsubscribed(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication,
        remoteDataTrack: RemoteDataTrack
    ) {
        
    }

    override fun onAudioTrackEnabled(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
        onStatusChanged(1)
    }

    override fun onAudioTrackDisabled(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
        onStatusChanged(2)
    }

    override fun onVideoTrackEnabled(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
        
    }

    override fun onVideoTrackDisabled(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
        
    }


}