package com.ui.listadapters

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.data.dataHolders.LocalConfrenseMic
import com.domain.BaseModels.VideoTracksBean
import com.example.twillioproject.R
import com.example.twillioproject.databinding.LayoutItemMemberVideoConfrenceBinding
import com.twilio.video.*
import com.ui.activities.meetingmemberslist.MemberListActivity

class ConnectedMemberListAdapter(
    val context: Context,
    val list: List<VideoTracksBean>,
    val hight: Int,
    val width: Int,
    val onClick: (pos: Int, action: Int, data: VideoTracksBean) -> Unit
) : RecyclerView.Adapter<ConnectedMemberListAdapter.ViewholderClass>() {

    val TAG="checkconnectUsers"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewholderClass {
        val binding =
            LayoutItemMemberVideoConfrenceBinding.inflate(LayoutInflater.from(context), parent, false)

        val layoutParams = binding.root.layoutParams
        Log.d("rvAdditem", "onCreateViewHolder: ${hight}  $width")
        layoutParams.width = width / 2
        layoutParams.height = (hight / 2) - 140
        binding.root.layoutParams = layoutParams
        return ViewholderClass(binding)
    }

    override fun onBindViewHolder(holder: ViewholderClass, position: Int) {
        holder.bindData(list.get(position))

    }

    override fun getItemCount(): Int {
        return list.size
    }


    // https://ui2.veriklick.in/video-session/Wg2UoJWNDT8uV1wzKzej
    inner class ViewholderClass(val binding: LayoutItemMemberVideoConfrenceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: VideoTracksBean) {
            binding.twiliovideoView.mirror = true
            binding.tvUsername.text = data.userName
            data.videoTrack.addSink(binding.twiliovideoView)

            binding.ivLeftMeeting.setOnClickListener {
                onClick(adapterPosition,1,data)
            }

            try {

                if (data.userName.equals("You")) {
                    binding.ivNetworkStatus.isVisible = false
                    binding.ivLeftMeeting.isVisible = false
                    LocalConfrenseMic.getLocalParticipant()
                        .observe(context as MemberListActivity, Observer {
                            Log.d("adduserlistadapter", "bindData: mic is $it ")
                            if (it) {
                               // binding.ivMic.isVisible = false
                            }
                            else {
                               // binding.ivMic.isVisible = true
                            }
                        })
                }
                else {


                    binding.ivNetworkStatus.isVisible = true
                    binding.ivLeftMeeting.isVisible = true

                  //  binding.ivMic.isVisible=data.remoteParticipant?.remoteAudioTracks?.firstOrNull()?.remoteAudioTrack?.isEnabled==false

                    data.remoteParticipant.let {
                       // it?.setListener(listener)
                    }


                }


            } catch (e: Exception) {
                Log.d("AddUserListAdapter", "bindData: exception ${e.printStackTrace()}")
            }


            /*  if (data.remoteParticipant.audioTracks.firstOrNull()?.audioTrack?.isEnabled!!)
              {
                  binding.ivMic.setImageDrawable(context.getDrawable(R.drawable.ic_img_btn_mic_unmute_white_mini))
              }else
              {
                  binding.ivMic.setImageDrawable(context.getDrawable(R.drawable.ic_img_muted_white))
              }
  */


            // binding.data=data
        }

        fun setMicStatus(status:Boolean)
        {
            if (status) {
                Log.e("audioTrack", "bindData: enable")
                Handler(Looper.getMainLooper()).post {
                 //   binding.ivMic.isVisible = false
                }
            }
            else {
                Log.e("audioTrack", "bindData: disabled")
                Handler(Looper.getMainLooper()).post {
                   // binding.ivMic.isVisible = true
                }
            }
        }


        val listener=object :RemoteParticipant.Listener{
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
                Log.d(TAG, "onNetworkQualityLevelChanged: network level ${networkQualityLevel.name}")
                Log.d(TAG, "onNetworkQualityLevelChanged: network level $networkQualityLevel  ")
                when(networkQualityLevel) {
                    NetworkQualityLevel.NETWORK_QUALITY_LEVEL_ZERO  -> {
                        binding.ivNetworkStatus.setImageResource(R.drawable.network_quality_level_0)
                    }
                    NetworkQualityLevel.NETWORK_QUALITY_LEVEL_ONE   -> {
                        binding.ivNetworkStatus.setImageResource(R.drawable.network_quality_level_1)
                    }
                    NetworkQualityLevel.NETWORK_QUALITY_LEVEL_TWO   -> {
                        binding.ivNetworkStatus.setImageResource(R.drawable.network_quality_level_2)
                    }
                    NetworkQualityLevel.NETWORK_QUALITY_LEVEL_THREE -> {
                        binding.ivNetworkStatus.setImageResource(R.drawable.network_quality_level_3)
                    }
                    NetworkQualityLevel.NETWORK_QUALITY_LEVEL_FOUR  -> {
                        binding.ivNetworkStatus.setImageResource(R.drawable.network_quality_level_4)
                    }
                    NetworkQualityLevel.NETWORK_QUALITY_LEVEL_FIVE  -> {
                        binding.ivNetworkStatus.setImageResource(R.drawable.network_quality_level_5)
                    }
                    NetworkQualityLevel.NETWORK_QUALITY_LEVEL_UNKNOWN  -> {
                        binding.ivNetworkStatus.setImageResource(R.drawable.network_quality_level_3)
                    }
                    NetworkQualityLevel.NETWORK_QUALITY_LEVEL_FIVE  -> {
                        binding.ivNetworkStatus.setImageResource(R.drawable.network_quality_level_5)
                    }
                    else-> binding.ivNetworkStatus.setImageResource(R.drawable.network_quality_level_3)
                }

                super.onNetworkQualityLevelChanged(remoteParticipant, networkQualityLevel)
            }
        }






    }



    fun setNetworQualityLevel(data: VideoTracksBean, ivNetwork: ImageView) {

        Log.d("networkCheck", "setNetworQualityLevel: network leve ${data.remoteParticipant?.networkQualityLevel?.name} ${data.remoteParticipant?.networkQualityLevel?.name} ")
        when(data.remoteParticipant?.networkQualityLevel!!) {
            NetworkQualityLevel.NETWORK_QUALITY_LEVEL_ZERO  -> {
                ivNetwork.setImageResource(R.drawable.network_quality_level_0)
            }
            NetworkQualityLevel.NETWORK_QUALITY_LEVEL_ONE   -> {
                ivNetwork.setImageResource(R.drawable.network_quality_level_1)
            }
            NetworkQualityLevel.NETWORK_QUALITY_LEVEL_TWO   -> {
                ivNetwork.setImageResource(R.drawable.network_quality_level_2)
            }
            NetworkQualityLevel.NETWORK_QUALITY_LEVEL_THREE -> {
                ivNetwork.setImageResource(R.drawable.network_quality_level_3)
            }
            NetworkQualityLevel.NETWORK_QUALITY_LEVEL_FOUR  -> {
                ivNetwork.setImageResource(R.drawable.network_quality_level_4)
            }
            NetworkQualityLevel.NETWORK_QUALITY_LEVEL_FIVE  -> {
                ivNetwork.setImageResource(R.drawable.network_quality_level_5)
            }
            NetworkQualityLevel.NETWORK_QUALITY_LEVEL_UNKNOWN  -> {
                ivNetwork.setImageResource(R.drawable.network_quality_level_3)
            }
            NetworkQualityLevel.NETWORK_QUALITY_LEVEL_FIVE  -> {
                ivNetwork.setImageResource(R.drawable.network_quality_level_5)
            }
            else-> ivNetwork.setImageResource(R.drawable.network_quality_level_3)
        }
    }











}