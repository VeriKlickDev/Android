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
import com.domain.BaseModels.VideoTracksBean
import com.example.twillioproject.databinding.LayoutItemConnectedUsersBinding
import com.twilio.video.*
import com.ui.activities.twilioVideo.MicStatusListener
import com.ui.activities.twilioVideo.VideoActivity

class ConnectedUserListAdapter(
    val context: Context,
    val list: List<VideoTracksBean>,
    val onClick: (pos: Int, action: Int, data: VideoTracksBean, tlist: List<VideoTracksBean>,videoTrack:VideoTrack) -> Unit
) : RecyclerView.Adapter<ConnectedUserListAdapter.ViewholderClass>()  {
    lateinit var binding:LayoutItemConnectedUsersBinding
    val TAG = "checkconnectedUserMain"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewholderClass {
         binding =
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
                    Log.d(TAG, "bindData: not null data.prt")
                    //it.setListener(this@ConnectedUserListAdapter)
                }


                if (data.userName.equals("You")) {
                    LocalConfrenseMic.getLocalParticipant()
                        .observe(context as VideoActivity, Observer {
                            Log.d("adduserlistadapter", "bindData: mic is $it ")
                            if (it) {
                                binding.ivMic.isVisible = false
                            }
                            else {
                                binding.ivMic.isVisible = true
                            }
                        })
                }
                else {
                    binding.ivMic.isVisible =
                        data.remoteParticipant?.remoteAudioTracks?.firstOrNull()?.remoteAudioTrack?.isEnabled == false

                    // binding.ivMic.isVisible=data.remoteParticipant?.remoteAudioTracks?.firstOrNull()?.audioTrack?.isEnabled==false
                    // data.remoteParticipant!!.setListener(this@ConnectedUserListAdapter)

                }
            } catch (e: Exception) {
                Log.d("AddUserListAdapter", "bindData: exception ${e.printStackTrace()}")
            }

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