package com.ui.activities.twilioVideo

//import com.ui.activities.chat.ChatActivity


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.data.*
import com.data.dataHolders.*
import com.data.helpers.RoomListenerCallback
import com.data.helpers.RoomParticipantListener
import com.data.helpers.TwilioHelper
import com.data.twiliochat.TwilioChatHelper
import com.ui.activities.twilioVideo.meetingnotificationservice.MeetingServiceManager
import com.domain.BaseModels.BodyUpdateRecordingStatus
import com.domain.BaseModels.TokenResponseBean
import com.domain.BaseModels.VideoTracksBean
import com.domain.OnViewClicked
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
import com.ui.activities.chat.ChatActivity
import com.ui.activities.documentviewer.DocumentViewerActivity
import com.ui.activities.feedBack.ActivityFeedBackForm
import com.ui.activities.meetingmemberslist.MemberListActivity
import com.ui.activities.twilioVideo.ScreenSharingCapturing.ScreenShareCapturerManager
import com.ui.listadapters.ConnectedUserListAdapter
import dagger.hilt.android.AndroidEntryPoint
import tvi.webrtc.VideoSink
import kotlin.properties.Delegates


@AndroidEntryPoint
class VideoActivity : AppCompatActivity(), RoomListenerCallback, RoomParticipantListener,
    OnViewClicked {

  /*  private val incomingCallRecevier = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            Log.d(TAG, "onReceive: oncall recive call end")
            endCall()
        }
    }*/
    lateinit var binding: ActivityTwilioVideoBinding
    private val globalParticipantList = mutableListOf<VideoTracksBean>()
    private val CAMERA_MIC_PERMISSION_REQUEST_CODE = 1
    private val TAG = "twiliovideotest"
    private val CAMERA_PERMISSION_INDEX = 0
    private val MIC_PERMISSION_INDEX = 1
    lateinit var adapter: ConnectedUserListAdapter
    private var room: Room? = null
    private var localParticipant: LocalParticipant? = null
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
    val viewModel by viewModels<VideoViewModel>()
    val tlist = mutableListOf<VideoTracksBean>()
    private val remoteParticipantVideoList = mutableListOf<VideoTracksBean>()
    private val remoteParticipantVideoListWithCandidate = mutableListOf<VideoTracksBean>()

    private lateinit var currentRemoteParticipant: RemoteParticipant
    private lateinit var screenShareTrack: LocalVideoTrack
    private lateinit var screenShareCapturerManager: ScreenShareCapturerManager
    private lateinit var meetingManager: MeetingServiceManager
    private var screenCapturer: ScreenCapturer? = null


    //  private lateinit var primaryVideoView:VideoView
    // private lateinit var thumbnailVideoView:VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTwilioVideoBinding.inflate(LayoutInflater.from(this))
        binding.onClick = this
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.root)
//        viewModel = ViewModelProvider(this).get(VideoViewModel::class.java)
        actionBar?.hide()
        screenShareCapturerManager = ScreenShareCapturerManager()
        meetingManager = MeetingServiceManager()

        currentVisibleUser=VideoTracksBean()

        screenShareCapturerManager.ScreenCapturerManager(this)
        meetingManager.meetingManager(this)
        //   primaryVideoView=findViewById(R.id.primary_video_view)
        //  thumbnailVideoView=findViewById(R.id.thumbnail_video_view)

        //Log.d("checkobj", "onCreate: ${CurrentMeetingDataSaver.getData()}")
        binding.btnEndCall.setOnClickListener {
            endMeetingAlert()
            //endCall()
        }

        binding.localVideoActionFab.setOnClickListener {

        }

        //handle incoming call
       /* LocalBroadcastManager.getInstance(this).registerReceiver(
            incomingCallRecevier,
            IntentFilter(AppConstants.IN_COMING_CALL_ACTION)
        )*/



        CurrentMeetingDataSaver.getData()?.users?.forEach {
            if (it.userType.contains("C")) {
               binding.tvNoParticipant.text =
                    "Waiting to join " + it.userFirstName + " " + it.userLastName
            }
        }

        binding.btnEllipsize.setOnClickListener {
            Log.d(TAG, "onCreate: layout clicked ")
            if (binding.llExtraButtons.isVisible) {
                binding.llExtraButtons.visibility = View.GONE
            } else {
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
            binding.btnSendMessage.isClickable = false
            /*Handler(Looper.getMainLooper()).postDelayed({
                binding.btnSendMessage.isClickable=true
            },2000)*/



            if (checkInternet()) {

                binding.btnSendMessage.isClickable = true
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra(AppConstants.CONNECT_PARTICIPANT,remoteParticipantVideoList.size.toString())
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)


              /*  viewModel.getChatToken(identity.toString(), response = { data, code ->
                    Log.d("chatcheck", "onCreate: data $data  chat channel $identity")
                    binding.btnSendMessage.isClickable = true
                    val intent = Intent(this, ChatActivity::class.java)
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
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                })
                */

            } else {
                Snackbar.make(
                    binding.root,
                    getString(com.example.twillioproject.R.string.txt_no_internet_connection),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
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
        } else {
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
//            room = TwilioHelper.getRoomInstance()
        }, 500)

        setConnectedUserProfileList()



        handleParticipantsViews()

        //  binding.btnAllowToMute.isVisible = CurrentMeetingDataSaver.getData().isPresenter!!

        binding.muteActionFab.setOnClickListener {

            handleMuteUnmutebyHost()
            /*
            if (CurrentMeetingDataSaver.getData().isPresenter == true) {
                Log.d(TAG, "onCreate: in mute host")
                handleMuteUnmutebyHost()
            } else {
                Log.d(TAG, "onCreate: in mute not host")
                handleMuteUnmute()
            }*/
        }
//
//        binding.btnAllowToMute.setOnClickListener {
//            muteIconUpdateDialog()
//        }

        binding.btnEllipsize.setOnClickListener {
            if (binding.llExtraButtons.visibility == View.VISIBLE) {
                binding.llExtraButtons.isVisible = false
            } else {
                binding.llExtraButtons.isVisible = true
            }
            //handleAllowToMute()
        }

        localParticipant?.let {
            currentVisibleUser =
                VideoTracksBean("C", null, localVideoTrack!!, "You", it.sid)

        }
        setAdapter()
        handleObserver()

    }

    fun handleParticipantsViews() {
        val currentLoggedUser = CurrentMeetingDataSaver.getData()
        Log.d("currcheck", "onCreate: $currentLoggedUser")

        if (CurrentMeetingDataSaver.getData().identity?.trim()?.lowercase()!!
                .contains("C".trim().lowercase())
        ) {

            binding.btnFeedback.isVisible = false
            //  binding.btnRecordVideo.isVisible=false
            binding.btnAddUserVideoActivity.isVisible = false

            Log.d("videocheck", "onCreate: you")
            binding.tvUsername.text =CurrentMeetingDataSaver.getData().interviewerFirstName + " (You)"
            setBlankBackground(false)
            removeAllSinksAndSetnew(localVideoTrack!!,true)
            //working localVideoTrack?.addSink(binding.primaryVideoView)

//uncomment
            try {
                if (CurrentMeetingDataSaver.getData().identity!!.contains("C")) {
                   /* uncomment 23 nov  viewModel.setCandidateJoinedStatus { action, data ->
                          Log.d(TAG, "handleObserver: candidate joined status $action")
                      }*/
                }
            } catch (e: Exception) {
                Log.d(
                    TAG,
                    "handleParticipantsViews: exception handle views of participants ${e.message}"
                )
            }

        } else { //working
            if (CurrentMeetingDataSaver.getData().isPresenter == true) {
                //binding.btnFeedback.isVisible=true
           /**newly added*/     binding.btnAddUserVideoActivity.isVisible = true
            } else {
                binding.btnAddUserVideoActivity.isVisible = false
                // binding.btnFeedback.isVisible=false
            }
            binding.btnFeedback.isVisible = true
            binding.btnRecordVideo.isVisible = true

            currentLoggedUser.users?.forEach {
                if (it.userType.trim().lowercase().equals("C".trim().lowercase())) {
                  //  binding.tvUsername.text = it.userFirstName + " " + it.userLastName
                    // localVideoTrack?.addSink(binding.primaryVideoView)
                    // localblankVideoTrack.addSink(binding.primaryVideoView)
                }
            }
        }
    }


    private fun endCall() {
        Log.d(TAG, "endCall: ")
        //  meetingManager.unbindService()


        localVideoTrack?.let { localParticipant?.unpublishTrack(it) }
        // screenShareCapturerManager.unbindService()
        screenShareCapturerManager.endForeground()
        meetingManager.endForeground()
        // localVideoTrack!!.release()
        TwilioHelper.disConnectRoom()
        //  room!!.disconnect()
        //viewModel.endVideoCall()
        viewModel.setScreenSharingStatus(true, onResult = { action, data -> })
        var isNotCandidate = true

        if (!CurrentConnectUserList.getListofParticipant().isNullOrEmpty()) {
            if (!CurrentMeetingDataSaver.getData().identity!!.contains("C")) {

                CurrentConnectUserList.getListofParticipant().forEach {
                  if(CurrentMeetingDataSaver.getData().isPresenter==false) {

                      if (it.identity!!.contains("C")) {
                          Log.d(TAG, "endCall: called feedback form")
                          val intent = Intent(this@VideoActivity, ActivityFeedBackForm::class.java)
                          intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                          startActivity(intent)
                          overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                          isNotCandidate = false
                      }
                  }

                }
            }
        }

        Log.d(TAG, "endCall: service status ${meetingManager.getServiceState()}")

    }
    //Are you sure to leave the meeting?

    private fun endMeetingAlert() {
        val alertdialog = AlertDialog.Builder(this)
        alertdialog.setPositiveButton("Yes", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                endCall()
                //  isEndCall=true
            }
        })
        alertdialog.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {

            }
        })

        alertdialog.setMessage(getString(R.string.txt_do_you_want_to_exit_the_meeting))
        alertdialog.create()
        alertdialog.show()

    }

    override fun onBackPressed() {
        var isEndCall = false
        endMeetingAlert()

        if (isEndCall) {
            super.onBackPressed()
        } else {

        }

    }

    private var isCallinProgress=false
    private  var currentUserIdentity = ""
    private var currentLocalVideoTrack: LocalVideoTrack? = null
    private fun handleObserver() {

        CallStatusHolder.getCallStatus().observe(this){
            isCallinProgress=it
            if (it)
                endCall()
        }


        viewModel.micStatus.observe(this, Observer {
            Log.d(TAG, "handleObserver: check mic ${it} ")

            if (it != null) {
                localAudioTrack?.enable(it)
                if (!it) {
                    Log.d(TAG, "handleMuteUnmutebyHost: muted ")
                    // icon = R.drawable.ic_img_btn_mic_muted
                    // micStatus = getString(R.string.txt_unmute)
                    binding.muteActionFab.setImageResource(R.drawable.ic_img_btn_mic_muted)

                } else {
                    Log.d(TAG, "handleMuteUnmutebyHost: muted unmuted")
                    // icon = R.drawable.ic_mic_off_black_24dp
                    // micStatus = getString(R.string.txt_mute)
                    binding.muteActionFab.setBackgroundResource(R.color.black_70)
                    binding.muteActionFab.setImageResource(R.drawable.ic_img_btn_mic_unmute_white)
                }
            }
        })

        viewModel.videoStatus.observe(this, Observer {
            if (it != null) {
                localVideoTrack?.enable(it)
                var icon: Int = R.drawable.ic_img_btn_video
                var isTint = false
                if (it) {
                    icon = R.drawable.ic_img_btn_video
                    // binding.switchCameraActionFab.show()
                    isTint = false
                } else {
                    icon = R.drawable.ic_img_video_off_black
                    isTint = true
                    // binding.switchCameraActionFab.hide()
                }

                if (isTint) {
                    binding.localVideoActionFab.setBackgroundResource(R.color.white)
                    binding.localVideoActionFab.setImageDrawable(
                        ContextCompat.getDrawable(this@VideoActivity, icon)
                    )
                } else {
                    binding.localVideoActionFab.setBackgroundResource(R.color.black_50)
                    binding.localVideoActionFab.setImageDrawable(
                        ContextCompat.getDrawable(this@VideoActivity, icon)
                    )
                }
            }
        })

        viewModel.currentlocalVideoTrack.observe(this, Observer {
            Log.d(TAG, "handleObserver: publish track after ro")
            // TwilioHelper.getRoomInstance()?.localParticipant?.publishTrack(it.localVideoTrack)
            currentRemoteVideoTrack = it.localVideoTrack
            currentLocalVideoTrack = it.localVideoTrack
        })

        viewModel.videoTrack.observe(this, Observer {
            currentUserIdentity = ""
            Log.d(TAG, "handleObserver: clicked item is ${it?.username} ${it?.identity} ")

            /*

             if (!CurrentMeetingDataSaver.getData().userType!!.contains("C")){
                  if (it?.identity!!.contains("C"))
                  {
                      viewModel.setIsCandidateEnterMeeting(true)
                  }
              }


             */

            setBlankBackground(false)
            removeAllSinksAndSetnew(it?.videoTrack!!, false)


            if (it?.username!!.contains("You")) {
                Log.d(TAG, "handleObserver: visible item local video")
                currentRemoteVideoTrack = localVideoTrack
            } else {
                Log.d(TAG, "handleObserver: visible item clicked video")
                currentRemoteVideoTrack = it?.videoTrack
            }
            currentUserIdentity = it.identity

            //removeAllSinksAndSetnew(currentRemoteVideoTrack!!,false)

           // removeAllSinksAndSetnew(currentRemoteVideoTrack!!,false)
            currentVisibleUser.videoTrack=currentRemoteVideoTrack
            currentVisibleUser.identity=it.identity
            currentVisibleUser.userName=it.username
            removeAllSinksAndSetnew(currentRemoteVideoTrack!!,true)
           // currentRemoteVideoTrack!!.addSink(binding.primaryVideoView)



            //working test
            //currentRemoteVideoTrack?.addSink(binding.primaryVideoView)
            binding.tvUsername.text = it?.username

            if (viewModel.currentlocalVideoTrackList[0] != null) {
                if (viewModel.currentlocalVideoTrackList[0].isSharing) {
                    Log.d(TAG, "handleObserver: if part is sharing true")
                    TwilioHelper.getRoomInstance()?.localParticipant!!.publishTrack(viewModel.currentlocalVideoTrackList[0].localVideoTrack)
                } else {
                    try {
                        TwilioHelper.getRoomInstance()?.localParticipant!!.publishTrack(localVideoTrack!!)    
                    }catch (e:Exception)
                    {
                        Log.d(TAG, "handleObserver: exception 601 line ${e.printStackTrace()}")
                    }
                    Log.d(TAG, "handleObserver: in elsepart of isshring local publish local")
                    
                }
            } else {
                Log.d(TAG, "handleObserver: in null part local sink publish local")
                TwilioHelper.getRoomInstance()?.localParticipant!!.publishTrack(localVideoTrack!!)
            }
            // localParticipant?.publishTrack(localVideoTrack!!)

        })

        /*

      if (it!=null)
      {
          Log.d(TAG, "handleObserver: observer primary sinks ${it.username}")
          setBlankBackground(false)
          it.videoTrack.addSink(binding.primaryVideoView)

          if (it.username.contains("You"))
          {
              currentVisibleUser.videoTrack.removeSink(binding.primaryVideoView)
             // currentRemoteVideoTrack!!.removeSink(binding.primaryVideoView)

              localVideoTrack!!.removeSink(binding.primaryVideoView)

              currentVisibleUser.videoTrack?.addSink(binding.primaryVideoView)
              //it.videoTrack.addSink(binding.primaryVideoView)
              if (CurrentMeetingDataSaver.getData().userType!!.contains("C"))
              {
                  //localVideoTrack?.addSink(binding.primaryVideoView)
              }else {

               /*   Log.d(TAG, "handleObserver: sinks or not ${it.type}")
                  if (it.type.equals("local")) {
                      localVideoTrack?.addSink(binding.primaryVideoView)
                  }
                  else {
                      it.videoTrack.addSink(binding.primaryVideoView)
                  }*/
              }
              binding.tvUsername.text="You"
          }else{
              currentVisibleUser.videoTrack.removeSink(binding.primaryVideoView)
              localVideoTrack!!.removeSink(binding.primaryVideoView)

              it.videoTrack!!.addSink(binding.primaryVideoView)
              binding.tvUsername.text=it.username
          }
      }
      else
      {
          Log.d(TAG, "handleObserver: null video sinks")
          setBlankBackground(true)
      }

          currentVisibleUser.videoTrack=it!!.videoTrack
          currentRemoteVideoTrack=it!!.videoTrack
          currentVisibleUser.userName=it.username

      })*/


        CurrentConnectUserList.listLiveData.observe(this) { list ->
            if (CurrentMeetingDataSaver.getData().identity?.contains("C")!!) {
                list.forEach {
                    if (it.remoteParticipant?.identity.equals(CurrentMeetingDataSaver.getData().identity!!)) {
                        currentRemoteVideoTrack = it.videoTrack
                        //working
                        Log.d(TAG, "handleObserver: current connected list live ")
                        removeAllSinksAndSetnew(currentRemoteVideoTrack!!,false)
                        currentRemoteVideoTrack!!.addSink(binding.primaryVideoView)
                        binding.tvUsername.isVisible = true
                        binding.tvUsername.text = it.userName
                    }
                }
            }
            Log.d(TAG, "handleObserver: bottom observer list ${list.size}  $list")

            val tlist = mutableListOf<VideoTracksBean>()
            tlist.addAll(list)

            list?.mapIndexed { index, data ->
                if (data.userName!!.contains("You")) {


                    TwilioHelper.getRoomInstance()?.let {
                        tlist.set(index,VideoTracksBean(
                                it.localParticipant?.identity!!,
                                data.remoteParticipant,
                                localVideoTrack!!,
                                "You",
                                it.localParticipant!!.sid
                            )
                        )
                    }
                }


            }

            try {
                tlist.forEach {
                    if (it.remoteParticipant?.identity.equals(currentUserIdentity)) {


                        it.videoTrack?.let { videot ->
                            removeAllSinksAndSetnew(videot, false)
                           removeAllSinksAndSetnew(videot, true)
                            currentRemoteVideoTrack = videot
                        }

                        Log.d(TAG, "handleObserver if part : list.foreach al ${it.userName}")


                        binding.tvUsername.text = it.userName

                     //   currentRemoteVideoTrack!!.addSink(binding.primaryVideoView)


                    } else {

                        Log.d(TAG, "handleObserver else part : list.foreach al ${it.userName}")
                        //  removeAllSinksAndSetnew(it.videoTrack, true)
                        //  binding.tvUsername.text = it.userName
                        //  currentRemoteVideoTrack = it.videoTrack
                        // currentRemoteVideoTrack!!.addSink(binding.primaryVideoView)
                    }
                    /* if (!it.remoteParticipant?.identity.equals(currentUserIdentity)) {
                         removeAllSinksAndSetnew(it.videoTrack, true)
                         binding.tvUsername.text = it.userName
                         currentRemoteVideoTrack = it.videoTrack
                         currentRemoteVideoTrack!!.addSink(binding.primaryVideoView)
                     }*/
                }

                val isExists = tlist.any { it.identity!!.contains(currentUserIdentity) }
                if (isExists == false) {
                    removeAllSinksAndSetnew(localVideoTrack!!, false)
                    Log.d(TAG, "handleObserver: isexidted if")
                    binding.tvUsername.text = "You"
                    currentRemoteVideoTrack = localVideoTrack
                    currentVisibleUser.videoTrack=localVideoTrack
                    removeAllSinksAndSetnew(currentRemoteVideoTrack!!,true)
                    //currentRemoteVideoTrack!!.addSink(binding.primaryVideoView)
                }

                globalParticipantList.clear()
                globalParticipantList.addAll(tlist)

                /* if (binding.tvUsername.text.toString().contains("You"))
                 {
                     localVideoTrack?.addSink(binding.primaryVideoView)
                 }
                 */

















                adapter = ConnectedUserListAdapter(viewModel,
                    this,
                    globalParticipantList,
                    onLongClick={pos,action->

                        Log.d(TAG, "handleObserver: longclicked")
                        handleLongClickedonParticipant(pos)
                    },
                    onClick = { pos, action, data, datalist, videotrack ->
                        try {


                            /*  if (!datalist.isNullOrEmpty()) {
                                  Log.d("checkonclick", "setConnectedUsersListInAdapter: on clicked")
                                  val isCandidateExists = datalist.any { it.remoteParticipant?.identity!!.contains("C") }
                                  isCandidateExists
                                  Log.d("checkonclick", "setConnectedUsersListInAdapter: on clicked")
                                  if (isCandidateExists) {
                                      Log.d("checkonclick", "setConnectedUsersListInAdapter: candidate ")
                                    //  handleRecyclerItemClick(pos, data, datalist,videotrack)
                                  }
                                  else {
                                      Log.d(
                                          "checkonclick",
                                          "setConnectedUsersListInAdapter: candidate no"
                                      )
                                  }
                              }
                              */
                            handleRecyclerItemClick(pos, data!!, datalist!!, videotrack)


                            Log.d(TAG, "handleObserver: clicked on ${data.userName} $pos")
                        } catch (e: Exception) {
                            Log.d(
                                TAG,
                                "setConnectedUsersListInAdapter: candidate exception ${e.printStackTrace()}"
                            )
                        }
                    })
                binding.rvConnectedUsers.adapter = adapter





                adapter.notifyDataSetChanged()
            }catch (e:Exception)
            {
                Log.d(TAG, "handleObserver: exception observer ${e.message}")
            }

            CurrentConnectUserList.setListForAddParticipantActivity(globalParticipantList)

            //adapter.notifyDataSetChanged()
        }
    }


    private fun setAdapter()
    {
/*
        adapter = ConnectedUserListAdapter(viewModel,
            this,
            globalParticipantList,
            onLongClick={pos,action->

                Log.d(TAG, "handleObserver: longclicked")
                handleLongClickedonParticipant(pos)
            },
            onClick = { pos, action, data, datalist, videotrack ->
                try {


                    /*  if (!datalist.isNullOrEmpty()) {
                          Log.d("checkonclick", "setConnectedUsersListInAdapter: on clicked")
                          val isCandidateExists = datalist.any { it.remoteParticipant?.identity!!.contains("C") }
                          isCandidateExists
                          Log.d("checkonclick", "setConnectedUsersListInAdapter: on clicked")
                          if (isCandidateExists) {
                              Log.d("checkonclick", "setConnectedUsersListInAdapter: candidate ")
                            //  handleRecyclerItemClick(pos, data, datalist,videotrack)
                          }
                          else {
                              Log.d(
                                  "checkonclick",
                                  "setConnectedUsersListInAdapter: candidate no"
                              )
                          }
                      }
                      */
                    handleRecyclerItemClick(pos, data!!, datalist!!, videotrack)


                    Log.d(TAG, "handleObserver: clicked on ${data.userName} $pos")
                } catch (e: Exception) {
                    Log.d(
                        TAG,
                        "setConnectedUsersListInAdapter: candidate exception ${e.printStackTrace()}"
                    )
                }
            })
        binding.rvConnectedUsers.adapter = adapter
