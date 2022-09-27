package com.ui.listadapters

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.data.dataHolders.LocalConfrenseMic
import com.data.helpers.RoomParticipantListener
import com.data.helpers.TwilioHelper
import com.domain.BaseModels.VideoTracksBean
import com.example.twillioproject.databinding.LayoutItemConnectedUsersBinding
import com.twilio.video.*
import com.ui.activities.twilioVideo.MicStatusListener
import com.ui.activities.twilioVideo.VideoActivity
import com.ui.activities.twilioVideo.VideoViewModel

class ConnectedUserListAdapter(val viewModel:VideoViewModel,
    val context: Context,
    val list: List<VideoTracksBean>,
    val onClick: (pos: Int, action: Int, data: VideoTracksBean, tlist: List<VideoTracksBean>,videoTrack:VideoTrack) -> Unit
) : RecyclerView.Adapter<ConnectedUserListAdapter.ViewholderClass>() {

    val TAG = "checkconnectedUserMain"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewholderClass {
    val     binding =
            LayoutItemConnectedUsersBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewholderClass(binding)
    }

    override fun onBindViewHolder(holder: ViewholderClass, position: Int) {
        holder.setIsRecyclable(false)
        holder.bindData(list.get(position))
        /* if (list[position].remoteParticipant!=null) {
            if (list[position].remoteParticipant?.identity!!.contains("C")) {
                holder.binding.parentLayout.visibility = View.GONE
            }
        }*/

        try {
            viewModel.getMicStatus()?.let {
                Log.d(TAG, "onBindViewHolder: list size of mic status ${it.size}")
                it.forEach { micStatus ->
                    micStatus?.let {micModel->
                        if (list[position].remoteParticipant?.identity!!.equals(micModel.removeParticipant!!.identity)) {
                            holder.binding.ivMic.isVisible = micModel!!.status
                            Log.d(TAG, "onBindViewHolder: mic status changed to ${micModel.status}")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "onBindViewHolder: exception ${e.message}")
        }

        holder.binding.cardView2.setOnClickListener { onClick(position, 1, list[position], list,list[position].videoTrack) }
        holder.binding.parentLayout.setOnClickListener {
            onClick(position, 1, list[position], list,list[position].videoTrack)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class ViewholderClass(val binding: LayoutItemConnectedUsersBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bindData(data: VideoTracksBean) {

            binding.ivUserVideoView.mirror = true
            binding.tvUsername.text = data.userName
            data.videoTrack.addSink(binding.ivUserVideoView)


                if (data.userName.equals("You")) {
                    binding.ivMic.isVisible = false
                }
                else {
                    Log.d(TAG, "bindData: participant listener")

                    data.remoteParticipant?.let {

                       /* it.setListener(object: RemoteParticipant.Listener{
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
                                Log.d(TAG, "onAudioTrackEnabled: ")
                                setMicStatus(true,binding)
                                viewModel.setMicStatus(data!!,false)
                            }

                            override fun onAudioTrackDisabled(
                                remoteParticipant: RemoteParticipant,
                                remoteAudioTrackPublication: RemoteAudioTrackPublication
                            ) {
                                setMicStatus(false,binding)
                                viewModel.setMicStatus(data!!,true)
                                Log.d(TAG, "onAudioTrackDisabled: ")
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
                        })*/
                       // TwilioHelper.setRemoteParticipantListener(it)
                    }

                }

        }
    }

    fun setMicStatus(status: Boolean,binding: LayoutItemConnectedUsersBinding) {
        if (status) {
            Log.e("audioTrack", "bindData: enable")
            Handler(Looper.getMainLooper()).post {
                binding.ivMic.isVisible = false
            }
        }
        else {
            Log.e("audioTrack", "bindData: disabled")
            Handler(Looper.getMainLooper()).post {
                binding.ivMic.isVisible = true
            }
        }
    }

}