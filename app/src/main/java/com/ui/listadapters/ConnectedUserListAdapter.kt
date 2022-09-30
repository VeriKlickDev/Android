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
         val binding =
            LayoutItemConnectedUsersBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewholderClass(binding)
    }

    override fun onBindViewHolder(holder: ViewholderClass, position: Int) {
        holder.bindData(list.get(position))

       /* if (list[position].remoteParticipant!=null) {
            if (list[position].remoteParticipant?.identity!!.contains("C")) {
                holder.binding.parentLayout.visibility = View.GONE
            }
        }*/
    try {
        
        if (!list[position].userName.equals("You")){
            list[position].remoteParticipant?.let {
                TwilioHelper.setRemoteParticipantListener(it)
            }
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    list[position].remoteParticipant?.let {
                        holder.setMicStatus(it.remoteAudioTracks.firstOrNull()?.isTrackEnabled!!)
                    }

                }catch (e:Exception)
                {

                }


            },500)

        }

    }catch (e:Exception)
    {
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

    // https://ui2.veriklick.in/video-session/Wg2UoJWNDT8uV1wzKzej
    inner class ViewholderClass(val binding: LayoutItemConnectedUsersBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bindData(data: VideoTracksBean) {
            binding.ivUserVideoView.mirror = true
            binding.tvUsername.text = data.userName
            data.videoTrack.addSink(binding.ivUserVideoView)

            try {
                data.remoteParticipant?.let {
                }
                /*data.remoteParticipant?.let {
                    Log.d(TAG, "bindData: not null data.prt")
                    //it.setListener(this@ConnectedUserListAdapter)
                }*/

                if (data.userName.equals("You")) {
                    binding.ivMic.isVisible = false
                  /* LocalConfrenseMic.getLocalParticipant()
                        .observe(context as VideoActivity, Observer {
                            Log.d("adduserlistadapter", "bindData: mic is $it ")
                            binding.ivMic.isVisible = !it
                        })*/
                }
                else {
                    Log.d(TAG, "bindData: participant listener")
                   // binding.ivMic.isVisible = data.remoteParticipant?.remoteAudioTracks?.firstOrNull()?.remoteAudioTrack?.isEnabled == false
                  /*  data.remoteParticipant?.let {
                        TwilioHelper.setRemoteParticipantListener(it)
                    }*/

                   /* if (data.remoteParticipant?.remoteAudioTracks?.firstOrNull()!!.isTrackEnabled)
                    {
                        setMicStatus(true)
                    }else{
                        setMicStatus(false)
                    }*/
                    // binding.ivMic.isVisible=data.remoteParticipant?.remoteAudioTracks?.firstOrNull()?.audioTrack?.isEnabled==false
                    // data.remoteParticipant!!.setListener(this@ConnectedUserListAdapter)

                }
            } catch (e: Exception) {
                Log.d("AddUserListAdapter", "bindData: exception ${e.printStackTrace()}")
            }
        }

        fun setMicStatus(status: Boolean) {
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



}