*/
    }



    private fun handleLongClickedonParticipant(pos: Int)
    {
        currentVisibleUser.userName
        currentVisibleUser.identity
        viewModel.videoTrack.observe(this){
            it?.identity
            it?.username
        }
        viewModel.setCurrentVisibleUser("",null,"","remote")

    }


    private var lastVideoTrack:VideoTrack?=null

    private fun removeAllSinksAndSetnew(videoTrack: VideoTrack?, isSink: Boolean) {
        Log.d(TAG, "removeAllSinksAndSetnew: remove sinks")
        currentRemoteVideoTrack?.removeSink(binding.primaryVideoView)

        lastVideoTrack?.let {
            it.removeSink(binding.primaryVideoView)
        }

        currentVisibleUser.videoTrack?.let {
            it!!.removeSink(binding.primaryVideoView)
            Log.d(TAG, "removeAllSinksAndSetnew: remove sinks current visible user")
        }
      //  currentVisibleUser.videoTrack!!.removeSink(binding.primaryVideoView)

        localVideoTrack!!.removeSink(binding.primaryVideoView)


        if (isSink) {
            Log.d(TAG, "removeAllSinksAndSetnew: remove sinks add new")
            if (videoTrack!=null)
            {
               // setBlankBackground(false)
                videoTrack!!.addSink(binding.primaryVideoView)
            }
            else
            {
              //  setBlankBackground(true)
                binding.tvNoParticipant.text="Video Not Available"
            }
           // videoTrack!!.addSink(binding.primaryVideoView)
            lastVideoTrack=videoTrack
        }
    }

    private fun handleMuteUnmute() {
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
                        viewModel.setMicStatus(enable)
                        icon = R.drawable.ic_img_btn_mic_muted
                        micStatus = getString(R.string.txt_unmute)
                        binding.muteActionFab.setImageResource(R.drawable.ic_img_btn_mic_muted)
                    } else {
                        showToast(this, getString(R.string.txt_mute_note_allowed))
                    }
                }
            } else {
                it.enable(enable)
                viewModel.setMicStatus(enable)
                Log.d(TAG, "onCreate: mic false")
                icon = R.drawable.ic_mic_off_black_24dp
                micStatus = getString(R.string.txt_mute)
                binding.muteActionFab.setBackgroundResource(R.color.black_50)
                binding.muteActionFab.setImageResource(R.drawable.ic_img_btn_mic_unmute_white)
            }
        }
    }

    private fun handleMuteUnmutebyHost() {
        var icon: Int
        var micStatus = ""
        localAudioTrack?.let {
            val enable = !it.isEnabled
            it.enable(enable)

            LocalConfrenseMic.setLocalParticipantMic(enable)

            if (!enable) {
                Log.d(TAG, "handleMuteUnmutebyHost: muted ")
                viewModel.setMicStatus(enable)
                icon = R.drawable.ic_img_btn_mic_muted
                micStatus = getString(R.string.txt_unmute)
                binding.muteActionFab.setImageResource(R.drawable.ic_img_btn_mic_muted)
            } else {
                viewModel.setMicStatus(enable)
                Log.d(TAG, "handleMuteUnmutebyHost: muted unmuted")
                icon = R.drawable.ic_mic_off_black_24dp
                micStatus = getString(R.string.txt_mute)
                binding.muteActionFab.setBackgroundResource(R.color.black_70)
                binding.muteActionFab.setImageResource(R.drawable.ic_img_btn_mic_unmute_white)
            }
        }
    }


    private fun getMuteStatus(isAllow: (status: Boolean) -> Unit) {
        Log.d(
            TAG,
            "getMuteStatus: mic status accessCode  ${CurrentMeetingDataSaver.getData().videoAccessCode}"
        )
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

    private fun handleAllowToMute() {
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

    private fun setConnectUser() {
        Log.d(TAG, "setConnectUser: ")
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

            Log.d(TAG, "setConnectUser: i am candidate")
            Log.d(
                TAG,
                "setConnectUser: i am candidate list size ${remoteParticipantVideoList.size}"
            )
            remoteParticipantVideoList.forEach {
                Log.d(
                    TAG,
                    "setConnectUser: i am Interviewer list candi loop ${it.remoteParticipant?.identity}"
                )
                userDataList.users?.forEach { user ->

                    Log.d(
                        TAG,
                        "setConnectUser: each parti ${it.remoteParticipant?.identity}  $identityCurrentUser  ${"I" + user.id}"
                    )

                    val userIdentity = "I" + user.id

                    if (userIdentity.equals(it.remoteParticipant?.identity)) {

                        /*  tlist.add(
                              VideoTracksBean(
                                  it.remoteParticipant!!,
                                  it.videoTrack!!,
                                  user.userFirstName !!
                              )
                          )
  */

                        try {
                            if (it.videoTrack != null) {
                                tlist.add(
                                    VideoTracksBean(
                                        it.identity,
                                        it.remoteParticipant!!,
                                        it.videoTrack!!,
                                        user.userFirstName, it.videoSid
                                    )
                                )
                                remoteParticipantVideoListWithCandidate.add(
                                    VideoTracksBean(
                                        it.identity,
                                        it.remoteParticipant!!,
                                        it.videoTrack!!,
                                        user.userFirstName!!,
                                        it.videoSid
                                    )
                                )

                            } else {
                                tlist.add(
                                    VideoTracksBean(
                                        it.identity,
                                        it.remoteParticipant!!,
                                        null,
                                        user.userFirstName,
                                        it.videoSid
                                    )
                                )
                                remoteParticipantVideoListWithCandidate.add(
                                    VideoTracksBean(
                                        it.identity,
                                        it.remoteParticipant!!,
                                        null,
                                        user.userFirstName!!,
                                        it.videoSid
                                    )
                                )
                            }

                        } catch (e: Exception) {
                            Log.d(TAG, "setConnectUser: exception in candidate block ${e.message}")
                        }

                        Log.d(
                            TAG,
                            "setConnectUser: you $identityCurrentUser   ${it.remoteParticipant?.identity}"
                        )
                    }
                }
            }
            Log.d(TAG, "setConnectUser: i am candidate in size ${tlist.size}")
        } else {

            Log.d(
                TAG,
                "setConnectUser: i am Interviewer list size ${remoteParticipantVideoList.size}"
            )

            remoteParticipantVideoList.forEach {
                Log.d(
                    TAG,
                    "setConnectUser: i am Interviewer and id of intervi ${it.remoteParticipant?.identity}"
                )

                userDataList.users?.forEach { user ->

                    Log.d(
                        TAG,
                        "setConnectUser: each parti I  ${it.remoteParticipant?.identity}  $identityCurrentUser  ${"I" + user.id}"
                    )

                    val identity = user.userType + user.id
                    Log.d(TAG, "setConnectUser: identity $identity")

                    if (identity.trim().lowercase()
                            .equals(it.remoteParticipant?.identity?.trim()?.lowercase())
                    ) {

                        if (it.remoteParticipant?.identity!!.contains("C")) {
                         //   removeAllSinksAndSetnew(null,false)
                          if (it.videoTrack!=null){
                              currentVisibleUser =
                                  VideoTracksBean(
                                      it.identity,
                                      it.remoteParticipant!!,
                                      it.videoTrack!!,
                                      user.userFirstName!!,
                                      it.videoSid
                                  )
                              currentRemoteVideoTrack = it.videoTrack!!
                          } else
                          {
                          //    removeAllSinksAndSetnew(null,false)
                              currentVisibleUser =
                                  VideoTracksBean(
                                      it.identity,
                                      it.remoteParticipant!!,
                                      null,
                                      user.userFirstName!!,
                                      it.videoSid
                                  )
                              currentRemoteVideoTrack = null
                              binding.tvNoParticipant.text="Video Not Available"

                          }


                            //  currentRemoteVideoTrack!!.addSink(binding.primaryVideoView)
                            //setCandidateToMainScreen()
                            Log.d(TAG, "setConnectUser: c")

                            try {

                                if (it.videoTrack!=null)
                                {
                                 //   removeAllSinksAndSetnew(null,false)
                                    tlist.add(
                                        VideoTracksBean(
                                            it.identity,
                                            it.remoteParticipant,
                                            it.videoTrack!!,
                                            user.userFirstName,
                                            it.videoSid
                                        )
                                    )

                                    remoteParticipantVideoListWithCandidate.add(
                                        VideoTracksBean(
                                            it.identity,
                                            it.remoteParticipant,
                                            it.videoTrack!!,
                                            user.userFirstName,
                                            it.videoSid
                                        )
                                    )

                                }else
                                {
                                 //   removeAllSinksAndSetnew(null,false)
                                    tlist.add(
                                        VideoTracksBean(
                                            it.identity,
                                            it.remoteParticipant,
                                           null,
                                            user.userFirstName,
                                            it.videoSid
                                        )
                                    )

                                    remoteParticipantVideoListWithCandidate.add(
                                        VideoTracksBean(
                                            it.identity,
                                            it.remoteParticipant,
                                            null,
                                            user.userFirstName,
                                            it.videoSid
                                        )
                                    )

                                }


                            } catch (e: Exception) {
                                Log.d(TAG, "setConnectUser:if  exception remote ${e.message}")
                            }

                        } else {
                            try {
                                if (it.videoTrack != null) {
                                    tlist.add(
                                        VideoTracksBean(
                                            it.identity,
                                            it.remoteParticipant!!,
                                            it.videoTrack!!,
                                            user.userFirstName,
                                            it.videoSid
                                        )
                                    )

                                    remoteParticipantVideoListWithCandidate.add(
                                        VideoTracksBean(
                                            it.identity,
                                            it.remoteParticipant,
                                            it.videoTrack!!,
                                            user.userFirstName,
                                            it.videoSid
                                        )
                                    )
                                } else {
                                    tlist.add(
                                        VideoTracksBean(
                                            it.identity,
                                            it.remoteParticipant!!,
                                            null,
                                            user.userFirstName,
                                            it.videoSid
                                        )
                                    )

                                    remoteParticipantVideoListWithCandidate.add(
                                        VideoTracksBean(
                                            it.identity,
                                            it.remoteParticipant,
                                            null,
                                            user.userFirstName,
                                            it.videoSid
                                        )
                                    )
                                }

                            } catch (e: Exception) {
                                Log.d(TAG, "setConnectUser:else  exception remote ${e.message}")
                            }

                        }
                       // removeAllSinksAndSetnew(null,isSink = false)
                        Log.d(
                            TAG,
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
                Log.d(TAG, "setConnectUser: in remove sink of local if part")
                removeAllSinksAndSetnew(null,false)

                localVideoTrack?.removeSink(binding.primaryVideoView)
                currentRemoteVideoTrack?.removeSink(binding.primaryVideoView)

                if (currentVisibleUser.videoTrack!=null)
                currentVisibleUser.videoTrack!!.removeSink(binding.primaryVideoView)



                //working
                //viewModel.setCurrentVisibleUser(it.remoteParticipant?.remoteVideoTracks?.firstOrNull()?.videoTrack!!,it.userName)
                // it.remoteParticipant?.remoteVideoTracks?.firstOrNull()?.videoTrack?.addSink(binding.primaryVideoView)
            } else {
                // setBlankBackground(true)
                Log.d(TAG, "setConnectUser: in remove sink of local else part")
            }
        }

        val candidateName =
            CurrentMeetingDataSaver.getData().users?.filter { it.userType.contains("C") }
        tlist.forEach {
            if (it.remoteParticipant?.identity!!.contains("C")) {
                /*working
                viewModel.setCurrentVisibleUser(
                    it.remoteParticipant?.identity!!,
                    it.remoteParticipant?.remoteVideoTracks?.firstOrNull()?.videoTrack!!,
                    candidateName?.firstOrNull()?.userFirstName!!,
                    "remote"
                )
  */

                Log.d(TAG, "setConnectUser: identity contains c")

                if (it.videoTrack!=null) {
                    viewModel.setCurrentVisibleUser(
                        it.remoteParticipant?.identity!!,
                        it.videoTrack!!,
                        candidateName?.firstOrNull()?.userFirstName!!,
                        "remote"
                    )
                }else
                {
                    setBlankBackground(true)
                    binding.tvNoParticipant.text="Video Not Available"
                }
            }

            if (it.videoTrack?.name.equals("screen"))
            {
                if (it.videoTrack!=null) {
                    viewModel.setCurrentVisibleUser(
                        it.remoteParticipant?.identity!!,
                        it.videoTrack!!,
                        candidateName?.firstOrNull()?.userFirstName!!,
                        "remote"
                    )
                }
            }
        }

        remoteParticipantVideoListWithCandidate.add(
            VideoTracksBean(
                localParticipant!!.identity,
                null,
                viewModel.currentlocalVideoTrackList[0].localVideoTrack,
                "You",
                localParticipant!!.sid
            )
        )

        tlist.add(
            VideoTracksBean(
                localParticipant!!.identity,
                null,
                viewModel.currentlocalVideoTrackList[0].localVideoTrack,
                "You",
                localParticipant!!.sid
            )
        )

        /*    if (!tlist.isNullOrEmpty())
             {
                 val isCandidateExists=tlist.any { it.remoteParticipant?.identity!!.contains("C") }
                 if (isCandidateExists)
                 {
                     setBlankBackground(false)
                 }else
                 {
                     setBlankBackground(true)
                 }
             }*/

        if (CurrentMeetingDataSaver.getData().identity?.contains("C")!!) {
            // tlist.add(VideoTracksBean(localParticipant!!.identity,null,localVideoTrack!!,"You"))
            if (tlist.size == 0) {
                currentVisibleUser.videoTrack!!.removeSink(binding.primaryVideoView)
                currentRemoteVideoTrack?.removeSink(binding.primaryVideoView)
                // localVideoTrack!!.addSink(binding.primaryVideoView)

                viewModel.setCurrentVisibleUser(
                    localParticipant!!.identity,
                    localVideoTrack!!,
                    "You",
                    "local"
                )

                currentRemoteVideoTrack = localVideoTrack
                currentVisibleUser.videoTrack = localVideoTrack!!
                currentVisibleUser.userName = "You"
            }
        } else {

        }
/*
        if (CurrentMeetingDataSaver.getData().userType!!.contains("C"))
        {
            CurrentConnectUserList.setListForAddParticipantActivity(
                remoteParticipantVideoListWithCandidate.reversed().distinctBy { it.identity }
            )
        }
        else
        {
            CurrentConnectUserList.setListForAddParticipantActivity(
                tlist.reversed().distinctBy { it.identity }
            )
        }
*/

        CurrentConnectUserList.setListForAddParticipantActivity(
            remoteParticipantVideoListWithCandidate.reversed().distinctBy { it.identity }
        )


        CurrentConnectUserList.setListForVideoActivity(
            // tlist.reversed().distinctBy { it.identity }
            tlist
        )

        Log.d(
            TAG,
            "setConnectUser: size of lists remot video ${remoteParticipantVideoListWithCandidate.size}"
        )


        Log.d(
            TAG,
            "setConnectUser: size of lists ${tlist.size}  "
        )
        Log.d(
            TAG,
            "setConnectUser: size of lists  "
        )


        // setConnectedUsersListInAdapter(tlist)

        // CurrentConnectUserList.setListForVideoActivity(tlist)

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
          }*/

    }


    fun setCandidateToMainScreen() {
        setBlankBackground(false)
        localVideoTrack?.removeSink(binding.primaryVideoView)
        currentRemoteVideoTrack?.removeSink(binding.primaryVideoView)
        binding.tvUsername.setText(currentVisibleUser.userName)
        currentVisibleUser.videoTrack!!.addSink(binding.primaryVideoView)
    }

    fun setConnectedUsersListInAdapter(tlist: List<VideoTracksBean>) {

        Log.d(TAG, "setConnectedUsersListInAdapter: in adapter refresh")
        CurrentConnectUserList.getListForVideoActivity().observe(this, Observer { list ->

            adapter =
                ConnectedUserListAdapter(viewModel,
                    this,
                    list,
                    onLongClick ={pos,action->
                        Log.d(TAG, "setConnectedUsersListInAdapter: longclicked")
                    }
                    ,

                    onClick = { pos, action, data, datalist, videoTrack ->
                        Log.d(
                            "checkonclick",
                            "setConnectedUsersListInAdapter: on clicked item ${data.userName}"
                        )

                        try {
                            handleRecyclerItemClick(pos, data!!, datalist, videoTrack)
                            /*if (CurrentMeetingDataSaver.getData().userType!!.contains("C"))
                            {
                                handleRecyclerItemClick(pos, data, datalist,videoTrack)
                            }

                            if (!datalist.isNullOrEmpty()) {
                                Log.d("checkonclick", "setConnectedUsersListInAdapter: on clicked")
                                val isCandidateExists = datalist.any { it.remoteParticipant?.identity!!.contains("C") }
                                Log.d("checkonclick", "setConnectedUsersListInAdapter: on clicked")
                                if (isCandidateExists) {
                                    Log.d("checkonclick", "setConnectedUsersListInAdapter: candidate ")
                                    handleRecyclerItemClick(pos, data, datalist,videoTrack)
                                }
                                else {
                                    Log.d(
                                        "checkonclick",
                                        "setConnectedUsersListInAdapter: candidate no"
                                    )
                                }
                            }*/

                        } catch (e: Exception) {
                            Log.d(
                                "checkonclick",
                                "setConnectedUsersListInAdapter: exception ${e.message}"
                            )
                        }
                    }
                )

            binding.rvConnectedUsers.adapter = adapter
            adapter.notifyDataSetChanged()
        })
    }

    private fun handleRecyclerItemClick(
        pos: Int,
        data: VideoTracksBean?,
        list: List<VideoTracksBean>,
        videoTrack: VideoTrack
    ) {

        /**working*/
        Log.d(TAG, "handleRecyclerItemClick: ")
        data?.videoTrack?.let {
            lastVideoTrack=it
        }

        removeAllSinksAndSetnew(data?.videoTrack!!,false)
        viewModel.setCurrentVisibleUser(data?.identity!!, videoTrack, data.userName!!, "local")
        /**working*/



        /*
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

       // clickedItem.videoTrack.addSink(binding.primaryVideoView)

        binding.tvUsername.setText(clickedItem.userName)

        currentRemoteVideoTrack = currentVisibleUser.videoTrack

        CurrentConnectUserList.setListForVideoActivity(tlist)

        if(binding.tvUsername.text.contains("You"))
        {
            Log.d(TAG, "handleRecyclerItemClick: onselected You ${binding.tvUsername.text.toString()}")
            viewModel.setCurrentVisibleUser(localVideoTrack!!,binding.tvUsername.text.toString(),"local")
        }
        else{
            Log.d(TAG, "handleRecyclerItemClick: onselected remotevideo  ${binding.tvUsername.text.toString()}")
            viewModel.setCurrentVisibleUser(clickedItem.videoTrack,binding.tvUsername.text.toString(),"remote")
        }

        currentVisibleUser = clickedItem
        //setCandidateToMainScreen()
*/
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
            } else {
                Toast.makeText(
                    this,
                    "R.string.permissions_needed",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun setCameraToLocalVideoTrack() {
        localVideoTrack = if (localVideoTrack == null && checkPermissionForCameraAndMicrophone()) {
            createLocalVideoTrack(
                this,
                true,
                cameraCapturerCompat
            )
        } else {
            localVideoTrack
        }
        localVideoTrack?.let { localParticipant?.publishTrack(it) }
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
        } else {
            localVideoTrack
        }
        /**
         * for add local video
         * */
        //   localVideoTrack?.addSink(localVideoView)
        // localVideoTrack?.let { localParticipant?.publishTrack(it) }
        /*
         * If connected to a Room then share the local video track.
         */
        // localVideoTrack?.let { localParticipant?.publishTrack(it) }
        // viewModel.setLocalVideoTrack(localVideoTrack!!)
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
        // localAudioTrack?.let { localParticipant?.unpublishTrack(it) }

        if (isCallinProgress)
        endCall()


        //localAudioTrack?.let { localParticipant?.unpublishTrack(it) }
        Log.d(TAG, "onPause: ")
        /*
         * Release the local video track before going in the background. This ensures that the
         * camera can be used by other applications while this app is in the background.
         */
        // localVideoTrack?.release()
        // localVideoTrack = null
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
        } else {
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
        } else {
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

        //val videoc = H264Codec()
        val videoc = Vp8Codec()//video/VP8/90000/1,fmtps:[]]
        val audioc = G722Codec()
        //    val videotrack = LocalVideoTrack.create(this, true, cameraCapturerCompat)
        //   val audioTrack = LocalAudioTrack.create(this, true)
        if (TwilioHelper.getRoomInstance() == null && room == null) {
            viewModel.setLocalVideoTrack(localVideoTrack!!, false)
            Log.d(TAG, "connectToRoom: creating room firsttime")
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
            getChatToken()
            Log.d(TAG, "connectToRoom: connect to room service ${meetingManager.getServiceState()}")
            Log.d(TAG, "onCreate: sid is ${TwilioHelper.getRoomInstance()?.sid}")
        } else {

            Log.d(TAG, "connectToRoom: on resume else part ")
            room = TwilioHelper.getRoomInstance()
            localParticipant = room?.localParticipant
            if (localParticipant?.localAudioTracks!!.size >= 1) {
                localParticipant?.unpublishTrack(localParticipant?.localAudioTracks!!.firstOrNull()!!.localAudioTrack)
                localParticipant?.publishTrack(localAudioTrack!!)
            } else {
                localParticipant?.publishTrack(localAudioTrack!!)
            }

            /**crashes*/
            Log.d(
                TAG,
                "connectToRoom: ${localParticipant?.audioTracks?.size}  ${localParticipant?.localAudioTracks?.size}"
            )

            try {

                if (!viewModel.currentlocalVideoTrackList.isNullOrEmpty()) {
                    if (viewModel.currentlocalVideoTrackList?.get(0) != null) {
                        if (viewModel.currentlocalVideoTrackList[0].isSharing) {
                            TwilioHelper.getRoomInstance()?.localParticipant!!.publishTrack(
                                viewModel.currentlocalVideoTrackList[0].localVideoTrack
                            )
                        } else {
                            Log.d(TAG, "connectToRoom: publish local")
                            TwilioHelper.getRoomInstance()?.localParticipant!!.publishTrack(
                                localVideoTrack!!
                            )
                        }
                    } else {
                        Log.d(TAG, "connectToRoom: publish local null else")
                        TwilioHelper.getRoomInstance()?.localParticipant!!.publishTrack(
                            localVideoTrack!!
                        )
                    }
                } else {

                }
            } catch (e: Exception) {
                Log.d(TAG, "connectToRoom: exception ${e.message}")
            }


            // Log.d(TAG, "connectToRoom: creating room second or after")
            // TwilioHelper.getExistingRoomInstance(this)
        }

        setDisconnectAction()
    }

    fun getChatToken() {
        viewModel.getChatToken(
            CurrentMeetingDataSaver.getData().identity.toString(),
            response = { data, code ->
                //dismissProgressDialog()
                //  initializeWithAccessToken(data?.Token.toString())
                TwilioChatHelper.setInstanceOfChat(
                    this,
                    data?.Token.toString(),
                    CurrentMeetingDataSaver.getData().chatChannel.toString()
                )
            })
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
            is WiredHeadset -> R.drawable.ic_headset_mic_white_24dp
            is Speakerphone -> R.drawable.ic_volume_up_white_24dp
            else -> R.drawable.ic_phonelink_ring_white_24dp
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
        Log.d(TAG, "addRemoteParticipant: new ")

        videoTrackList.add(remoteParticipant)

        remoteParticipant.remoteVideoTracks
        remoteParticipant.remoteVideoTracks.size

        if (remoteParticipant.remoteVideoTracks.size == 0) {
            Log.d(TAG, "addRemoteParticipant: if part size 0")
            // TwilioHelper.videoTrackList.add(remoteVideoTrackPublication)
            addRemoteParticipantVideo(
                null,
                remoteParticipant,
                "novideo"
            )
        } else {
            Log.d(TAG, "addRemoteParticipant: else part not 0")
            remoteParticipant.remoteVideoTracks.firstOrNull()?.let { remoteVideoTrackPublication ->
                Log.d(TAG, "addRemoteParticipant method: add out is tracksubs ")
                //if (remoteVideoTrackPublication.isTrackSubscribed) {
                Log.d(TAG, "addRemoteParticipant method: add participant ")
                remoteVideoTrackPublication.remoteVideoTrack?.let {
                    // TwilioHelper.videoTrackList.add(remoteVideoTrackPublication)
                    addRemoteParticipantVideo(it, remoteParticipant, it.sid)
                }
                //}
            }
        }

        TwilioHelper.setRemoteParticipantListener(remoteParticipant)

    }

    /*
     * Set primary view as renderer for participant video track
     */
    private fun addRemoteParticipantVideo(
        videoTrack: VideoTrack?,
        remoteParticipant: RemoteParticipant,
        videoSid: String
    ) {

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


        if (videoTrack != null) {
            remoteParticipantVideoList.add(
                VideoTracksBean(
                    remoteParticipant.identity!!,
                    remoteParticipant!!,
                    videoTrack!!,
                    "",
                    videoSid
                )
            )
            Log.d(TAG, "addRemoteParticipantVideo: vide track not null")
        } else {
            remoteParticipantVideoList.add(
                VideoTracksBean(
                    remoteParticipant.identity!!,
                    remoteParticipant!!,
                    null,
                    "",
                    videoSid
                )
            )

            Log.d(TAG, "addRemoteParticipantVideo: vide track null")
        }

            /**testing*/
    /*    remoteParticipantVideoList?.let { list ->
          //  val isSameExists = list.filter { it.identity.equals(remoteParticipant.identity) }
            //val isScreenExists = list.filter { remoteParticipant.identity.equals(it.identity) }

            var isScreenExits=false

            list.forEachIndexed { index, videoTracksBean ->
                if (videoTracksBean.videoTrack?.name.equals("screen"))
                {
                isScreenExits=true
                }

                if (isScreenExits)
                {

                }
            }




        /*    if (isScreenExists.size == 1) {
                if (isSameExists.size == 0) {
                    remoteParticipantVideoList.add(
                        VideoTracksBean(
                            remoteParticipant.identity!!,
                            remoteParticipant!!,
                            null,
                            "",
                            videoSid
                        )
                    )
                }
            }
            */


        }*/




        //viewModel.setConnectUser(remoteParticipantVideoList,localVideoTrack!!)

        //https://ui2.veriklick.in/video-session/WrbqaObRGjzWowSZFVo8
        //  remoteParticipantVideoList.add(VideoTracksBean(remoteParticipant,videoTrack,"",true))

        Log.d(
            TAG,
            "addRemoteParticipantVideo: list size is after ${remoteParticipantVideoList.size}"
        )

        if (videoTrack != null) {
          //  currentVisibleUser.videoTrack = videoTrack
          //  currentRemoteVideoTrack = videoTrack
        }else{
      //  currentVisibleUser.videoTrack = null
      //  currentRemoteVideoTrack = null
        }

        Log.d(
            TAG,
            "addRemoteParticipantVideo: remote after ${remoteParticipant.identity}  ${remoteParticipantVideoList.size}"
        )

        //testing working
        setConnectUser()
        // viewModel.setConnectUser(remoteParticipantVideoList,localVideoTrack!!)
        Log.d(
            TAG,
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
            /**
             * working too
             * */
            templist.addAll(remoteParticipantVideoList)
            templist.forEach {
                if (remoteParticipant.identity.equals(it.remoteParticipant?.identity)) {
                    Log.d(
                        TAG,
                        "removeRemoteParticipant: ${it.userName}  ${it.remoteParticipant?.identity}"
                    )
                    // it.videoTrack!!.removeSink(binding.primaryVideoView)
                    remoteParticipantVideoList.remove(it)
                    if (remoteParticipant.identity.contains("C")) {
                        setBlankBackground(true)
                    }
                }
            }


            /**testing working*/
            /*    templist.addAll(remoteParticipantVideoList)
                templist.forEach {
                    if (remoteParticipant.identity.equals(it.remoteParticipant?.identity)) {
                        Log.d(
                            "removeParti",
                            "removeRemoteParticipant: ${it.userName}  ${it.remoteParticipant?.identity}"
                        )
                        remoteParticipantVideoList.remove(it)
                    }
                }
              remoteParticipantVideoListWithCandidate.forEach {
                  if (remoteParticipant.identity.equals(it.remoteParticipant!!.identity))
                  {
                      if (CurrentMeetingDataSaver.getData().identity.equals(remoteParticipant.identity))
                      {

                      }else
                      {
                          remoteParticipantVideoListWithCandidate.remove(it)
                      }
                  }
              }*/
        } catch (e: Exception) {
            Log.d(TAG, "removeRemoteParticipant: exception  ${e.message}")
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
        Log.d(TAG, "removeRemoteParticipant: remove participant track video ")
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
            } else {
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
                viewModel.setVideoStatus(enable)
                it.enable(enable)
                val icon: Int
                // localVideoTrack!!.enable(enable)
                if (enable) {
                    icon = R.drawable.ic_img_btn_video
                    // binding.switchCameraActionFab.show()
                    isTint = false
                } else {
                    icon = R.drawable.ic_img_video_off_black
                    isTint = true
                    // binding.switchCameraActionFab.hide()
                }
                if (isTint) {
                    binding.localVideoActionFab.setBackgroundResource(R.color.white)
                    binding.localVideoActionFab.setImageDrawable(
                        ContextCompat.getDrawable(this@VideoActivity, icon)
                    )
                } else {
                    binding.localVideoActionFab.setBackgroundResource(R.color.black_50)
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

    private var isRoomConnected = false

    private fun muteIconUpdateDialog() {
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
            } else {
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
        Log.d(
            TAG,
            "connectToRoom: participant connected service ${meetingManager.getServiceState()}"
        )
        meetingManager.startForeground()


        try {
            this.room = room
            Log.d(
                TAG,
                "onParticipantConnect: room sid is ${room.sid} room parti size ${room.remoteParticipants.size}"
            )
            localParticipant = room.localParticipant
            TwilioHelper.setLocalParticipantListener(localParticipant!!)
            binding.videoStatusTextview.text = "Connected to ${room.name}"
            title = room.name
            isRoomConnected = true

            CurrentMeetingDataSaver.setRoomData(
                TokenResponseBean(
                    identity = CurrentMeetingDataSaver.getData().identity,
                    roomName = room.name!!
                )
            )

            Log.d(TAG, "onParticipantConnect: connect in connected  ${room.name}")
            Log.d(
                TAG,
                "onParticipantConnect: connect in connected  ${room.localParticipant?.identity}"
            )


//working apply other place
            /*    if(CurrentMeetingDataSaver.getData().userType!!.contains("C"))
                 {
                     /* viewModel.setCandidateJoinedStatus { action, data ->
                          Log.d(TAG, "handleObserver: candidate joined status $action")
                      }*/
                 }
 */

            remoteParticipantVideoListWithCandidate.add(
                VideoTracksBean(
                    localParticipant!!.identity,
                    null,
                    localVideoTrack!!,
                    "You",
                    localParticipant!!.sid
                )
            )

            viewModel.setConnectUser(remoteParticipantVideoList, localVideoTrack!!)

            CurrentConnectUserList.setListForAddParticipantActivity(
                remoteParticipantVideoListWithCandidate
            )

            CurrentConnectUserList.setListForVideoActivity(remoteParticipantVideoListWithCandidate)

            Log.d(
                "partichecktotal",
                "onParticipantConnect: parti connected total ${room.remoteParticipants.size}"
            )

            //  addRemoteParticipant(room.remoteParticipants.firstOrNull()!!)
            room.remoteParticipants

            room.remoteParticipants.size

            room.remoteParticipants.forEach {
                // remoteParticipantVideoList.add(VideoTracksBean(it,it.remoteVideoTracks.firstOrNull()?.videoTrack!!,"",true))
                addRemoteParticipant(it!!)
                Log.d(
                    "partichecktotal",
                    "onParticipantConnect: in on parti connect new ${it.identity} "
                )
            }

            setConnectUser()

        } catch (e: Exception) {
            Log.d(
                TAG,
                "onConnected: exception on connected ${e.printStackTrace()}  message ${e.message} "
            )
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

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
       // LocalBroadcastManager.getInstance(this).unregisterReceiver(incomingCallRecevier)
        localVideoTrack?.let { localParticipant?.unpublishTrack(it) }
        // localAudioTrack?.let { localParticipant?.unpublishTrack(it) }
//        screenShareCapturerManager.endForeground()
//        screenShareCapturerManager.unbindService()
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
        Log.d(TAG, "onParticipantConnected: called")

        addRemoteParticipant(participant!!)
        //  setBlankBackground(false)
        /*  participant.remoteVideoTracks?.let {
              it[0].remoteVideoTrack?.addSink(binding.primaryVideoView)
          }
  */
        // participant.videoTracks.firstOrNull()?.videoTrack?.addSink(binding.videoviewtesting)
        /* room.remoteParticipants.firstOrNull()?.let {
             videoTrackList.add(it)
         }
         TwilioHelper.setParticipantListener(participant)
         setConnectUser(participant)
 */
        //  videoTrackList.get(0).videoTrack?.addSink(binding.videoviewtesting)
        // setConnectUser(room.remoteParticipants.firstOrNull(),videoTrackList)
        Log.d(
            TAG,
            "onParticipantConnected: remote user connected ${participant.remoteVideoTracks.size} parti $participant"
        )
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
        TwilioHelper.setRemoteParticipantListener(participant!!)

        //      setConnectedUsersListInAdapter(remoteParticipantVideoList)


        removeRemoteParticipant(participant)

        if(remoteParticipantVideoList.size==1)
        {
            viewModel.setCurrentVisibleUser(localParticipant!!.identity,localVideoTrack,"You","remote")
        }

        //  currentRemoteVideoTrack?.removeSink(binding.primaryVideoView)
        //  localVideoTrack?.addSink(binding.primaryVideoView)
    }

    override fun onRecordingStarted(room: Room) {
        /*
             * Indicates when media shared to a Room is being recorded. Note that
             * recording is only available in our Group Rooms developer preview.
             */
        binding.btnRecording.setImageResource(R.drawable.ic_img_sc_recording_red)
        Log.d(TAG, "onRecordingStarted")
    }

    override fun onRecordingStopped(room: Room) {
        /*
           * Indicates when media shared to a Room is no longer being recorded. Note that
           * recording is only available in our Group Rooms developer preview.
           */
        binding.btnRecording.setImageResource(R.drawable.ic_img_sc_recording_white)
        Log.d(TAG, "onRecordingStopped")
    }

    override fun onNetworkQualityLevelChanged(
        remoteParticipant: RemoteParticipant,
        networkQualityLevel: NetworkQualityLevel
    ) {
        Log.d(TAG, "onNetworkQualityLevelChanged: ")
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

        TwilioHelper.setRemoteParticipantListener(remoteParticipant)


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
        TwilioHelper.setRemoteParticipantListener(remoteParticipant)
        remoteVideoTrack
        remoteParticipant
        Log.d(TAG, "onVideoTrackSubscribed: ")
        Log.d(
            TAG,
            "onVideoTrackSubscribed: in video track subscribed size ${remoteParticipant.remoteVideoTracks.size}"
        )
        Log.i(
            TAG, "onVideoTrackSubscribed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteVideoTrack: enabled=${remoteVideoTrack.isEnabled}, " +
                    "name=${remoteVideoTrack.name}]"
        )
        binding.videoStatusTextview.text = "onVideoTrackSubscribed"

        /**testing*/
        try {
            /*      val templist= mutableListOf<VideoTracksBean>()
                  templist.addAll(remoteParticipantVideoList)
                   if(remoteParticipantVideoList.size>0)
                   {
                       val isExists=remoteParticipantVideoList.any { it.remoteParticipant?.identity.equals(remoteParticipant.identity) }

                       if (isExists)
                       {
                           remoteParticipantVideoList.mapIndexed { index, videoTracksBean ->
                           if (videoTracksBean.remoteParticipant?.identity.equals(remoteParticipant.identity))
                           {
                              // templist.remove(videoTracksBean)
                               remoteParticipantVideoList.set(index,
                                   VideoTracksBean(remoteParticipant.identity,remoteParticipant,remoteVideoTrack,videoTracksBean.userName)
                               )
                               setConnectUser()
                           }
                           }

                          // remoteParticipantVideoList.clear()
                          // remoteParticipantVideoList.addAll(templist)

                       }
                   }else
                   {
                       addRemoteParticipantVideo(remoteVideoTrack, remoteParticipant)
                   }

  */

         /***/
            currentRemoteParticipant = remoteParticipant

            val tempList= mutableListOf<VideoTracksBean>()
                tempList.addAll(remoteParticipantVideoList)

                tempList?.let {
                    it.forEachIndexed { index, videoTracksBean ->
                        if (videoTracksBean.identity.equals(remoteParticipant.identity)) {
                            if (videoTracksBean.videoTrack == null) {
                                remoteParticipantVideoList.remove(videoTracksBean)
                            }
                        }
                    }
                }

           setConnectUser()

                if (remoteVideoTrack!=null)
                {
                    addRemoteParticipantVideo(
                        remoteVideoTrack,
                        remoteParticipant,
                        remoteVideoTrack.sid
                    )

                }else
                {
                    addRemoteParticipantVideo(
                        null,
                        remoteParticipant,
                        remoteVideoTrack.sid
                    )
                }



/***/

          /*  if (remoteVideoTrack!=null)
            {
                addRemoteParticipantVideo(remoteVideoTrack,remoteParticipant,remoteVideoTrack.sid)
            }else
            {
                addRemoteParticipantVideo(null,remoteParticipant,remoteVideoTrack.sid)
            }
*/

            //working
           /* remoteParticipant.remoteVideoTracks.forEach { it ->
                addRemoteParticipantVideo(
                    it.videoTrack,
                    remoteParticipant,
                    it.remoteVideoTrack!!.sid
                )
            }*/

            // setConnectUser()
            // addRemoteParticipantVideo(remoteVideoTrack, remoteParticipant)
        } catch (e: Exception) {
            Log.d(TAG, "onVideoTrackSubscribed: exception ${e.message}")
        }
    }

    override fun onVideoTrackUnsubscribed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        remoteVideoTrack: RemoteVideoTrack
    ) {
        TwilioHelper.setRemoteParticipantListener(remoteParticipant)

        remoteVideoTrack
        remoteVideoTrackPublication

        Log.i(
            TAG, "onVideoTrackUnsubscribed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteVideoTrack: enabled=${remoteVideoTrack.isEnabled}, " +
                    "name=${remoteVideoTrack.name}] size of list ${remoteParticipant.remoteVideoTracks.size}"
        )
        binding.videoStatusTextview.text = "onVideoTrackUnsubscribed"

        if (remoteParticipant.identity.contains("C")) {
            currentRemoteVideoTrack?.removeSink(binding.primaryVideoView)
            setBlankBackground(true)
            //remoteParticipantVideoListWithCandidate.removeAt(0)
            //CurrentConnectUserList.setListForAddParticipantActivity(remoteParticipantVideoListWithCandidate)
        }
        var identityCheck = ""

        /**working method*/
        val tempList = mutableListOf<VideoTracksBean>()
        tempList.addAll(remoteParticipantVideoList)

        try {

            tempList.mapIndexed { index, videoTracksBean ->

                    if (remoteVideoTrack.sid.equals(videoTracksBean.videoSid))
                    {
                        remoteParticipantVideoList.removeAt(index)
                    }
            }
            remoteParticipantVideoList

        } catch (e: Exception) {
            Log.d(TAG, "onVideoTrackUnsubscribed: exception removing participants ${e.message}")
        }

/***/
        if (remoteVideoTrack.name.equals("screen"))
        {
            remoteParticipantVideoList
            val isParticipantExists=remoteParticipantVideoList.filter { it.identity.equals(remoteParticipant.identity) }
            isParticipantExists.size
            if (isParticipantExists.size==0 || isParticipantExists==null)
            {
                    remoteParticipantVideoList.add(
                        VideoTracksBean(
                            remoteParticipant.identity!!,
                            remoteParticipant!!,
                            null,
                            "",
                            remoteVideoTrack.sid
                        )
                    )

                Handler(Looper.getMainLooper()).postDelayed({
                    removeAllSinksAndSetnew(null,true)
                },200)
            }
        }

        /***/


        setConnectUser()

        // localVideoTrack?.addSink(binding.primaryVideoView)
        removeParticipantVideo(remoteVideoTrack)

        localVideoTrack.let {
            removeAllSinksAndSetnew(it,true)
        }

        if (CurrentMeetingDataSaver.getData().identity!!.contains("C"))
        {
            currentVisibleUser.userName="You"
            currentVisibleUser.videoTrack=localVideoTrack
            Handler(Looper.getMainLooper()).post(Runnable {
                binding.tvUsername.text="You"
            })
            removeAllSinksAndSetnew(localVideoTrack!!,true)
        }

       // setConnectUser()

        //localVideoTrack!!.addSink(binding.primaryVideoView)

    }

    override fun onVideoTrackSubscriptionFailed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        twilioException: TwilioException
    ) {
        TwilioHelper.setRemoteParticipantListener(remoteParticipant)
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
        Log.d(TAG, "onAudioTrackEnabled: ")

        /*  try {
              val pos= CurrentConnectUserList.setItemToParticipantList(
                  VideoTracksBean(
                      remoteParticipant.identity,
                      remoteParticipant!!,
                      remoteParticipant.remoteVideoTracks.firstOrNull()?.remoteVideoTrack!!,
                      ""
                  )
              )
              adapter.notifyItemChanged(pos)
          }catch (e:Exception)
          {
              Log.d(TAG, "onAudioTrackEnabled: exception ${e.message}")
          }*/
    }

    override fun onVideoTrackEnabled(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
        Log.d(TAG, "onVideoTrackEnabled: ")
    }

    override fun onVideoTrackDisabled(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
        Log.d(TAG, "onVideoTrackDisabled: ")
    }

    override fun onAudioTrackDisabled(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
        Log.d(TAG, "onAudioTrackDisabled: ")

        /* try {
             val pos = CurrentConnectUserList.setItemToParticipantList(
                 VideoTracksBean(
                     remoteParticipant.identity,
                     remoteParticipant!!,
                     remoteParticipant.remoteVideoTracks.firstOrNull()?.remoteVideoTrack!!,
                     ""
                 )
             )
             adapter.notifyItemChanged(pos)
         }catch (e:Exception)
         {
             Log.d(TAG, "onAudioTrackDisabled: exception ${e.message}")
         }*/

    }

    override fun onViewClicked(view: View) {
        when (view.id) {
            R.id.btn_add_user_videoActivity -> {
                binding.llExtraButtons.isVisible = false
                showAddUserActivity()
            }
            R.id.btn_feedback -> {
                binding.llExtraButtons.isVisible = false
                val intent = Intent(this@VideoActivity, ActivityFeedBackForm::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

            }
            /*R.id.btn_allow_to_mute -> {
                 muteIconUpdateDialog()
                 Log.d(TAG, "onViewClicked: mute others")
             }*/
            R.id.btn_show_documents -> {
                binding.llExtraButtons.isVisible = false
                val intent = Intent(this, DocumentViewerActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.btn_share_screen -> {
                binding.llExtraButtons.isVisible = false
                getScreenSharingStatus {
                    if (it) {
                        shareScreen()
                    } else {
                        showToast(this, getString(R.string.txt_only_one_person_can_share_screen))
                    }
                }
            }
            R.id.btn_record_video -> {
                binding.llExtraButtons.isVisible = false
                if (CurrentMeetingDataSaver.getData().identity?.trim()?.lowercase()!!
                        .contains("C".trim().lowercase())
                ) {
                    Log.d(TAG, "onViewClicked: candidate clicked recording btn")
                    showToast(this, getString(R.string.txt_you_are_not_authorized_to_record_video))
                } else {
                    if (isRoomConnected != false) {
                        handleVideoRecording()
                    } else {
                        showToast(this, getString(R.string.txt_please_try_again))
                    }
                }


                //Log.d("checkvideostatus", "onViewClicked: status ${VideoRecordingStatusHolder.setStatus()}")
            }
        }
    }

    fun getScreenSharingStatus(onResonse: (isSharing: Boolean) -> Unit) {
        viewModel.getScreenSharingStatus(
            CurrentMeetingDataSaver.getData().videoAccessCode.toString(),
            onResult = { action, data ->
                when (action) {
                    200 -> {
                        if (data?.OwnScreenShareStatus != null) {
                            if (data?.OwnScreenShareStatus == true) {
                                onResonse(true)
                            } else {
                                onResonse(false)
                            }
                        } else {
                            onResonse(false)
                        }
                        Log.d(TAG, "handleAllowToMute: success ${data}")
                    }
                    400 -> {
                        onResonse(false)
                        Log.d(TAG, "handleAllowToMute: null data")
                    }
                    404 -> {
                        onResonse(false)
                        Log.d(TAG, "handleAllowToMute: not found")
                    }
                    500 -> {
                        onResonse(false)
                        Log.d(TAG, "handleAllowToMute exception ")
                    }

                }
            })
    }

    fun setAllSinkRemove() {
        localVideoTrack?.removeSink(binding.primaryVideoView)
        currentRemoteVideoTrack?.removeSink(binding.primaryVideoView)
        currentVisibleUser.videoTrack!!.removeSink(binding.primaryVideoView)
    }

    fun shareScreen() {

        try {

            Log.d(
                TAG,
                "shareScreen: start screensharing ${screenShareCapturerManager.getServiceState()} "
            )

            if (CurrentMeetingDataSaver.getScreenSharingStatus()) {
                binding.tvScreenShareStatus.isVisible = false
               // showToast(this, getString(R.string.txt_screen_sharing_stopped))
                viewModel.setScreenSharingStatus(true, onResult = { action, data -> })
                CurrentMeetingDataSaver.setScreenSharingStatus(false)
                screenShareCapturerManager.endForeground()
                // screenShareCapturerManager.unbindService()
                Log.d(TAG, "shareScreen: stoped capturing")
                currentRemoteVideoTrack?.removeSink(binding.primaryVideoView)
                localParticipant?.unpublishTrack(currentLocalVideoTrack!!)
                localParticipant?.publishTrack(localVideoTrack!!)
                viewModel.setLocalVideoTrack(localVideoTrack!!, false)
                binding.tvScreenshareText.setText(getString(R.string.txt_screensharing))
                // setCameraToLocalVideoTrack()
            } else {

             //   binding.tvScreenshareText.setText(getString(R.string.txt_stop_screensharing))
             //   showToast(this, getString(R.string.txt_screen_sharing_started))
                Log.d(TAG, "shareScreen: start screensharing")
             //   screenShareCapturerManager.startForeground()

                if (screenCapturer == null) {
                    Log.d(TAG, "shareScreen: screen capture null")
                    val mediaProjectionManager =
                        getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                    // startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(),100)
                    resultLauncher.launch(mediaProjectionManager.createScreenCaptureIntent())
                } else {
                    val mediaProjectionManager =
                        getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                    resultLauncher.launch(mediaProjectionManager.createScreenCaptureIntent())

                    Log.d(TAG, "shareScreen: screen capture  null capturing start")
                    // startScreenCapture()
                }
            }

        } catch (e: Exception) {
            Log.d(TAG, "shareScreen: screen share exeception ${e.message}")
        }


    }


    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data

//                showToast(this, getString(R.string.txt_permissionScreenSharingGranted))

                screenCapturer = ScreenCapturer(
                    this@VideoActivity,
                    result.resultCode,
                    data!!,
                    screenCapturerListener
                )

                startScreenCapture()
                Log.d(TAG, "start recording listener: ")
            } else {

//                showToast(this, getString(R.string.txt_permissionScreenSharingNotGranted))
            }
        }


    fun startScreenCapture() {
        Log.d(TAG, "startScreenCapture: start capturing method ")
        // setAllSinkRemove()
        // setBlankBackground(false)

        binding.tvScreenshareText.setText(getString(R.string.txt_stop_screensharing))
        showToast(this, getString(R.string.txt_screen_sharing_started))
        Log.d(TAG, "shareScreen: start screensharing")
        screenShareCapturerManager.startForeground()
        binding.tvScreenShareStatus.isVisible = true
        screenShareTrack = LocalVideoTrack.create(this, true, screenCapturer!!,AppConstants.SCREEN_SHARE_NAME)!!

        //screenShareTrack = LocalVideoTrack.create(this, true, screenCapturer!!)!!
        screenShareTrack.enable(true)
    }

    private val screenCapturerListener = object : ScreenCapturer.Listener {
        override fun onScreenCaptureError(errorDescription: String) {
            Log.e(TAG, "Screen capturer error: $errorDescription")
            //stopScreenCapture()
            //  setBlankBackground(true)
            showToast(
                this@VideoActivity,
                getString(R.string.txt_screen_sharing_error) + errorDescription.toString()
            )
        }

        override fun onFirstFrameAvailable() {
            CurrentMeetingDataSaver.setScreenSharingStatus(true)
            // localParticipant?.publishTrack(localVideoTrack!!)
            Log.d(TAG, "First frame from screen capturer available")
            viewModel.setScreenSharingStatus(false, onResult = { action, data -> })
            localParticipant?.unpublishTrack(localVideoTrack!!)
            localParticipant?.publishTrack(screenShareTrack)
            //localVideoTrack=screenShareTrack
            currentVisibleUser.videoTrack = screenShareTrack
            currentRemoteVideoTrack = screenShareTrack
            viewModel.setLocalVideoTrack(screenShareTrack, true)
            // viewModel.setCurrentVisibleUser(localParticipant?.identity!!,screenShareTrack,"You","remote")
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    fun showAddUserActivity() {
        val intent = Intent(this, MemberListActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    fun handleVideoRecording() {
        showProgressDialog()
        viewModel.getRecordingStatusUpdate(
            onResult = { action: Int, data: BodyUpdateRecordingStatus? ->
                when (action) {
                    200 -> {
                        dismissProgressDialog()
                    }
                    400 -> {
                        dismissProgressDialog()
                        Log.d(
                            "checkvideostatus",
                            "Recording: data stats ${data?.RecordingStatus}  ${data?.Message}  ${data?.StatusCode}"
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


    private val participantListener = object : RemoteParticipant.Listener {
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
            // binding.videoStatusTextview.text = "onAudioTrackAdded"
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
            // binding.videoStatusTextview.text = "onAudioTrackRemoved"
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
            //  binding.videoStatusTextview.text = "onDataTrackPublished"
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
            // binding.videoStatusTextview.text = "onDataTrackUnpublished"
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
            // binding.videoStatusTextview.text = "onVideoTrackPublished"
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
            // binding.videoStatusTextview.text = "onVideoTrackUnpublished"
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
            //  binding.videoStatusTextview.text = "onAudioTrackSubscribed"
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
            //  binding.videoStatusTextview.text = "onAudioTrackUnsubscribed"
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
            // binding.videoStatusTextview.text = "onAudioTrackSubscriptionFailed"
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
            //  binding.videoStatusTextview.text = "onDataTrackSubscribed"
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
            // binding.videoStatusTextview.text = "onDataTrackUnsubscribed"
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
            // binding.videoStatusTextview.text = "onDataTrackSubscriptionFailed"
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
            //  binding.videoStatusTextview.text = "onVideoTrackSubscribed"
            //  addRemoteParticipantVideo(remoteVideoTrack)
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
            //  binding.videoStatusTextview.text = "onVideoTrackUnsubscribed"
            // removeParticipantVideo(remoteVideoTrack)
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
            Log.d(TAG, "onAudioTrackEnabled: ")
        }

        override fun onVideoTrackEnabled(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication
        ) {
            Log.d(TAG, "onVideoTrackEnabled: ")
        }

        override fun onVideoTrackDisabled(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication
        ) {
            Log.d(TAG, "onVideoTrackDisabled: ")
        }

        override fun onAudioTrackDisabled(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication
        ) {
            Log.d(TAG, "onAudioTrackDisabled: ")
        }
    }

}
