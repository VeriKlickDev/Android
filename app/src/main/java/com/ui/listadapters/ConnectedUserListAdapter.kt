package com.ui.listadapters

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.data.LocalConfrenseMic
import com.domain.BaseModels.VideoTracksBean
import com.example.twillioproject.R
import com.example.twillioproject.databinding.LayoutItemConnectedUsersBinding
import com.twilio.video.*
import com.ui.activities.twilioVideo.VideoActivity
import okhttp3.internal.waitMillis

class ConnectedUserListAdapter(
    val context: Context,
    val list: List<VideoTracksBean>,
    val onClick: (pos: Int, action: Int, data: VideoTracksBean,tlist:List<VideoTracksBean>) -> Unit
) : RecyclerView.Adapter<ConnectedUserListAdapter.ViewholderClass>() {
    val TAG="checkconnectedUserMain"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewholderClass {
        val binding =
            LayoutItemConnectedUsersBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewholderClass(binding)
    }

    override fun onBindViewHolder(holder: ViewholderClass, position: Int) {
        holder.bindData(list.get(position))
        holder.binding.parentLayout.setOnClickListener {
            onClick(position,1,list[position],list)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    // https://ui2.veriklick.in/video-session/Wg2UoJWNDT8uV1wzKzej
    inner class ViewholderClass(val binding: LayoutItemConnectedUsersBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: VideoTracksBean) {
            binding.ivUserVideoView.mirror = true
            binding.tvUsername.text = data.userName
            data.videoTrack.addSink(binding.ivUserVideoView)

            try {
                if (data.userName.equals("You"))
                {
                    LocalConfrenseMic.getLocalParticipant().observe(context as VideoActivity, Observer {
                        Log.d("adduserlistadapter", "bindData: mic is $it ")
                        if (it)
                        {
                            binding.ivMic.isVisible = false
                        }else
                        {
                            binding.ivMic.isVisible = true
                        }
                    })
                }else
                {
                    binding.ivMic.isVisible=data.remoteParticipant?.remoteAudioTracks?.firstOrNull()?.remoteAudioTrack?.isEnabled==false

                   // binding.ivMic.isVisible=data.remoteParticipant?.remoteAudioTracks?.firstOrNull()?.audioTrack?.isEnabled==false
                   data.remoteParticipant?.setListener(listener)
                }
            }catch (e:Exception)
            {
                Log.d("AddUserListAdapter", "bindData: exception ${e.printStackTrace()}")
            }



        }









        fun setMicStatus(status:Boolean)
        {
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


        val listener=object : RemoteParticipant.Listener{
            override fun onAudioTrackPublished(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {
                Log.d(TAG, "onAudioTrackSubscribed: audio trck publ")

            }

            override fun onAudioTrackUnpublished(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {
                Log.d(TAG, "onAudioTrackSubscribed: audio trck unpubl")
            }

            override fun onAudioTrackSubscribed(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
                remoteAudioTrack: RemoteAudioTrack
            ) {
                Log.d(TAG, "onAudioTrackSubscribed: audio trck subs")
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
                Log.d(TAG, "onAudioTrackUnsubscribed: unsubcr audio")
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
                Log.d(TAG, "onAudioTrackEnabled: audio track enabled ")
                setMicStatus(true)
            }

            override fun onAudioTrackDisabled(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {
                Log.d(TAG, "onAudioTrackEnabled: audio track disabled")
                setMicStatus(false)
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

            override fun onNetworkQualityLevelChanged(
                remoteParticipant: RemoteParticipant,
                networkQualityLevel: NetworkQualityLevel
            ) {

                super.onNetworkQualityLevelChanged(remoteParticipant, networkQualityLevel)
            }
        }























    }


}