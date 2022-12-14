package com.ui.activities.forgotPassword

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.data.*
import com.veriklick.R
import com.veriklick.databinding.ActivityForgotPasswordBinding
import com.google.android.material.snackbar.Snackbar
import com.ui.activities.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {
   private lateinit var binding : ActivityForgotPasswordBinding

    private lateinit var viewModel: ForgotPasswordViewModel

    private val TAG="forgotpasswordactivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel=ViewModelProvider(this).get(ForgotPasswordViewModel::class.java)

        binding.btnJumpBack.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        validateEmail()
        binding.btnForgotPassword.setOnClickListener {
            if(binding.etForgotPassword.text.toString().equals("")){
                showError(getString(R.string.txt_required))
            }
            else
            {
                if (checkInternet()){
                    handleEmailEvent()
                    hideKeyboard(this)
                }else
                {
                    showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                    //Snackbar.make(it,getString(R.string.txt_no_internet_connection),Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }

    fun validateEmail()
    {
        binding.etForgotPassword.doOnTextChanged { text, start, before, count ->
            emailValidator(this,text.toString(),
            validateEmail = { isEmailOk,mEmail,error->
                if (isEmailOk) {
                    binding.tvEmailError.visibility = View.INVISIBLE
                }
                else {
                    if (error != null)
                    //binding.etEmail.error = error
                        binding.tvEmailError.text = error
                    binding.tvEmailError.visibility = View.VISIBLE
                }
            })
        }
    }
    fun showError(errorMessage:String)
    {
        Handler(Looper.getMainLooper()).post(kotlinx.coroutines.Runnable {
            binding.tvEmailError.visibility= View.VISIBLE
            binding.tvEmailError.setText(errorMessage)
        })
    }

    fun handleEmailEvent(){

        viewModel.sendMailForForgotPassword(binding.etForgotPassword.text.toString(),

            response = { result, data, exception ->
            when (result) {
                200 -> {
                    Log.d(TAG, "onCreate: success ${data.success} ${data.message}")
                    showCustomToast(data.message.toString())
                   // showToast(this@ForgotPasswordActivity,data.message.toString())
                   setHandler().postDelayed({
                       startActivity(
                           Intent(
                               this,
                               LoginActivity::class.java
                           )
                       )
                       finish()
                   },500)

                }
                400 -> {
                    Log.d(TAG, "onCreate: null data ${data.success} ${data.message}")
                    Log.d(TAG, "onCreate: null data")
                    showCustomToast(data.message.toString())
                    //Toast.makeText(this,data.message.toString(),Toast.LENGTH_LONG).show()
                  //  binding.tvEmailError.setText(getString(R.string.txt_failed_to_process_try_again))
                }
                404 -> {
                    Log.d(TAG, "onCreate: not found ${data.success} ${data.message}")
                    Log.d(TAG, "onCreate: not found ")
                }
                401->{
                    Log.d(TAG, "onCreate: failed to process sending mail ${data.success} ${data.message}")
                    Log.d(TAG, "onCreate: failed to process sending mail")
                    showCustomToast(data.message.toString())
                  /* showError(getString(R.string.txt_email_not_registerd_with_us))
                    Handler(Looper.getMainLooper()).postDelayed(kotlinx.coroutines.Runnable {
                        binding.tvEmailError.visibility= View.INVISIBLE
                    },3000)*/
                }
                500 -> {
                    Log.d(TAG, "onCreate: exception ${data.success} ${data.message}")
                    Log.d(TAG, "onCreate: exception $exception")
                }
            }
    },
    actionProgress = { action ->
        if (action == 1) {
            Log.d(TAG, "handleLoginApi: visible progress ")
            showProgressDialog()
        }
        else {
            Log.d(TAG, "handleLoginApi: not visible progress ")
            dismissProgressDialog()
        }
    })

    }


}