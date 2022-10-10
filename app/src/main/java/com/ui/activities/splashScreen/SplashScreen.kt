package com.ui.activities.splashScreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.data.dataHolders.DataStoreHelper
import com.data.setHandler
import com.domain.constant.AppConstants
import com.example.twillioproject.databinding.ActivitySplashScreenBinding
import com.ui.activities.login.LoginActivity
import com.ui.activities.upcomingMeeting.UpcomingMeetingActivity
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

          CoroutineScope(Dispatchers.IO).launch {
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
                finish()
            }
        }
        else {
            Log.d("datauser", "checkEmail: twilio video email else $email")
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    fun startActivityUpcomingMeeting()
    {
        val intent=Intent(this, UpcomingMeetingActivity::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            intent.putExtra(AppConstants.LOGIN_WITH_OTP,DataStoreHelper.getLoggedInStatus())
        }
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            finish()
        },500)

    }


}