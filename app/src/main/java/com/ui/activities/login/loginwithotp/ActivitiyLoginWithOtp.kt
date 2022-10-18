package com.ui.activities.login.loginwithotp

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.data.*
import com.data.dataHolders.DataStoreHelper
import com.domain.BaseModels.ResponseOtpVerificationStatus
import com.domain.constant.AppConstants


import com.example.twillioproject.R
import com.example.twillioproject.databinding.ActivityActivitiyLoginWithOtpBinding
import com.example.twillioproject.databinding.LayoutOtpVerificationBottomsheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ui.activities.upcomingMeeting.UpcomingMeetingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ActivitiyLoginWithOtp : AppCompatActivity() {
    private lateinit var binding: ActivityActivitiyLoginWithOtpBinding
    private val viewModel by viewModels<OtpLoginViewModel>()
    private val TAG = "activityloginwithotp"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActivitiyLoginWithOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnJumpBack.setOnClickListener {
          onBackPressed()
        }
        btnDisabledbackGround()

        binding.btnSendOtp.setOnClickListener {

            hideKeyboard(this)
            /**woking*/ sendOtpToEmail()
        }
        binding.btnResendOtp.setOnClickListener {
            hideKeyboard(this)
            /**woking*/ sendOtpToEmail()
        }

        binding.parentLayout.setOnClickListener {
            hideKeyboard(this)
        }

        handleEmailOtpVerification()

        binding.btnVerifyOtp.setOnClickListener {
            verifyOtp(binding.etOtp.text.toString())
        }
    }

    override fun onBackPressed() {
        if (binding.etEmail.isEnabled)
        {
            super.onBackPressed()
        }else
        {
         handleUI(400)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }

    fun btnEnabledbackGround()
    {
        binding.btnSendOtp.alpha=1f
        binding.btnSendOtp.isEnabled=true
    }

    fun btnDisabledbackGround()
    {
        binding.btnSendOtp.alpha=0.5f
        binding.btnSendOtp.isEnabled=false
    }

    private var email: String = ""
    private fun handleEmailOtpVerification() {
        binding.etEmail.doOnTextChanged { text, start, before, count ->
            Log.d(TAG, "sendEmailOtpVerification: text ${text.toString()}")
            emailValidator(
                this,
                email = text.toString(),
                validateEmail = { isEmailOk, mEmail, error ->
                    Log.d(TAG, "sendEmailOtpVerification: text ${isEmailOk} ${mEmail}  ${error.toString()}")
                    if (isEmailOk) {
                        email=mEmail
                        btnEnabledbackGround()
                    }
                    else
                    {
                        btnDisabledbackGround()
                        binding.etEmail.setError(getString(R.string.txt_enter_valid_email))
                        Log.d(TAG, "sendEmailOtpVerification: error $error")
                    }
                })
        }
    }

    fun handleUI(onResponse:Int)
    {
        Handler(Looper.getMainLooper()).post(Runnable {
            when(onResponse)
            {
                200->{
                    binding.btnResendOtp.isVisible=true
                    binding.btnSendOtp.isVisible=false
                    binding.parentLayoutVerifyotp.isVisible=true
                    binding.btnVerifyOtp.isVisible=true
                    binding.etEmail.isEnabled=false
                }
                400->{
                    binding.btnResendOtp.isVisible=false
                    binding.btnSendOtp.isVisible=true
                    binding.parentLayoutVerifyotp.isVisible=false
                    binding.btnVerifyOtp.isVisible=false
                    binding.etEmail.isEnabled=true
                }
                401->{
                    binding.btnResendOtp.isVisible=false
                    binding.btnSendOtp.isVisible=true
                    binding.parentLayoutVerifyotp.isVisible=false
                    binding.btnVerifyOtp.isVisible=false
                    binding.etEmail.isEnabled=true
                }
            }
        })
    }

    fun sendOtpToEmail()
    {
        viewModel.sendOtp(email, response = { result, data, exception ->
            when (result) {
                200 -> {
                    Log.d(TAG, "handleEmailVerification: 200 data $data")
                    handleUI(200)
                    binding.parentLayout.showSnackBar(data.Message.toString())
                }
                400 -> {
                    binding.parentLayout.showSnackBar(data.Message.toString())
                    handleUI(400)

                }
                401 -> {
                    Log.d(TAG, "handleEmailVerification: 401")
                    binding.parentLayout.showSnackBar(data.Message.toString())
                    handleUI(401)
                }
                404 -> {
                    Log.d(TAG, "handleEmailVerification: 404")
                }
                500 -> {
                    Log.d(
                        TAG,
                        "handleEmailVerification: 500 exception ${exception.toString()}"
                    )
                }
            }

        }, actionProgress = { action ->
            when (action) {
                1 -> {
                    showProgressDialog()
                }
                0 -> {
                    dismissProgressDialog()
                }
            }
        })
    }


    fun verifycationBottomSheetDialog()
    {
        val dialog=BottomSheetDialog(this,R.style.AppBottomSheetDialogTheme)
        val dialogbinding=LayoutOtpVerificationBottomsheetBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogbinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        dialogbinding.btnExitDialog.setOnClickListener {
            dialog.dismiss()
        }
        dialogbinding.btnVerify.setOnClickListener {
            verifyOtp(dialogbinding.etOtp.text.toString())
        }

        dialog.create()
        dialog.show()

    }

    private fun verifyOtp(otp:String) {
        viewModel.verifyOtp(email, otp, response = { result, data, exception ->
            when (result) {
                200 -> {
                    Log.d(TAG, "handleEmailVerification: 200 data $data")
                    if (data.Status!!.contains("VALID"))
                    {
                        binding.tvOtpError.text=""
                        handleVerifiedStatus(data)
                    }else {
                        binding.tvOtpError.text= data.Status.toString()
                    }
                }
                400 -> {
                    showToast(this, data.Status.toString())
                }
                401 -> {
                    Log.d(TAG, "handleEmailVerification: 401")
                }
                404 -> {
                    Log.d(TAG, "handleEmailVerification: 404")
                }
                500 -> {
                    Log.d(TAG, "handleEmailVerification: 500 exception ${exception.toString()}")
                }
            }

        }, actionProgress = { action ->
            when (action) {
                1 -> {
                    showProgressDialog()
                }
                0 -> {
                    dismissProgressDialog()
                }
            }
        })
    }
    fun handleVerifiedStatus(data: ResponseOtpVerificationStatus)
    {
        DataStoreHelper.insertValue(email,data.recruiterid!!)
        CoroutineScope(Dispatchers.IO).launch {
            DataStoreHelper.setMeetingRecruiterAndUserIds(data.recruiterid!!,data.subscriberId!!)
            DataStoreHelper.setLoggedInWithOtp(true)
        }

        val intent=Intent(this, UpcomingMeetingActivity::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            intent.putExtra(AppConstants.LOGIN_WITH_OTP,true)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        },500)
    }
}
