package com.ui.activities.upcomingMeeting.audioRecord

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ui.activities.upcomingMeeting.audioRecord.player.AudioPlayer
import com.ui.activities.upcomingMeeting.audioRecord.utils.formatAsTime
import com.ui.activities.upcomingMeeting.audioRecord.utils.getDrawableCompat
import com.veriKlick.R
import com.veriKlick.databinding.ActivityPlayBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.sqrt

@AndroidEntryPoint
class PlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayBinding
    private lateinit var player: AudioPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


    override fun onStart() {
        super.onStart()
        listenOnPlayerStates()
        initUI()
        player.togglePlay()
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
        playButton.setOnClickListener { player.togglePlay() }
        seekForwardButton.setOnClickListener {
            //visualizer.seekOver(SEEK_OVER_AMOUNT)
            Toast.makeText(this@PlayActivity,"Saved",Toast.LENGTH_SHORT).show()
        }
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