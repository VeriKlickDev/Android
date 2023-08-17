package com.ui.activities.upcomingMeeting.audioRecord

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.data.dataHolders.CreateProfileDeepLinkHolder
import com.data.dataHolders.DataStoreHelper
import com.data.selectLangaugeDialogGlobal
import com.data.setLanguagetoApp
import com.ui.activities.createCandidate.ActivityCreateCandidate
import com.ui.activities.upcomingMeeting.audioRecord.recorder.Recorder
import com.ui.activities.upcomingMeeting.audioRecord.utils.checkAudioPermission
import com.ui.activities.upcomingMeeting.audioRecord.utils.formatAsTime
import com.ui.activities.upcomingMeeting.audioRecord.utils.getDrawableCompat
import com.ui.activities.uploadResumeDocument.ActivityResumeDocument
import com.veriKlick.R
import com.veriKlick.databinding.ActivityAudiomainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.concurrent.timer
import kotlin.math.sqrt


@AndroidEntryPoint
class AudioMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudiomainBinding
    private lateinit var recorder: Recorder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudiomainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //getAppLanguage()
        Log.d("TAG", "onCreate: in audio activity link ${CreateProfileDeepLinkHolder.get()}")
        binding.audioProgressBar.max=3400
       // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
       /**18may binding.btnSkip.setOnClickListener {
            val intent=Intent(this,ActivityCreateCandidateForm::class.java)
            startActivity(intent)
        }*/
        binding.btnJumpBack.setOnClickListener {onBackPressedDispatcher.onBackPressed()}
        checkAudioPermission(AUDIO_PERMISSION_REQUEST_CODE)

        initUI()
        // watchObserver()
        //  binding.btnPlay.setOnClickListener { jumpToPlayActivity() }
        binding.btnSkip.setOnClickListener {
            //clearAllData()
            Log.d("TAG", "onCreate: record on click ${recorder.isRecording}")
            if (recorder.isRecording)
            {
                Log.d("TAG", "onCreate: record ${recorder.isRecording}")
                recorder.toggleRecording()
                setupTimerForAudio()
            }
            else
            {
                clearAllData()
               // clearAllData()
            }
        }

        binding.tvSetPreference.setOnClickListener {
            selectLangaugeDialogGlobal()
        }

    }

    private fun getAppLanguage()
    {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (DataStoreHelper.getAppLanguage()!=null &&  !DataStoreHelper.getAppLanguage().equals("null")){
                    var language= DataStoreHelper.getAppLanguage()
                    runOnUiThread { setLanguagetoApp(intent,language,false) }
                }
                else{

                }
            }catch (e:Exception)
            {

            }
        }
    }

    override fun onStart() {
        super.onStart()
        listenOnRecorderStates()
    }



    override fun onStop() {
      //  recorder.release()
        super.onStop()
    }

    private fun initUI() = with(binding) {
        recordButton.setOnClickListener {
            recorder.toggleRecording()
            setupTimerForAudio()

           // millisTimer.start()
           // secondsTimer.start()
        }
       // btnSkip.isEnabled=false
        visualizer.ampNormalizer = { sqrt(it.toFloat()).toInt() }
    }

    private fun setupTimerForAudio()
    {
        getSecondsAndMillis{seconds, mmillis, isDone ->
            Log.d("TAG", "setupTimerForAudio: seconds is $millis")
            runOnUiThread {
                binding.audioProgressBar.setProgress(mmillis)
                if (isDone){
                recorder.toggleRecording()
                    jumpToPlayActivity()
                }
            }
        }
    }

    private var millis=0
    private var seconds=0

    var timer1:CountDownTimer?=null
    var timer2:CountDownTimer?=null
    private fun getSecondsAndMillis(onTick:(seconds:Int,millis:Int,isDone:Boolean)->Unit)
    {
        timer1?.let {
            it.cancel()
        }
        timer2?.let { it.cancel() }
        timer1= object : CountDownTimer(60000, 1) {
            override fun onTick(millisUntilFinished: Long) {
                onTick(seconds,millis,false)
                millis++
            }

            override fun onFinish() {

            }
        }
        timer1?.start()
        timer2=object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d("TAG", "onTick: seconds ${millis}")
                // logic to set the EditText could go here
                onTick(seconds,millis,false)
                seconds--
            }

            override fun onFinish() {
                onTick(seconds,millis,true)
            }
        }
        timer2?.start()


    }

    override fun onResume() {
        super.onResume()
       // binding.btnSkip.isEnabled=true
        millis=0
        seconds=0
        binding.audioProgressBar.setProgress(0)
        timer2?.cancel()
        timer1?.cancel()
    }

    private fun clearAllData()
    {
        Log.d("TAG", "clearAllData: ")
        try {
            millis=0
            seconds=0
            binding.audioProgressBar.setProgress(0)
            timer2?.cancel()
            timer1?.cancel()
           // recorder.release()
           // recorder.stopRecording()
            runOnUiThread {
                binding.visualizer.clear()
                binding.timelineTextView.text = 0L.formatAsTime()
                binding.recordButton.setImageDrawable(getDrawableCompat(R.drawable.ic_record_24))
                val intent=Intent(this, ActivityResumeDocument::class.java)
                //val intent= Intent(this, ActivityCreateCandidate::class.java)
                startActivity(intent)
                overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }

        }catch (e:Exception)
        {
            Log.d("TAG", "clearAllData: exception clearalldata ${e.message}")
        }

    }


    override fun onPause() {
        super.onPause()

        try {
            Log.d("TAG", "onPause: ")
            millis=0
            seconds=0
            binding.audioProgressBar.setProgress(0)
            timer2?.cancel()
            timer1?.cancel()
           // recorder.release()
        }catch (e:Exception)
        {
            Log.d("TAG", "onPause: exception 180 ")
        }

         /*   millis=0
            seconds=15
            millisTimer.cancel()
            secondsTimer.cancel()*/
    }


    override fun finish() {
        super.finish()
       // recorder.release()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun jumpToPlayActivity()
    {
        runOnUiThread {
            val intent = Intent(this@AudioMainActivity, PlayActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }
    }

    private fun listenOnRecorderStates() = with(binding) {
        runOnUiThread {
            recorder = Recorder.getInstance(applicationContext).init().apply {
                onStart = {
                    recordButton.setImageDrawable(getDrawableCompat(R.drawable.ic_stop_24))
                }
                onStop = {
                    visualizer.clear()
                    timelineTextView.text = 0L.formatAsTime()
                    recordButton.setImageDrawable(getDrawableCompat(R.drawable.ic_record_24))
                    // millisLiveData.postValue("")
                    // secondsLiveData.postValue("")
                    jumpToPlayActivity()
                    // binding.btnPlay.isVisible=true
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
    }

    companion object {
        private const val AUDIO_PERMISSION_REQUEST_CODE = 1
    }
}