package com.ui.activities.splashScreen

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.data.dataHolders.DataStoreHelper
import com.data.dismissProgressDialog
import com.data.exceptionHandler
import com.data.setHandler
import com.data.setLanguagetoApp
import com.domain.constant.AppConstants
import com.veriKlick.*
import com.ui.activities.login.LoginActivity
import com.ui.activities.upcomingMeeting.UpcomingMeetingActivity
import com.veriKlick.databinding.ActivitySplashScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashScreen : AppCompatActivity() {
    lateinit var binding: ActivitySplashScreenBinding
    var email: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getAppLanguage()
        val content = findViewById<View>(android.R.id.content)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            content.viewTreeObserver.addOnDrawListener { false }
        }

          CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
              email= DataStoreHelper.getUserEmail()
          }

        setHandler().postDelayed(kotlinx.coroutines.Runnable {
            checkLogedInUser()
        }, 1000)

    }
    /*
        val intent= Intent(this@SplashScreen,DashboardActiviy::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_left,R.anim.slide_right)
                finish()
     */

    private fun checkLogedInUser() {
        Log.d("datauser", "checkEmail: splash $email")
        if (email != null) {
            if (!email.equals("null")) {
                Log.d("datauser", "checkEmail: twilio video email $email")
                startActivityUpcomingMeeting()
            }
            else {
                Log.d("datauser", "checkEmail: login screen email $email")
                startActivity(Intent(this, LoginActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
            }
        }
        else {
            Log.d("datauser", "checkEmail: twilio video email else $email")
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
    }

    private fun getAppLanguage()
    {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (DataStoreHelper.getAppLanguage()!=null){
                    setLanguagetoApp(intent,DataStoreHelper.getAppLanguage(),false)
                }
                else{

                }
            }catch (e:Exception)
            {

            }
        }
    }

    fun startActivityUpcomingMeeting()
    {
        val intent=Intent(this, UpcomingMeetingActivity::class.java)
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            intent.putExtra(AppConstants.LOGIN_WITH_OTP,DataStoreHelper.getLoggedInStatus())
        }
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        },500)

    }

    override fun onDestroy() {
        dismissProgressDialog()
        super.onDestroy()
    }


}