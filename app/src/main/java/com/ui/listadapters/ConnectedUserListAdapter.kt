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
import com.data.showToast
import com.domain.BaseModels.NetworkQualityModel
import com.domain.BaseModels.VideoTracksBean
import com.example.twillioproject.R
import com.example.twillioproject.databinding.LayoutItemConnectedUsersBinding
import com.twilio.video.*
import com.ui.activities.twilioVideo.MicStatusListener
import com.ui.activities.twilioVideo.VideoActivity
import com.ui.activities.twilioVideo.VideoViewModel

class ConnectedUserListAdapter(val viewModel:VideoViewModel,
    val context: Context,
    val list: List<VideoTracksBean>,
    val onClick: (pos: Int, action: Int, data: VideoTracksBean, tlist: List<VideoTracksBean>,videoTrack:VideoTrack) -> Unit,
    val onLongClick: (pos: Int, action: Int) -> Unit
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


        holder.binding.cardView2.setOnClickListener {
            if (list[position].videoTrack!=null){
            onClick(position, 1, list[position], list,list[position].videoTrack!!)}
            else {
                context.showToast(context, context.getString(R.string.txt_video_not_available))
            }
        }


        holder.binding.parentLayout.setOnClickListener {
            if (list[position].videoTrack!=null)
                onClick(position, 1, list[position], list,list[position].videoTrack!!)
            else
                context.showToast(context,context.getString(R.string.txt_video_not_available))
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


            binding.cardView2.setOnLongClickListener(longclick)


            if (data.videoTrack!=null)
            {
                binding.blankvideoLayout.visibility=View.GONE
                binding.ivUserVideoView.visibility=View.VISIBLE
                data.videoTrack!!.addSink(binding.ivUserVideoView)    
            }else
            {
                binding.blankvideoLayout.visibility=View.VISIBLE
                binding.ivUserVideoView.visibility=View.GONE
                binding.tvNovideoText.setText(data.userName!!.get(0).toString())
                Log.d(TAG, "bindData: video trak null")
            }

            TwilioHelper.getMicStatusLive().observe(context as VideoActivity, Observer {
                if (it.identity.equals(data.identity))
                {
                    setMicStatus(it.micStatus)
                }
            })

            TwilioHelper.getNetWorkQualityLevel().observe(context as VideoActivity){
                if (it.identity.equals(data.identity))
                {
                    setNetworkLevel(it)
                }
            }

            try {
                data.remoteParticipant?.let {
                    TwilioHelper.setRemoteParticipantListener(it)
                    Log.d(TAG, "bindData: setting listener ")
                }
                /*data.remoteParticipant?.let {
                    Log.d(TAG, "bindData: not null data.prt")
                    //it.setListener(this@ConnectedUserListAdapter)
                }*/

                if (data.userName.equals("You")) {
                    binding.ivMic.isVisible = false
                    binding.ivNetworkStatus.isVisible=true
                    TwilioHelper.getNetWorkQualityLevelLocal().observe(context){network->
                        setNetworkLevel(network)
                    }

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

        val longclick= object : View.OnLongClickListener {
            override fun onLongClick(p0: View?): Boolean {

                onLongClick(adapterPosition,5)

                return true
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

        fun setNetworkLevel(networkQualityModel: NetworkQualityModel) {
            when (networkQualityModel.network) {
                NetworkQualityLevel.NETWORK_QUALITY_LEVEL_ZERO -> {
                    binding.ivNetworkStatus.setImageResource(R.drawable.network_quality_level_0)
                }
                NetworkQualityLevel.NETWORK_QUALITY_LEVEL_ONE -> {
                    binding.ivNetworkStatus.setImageResource(R.drawable.network_quality_level_1)
                }
                NetworkQualityLevel.NETWORK_QUALITY_LEVEL_TWO -> {
                    binding.ivNetworkStatus.setImageResource(R.drawable.network_quality_level_2)
                }
                NetworkQualityLevel.NETWORK_QUALITY_LEVEL_THREE -> {
                    binding.ivNetworkStatus.setImageResource(R.drawable.network_quality_level_3)
                }
                NetworkQualityLevel.NETWORK_QUALITY_LEVEL_FOUR -> {
                    binding.ivNetworkStatus.setImageResource(R.drawable.network_quality_level_4)
                }
                NetworkQualityLevel.NETWORK_QUALITY_LEVEL_FIVE -> {
                    binding.ivNetworkStatus.setImageResource(R.drawable.network_quality_level_5)
                }
                NetworkQualityLevel.NETWORK_QUALITY_LEVEL_UNKNOWN -> {
                    binding.ivNetworkStatus.setImageResource(R.drawable.network_quality_level_3)
                }
                NetworkQualityLevel.NETWORK_QUALITY_LEVEL_FIVE -> {
                    binding.ivNetworkStatus.setImageResource(R.drawable.network_quality_level_5)
                }
                else -> binding.ivNetworkStatus.setImageResource(R.drawable.network_quality_level_3)
            }

        }


    }

}