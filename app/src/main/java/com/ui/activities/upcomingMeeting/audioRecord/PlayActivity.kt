package com.ui.activities.upcomingMeeting.audioRecord

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.data.checkInternet
import com.data.dataHolders.CandidateImageAndAudioHolder
import com.data.dataHolders.DataStoreHelper
import com.data.dismissProgressDialog
import com.data.exceptionHandler
import com.data.setHandler
import com.data.showCustomSnackbarOnTop
import com.data.showProgressDialog
import com.domain.BaseModels.BodyCandidateImageAudioModel
import com.domain.BaseModels.CandidateDeepLinkDataModel
import com.ui.activities.createCandidate.ActivityCreateCandidate
import com.ui.activities.upcomingMeeting.audioRecord.player.AudioPlayer
import com.ui.activities.upcomingMeeting.audioRecord.player.VMPlayAudio
import com.ui.activities.upcomingMeeting.audioRecord.utils.formatAsTime
import com.ui.activities.upcomingMeeting.audioRecord.utils.getDrawableCompat
import com.veriKlick.R
import com.veriKlick.databinding.ActivityPlayBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sqrt

@AndroidEntryPoint
class PlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayBinding
    private lateinit var player: AudioPlayer
    private lateinit var viewModel:VMPlayAudio
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        viewModel=ViewModelProvider(this).get(VMPlayAudio::class.java)
        setContentView(binding.root)

        binding.playButton.setImageDrawable( getDrawableCompat(R.drawable.ic_play_arrow_24))
        binding.btnJumpBack.setOnClickListener {onBackPressedDispatcher.onBackPressed()}
        binding.btnUploadData.setOnClickListener {
            if (checkInternet()) {
                uploadData()
            } else {
                showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
            }


        }
        binding.btnSkip.setOnClickListener {
                val intent= Intent(this, ActivityCreateCandidate::class.java)
                startActivity(intent)
                overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }
    }

    private val TAG="playAudioActivity"

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun uploadData()
    {
        try {
            runOnUiThread { showProgressDialog() }
            viewModel?.updateUserImageWithoutAuth{isSuccess, code, msg ->
                runOnUiThread {dismissProgressDialog() }
                when(code)
                {
                    200->{
                        Log.d(TAG, "uploadProfilePhoto: $msg")
                        showCustomSnackbarOnTop(msg)
                       // setHandler().postDelayed({finish()},3000)
                    }
                    400->{
                        showCustomSnackbarOnTop(msg)
                        Log.d(TAG, "uploadProfilePhoto: $msg $isSuccess $code")
                    }
                    401->{
                        showCustomSnackbarOnTop(msg)
                        Log.d(TAG, "uploadProfilePhoto: $msg $isSuccess $code")
                    }
                    500->{
                        showCustomSnackbarOnTop(msg)
                        Log.d(TAG, "uploadProfilePhoto: $msg $isSuccess $code")
                    }
                    501->{
                        showCustomSnackbarOnTop(msg)
                        Log.d(TAG, "uploadProfilePhoto: $msg $isSuccess $code")
                    }
                    404->{
                        Log.d(TAG, "uploadProfilePhoto: $msg $isSuccess $code")
                    }
                    503->{
                        showCustomSnackbarOnTop(msg)
                        Log.d(TAG, "uploadProfilePhoto: $msg $isSuccess $code")
                    }
                    502->{
                        showCustomSnackbarOnTop(msg)
                        Log.d(TAG, "uploadProfilePhoto: $msg $isSuccess $code")
                    }

                }
            }
        }catch (e:Exception)
        {
            Log.d(TAG, "uploadData: exception ${e.message}")
        }
    }


    override fun onStart() {
        super.onStart()
        listenOnPlayerStates()
        initUI()
       // player.togglePlay()
    }

    override fun onStop() {
        player.release()
        super.onStop()
    }



    private fun initUI() = with(binding) {
        visualizer.apply {
            ampNormalizer = { sqrt(it.toFloat()).toInt() }
            onStartSeeking = {
                player.pause()
            }
            onSeeking = { binding.timelineTextView.text = it.formatAsTime() }
            onFinishedSeeking = { time, isPlayingBefore ->
                player.seekTo(time)
                if (isPlayingBefore) {
                    player.resume()
                }
            }
            onAnimateToPositionFinished = { time, isPlaying ->
                updateTime(time, isPlaying)
                player.seekTo(time)
            }
        }
        playButton.setOnClickListener {
            player.togglePlay()
        }
        /*seekForwardButton.setOnClickListener {
            //visualizer.seekOver(SEEK_OVER_AMOUNT)
            Toast.makeText(this@PlayActivity,"Saved",Toast.LENGTH_SHORT).show()
        }*/
        seekBackwardButton.setOnClickListener {
            //visualizer.seekOver(-SEEK_OVER_AMOUNT)
            onBackPressed()
        }




        lifecycleScope.launchWhenCreated {
            val amps = player.loadAmps()
            visualizer.setWaveForm(amps, player.tickDuration)
        }
    }

    private fun listenOnPlayerStates() = with(binding) {
        player = AudioPlayer.getInstance(applicationContext).init().apply {
            onStart = { playButton.setImageDrawable( getDrawableCompat(R.drawable.ic_pause_24)) }
            onStop = { playButton.setImageDrawable( getDrawableCompat(R.drawable.ic_play_arrow_24)) }
            onPause = { playButton.setImageDrawable( getDrawableCompat(R.drawable.ic_play_arrow_24)) }
            onResume = { playButton.setImageDrawable( getDrawableCompat(R.drawable.ic_pause_24)) }
            onProgress = { time, isPlaying -> updateTime(time, isPlaying) }
        }
    }

    private fun updateTime(time: Long, isPlaying: Boolean) = with(binding) {
        timelineTextView.text = time.formatAsTime()
        visualizer.updateTime(time, isPlaying)
    }

    companion object {
        const val SEEK_OVER_AMOUNT = 5000
    }
}