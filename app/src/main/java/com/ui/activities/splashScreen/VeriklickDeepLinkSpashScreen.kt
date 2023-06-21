package com.ui.activities.splashScreen

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.data.dataHolders.CreateProfileDeepLinkHolder
import com.data.dataHolders.DataStoreHelper
import com.ui.activities.candidateQuestionnaire.ActivityCandidateQuestinnaire
import com.veriKlick.*
import com.ui.activities.uploadProfilePhoto.ActivityUploadProfilePhoto
import com.veriKlick.databinding.ActivitySplashScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class VeriklickDeepLinkSpashScreen : AppCompatActivity() {
    lateinit var binding: ActivitySplashScreenBinding
    private var pathstr:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            val deepLinkingIntent = intent
            val schemestr=deepLinkingIntent.scheme
            pathstr=deepLinkingIntent.data!!.path

        }catch (e:Exception)
        {
            Log.d(TAG, "onCreate: 71 excpetion ${e.message}")
        }


        Log.d(TAG, "onCreate: intent data from deeplink   ${getString(R.string.url_createCandidatebase)+pathstr.toString()}")
        if (!pathstr.equals("") || !pathstr.equals("null") || pathstr!=null) {
            if (pathstr!!.contains("CreateCandidateProfile")){
                CreateProfileDeepLinkHolder.setLink(getString(R.string.url_createCandidatebase) + pathstr.toString())
                CreateProfileDeepLinkHolder.setPathCreateCandidateString(pathstr.toString())
                Log.d(TAG, "onCreate: createCandiate")
            }
            if(pathstr!!.contains("CandidateQuestionierAnswer"))
            {
                CreateProfileDeepLinkHolder.setLink(getString(R.string.url_createCandidatebase) + pathstr.toString())
                CreateProfileDeepLinkHolder.setQuestionnaireLink(pathstr.toString())
                Log.d(TAG, "onCreate: candidate Question")
            }
        }

        val content = findViewById<View>(android.R.id.content)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            content.viewTreeObserver.addOnDrawListener { false }
        }

        getAppLanguage()

    }

    private val TAG="langugaeWalaDeeplink"
    private fun getAppLanguage()
    {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (DataStoreHelper.getAppLanguage()!=null &&  !DataStoreHelper.getAppLanguage().equals("null")){
                    Log.d(TAG, "getAppLanguage: getapplange not null ${DataStoreHelper.getAppLanguage()}")
                    var language= DataStoreHelper.getAppLanguage()
                    runOnUiThread { setLanguagetoApp1(language) }
                }
                else{
                    Log.d(TAG, "getAppLanguage: getapplange  null")
                }
            }catch (e:Exception)
            {
                Log.d(TAG, "getAppLanguage: getapplange exception ${e.message}")
            }
        }
    }


    private fun setLanguagetoApp1(langCode:String)
    {
        runOnUiThread {
            val local= Locale(langCode)
            Locale.setDefault(local)
            val config=resources.configuration
            config.setLocale(local)
            resources.updateConfiguration(config,resources.displayMetrics)
            Handler(Looper.getMainLooper()).postDelayed({
             var intent:Intent?=null
                if (!pathstr.equals("") || !pathstr.equals("null") || pathstr!=null) {
                    if (pathstr!!.contains("CreateCandidateProfile")){
                        intent=Intent(this,ActivityUploadProfilePhoto::class.java)
                    }
                    if(pathstr!!.contains("CandidateQuestionierAnswer"))
                    {
                        intent=Intent(this,ActivityCandidateQuestinnaire::class.java)
                    }
                }



                startActivity(intent)
                overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
                finish()
            },1000)

        }
    }



}