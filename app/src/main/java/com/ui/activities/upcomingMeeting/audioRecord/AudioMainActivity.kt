package com.ui.activities.upcomingMeeting.audioRecord

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.ui.activities.upcomingMeeting.audioRecord.recorder.Recorder
import com.ui.activities.upcomingMeeting.audioRecord.utils.checkAudioPermission
import com.ui.activities.upcomingMeeting.audioRecord.utils.formatAsTime
import com.ui.activities.upcomingMeeting.audioRecord.utils.getDrawableCompat
import com.veriKlick.R
import com.veriKlick.databinding.ActivityAudiomainBinding
import dagger.hilt.android.AndroidEntryPoint

import kotlin.math.sqrt

@AndroidEntryPoint
class AudioMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudiomainBinding
    private lateinit var recorder: Recorder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudiomainBinding.inflate(layoutInflater)
        setContentView(binding.root)

       // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        checkAudioPermission(AUDIO_PERMISSION_REQUEST_CODE)

        initUI()
    }

    override fun onStart() {
        super.onStart()
        listenOnRecorderStates()
    }

    override fun onStop() {
        recorder.release()
        super.onStop()
    }

    private fun initUI() = with(binding) {
        recordButton.setOnClickListener { recorder.toggleRecording() }
        visualizer.ampNormalizer = { sqrt(it.toFloat()).toInt() }
    }

    private fun listenOnRecorderStates() = with(binding) {
        recorder = Recorder.getInstance(applicationContext).init().apply {
            onStart = { recordButton.setImageDrawable(getDrawableCompat(R.drawable.ic_stop_24)) }
            onStop = {
                visualizer.clear()
                timelineTextView.text = 0L.formatAsTime()
                recordButton.setImageDrawable(getDrawableCompat(R.drawable.ic_record_24))
                startActivity(Intent(this@AudioMainActivity, PlayActivity::class.java))
            }
            onAmpListener = {
                runOnUiThread {
                    if (recorder.isRecording) {
                        timelineTextView.text = recorder.getCurrentTime().formatAsTime()
                        visualizer.addAmp(it, tickDuration)
                    }
                }
            }
        }
    }

    companion object {
        private const val AUDIO_PERMISSION_REQUEST_CODE = 1
    }
}