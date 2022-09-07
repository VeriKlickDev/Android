package com.ui.activities.twilioVideo

//import com.ui.activities.chat.ChatActivity
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.data.*
import com.domain.BaseModels.BodyUpdateRecordingStatus
import com.domain.BaseModels.VideoTracksBean
import com.domain.OnViewClicked
import com.domain.RoomListnerCallback
import com.domain.RoomParticipantListner
import com.domain.constant.AppConstants
import com.example.twillioproject.R
import com.example.twillioproject.databinding.ActivityTwilioVideoBinding
import com.example.twillioproject.databinding.LayoutMuteMicUpdateBinding
import com.google.android.material.snackbar.Snackbar
import com.twilio.audioswitch.AudioDevice
import com.twilio.audioswitch.AudioDevice.*
import com.twilio.audioswitch.AudioSwitch
import com.twilio.video.*
import com.twilio.video.ktx.createLocalAudioTrack
import com.twilio.video.ktx.createLocalVideoTrack
import com.twilio.video.quickstart.kotlin.CameraCapturerCompat
import com.ui.activities.chat.ChatActivityTest
import com.ui.activities.documentviewer.DocumentViewerActivity
import com.ui.activities.meetingmemberslist.MemberListActivity
import com.ui.activities.twilioVideo.ScreenSharingCapturing.ScreenShareCapturerManager
import com.ui.listadapters.ConnectedUserListAdapter
import dagger.hilt.android.AndroidEntryPoint
import tvi.webrtc.VideoSink
import kotlin.properties.Delegates

