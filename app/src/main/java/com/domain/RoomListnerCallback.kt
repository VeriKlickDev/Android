package com.domain

import com.twilio.video.RemoteParticipant
import com.twilio.video.Room
import com.twilio.video.TwilioException

interface RoomListnerCallback1 {
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

