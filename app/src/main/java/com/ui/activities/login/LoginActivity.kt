package com.ui.activities.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.data.dataHolders.DataStoreHelper
import com.data.*
import com.domain.constant.AppConstants
import com.veriKlick.*
import com.veriKlick.databinding.ActivityLoginBinding
import com.ui.activities.forgotPassword.ForgotPasswordActivity
import com.ui.activities.joinmeeting.JoinMeetingActivity
import com.ui.activities.login.loginwithotp.ActivitiyLoginWithOtp
import com.ui.activities.upcomingMeeting.UpcomingMeetingActivity
import com.veriKlick.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private var mEmail = ""
    private var mPassword = ""
    private var mError = ""
    lateinit var binding: ActivityLoginBinding
    private val TAG = "loginActivitytest"
    private var existingTime=""
    //lateinit var navController: NavController
    private lateinit var viewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_login, null, false)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)


        checkPermissions()

        handleOnEventChanges()

        Log.d(TAG, "onCreate: local time ${getCurrentDate()}  \n ${getCurrentUtcFormatedDate()} \n ${getCurrentDate()} \n ${getIntervalMonthDate()} ")

        /**crash code*/
       /* val crashButton = Button(this)
        crashButton.text = "Test Crash"
        crashButton.setOnClickListener {
            throw RuntimeException("Test Crash") // Force a crash
        }

        addContentView(crashButton, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT))
*/

        var istoggle=true
        binding.btnToggleEye.setOnClickListener {

            if(istoggle)
            {
               // binding.etPassword.inputType=InputType.TYPE_CLASS_TEXT
                binding.btnToggleEye.setBackgroundResource(R.drawable.ic_img_toggle_eye)
                binding.etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                istoggle=false
            }
            else
            {
                binding.btnToggleEye.setBackgroundResource(R.drawable.ic_img_toggle_eye_hide)
                binding.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                //binding.etPassword.inputType=InputType.TYPE_TEXT_VARIATION_PASSWORD
                istoggle=true
            }
            binding.tvPsswdError.visibility=View.INVISIBLE
        }

        binding.btnContinueGuest.setOnClickListener {
           // viewModel.getVideoSession("I2D8o1imAlVv3JVIxKdG")
            val intent=Intent(this@LoginActivity,JoinMeetingActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }

        binding.tvForgotPassword.setOnClickListener {
            val intent=Intent(this,ForgotPasswordActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }

        binding.btnSubmitButton.setOnClickListener {

           handleBlankfields()
            if (!binding.etPassword.text.toString().equals(""))
            if(binding.etPassword.text.toString().length>6) {
                binding.tvPsswdError.isVisible=true
                checkTextFieldsAndPerformAction()
            }else
            {
                binding.tvPsswdError.isVisible=true
                binding.tvPsswdError.text=getString(R.string.txt_password_must_be_greaterthan_6)
            }
            hideKeyboard(this)
        }

        binding.btnOtpLogin.setOnClickListener {
            val intent=Intent(this,ActivitiyLoginWithOtp::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
    }

    override fun onDestroy() {
        dismissProgressDialog()
        super.onDestroy()
    }


    fun checkPermissions()
    {

        requestVideoPermissions {
            if (it)
            {

            }else
            {
                requestVideoPermissions {
                    if (it)
                    {
                        requestNearByPermissions(){
                            Log.d(TAG, "onCreate: onNearbyPermission $it")
                        }
                        //showToast(this,getString(R.string.txt_permission_required))
                    }else
                    {
                        requestVideoPermissions {

                        }
                    }
                }
            }
        }

    }

    fun handleBlankfields() {
        if (binding.etEmail.text.toString().equals("")) {
            binding.tvEmailError.text = getString(R.string.txt_required)
        }
        else {
            binding.tvEmailError.text = ""
        }
        if (binding.etPassword.text.toString().equals("")) {
            binding.tvPsswdError.text = getString(R.string.txt_required)
        }
        else {
            binding.tvPsswdError.text = ""
        }
    }


    /* "userName": "gaurav+firstlevel@synkriom.com",
        "password": "PassRecruiter@1"
*/


    fun handleLoginApi(email: String, psswd: String) {
        viewModel.login(
            //encrypt(email), encrypt(psswd) ,
            email,psswd,
            response = { result, data, exception ->
                when (result) {
                    200 -> {
                        Log.d(TAG, "onCreate: success ${data.data?.accessToken}")
                        DataStoreHelper.setToken(data.data?.accessToken!!)
                       // DataStoreHelper.insertValue(email,psswd)

                        DataStoreHelper.insertValue(email,psswd)
                        DataStoreHelper.setLoggedInWithOtp(false)
                        decodeLoginToken(data.data?.accessToken!!, response = { response ->

                            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                                if (response!=null){
                                    Log.d(TAG, "handleLoginApi: userids ${response.UserId.toString()}   ${response.CreatedBy.toString()} ")
                                    DataStoreHelper.setMeetingRecruiterAndUserIds(response.UserId.toString(),response.CreatedBy.toString())

                                    val intent=Intent(this@LoginActivity, UpcomingMeetingActivity::class.java)
                                    CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                                        intent.putExtra(AppConstants.LOGIN_WITH_OTP,false)
                                    }

                                    Handler(Looper.getMainLooper()).postDelayed({
                                        startActivity(intent)
                                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
                                        finish()
                                    },500)

                                }else
                                {
                                    showCustomSnackbarOnTop(getString(R.string.txt_something_went_wrong))
                                }
                            }

                            Log.d(TAG, "handleLoginApi: token decoded success $response ")
                            //DataStoreHelper.insertValue(response.Email, response.Phone.toString())
                        })

                    }
                    400 -> {
                        Log.d(TAG, "onCreate: null data")
                    }
                    404 -> {
                        Log.d(TAG, "onCreate: not found ")
                    }
                    401->{
                        Log.d(TAG, "onCreate: wrong credentials")
                        Log.d(TAG, "onCreate: wrong credentials")
                        //data.message.toString()
                        showCustomSnackbarOnTop(data.message.toString())
                        //showToast(this,data.message.toString())//getString(R.string.txt_wrong_credentials)
                    }
                    500 -> {
                        Log.d(TAG, "onCreate: exception $exception")
                        showCustomSnackbarOnTop("Please try again")
                        //showToast(this,"Please try again")
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

    fun checkTextFieldsAndPerformAction() {
        CoroutineScope(Dispatchers.IO+exceptionHandler)
            .launch {

                    validate(
                        binding.etEmail.text.toString(),
                        binding.etPassword.text.toString()
                    ) { email, psswd, isSuccess ,error->
                        if (isSuccess) {
                            if (checkInternet()){
                                    handleLoginApi(email, psswd)
                            }else
                            {
                                showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                             /*   Snackbar.make(binding.root,getString(R.string.txt_no_internet_connection),
                                    Snackbar.LENGTH_SHORT).show()*/
                            }

                            Log.d("textcheck", "click on success btn $email $psswd ")
                        }
                        else {

                        }
                    }
                Log.d("TAG", "onCreate: ${DataStoreHelper.getUserEmail()}")
                }


    }


    //expiration=0, email=null, userId=0, role=null), errorMessage=null

    fun handleOnEventChanges() {
        binding.etEmail.doOnTextChanged { text, start, before, _ ->
            emailValidator(this, text.toString()) { isOk, email, error ->
                if (isOk) {
                    mEmail = email
                    binding.tvEmailError.visibility = View.INVISIBLE
                }
                else {
                    if (error != null)
                    //binding.etEmail.error = error
                        binding.tvEmailError.text = error
                    binding.tvEmailError.visibility = View.VISIBLE
                }
            }
        }

        binding.etPassword.doOnTextChanged { text, start, before, count ->
            passwordValidator(this, text.toString()) { isOk, psswd, error ->
                if (isOk) {
                    Log.d("textcheck", "onCreate: empty $psswd")
                    binding.tvPsswdError.visibility = View.INVISIBLE
                }
                else {
                    if (error != null) {
                        mError = error
                    }
                    //binding.etPassword.error = error
                    binding.tvPsswdError.text = error
                    binding.tvPsswdError.visibility = View.VISIBLE
                }
            }
        }
    }


}