@AndroidEntryPoint
class VideoActivity : AppCompatActivity(), RoomListnerCallback, RoomParticipantListner,
    OnViewClicked {

    lateinit var binding: ActivityTwilioVideoBinding

    private val CAMERA_MIC_PERMISSION_REQUEST_CODE = 1
    private val TAG = "twiliovideotest"
    private val CAMERA_PERMISSION_INDEX = 0
    private val MIC_PERMISSION_INDEX = 1
    lateinit var adapter: ConnectedUserListAdapter
    private var room: Room? = null
    private var localParticipant: LocalParticipant? = null
    private lateinit var localblankVideoTrack: LocalVideoTrack
    private val audioCodec: AudioCodec? = null
    private var videoCodec: VideoCodec? = null
    /*audio
                IsacCodec.NAME -> IsacCodec()
                OpusCodec.NAME -> OpusCodec()
                PcmaCodec.NAME -> PcmaCodec()
                PcmuCodec.NAME -> PcmuCodec()
                G722Codec.NAME -> G722Codec()
                H264Codec()
                else -> OpusCodec()
            }
        */

    /*video
                H264Codec.NAME -> H264Codec()
                Vp9Codec.NAME -> Vp9Codec()
                else -> Vp8Codec()
    */
    private lateinit var currentVisibleUser: VideoTracksBean

    private val encodingParameters: EncodingParameters
        get() {
            val defaultMaxAudioBitrate =
                320 //SettingsActivity.PREF_SENDER_MAX_AUDIO_BITRATE_DEFAULT
            val defaultMaxVideoBitrate =
                128 //SettingsActivity.PREF_SENDER_MAX_VIDEO_BITRATE_DEFAULT
            val maxAudioBitrate =
                128 //Integer.parseInt(sharedPreferences.getString(SettingsActivity.PREF_SENDER_MAX_AUDIO_BITRATE,defaultMaxAudioBitrate) ?: defaultMaxAudioBitrate)
            val maxVideoBitrate =
                //Integer.parseInt(sharedPreferences.getString(SettingsActivity.PREF_SENDER_MAX_VIDEO_BITRATE,defaultMaxVideoBitrate) ?: defaultMaxVideoBitrate)

                return EncodingParameters(128, 720)
        }

    private var localAudioTrack: LocalAudioTrack? = null
    private var localVideoTrack: LocalVideoTrack? = null
    private var alertDialog: AlertDialog? = null
    private val videoTrackList = mutableListOf<RemoteParticipant>()
    private val cameraCapturerCompat by lazy {
        CameraCapturerCompat(this, CameraCapturerCompat.Source.FRONT_CAMERA)
    }

    /*
     * Audio management
     */
    private val audioSwitch by lazy {
        AudioSwitch(
            applicationContext, preferredDeviceList = listOf(
                BluetoothHeadset::class.java,
                WiredHeadset::class.java, Speakerphone::class.java, Earpiece::class.java
            )
        )
    }
    private var savedVolumeControlStream by Delegates.notNull<Int>()
    private var audioDeviceMenuItem: MenuItem? = null
    private var currentRemoteVideoTrack: VideoTrack? = null
    private var participantIdentity: String? = null
    private lateinit var localVideoView: VideoSink
    private var disconnectedFromOnDestroy = false
    private var isSpeakerPhoneEnabled = true
    lateinit var viewModel: VideoViewModel
    val tlist = mutableListOf<VideoTracksBean>()
    private val remoteParticipantVideoList = mutableListOf<VideoTracksBean>()
    private val remoteParticipantVideoListWithCandidate = mutableListOf<VideoTracksBean>()


    private lateinit var screenShareTrack: LocalVideoTrack
    private lateinit var screenShareCapturerManager: ScreenShareCapturerManager
    private  var screenCapturer: ScreenCapturer?=null


    //  private lateinit var primaryVideoView:VideoView
    // private lateinit var thumbnailVideoView:VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTwilioVideoBinding.inflate(LayoutInflater.from(this))
        binding.onClick = this
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(VideoViewModel::class.java)
        actionBar?.hide()
        screenShareCapturerManager = ScreenShareCapturerManager()
        screenShareCapturerManager.ScreenCapturerManager(this)
        //   primaryVideoView=findViewById(R.id.primary_video_view)
        //  thumbnailVideoView=findViewById(R.id.thumbnail_video_view)

        //Log.d("checkobj", "onCreate: ${CurrentMeetingDataSaver.getData()}")
        binding.btnEndCall.setOnClickListener {
            TwilioHelper.disConnectRoom()
        }

        binding.localVideoActionFab.setOnClickListener {

        }

        CurrentMeetingDataSaver.getData().users?.forEach {
            if (it.userType.contains("C")) {
                binding.tvNoParticipant.text =
                    "Waiting for " + it.userFirstName + " " + it.userLastName + " to join..."
            }
        }



        binding.btnEllipsize.setOnClickListener {
            Log.d(TAG, "onCreate: layout clicked ")
            if (binding.llExtraButtons.isVisible) {
                binding.llExtraButtons.visibility = View.GONE
            }
            else {
                binding.llExtraButtons.visibility = View.VISIBLE
            }
        }

        binding.parentVideoLayout.setOnClickListener {
            if (binding.llExtraButtons.isVisible == true) {
                binding.llExtraButtons.isVisible = false
            }
        }

        binding.btnSendMessage.setOnClickListener {
            val identity = CurrentMeetingDataSaver.getData().identity
            viewModel.getChatToken(identity.toString(), response = { data, code ->
                Log.d("chatcheck", "onCreate: data $data  chat channel $identity")

                val intent = Intent(this, ChatActivityTest::class.java)
                intent.putExtra(AppConstants.CHAT_ACCESS_TOKEN, data?.Token)
                intent.putExtra(
                    AppConstants.CHAT_CHANNEL,
                    CurrentMeetingDataSaver.getData().chatChannel
                )
                intent.putExtra(
                    AppConstants.CONNECT_PARTICIPANT,
                    remoteParticipantVideoList.size.toString()
                )
                startActivity(intent)
            })

        }


        /*
          * Set local video view to primary view
          */
        localVideoView = binding.primaryVideoView
        //localVideoView = thumbnailVideoView

        /*
         * Enable changing the volume using the up/down keys during a conversation
         */
        savedVolumeControlStream = volumeControlStream
        volumeControlStream = AudioManager.STREAM_VOICE_CALL

        /*
         * Set access token
         */
        setAccessToken()

        createAudioAndVideoTracks()

        if (!checkPermissionForCameraAndMicrophone()) {
            requestPermissionForCameraMicrophoneAndBluetooth()
        }
        else {
            audioSwitch.start { audioDevices, audioDevice ->
                updateAudioDeviceIcon(audioDevice)
            }
        }

        CurrentMeetingDataSaver.getData().users?.forEach {
            Log.d("checkobj", "onCreate: user id ${it.isPresenter} ${it.userType + it.id}")
        }


        initializeUI()

        Handler(Looper.getMainLooper()).postDelayed(kotlinx.coroutines.Runnable {
            connectToRoom()
            room = TwilioHelper.getRoomInstance()
        }, 500)

        setConnectedUserProfileList()


        val currentLoggedUser = CurrentMeetingDataSaver.getData()
        Log.d("currcheck", "onCreate: $currentLoggedUser")
        if (CurrentMeetingDataSaver.getData().identity?.trim()?.lowercase()!!
                .contains("C".trim().lowercase())
        ) {
            Log.d("videocheck", "onCreate: you")
            binding.tvUsername.text =
                CurrentMeetingDataSaver.getData().interviewerFirstName + " (You)"
            setBlankBackground(false)
            //working localVideoTrack?.addSink(binding.primaryVideoView)
        }
        else { //working
            /* currentLoggedUser.users?.forEach {
                 if (it.userType.trim().lowercase().equals("C".trim().lowercase())) {
                     binding.tvUsername.text = it.userFirstName + " " + it.userLastName
                     // localVideoTrack?.addSink(binding.primaryVideoView)
                     //  localblankVideoTrack.addSink(binding.primaryVideoView)
                 }
             }*/
        }

        if (CurrentMeetingDataSaver.getData().isPresenter!!) {
            binding.btnAllowToMute.isVisible = true
        }
        else {
            binding.btnAllowToMute.isVisible = false
        }


        binding.muteActionFab.setOnClickListener {

            if (CurrentMeetingDataSaver.getData().isPresenter == true) {
                Log.d(TAG, "onCreate: in mute host")
                handleMuteUnmutebyHost()
            }
            else {
                Log.d(TAG, "onCreate: in mute not host")
                handleMuteUnmute()
            }
        }

        binding.btnAllowToMute.setOnClickListener {
            muteIconUpdateDialog()
        }

        binding.btnEllipsize.setOnClickListener {
            if (binding.llExtraButtons.visibility == View.VISIBLE) {
                binding.llExtraButtons.isVisible = false
            }
            else {
                binding.llExtraButtons.isVisible = true
            }
            //handleAllowToMute()
        }


        currentVisibleUser = VideoTracksBean(null, localVideoTrack!!, "")
        handleObserver()



    }

    fun handleObserver() {
        /*
    viewModel.remoteVideoLiveList.observe(this, Observer { list ->
        list?.let {
            adapter = ConnectedUserListAdapter(this, it, onClick = { pos, action, data ->

            })
            binding.rvConnectedUsers.adapter = adapter
            adapter.notifyDataSetChanged()
            adapter.notifyDataSetChanged()


        }
    })*/
    }


    fun handleMuteUnmute() {
        var icon: Int
        var micStatus = ""
        localAudioTrack?.let {
            val enable = !it.isEnabled
            if (enable == false) {
                Log.d(TAG, "onCreate: mic")
                getMuteStatus { status ->
                    if (status) {
                        Log.d(TAG, "onCreate: mic true")
                        it.enable(enable)
                        icon = R.drawable.ic_img_btn_mic_muted
                        micStatus = getString(R.string.txt_unmute)
                        binding.muteActionFab.setImageResource(R.drawable.ic_img_btn_mic_muted)
                    }
                    else {
                        showToast(this, getString(R.string.txt_mute_note_allowed))
                    }
                }

            }
            else {
                it.enable(enable)
                Log.d(TAG, "onCreate: mic false")
                icon = R.drawable.ic_mic_off_black_24dp
                micStatus = getString(R.string.txt_mute)
                binding.muteActionFab.setBackgroundResource(R.color.black)
                binding.muteActionFab.setImageResource(R.drawable.ic_img_btn_mic_unmute_white)
            }
        }

    }

    fun handleMuteUnmutebyHost() {
        var icon: Int
        var micStatus = ""
        localAudioTrack?.let {
            val enable = !it.isEnabled
            it.enable(enable)
            LocalConfrenseMic.setLocalParticipantMic(enable)
            if (!enable) {
                Log.d(TAG, "handleMuteUnmutebyHost: muted ")
                icon = R.drawable.ic_img_btn_mic_muted
                micStatus = getString(R.string.txt_unmute)
                binding.muteActionFab.setImageResource(R.drawable.ic_img_btn_mic_muted)
            }
            else {
                Log.d(TAG, "handleMuteUnmutebyHost: muted unmuted")
                icon = R.drawable.ic_mic_off_black_24dp
                micStatus = getString(R.string.txt_mute)
                binding.muteActionFab.setBackgroundResource(R.color.black)
                binding.muteActionFab.setImageResource(R.drawable.ic_img_btn_mic_unmute_white)
            }
        }
    }


    fun getMuteStatus(isAllow: (status: Boolean) -> Unit) {
        viewModel.getMuteStatus(
            CurrentMeetingDataSaver.getData().videoAccessCode.toString(),
            onResult = { action, data ->
                when (action) {
                    200 -> {
                        isAllow(data?.AllowToMute!!)
                        Log.d(TAG, "handleAllowToMute: success ${data.AllowToMute}")
                    }
                    400 -> {
                        isAllow(false)
                        Log.d(TAG, "handleAllowToMute: null data")
                    }
                    404 -> {
                        isAllow(false)
                        Log.d(TAG, "handleAllowToMute: not found")
                    }
                    500 -> {
                        isAllow(false)
                        Log.d(TAG, "handleAllowToMute exception ")
                    }

                }
            })
    }

    fun handleAllowToMute() {
        viewModel.setMuteUnmuteStatus(
            AllowToMuteHolder.set(),
            CurrentMeetingDataSaver.getData().interviewModel?.interviewId.toString(),
            onResult = { action, data ->
                when (action) {
                    200 -> {
                        Log.d(TAG, "handleAllowToMute: success ${data?.AllowToMute}")
                    }
                    400 -> {
                        Log.d(TAG, "handleAllowToMute: null data")
                    }
                    404 -> {
                        Log.d(TAG, "handleAllowToMute: not found")
                    }
                    500 -> {

                        Log.d(TAG, "handleAllowToMute exception ")
                    }
                }
            })
    }


    //connected Users
    private fun setConnectedUserProfileList() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true)
        layoutManager.stackFromEnd = true
        binding.rvConnectedUsers.layoutManager = layoutManager

    }

    fun setConnectUser() {
        remoteParticipantVideoListWithCandidate.clear()
        tlist.clear()
        val userDataList = CurrentMeetingDataSaver.getData()

        //current user
        val identityCurrentUser = userDataList.identity
        Log.d("videocheck", "setConnectUser: remotlist size ${remoteParticipantVideoList.size}")

        remoteParticipantVideoList.forEach {
            Log.d("videocheck", "setConnectUser: remotlist looop ${it.remoteParticipant?.identity}")
        }


        if (CurrentMeetingDataSaver.getData().identity!!.contains("C")) {
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
                userDataList.users?.forEach { user ->

                    Log.d(
                        "setconn",
                        "setConnectUser: each parti ${it.remoteParticipant?.identity}  $identityCurrentUser  ${"I" + user.id}"
                    )

                    val userIdentity = "I" + user.id

                    if (userIdentity.equals(it.remoteParticipant?.identity)) {
                        val isContain = tlist.contains(
                            VideoTracksBean(
                                it.remoteParticipant,
                                it.videoTrack,
                                user.userFirstName + " " + user.userLastName
                            )
                        )
                        if (isContain) {
                            Log.d("iscontainsvalue", "setConnectUser: contains")
                        }
                        else {
                            Log.d("iscontainsvalue", "setConnectUser: contains not")
                        }
                        remoteParticipantVideoListWithCandidate.add(
                            VideoTracksBean(
                                it.remoteParticipant,
                                it.videoTrack,
                                user.userFirstName
                            )
                        )
                        tlist.add(
                            VideoTracksBean(
                                it.remoteParticipant,
                                it.videoTrack,
                                user.userFirstName + " " + user.userLastName
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
                userDataList.users?.forEach { user ->

                    Log.d(
                        "setconn",
                        "setConnectUser: each parti I  ${it.remoteParticipant?.identity}  $identityCurrentUser  ${"I" + user.id}"
                    )

                    val identity = user.userType + user.id

                    if (identity.trim().lowercase()
                            .equals(it.remoteParticipant?.identity?.trim()?.lowercase())
                    ) {

                        if (it.remoteParticipant?.identity!!.contains("C")) {
                            currentVisibleUser =
                                VideoTracksBean(
                                    it.remoteParticipant,
                                    it.videoTrack,
                                    user.userFirstName
                                )
                            currentRemoteVideoTrack = it.videoTrack
                            currentRemoteVideoTrack!!.addSink(binding.primaryVideoView)
                            //setCandidateToMainScreen()

                            tlist.add(
                                VideoTracksBean(
                                    it.remoteParticipant,
                                    localVideoTrack!!,
                                    "You"
                                )
                            )

                            //test index 0
                            remoteParticipantVideoListWithCandidate.add(
                                VideoTracksBean(
                                    it.remoteParticipant,
                                    it.videoTrack!!,
                                    user.userFirstName
                                )
                            )
                        }
                        else {
                            tlist.add(
                                VideoTracksBean(
                                    it.remoteParticipant,
                                    it.videoTrack!!,
                                    user.userFirstName + " " + user.userLastName
                                )
                            )
                            /* tlist.add(
                                 VideoTracksBean(
                                     it.remoteParticipant,
                                     localVideoTrack!!,
                                     user.userFirstName + " (You)"
                                 )
                             )*/
                            remoteParticipantVideoListWithCandidate.add(
                                VideoTracksBean(
                                    it.remoteParticipant,
                                    it.videoTrack!!,
                                    user.userFirstName
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



        remoteParticipantVideoList.forEach {
            if (it.remoteParticipant?.identity!!.contains("C")) {
                Log.d("flickervideo", "setConnectUser: in remove sink of local")
                setBlankBackground(false)
              /*  localVideoTrack?.removeSink(binding.primaryVideoView)
                currentRemoteVideoTrack?.removeSink(binding.primaryVideoView)
                currentVisibleUser.videoTrack.removeSink(binding.primaryVideoView)*/
                //working
                it.remoteParticipant?.remoteVideoTracks?.firstOrNull()?.videoTrack?.addSink(binding.primaryVideoView)
            }
            else {

                setBlankBackground(true)
                localVideoTrack?.addSink(binding.primaryVideoView)
            /*    localVideoTrack?.removeSink(binding.primaryVideoView)
                currentRemoteVideoTrack?.removeSink(binding.primaryVideoView)
                currentVisibleUser.videoTrack.removeSink(binding.primaryVideoView)
                */
              /*  tlist.add(
                    VideoTracksBean(
                        it.remoteParticipant,
                        localVideoTrack!!,
                        "You"
                    )
                )
*/
               // currentRemoteVideoTrack?.removeSink(binding.primaryVideoView)
                // it.remoteParticipant.remoteVideoTracks.firstOrNull()?.videoTrack?.addSink(binding.primaryVideoView)
                Log.d("flickervideo", "setConnectUser: in remove sink of local else part")


            }
        }

        remoteParticipantVideoListWithCandidate.add(
            VideoTracksBean(
                null,
                localVideoTrack!!,
                "You"
            )
        )


        CurrentConnectUserList.setListForAddParticipantActivity(
            remoteParticipantVideoListWithCandidate
        )

        if (CurrentMeetingDataSaver.getData().userType!!.contains("C"))
        {
            setBlankBackground(false)
            localVideoTrack?.addSink(binding.primaryVideoView)
        }


        /*  if (!remoteParticipantVideoListWithCandidate.isNullOrEmpty()) {

            val isCandidate = remoteParticipantVideoListWithCandidate.any {
                it.remoteParticipant.identity.contains("C")
            }
            if (isCandidate == true) {
                CurrentConnectUserList.setList(remoteParticipantVideoListWithCandidate)
            }
            else {
                remoteParticipantVideoListWithCandidate.add(
                    VideoTracksBean(
                        remoteParticipantVideoListWithCandidate.get(0).remoteParticipant,
                        localVideoTrack!!,
                        "You"
                    )
                )
                CurrentConnectUserList.setList(remoteParticipantVideoListWithCandidate)
            }
        }*/

        setConnectedUsersListInAdapter(tlist.distinctBy { it.userName })
        /*  if (tlist.size==0)
          {
              currentRemoteVideoTrack?.removeSink(binding.primaryVideoView)
              setBlankBackground(true)
          }
          if(!tlist.any { it.remoteParticipant?.identity!!.contains("C") })
          {
              currentRemoteVideoTrack?.removeSink(binding.primaryVideoView)
              setBlankBackground(true)
          }
          else{
              val candidate=tlist.find { it.remoteParticipant?.identity!!.contains("C") }
              binding.tvUsername.setText(candidate?.userName)
              candidate?.videoTrack!!.addSink(binding.primaryVideoView)
          }
          */
    }

    fun setCandidateToMainScreen() {
        setBlankBackground(false)
        localVideoTrack?.removeSink(binding.primaryVideoView)
        currentRemoteVideoTrack?.removeSink(binding.primaryVideoView)
        binding.tvUsername.setText(currentVisibleUser.userName)
        currentVisibleUser.videoTrack.addSink(binding.primaryVideoView)
    }

    fun setConnectedUsersListInAdapter(list: List<VideoTracksBean>) {
        //val tlist=list.filter { it.remoteParticipant.identity.equals(it.remoteParticipant.identity) }.toMutableList()

        val listt = list.distinctBy { it.remoteParticipant?.identity }.toMutableList()
        Log.d("adapterList", "setConnectedUsersListInAdapter:  listt size  dist ${listt.size}")

        CurrentConnectUserList.setListForVideoActivity(list)

        CurrentConnectUserList.getListForVideoActivity().observe(this, Observer { list ->

            /*  val tlist= mutableListOf<VideoTracksBean>()
              list.forEach {
                  if (!it.remoteParticipant?.identity!!.contains("C"))
                  {
                      tlist.add(it)
                  }
              }
              */
            adapter =
                ConnectedUserListAdapter(this, list, onClick = { pos, action, data, datalist ->
                    Log.d(
                        "checkonclick",
                        "setConnectedUsersListInAdapter: on clicked item ${data.userName}"
                    )

                    try {

                        if (CurrentMeetingDataSaver.getData().userType!!.contains("C"))
                        {
                            handleRecyclerItemClick(pos, data, datalist)
                        }

                        if (!datalist.isNullOrEmpty()) {
                            Log.d("checkonclick", "setConnectedUsersListInAdapter: on clicked")
                            val isCandidateExists =
                                datalist.any { it.remoteParticipant?.identity!!.contains("C") }
                            Log.d("checkonclick", "setConnectedUsersListInAdapter: on clicked")
                            if (isCandidateExists) {
                                Log.d("checkonclick", "setConnectedUsersListInAdapter: candidate ")
                                handleRecyclerItemClick(pos, data, datalist)
                            }
                            else {
                                Log.d(
                                    "checkonclick",
                                    "setConnectedUsersListInAdapter: candidate no"
                                )
                            }
                        }

                    } catch (e: Exception) {
                        Log.d(
                            "checkonclick",
                            "setConnectedUsersListInAdapter: candidate exception ${e.printStackTrace()}"
                        )
                    }

                })

            binding.rvConnectedUsers.adapter = adapter
            adapter.notifyDataSetChanged()
        })
    }

    fun handleRecyclerItemClick(pos: Int, data: VideoTracksBean, list: List<VideoTracksBean>) {

        localVideoTrack?.removeSink(binding.primaryVideoView)
        currentRemoteVideoTrack?.removeSink(binding.primaryVideoView)
        data.videoTrack.removeSink(binding.primaryVideoView)

        setBlankBackground(false)
        val tlist = mutableListOf<VideoTracksBean>()
        tlist.addAll(list)

        val currentMainScreenUser = VideoTracksBean(
            currentVisibleUser.remoteParticipant,
            currentVisibleUser.videoTrack,
            currentVisibleUser.userName
        )
        val clickedItem = VideoTracksBean(data.remoteParticipant, data.videoTrack, data.userName)

        tlist.set(pos, currentMainScreenUser)
        Log.d(TAG, "handleRecyclerItemClick: username ${data.userName}")

        currentMainScreenUser.videoTrack.removeSink(binding.primaryVideoView)

        clickedItem.videoTrack.addSink(binding.primaryVideoView)

        binding.tvUsername.setText(clickedItem.userName)


        currentRemoteVideoTrack = currentVisibleUser.videoTrack
        CurrentConnectUserList.setListForVideoActivity(tlist)
        currentVisibleUser = clickedItem
        //setCandidateToMainScreen()


    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_MIC_PERMISSION_REQUEST_CODE) {
            /*
             * The first two permissions are Camera & Microphone, bluetooth isn't required but
             * enabling it enables bluetooth audio routing functionality.
             */
            val cameraAndMicPermissionGranted =
                ((PackageManager.PERMISSION_GRANTED == grantResults[CAMERA_PERMISSION_INDEX])
                        and (PackageManager.PERMISSION_GRANTED == grantResults[MIC_PERMISSION_INDEX]))

            /*
             * Due to bluetooth permissions being requested at the same time as camera and mic
             * permissions, AudioSwitch should be started after providing the user the option
             * to grant the necessary permissions for bluetooth.
             */
            audioSwitch.start { audioDevices, audioDevice -> updateAudioDeviceIcon(audioDevice) }

            if (cameraAndMicPermissionGranted) {
                createAudioAndVideoTracks()
                Log.d(TAG, "onRequestPermissionsResult: permission granted ")
            }
            else {
                Toast.makeText(
                    this,
                    "R.string.permissions_needed",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        /*
         * If the local video track was released when the app was put in the background, recreate.
         */
        Log.d(TAG, "onResume: ")
        localVideoTrack = if (localVideoTrack == null && checkPermissionForCameraAndMicrophone()) {
            createLocalVideoTrack(
                this,
                true,
                cameraCapturerCompat
            )
        }
        else {
            localVideoTrack
        }
        /**
         * for add local video
         * */
        //   localVideoTrack?.addSink(localVideoView)

        /*
         * If connected to a Room then share the local video track.
         */
        localVideoTrack?.let { localParticipant?.publishTrack(it) }

        /*
         * Update encoding parameters if they have changed.
         */
        localParticipant?.setEncodingParameters(encodingParameters)

        /*
         * Update reconnecting UI
         */

        room?.let {

            /*binding.reconnectingProgressBar.visibility = if (it.state != Room.State.RECONNECTING)
                View.GONE else
                View.VISIBLE*/
            binding.videoStatusTextview.text = "Connected to ${it.name}"
        }
    }

    override fun onPause() {
        /*
         * If this local video track is being shared in a Room, remove from local
         * participant before releasing the video track. Participants will be notified that
         * the track has been removed.
         */
        /*  localVideoTrack?.let { localParticipant?.unpublishTrack(it) }

          /*
           * Release the local video track before going in the background. This ensures that the
           * camera can be used by other applications while this app is in the background.
           */
          localVideoTrack?.release()
          localVideoTrack = null*/
        super.onPause()
    }


    private fun checkPermissions(permissions: Array<String>): Boolean {
        var shouldCheck = true
        for (permission in permissions) {
            shouldCheck = shouldCheck and (PackageManager.PERMISSION_GRANTED ==
                    ContextCompat.checkSelfPermission(this, permission))
        }
        return shouldCheck
    }


    private fun requestPermissions(permissions: Array<String>) {
        var displayRational = false
        for (permission in permissions) {
            displayRational =
                displayRational or ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    permission
                )
        }
        if (displayRational) {
            Toast.makeText(this, "R.string.permissions_needed", Toast.LENGTH_LONG).show()
        }
        else {
            ActivityCompat.requestPermissions(
                this, permissions, CAMERA_MIC_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun checkPermissionForCameraAndMicrophone(): Boolean {
        return checkPermissions(
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        )
    }

    private fun requestPermissionForCameraMicrophoneAndBluetooth() {
        val permissionsList: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        }
        else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        }
        requestPermissions(permissionsList)
    }

    private fun createAudioAndVideoTracks() {
        // Share your microphone
        localAudioTrack = createLocalAudioTrack(this, true)
        // Share your camera
        localVideoTrack = createLocalVideoTrack(
            this,
            true,
            cameraCapturerCompat
        )
    }

    private fun setAccessToken() {


    }

    private fun connectToRoom() {
        audioSwitch.activate()

        val videoc = H264Codec()
        val audioc = G722Codec()
        //    val videotrack = LocalVideoTrack.create(this, true, cameraCapturerCompat)
        //   val audioTrack = LocalAudioTrack.create(this, true)
        room = TwilioHelper.connectToRoom(
            this,
            onRoomEvent = { action ->

            },
            this,
            localAudioTrack!!,
            localVideoTrack!!,
            audioc!!,
            videoc!!
        )

        setDisconnectAction()
    }

    /*
     * The initial state when there is no active room.
     */
    private fun initializeUI() {
        /* binding.connectActionFab.setImageDrawable(
             ContextCompat.getDrawable(
                 this,
                 R.drawable.ic_video_call_white_24dp
             )
         )*/

        // binding.connectActionFab.setOnClickListener(connectActionClickListener())
        //  binding.switchCameraActionFab.setOnClickListener(switchCameraClickListener())
        binding.localVideoActionFab.setOnClickListener(localVideoClickListener())
        binding.muteActionFab.setOnClickListener(muteClickListener())
    }

    /*
     * Show the current available audio devices.
     */
    private fun showAudioDevices() {
        val availableAudioDevices = audioSwitch.availableAudioDevices

        audioSwitch.selectedAudioDevice?.let { selectedDevice ->
            val selectedDeviceIndex = availableAudioDevices.indexOf(selectedDevice)
            val audioDeviceNames = ArrayList<String>()

            for (a in availableAudioDevices) {
                audioDeviceNames.add(a.name)
            }

            AlertDialog.Builder(this)
                .setTitle(R.string.room_screen_select_device)
                .setSingleChoiceItems(
                    audioDeviceNames.toTypedArray<CharSequence>(),
                    selectedDeviceIndex
                ) { dialog, index ->
                    dialog.dismiss()
                    val selectedAudioDevice = availableAudioDevices[index]
                    updateAudioDeviceIcon(selectedAudioDevice)
                    audioSwitch.selectDevice(selectedAudioDevice)
                }.create().show()
        }
    }

    /*
     * Update the menu icon based on the currently selected audio device.
     */
    private fun updateAudioDeviceIcon(selectedAudioDevice: AudioDevice?) {
        val audioDeviceMenuIcon = when (selectedAudioDevice) {
            is BluetoothHeadset -> R.drawable.ic_bluetooth_white_24dp
            is WiredHeadset     -> R.drawable.ic_headset_mic_white_24dp
            is Speakerphone     -> R.drawable.ic_volume_up_white_24dp
            else                -> R.drawable.ic_phonelink_ring_white_24dp
        }

        audioDeviceMenuItem?.setIcon(audioDeviceMenuIcon)
    }

    /*
     * The actions performed during disconnect.
     */
    private fun setDisconnectAction() {
        /*  binding.connectActionFab.setImageDrawable(
              ContextCompat.getDrawable(
                  this,
                  R.drawable.ic_call_end_white_24px
              )
          )*/
    }


    /*
     * Called when participant joins the room
     */
    private fun addRemoteParticipant(remoteParticipant: RemoteParticipant) {
        Log.d("joinuser", "addRemoteParticipant: new ")

        videoTrackList.add(remoteParticipant)

        //addRemoteParticipantVideo(remoteParticipant.remoteVideoTracks.firstOrNull()?.videoTrack!!)
        /*  remoteParticipant.remoteVideoTracks.mapIndexed { index, remoteVideoTrackPublication ->
              remoteVideoTrackPublication.remoteVideoTrack
              remoteParticipant.remoteAudioTracks[index]

              if (remoteVideoTrackPublication.isTrackSubscribed) {
                  Log.d("joinuser", "addRemoteParticipant method: track subscribed ")
                  remoteVideoTrackPublication.remoteVideoTrack?.let {
                      TwilioHelper.videoTrackList.add(remoteVideoTrackPublication)
                      addRemoteParticipantVideo(it,remoteParticipant.remoteAudioTracks[0].remoteAudioTrack!!,remoteParticipant)
                      Log.d("joinuser", "addRemoteParticipant method: add participant ")
                  }
              }
          }
  */

        remoteParticipant.remoteVideoTracks.firstOrNull()?.let { remoteVideoTrackPublication ->
            Log.d("joinuser", "addRemoteParticipant method: add out is tracksubs ")
            //if (remoteVideoTrackPublication.isTrackSubscribed) {
            Log.d("joinuser", "addRemoteParticipant method: add participant ")
            remoteVideoTrackPublication.remoteVideoTrack?.let {
                TwilioHelper.videoTrackList.add(remoteVideoTrackPublication)
                addRemoteParticipantVideo(it, remoteParticipant)
            }
            //}
        }


        /*  remoteParticipant.remoteVideoTracks.firstOrNull()?.let { remoteVideoTrackPublication ->
              if (remoteVideoTrackPublication.isTrackSubscribed) {

                  remoteVideoTrackPublication.remoteVideoTrack?.let {videoT->
                      //TwilioHelper.videoTrackList.add(remoteVideoTrackPublication)
                      Log.d("joinuser", "addRemoteParticipant method: video added ")

                          addRemoteParticipantVideo(videoT,remoteParticipant.remoteAudioTracks.firstOrNull()?.audioTrack!!,remoteParticipant)
                          Log.d("joinuser", "addRemoteParticipant method: audio added ")

                  }
              }
          }*/
        /*
                    remoteParticipant.remoteAudioTracks.firstOrNull()?.let {remoteAudio->
                        if (remoteAudio.isTrackSubscribed)
                        {

                            Log.d("joinuser", "addRemoteParticipant method: audio added ")
                        }
                    }
        */
        /*       if(audioTrack!=null)
               {
                   Log.d("audiotrackcheck", "addRemoteParticipant: not null")
               }else
               {
                   Log.d("audiotrackcheck", "addRemoteParticipant: null")
               }

               if(videoTrack!=null)
               {
                   Log.d("audiotrackcheck", "addRemoteParticipant: not null")
               }else
               {
                   Log.d("audiotrackcheck", "addRemoteParticipant: null")
               }
   */


        // addRemoteParticipantVideo(videoTrack!!,audioTrack!!,remoteParticipant)

        TwilioHelper.setParticipantListener(remoteParticipant)


        /* }catch (e:Exception)
         {
             Log.d("joinuser", "addRemoteParticipant: exception ${e.printStackTrace()}")
         }*/


        /* TwilioHelper.addRemoteParticipant(
                remoteParticipant,
                binding,
                onAddParticipant = { remoteVideoTrack ->
                    Log.d(TAG, "addRemoteParticipant: call back remote video participant")
                    addRemoteParticipantVideo(remoteVideoTrack)
                })*/
    }

    /*
     * Set primary view as renderer for participant video track
     */
    private fun addRemoteParticipantVideo(
        videoTrack: VideoTrack,
        remoteParticipant: RemoteParticipant
    ) {

        /*  if (!remoteParticipantVideoList.isNullOrEmpty())
          {
              remoteParticipantVideoList.forEach {
                  if (it.remoteParticipant.identity.equals(remoteParticipant.identity))
                  {
                      remoteParticipantVideoList.set(remoteParticipantVideoList.indexOf(it),VideoTracksBean(remoteParticipant,videoTrack,"",true))
                  }
              }
          }else
          {

          }*/


        /**testing working*/
        /* if (!remoteParticipantVideoList.isNullOrEmpty()) {

             remoteParticipantVideoList.mapIndexed { index, data ->

                 if (data.remoteParticipant?.identity.equals(remoteParticipant.identity)) {
                     remoteParticipantVideoList.set(
                         index,
                         VideoTracksBean(remoteParticipant!!, videoTrack!!, "")
                     )
                 }
                 else {
                     remoteParticipantVideoList.add(
                         VideoTracksBean(
                             remoteParticipant!!,
                             videoTrack!!,
                             ""
                         )
                     )
                 }
             }
         }
         else {
             remoteParticipantVideoList.add(VideoTracksBean(remoteParticipant!!, videoTrack!!, ""))
         }*/


        remoteParticipantVideoList.add(
            VideoTracksBean(
                remoteParticipant!!,
                videoTrack!!,
                ""
            )
        )
        //viewModel.setConnectUser(remoteParticipantVideoList,localVideoTrack!!)

        //https://ui2.veriklick.in/video-session/WrbqaObRGjzWowSZFVo8
        //  remoteParticipantVideoList.add(VideoTracksBean(remoteParticipant,videoTrack,"",true))

        Log.d(
            "addpartic",
            "addRemoteParticipantVideo: list size is after ${remoteParticipantVideoList.size}"
        )
        currentRemoteVideoTrack = videoTrack

        Log.d(
            "addpartic",
            "addRemoteParticipantVideo: remote after ${remoteParticipant.identity}  ${remoteParticipantVideoList.size}"
        )

        //testing working
        setConnectUser()
        // viewModel.setConnectUser(remoteParticipantVideoList,localVideoTrack!!)
        Log.d(
            "checkiden",
            "addRemoteParticipantVideo: in remotepart added ${remoteParticipant.identity}"
        )

    }

    /*
     * Called when participant leaves the room
     */
    private fun removeRemoteParticipant(remoteParticipant: RemoteParticipant) {
        TwilioHelper.removeRemoteParticipant(remoteParticipant, binding) { videoTrack ->
            removeParticipantVideo(videoTrack)
        }
        // moveLocalVideoToPrimaryView()
        Log.d(TAG, "removeRemoteParticipant: remove participant ")
        /*
            remoteParticipant.remoteVideoTracks.firstOrNull()?.remoteVideoTrack?.removeSink(binding.primaryVideoView)
            localblankVideoTrack.addSink(binding.primaryVideoView)
        */
        val templist = mutableListOf<VideoTracksBean>()

        try {

            templist.addAll(remoteParticipantVideoList)
            templist.forEach {
                if (remoteParticipant.identity.equals(it.remoteParticipant?.identity)) {
                    Log.d(
                        "removeParti",
                        "removeRemoteParticipant: ${it.userName}  ${it.remoteParticipant?.identity}"
                    )
                    it.videoTrack.removeSink(binding.primaryVideoView)
                    remoteParticipantVideoList.remove(it)

                }
            }
        }
        /**testing working*/
        /*  templist.addAll(remoteParticipantVideoList)
          templist.forEach {
              if (remoteParticipant.identity.equals(it.remoteParticipant?.identity)) {
                  Log.d(
                      "removeParti",
                      "removeRemoteParticipant: ${it.userName}  ${it.remoteParticipant?.identity}"
                  )
                  remoteParticipantVideoList.remove(it)
              }
          }*/
        /*remoteParticipantVideoListWithCandidate.forEach {
            if (remoteParticipant.identity.equals(it.remoteParticipant.identity))
            {
                if (CurrentMeetingDataSaver.getData().identity.equals(remoteParticipant.identity))
                {

                }else
                {
                    remoteParticipantVideoListWithCandidate.remove(it)
                }
            }
        }*/
        catch (e: Exception) {
            Log.d("exceptionInRemoving", "removeRemoteParticipant: ${e.printStackTrace()}")
        }
        //testing working
        setConnectUser()
        // viewModel.setConnectUser(remoteParticipantVideoList,localVideoTrack!!)
    }


    private fun setBlankBackground(isVisible: Boolean) {
        binding.ivBlankView.isVisible = isVisible
    }

    private fun removeParticipantVideo(videoTrack: VideoTrack) {

        videoTrack.removeSink(binding.primaryVideoView)

        // localblankVideoTrack.enable(false)
        //localblankVideoTrack.addSink(binding.primaryVideoView)
        Log.d("onremovepart", "removeRemoteParticipant: remove participant track video ")
    }

    private fun moveLocalVideoToPrimaryView() {
        if (binding.thumbnailVideoView.visibility == View.VISIBLE) {
            binding.thumbnailVideoView.visibility = View.GONE
            with(localVideoTrack) {
                this?.removeSink(binding.thumbnailVideoView)
                this?.addSink(binding.primaryVideoView)
            }
            localVideoView = binding.primaryVideoView
            binding.primaryVideoView.mirror = cameraCapturerCompat.cameraSource ==
                    CameraCapturerCompat.Source.FRONT_CAMERA
        }
    }

    private fun switchCameraClickListener(): View.OnClickListener {
        return View.OnClickListener {
            val cameraSource = cameraCapturerCompat.cameraSource
            cameraCapturerCompat.switchCamera()
            if (binding.thumbnailVideoView.visibility == View.VISIBLE) {
                binding.thumbnailVideoView.mirror =
                    cameraSource == CameraCapturerCompat.Source.BACK_CAMERA
            }
            else {
                binding.primaryVideoView.mirror =
                    cameraSource == CameraCapturerCompat.Source.BACK_CAMERA
            }
        }
    }

    private fun localVideoClickListener(): View.OnClickListener {
        return View.OnClickListener {
            /*
             * Enable/disable the local video track
             */
            localVideoTrack?.let {
                var isTint = false
                val enable = !it.isEnabled
                it.enable(enable)
                val icon: Int
                // localVideoTrack!!.enable(enable)
                if (enable) {
                    icon = R.drawable.ic_img_btn_video
                    // binding.switchCameraActionFab.show()
                    isTint = false
                }
                else {
                    icon = R.drawable.ic_img_video_off_black
                    isTint = true
                    // binding.switchCameraActionFab.hide()
                }
                if (isTint) {
                    binding.localVideoActionFab.setBackgroundResource(R.color.white)
                    binding.localVideoActionFab.setImageDrawable(
                        ContextCompat.getDrawable(this@VideoActivity, icon)
                    )
                }
                else {
                    binding.localVideoActionFab.setBackgroundResource(R.color.black)
                    binding.localVideoActionFab.setImageDrawable(
                        ContextCompat.getDrawable(this@VideoActivity, icon)
                    )
                }
            }
        }
    }

    private fun muteClickListener(): View.OnClickListener {
        return View.OnClickListener {

            // muteIconUpdateDialog()
        }
    }

    fun muteIconUpdateDialog() {
        val dialog = Dialog(this)

        val dialogView = DataBindingUtil.inflate<LayoutMuteMicUpdateBinding>(
            LayoutInflater.from(this),
            R.layout.layout_mute_mic_update,
            null,
            false
        )
        dialog.setContentView(dialogView.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var icon: Int
        var micStatus = ""
        dialogView.tvMicmute.text = getString(R.string.txt_allow_to_mute)

        getMuteStatus {
            if (it) {
                dialogView.ivMic.setBackgroundResource(R.drawable.ic_img_mic_unmuted_black_mini)
            }
            else {
                dialogView.ivMic.setBackgroundResource(R.drawable.ic_mic_mute_white)
            }
        }
        dialogView.parentLinearLayout.setOnClickListener {
            handleAllowToMute()
            dialog.dismiss()
        }
        dialog.create()
        dialog.show()
    }

    override fun onParticipantConnect(room: Room) {
        try {

            localParticipant = room.localParticipant
            binding.videoStatusTextview.text = "Connected to ${room.name}"
            title = room.name
            Log.d(TAG, "onParticipantConnect: connect in connected  ${room.name}")
            Log.d(
                TAG,
                "onParticipantConnect: connect in connected  ${room.localParticipant?.identity}"
            )

            remoteParticipantVideoListWithCandidate.add(
                VideoTracksBean(
                    null,
                    localVideoTrack!!,
                    "You"
                )
            )
            // viewModel.setConnectUser(remoteParticipantVideoList,localVideoTrack!!)

            CurrentConnectUserList.setListForAddParticipantActivity(
                remoteParticipantVideoListWithCandidate
            )

            Log.d(
                "partichecktotal",
                "onParticipantConnect: parti connected total ${room.remoteParticipants.size}"
            )

            addRemoteParticipant(room.remoteParticipants.firstOrNull()!!)

            room.remoteParticipants.forEach {
                // remoteParticipantVideoList.add(VideoTracksBean(it,it.remoteVideoTracks.firstOrNull()?.videoTrack!!,"",true))
                addRemoteParticipant(it!!)
                Log.d(
                    "partichecktotal",
                    "onParticipantConnect: in on parti connect new ${it.identity} "
                )
            }


        } catch (e: Exception) {
            Log.d(TAG, "onConnected: exception on connected ${e.message} ")
        }
    }

    override fun onParticipantDisconnect(room: Room) {
        binding.videoStatusTextview.text = "Disconnected from ${room.name}"
    }

    override fun onParticipantReconnect(room: Room) {
        binding.videoStatusTextview.text = "participant reconnected to ${room.name}"

    }

    override fun onParticipantReconnecting(room: Room) {
        binding.videoStatusTextview.text = "Reconnecting to ${room.name}"
    }

    override fun onConnectFailure(room: Room, e: TwilioException) {
        binding.videoStatusTextview.text = "Failed to connect"
        audioSwitch.deactivate()
    }

    override fun onDestroy() {
        screenShareCapturerManager.endForeground()
        screenShareCapturerManager.unbindService()
        super.onDestroy()
    }


    override fun onDisconnected(room: Room, e: TwilioException?) {
        localParticipant = null
        binding.videoStatusTextview.text = "Disconnected from ${room.name}"
        // CurrentMeetingDataSaver.setIsRoomDisconnected(true)
        // reconnectingProgressBar.visibility = View.GONE

        this@VideoActivity.room = null


        // Only reinitialize the UI if disconnect was not called from onDestroy()
        if (!disconnectedFromOnDestroy) {
            audioSwitch.deactivate()
            initializeUI()
            //  moveLocalVideoToPrimaryView()
        }
        finish()
    }

    override fun onParticipantConnected(room: Room, participant: RemoteParticipant) {
        /***/
        //remoteParticipantVideoList.add(VideoTracksBean(participant,room.remoteParticipants.,"",true))
        Log.d("checklists", "onParticipantConnected: called")

        addRemoteParticipant(participant!!)


        // participant.videoTracks.firstOrNull()?.videoTrack?.addSink(binding.videoviewtesting)
        /* room.remoteParticipants.firstOrNull()?.let {
             videoTrackList.add(it)
         }
         TwilioHelper.setParticipantListener(participant)
         setConnectUser(participant)
 */
        //  videoTrackList.get(0).videoTrack?.addSink(binding.videoviewtesting)
        // setConnectUser(room.remoteParticipants.firstOrNull(),videoTrackList)
        Log.d(TAG, "onParticipantConnected: remote user connected ")
        /*TwilioHelper.addRemoteParticipant(participant, binding) { videoTrack ->

        }*/
    }

    override fun onParticipantDisconnected(room: Room, participant: RemoteParticipant) {

        Log.d(TAG, "onParticipantDisconnect: on partic disconnect")
        /*  remoteParticipantVideoList.forEach {
              Log.d(TAG, "onParticipantDisconnect:  ${it.remoteParticipant.identity}  ${room.remoteParticipants.firstOrNull()?.identity} ")
              if (it.remoteParticipant.identity.trim().lowercase().equals(room.remoteParticipants.firstOrNull()?.identity?.trim()?.lowercase()))
              {
                  remoteParticipantVideoList.remove(it)
              }
          }*/
        Log.d(
            TAG,
            "onParticipantDisconnect: on partic disconnect list ${remoteParticipantVideoList.size}"
        )
        Log.d("listcheckk", "onParticipantDisconnect: on partic disconnect")
        TwilioHelper.setParticipantListener(participant!!)

        //      setConnectedUsersListInAdapter(remoteParticipantVideoList)


        removeRemoteParticipant(participant)
        //  currentRemoteVideoTrack?.removeSink(binding.primaryVideoView)
        //  localVideoTrack?.addSink(binding.primaryVideoView)
    }

    override fun onRecordingStarted(room: Room) {
        /*
             * Indicates when media shared to a Room is being recorded. Note that
             * recording is only available in our Group Rooms developer preview.
             */
        binding.btnRecordVideo.setImageResource(R.drawable.ic_img_sc_recording_red)
        Log.d(TAG, "onRecordingStarted")
    }

    override fun onRecordingStopped(room: Room) {
        /*
           * Indicates when media shared to a Room is no longer being recorded. Note that
           * recording is only available in our Group Rooms developer preview.
           */
        binding.btnRecordVideo.setImageResource(R.drawable.ic_img_sc_recording_white)
        Log.d(TAG, "onRecordingStopped")
    }


    /**
     * RemoteParticipant events listener
     */
    override fun onAudioTrackPublished(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
        Log.i(
            TAG, "onAudioTrackPublished: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteAudioTrackPublication: sid=${remoteAudioTrackPublication.trackSid}, " +
                    "enabled=${remoteAudioTrackPublication.isTrackEnabled}, " +
                    "subscribed=${remoteAudioTrackPublication.isTrackSubscribed}, " +
                    "name=${remoteAudioTrackPublication.trackName}]"
        )
        binding.videoStatusTextview.text = "onAudioTrackAdded"
    }

    override fun onAudioTrackUnpublished(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
        Log.i(
            TAG, "onAudioTrackUnpublished: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteAudioTrackPublication: sid=${remoteAudioTrackPublication.trackSid}, " +
                    "enabled=${remoteAudioTrackPublication.isTrackEnabled}, " +
                    "subscribed=${remoteAudioTrackPublication.isTrackSubscribed}, " +
                    "name=${remoteAudioTrackPublication.trackName}]"
        )
        binding.videoStatusTextview.text = "onAudioTrackRemoved"
    }

    override fun onDataTrackPublished(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication
    ) {
        Log.i(
            TAG, "onDataTrackPublished: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteDataTrackPublication: sid=${remoteDataTrackPublication.trackSid}, " +
                    "enabled=${remoteDataTrackPublication.isTrackEnabled}, " +
                    "subscribed=${remoteDataTrackPublication.isTrackSubscribed}, " +
                    "name=${remoteDataTrackPublication.trackName}]"
        )
        binding.videoStatusTextview.text = "onDataTrackPublished"
    }

    override fun onDataTrackUnpublished(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication
    ) {
        Log.i(
            TAG, "onDataTrackUnpublished: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteDataTrackPublication: sid=${remoteDataTrackPublication.trackSid}, " +
                    "enabled=${remoteDataTrackPublication.isTrackEnabled}, " +
                    "subscribed=${remoteDataTrackPublication.isTrackSubscribed}, " +
                    "name=${remoteDataTrackPublication.trackName}]"
        )
        binding.videoStatusTextview.text = "onDataTrackUnpublished"
    }

    override fun onVideoTrackPublished(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
        Log.i(
            TAG, "onVideoTrackPublished: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteVideoTrackPublication: sid=${remoteVideoTrackPublication.trackSid}, " +
                    "enabled=${remoteVideoTrackPublication.isTrackEnabled}, " +
                    "subscribed=${remoteVideoTrackPublication.isTrackSubscribed}, " +
                    "name=${remoteVideoTrackPublication.trackName}]"
        )
        binding.videoStatusTextview.text = "onVideoTrackPublished"
    }

    override fun onVideoTrackUnpublished(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
        Log.i(
            TAG, "onVideoTrackUnpublished: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteVideoTrackPublication: sid=${remoteVideoTrackPublication.trackSid}, " +
                    "enabled=${remoteVideoTrackPublication.isTrackEnabled}, " +
                    "subscribed=${remoteVideoTrackPublication.isTrackSubscribed}, " +
                    "name=${remoteVideoTrackPublication.trackName}]"
        )
        binding.videoStatusTextview.text = "onVideoTrackUnpublished"
    }

    override fun onAudioTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication,
        remoteAudioTrack: RemoteAudioTrack
    ) {
        Log.i(
            TAG, "onAudioTrackSubscribed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteAudioTrack: enabled=${remoteAudioTrack.isEnabled}, " +
                    "playbackEnabled=${remoteAudioTrack.isPlaybackEnabled}, " +
                    "name=${remoteAudioTrack.name}]"
        )
        binding.videoStatusTextview.text = "onAudioTrackSubscribed"
    }

    override fun onAudioTrackUnsubscribed(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication,
        remoteAudioTrack: RemoteAudioTrack
    ) {
        Log.i(
            TAG, "onAudioTrackUnsubscribed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteAudioTrack: enabled=${remoteAudioTrack.isEnabled}, " +
                    "playbackEnabled=${remoteAudioTrack.isPlaybackEnabled}, " +
                    "name=${remoteAudioTrack.name}]"
        )
        binding.videoStatusTextview.text = "onAudioTrackUnsubscribed"
    }

    override fun onAudioTrackSubscriptionFailed(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication,
        twilioException: TwilioException
    ) {
        Log.i(
            TAG, "onAudioTrackSubscriptionFailed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteAudioTrackPublication: sid=${remoteAudioTrackPublication.trackSid}, " +
                    "name=${remoteAudioTrackPublication.trackName}]" +
                    "[TwilioException: code=${twilioException.code}, " +
                    "message=${twilioException.message}]"
        )
        binding.videoStatusTextview.text = "onAudioTrackSubscriptionFailed"
    }

    override fun onDataTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication,
        remoteDataTrack: RemoteDataTrack
    ) {
        Log.i(
            TAG, "onDataTrackSubscribed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteDataTrack: enabled=${remoteDataTrack.isEnabled}, " +
                    "name=${remoteDataTrack.name}]"
        )
        binding.videoStatusTextview.text = "onDataTrackSubscribed"
    }

    override fun onDataTrackUnsubscribed(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication,
        remoteDataTrack: RemoteDataTrack
    ) {
        Log.i(
            TAG, "onDataTrackUnsubscribed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteDataTrack: enabled=${remoteDataTrack.isEnabled}, " +
                    "name=${remoteDataTrack.name}]"
        )
        binding.videoStatusTextview.text = "onDataTrackUnsubscribed"
    }

    override fun onDataTrackSubscriptionFailed(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication,
        twilioException: TwilioException
    ) {
        Log.i(
            TAG, "onDataTrackSubscriptionFailed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteDataTrackPublication: sid=${remoteDataTrackPublication.trackSid}, " +
                    "name=${remoteDataTrackPublication.trackName}]" +
                    "[TwilioException: code=${twilioException.code}, " +
                    "message=${twilioException.message}]"
        )
        binding.videoStatusTextview.text = "onDataTrackSubscriptionFailed"
    }

    override fun onVideoTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        remoteVideoTrack: RemoteVideoTrack
    ) {
        Log.i(
            TAG, "onVideoTrackSubscribed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteVideoTrack: enabled=${remoteVideoTrack.isEnabled}, " +
                    "name=${remoteVideoTrack.name}]"
        )
        binding.videoStatusTextview.text = "onVideoTrackSubscribed"
        /***/
        try {
            addRemoteParticipantVideo(remoteVideoTrack, remoteParticipant)
        } catch (e: Exception) {
            Log.d("exceptionvideo", "onVideoTrackSubscribed: ${e.printStackTrace()}")
        }
    }

    override fun onVideoTrackUnsubscribed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        remoteVideoTrack: RemoteVideoTrack
    ) {
        Log.i(
            TAG, "onVideoTrackUnsubscribed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteVideoTrack: enabled=${remoteVideoTrack.isEnabled}, " +
                    "name=${remoteVideoTrack.name}]"
        )
        binding.videoStatusTextview.text = "onVideoTrackUnsubscribed"

        if (remoteParticipant.identity.contains("C")) {
            currentRemoteVideoTrack?.removeSink(binding.primaryVideoView)
            localblankVideoTrack?.addSink(binding.primaryVideoView)
            setBlankBackground(true)
            //remoteParticipantVideoListWithCandidate.removeAt(0)
            //CurrentConnectUserList.setListForAddParticipantActivity(remoteParticipantVideoListWithCandidate)
        }


        // localVideoTrack?.addSink(binding.primaryVideoView)

        removeParticipantVideo(remoteVideoTrack)
    }

    override fun onVideoTrackSubscriptionFailed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        twilioException: TwilioException
    ) {
        Log.i(
            TAG, "onVideoTrackSubscriptionFailed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteVideoTrackPublication: sid=${remoteVideoTrackPublication.trackSid}, " +
                    "name=${remoteVideoTrackPublication.trackName}]" +
                    "[TwilioException: code=${twilioException.code}, " +
                    "message=${twilioException.message}]"
        )
        binding.videoStatusTextview.text = "onVideoTrackSubscriptionFailed"
        Snackbar.make(
            binding.btnEllipsize,
            "Failed to subscribe to ${remoteParticipant.identity}",
            Snackbar.LENGTH_LONG
        )
            .show()
    }

    override fun onAudioTrackEnabled(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
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

    override fun onAudioTrackDisabled(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
    }

    override fun onViewClicked(view: View) {
        when (view.id) {
            R.id.btn_add_user_videoActivity -> {
                showAddUserActivity()
            }
            R.id.btn_allow_to_mute          -> {
                muteIconUpdateDialog()
                Log.d(TAG, "onViewClicked: mute others")
            }
            R.id.btn_show_documents         -> {
                startActivity(Intent(this, DocumentViewerActivity::class.java))
            }
            R.id.btn_share_screen           -> {
                shareScreen()
            }
            R.id.btn_record_video           -> {
                handleVideoRecording()
                //Log.d("checkvideostatus", "onViewClicked: status ${VideoRecordingStatusHolder.setStatus()}")
            }
        }
    }

    fun setAllSinkRemove()
    {
        localVideoTrack?.removeSink(binding.primaryVideoView)
        currentRemoteVideoTrack?.removeSink(binding.primaryVideoView)
        currentVisibleUser.videoTrack.removeSink(binding.primaryVideoView)
    }

    fun shareScreen() {

       screenShareCapturerManager.startForeground()

        if (screenCapturer == null) {
            Log.d(TAG, "shareScreen: screen capture null")
            val mediaProjectionManager =getSystemService(MEDIA_PROJECTION_SERVICE) as  MediaProjectionManager
           // startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(),100)
            resultLauncher.launch(mediaProjectionManager.createScreenCaptureIntent())
        }
        else {
            Log.d(TAG, "shareScreen: screen capture  null capturing start")
            startScreenCapture()
        }
    }



    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
               showToast(this,getString(R.string.txt_permissionScreenSharingGranted))
                screenCapturer= ScreenCapturer(this@VideoActivity,result.resultCode,data!!, screenCapturerListener)
                startScreenCapture()
                Log.d(TAG, "start recording listener: ")
            }else
            {
                showToast(this,getString(R.string.txt_permissionScreenSharingNotGranted))
            }
        }


    fun startScreenCapture()
    {
        Log.d(TAG, "startScreenCapture: start capturing method ")
       // setAllSinkRemove()
       // setBlankBackground(false)
        screenShareTrack=LocalVideoTrack.create(this,true,screenCapturer!!)!!
        screenShareTrack.enable(true)

        TwilioHelper.getRoomInstance()?.localParticipant?.unpublishTrack(localVideoTrack!!)

        TwilioHelper.getRoomInstance()?.localParticipant?.publishTrack(screenShareTrack)

        //screenShareTrack.addSink(binding.primaryVideoView)
    }

    private val screenCapturerListener=object : ScreenCapturer.Listener{
        override fun onScreenCaptureError(errorDescription: String) {
            Log.e(TAG, "Screen capturer error: $errorDescription")
            //stopScreenCapture()
            //  setBlankBackground(true)
            showToast(this@VideoActivity,getString(R.string.txt_screen_sharing_error)+errorDescription.toString())
        }

        override fun onFirstFrameAvailable() {
           // localParticipant?.publishTrack(localVideoTrack!!)
            Log.d(TAG, "First frame from screen capturer available")
        }

    }


    fun showAddUserActivity() {
        val intent = Intent(this, MemberListActivity::class.java)
        startActivity(intent)
    }

    fun handleVideoRecording() {
        showProgressDialog()
        viewModel.getRecordingStatusUpdate(CurrentMeetingDataSaver.getData().interviewModel?.interviewId!!,
            CurrentMeetingDataSaver.getRoomData().firstOrNull()?.roomName!!,
            VideoRecordingStatusHolder.setStatus(),
            CurrentMeetingDataSaver.getData().interviewModel?.status!!,
            //CurrentMeetingDataSaver.getData().interviewModel
            "recStart or not",
            CurrentMeetingDataSaver.getData().videoAccessCode!!,
            onResult = { action: Int, data: BodyUpdateRecordingStatus? ->
                when (action) {
                    200 -> {
                        dismissProgressDialog()
                        /*  if (data?.RecordingStatus.equals("include")) {
                              binding.btnRecordVideo.setImageResource(R.drawable.ic_img_sc_recording_red)
                          }
                          else {
                              binding.btnRecordVideo.setImageResource(R.drawable.ic_img_sc_recording_white)
                          }
                          Log.d(
                              "checkvideostatus",
                              "handleVideoRecording: data stats ${data?.RecordingStatus}  ${data?.Message}  ${data?.StatusCode}"
                          )*/
                    }
                    400 -> {
                        dismissProgressDialog()
                        Log.d(
                            "checkvideostatus",
                            "handleVideoRecording: data stats ${data?.RecordingStatus}  ${data?.Message}  ${data?.StatusCode}"
                        )
                    }
                    404 -> {
                        dismissProgressDialog()
                        Log.d(
                            "checkvideostatus",
                            "handleVideoRecording: data stats ${data?.RecordingStatus}  ${data?.Message}  ${data?.StatusCode}"
                        )
                    }
                }
            }
        )
    }

}
