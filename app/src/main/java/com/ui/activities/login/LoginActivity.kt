package com.ui.activities.login

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.data.dataHolders.DataStoreHelper
import com.data.*
import com.domain.BaseModels.ModelLanguageSelect
import com.domain.constant.AppConstants
import com.veriKlick.databinding.ActivityLoginBinding
import com.ui.activities.forgotPassword.ForgotPasswordActivity
import com.ui.activities.joinmeeting.JoinMeetingActivity
import com.ui.activities.login.loginwithotp.ActivitiyLoginWithOtp
import com.ui.activities.upcomingMeeting.UpcomingMeetingActivity
import com.veriKlick.R
import com.veriKlick.databinding.LayoutChooseLanguageDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.util.Calendar
import java.util.TimeZone
import kotlin.concurrent.thread

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private var mEmail = ""
    private var mPassword = ""
    private var mError = ""
    lateinit var binding: ActivityLoginBinding
    private val TAG = "loginActivitytest"
    private var existingTime = ""

    //lateinit var navController: NavController
    private lateinit var viewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_login, null, false)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)



        checkPermissions()

        handleOnEventChanges()

        Log.d(
            TAG,
            "onCreate: local time ${getCurrentDate()}  \n ${getCurrentUtcFormatedDate()} \n ${getCurrentDate()} \n ${getIntervalMonthDate()} "
        )

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

        var istoggle = true
        binding.btnToggleEye.setOnClickListener {

            if (istoggle) {
                // binding.etPassword.inputType=InputType.TYPE_CLASS_TEXT
                binding.btnToggleEye.setBackgroundResource(R.drawable.ic_img_toggle_eye)
                binding.etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                istoggle = false
            } else {
                binding.btnToggleEye.setBackgroundResource(R.drawable.ic_img_toggle_eye_hide)
                binding.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                //binding.etPassword.inputType=InputType.TYPE_TEXT_VARIATION_PASSWORD
                istoggle = true
            }
            binding.tvPsswdError.visibility = View.INVISIBLE
        }

        binding.btnContinueGuest.setOnClickListener {
            // viewModel.getVideoSession("I2D8o1imAlVv3JVIxKdG")

            val intent = Intent(this@LoginActivity, JoinMeetingActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)


        }

        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.btnSubmitButton.setOnClickListener {

            handleBlankfields()
            if (!binding.etPassword.text.toString().equals(""))
                if (binding.etPassword.text.toString().length > 6) {
                    binding.tvPsswdError.isVisible = true
                    checkTextFieldsAndPerformAction()
                } else {
                    binding.tvPsswdError.isVisible = true
                    binding.tvPsswdError.text =
                        getString(R.string.txt_password_must_be_greaterthan_6)
                }
            hideKeyboard(this)
        }

        binding.btnOtpLogin.setOnClickListener {
            val intent = Intent(this, ActivitiyLoginWithOtp::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.tvSetPreference.setOnClickListener {
            selectLangaugeDialog()
        }

    }

    override fun onDestroy() {
        dismissProgressDialog()
        super.onDestroy()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }

    fun checkPermissions() {

        checkAllPermisions()
        //getPermissionWithoutSetting()
    }

    private fun checkDeepLinkIsOpenFirst(){
        CoroutineScope(Dispatchers.IO).launch {

            try {
                if (DataStoreHelper.getDeeplinkIsOpenStatus() != null && !DataStoreHelper.getDeeplinkIsOpenStatus()) {
                    runOnUiThread { getDeepLinkPermission {  } }

                } else {
                    runOnUiThread {  }

                }
            } catch (e: Exception) {
                runOnUiThread { getDeepLinkPermission {  } }

            }
        }

        }

    private fun checkAllPermisions() {
        if (Build.VERSION.SDK_INT==Build.VERSION_CODES.TIRAMISU){
            Log.d(TAG, "checkAllPermisions: up q")
            requestCameraAndMicPermissionsTiramishu{
                requestWriteExternamlStoragePermissions {
                    requestNearByPermissions {
                       // checkDeepLinkIsOpenFirst()
                }
                }
            }
        }else
        {
            requestCameraAndMicPermissions{
                requestWriteExternamlStoragePermissions {
                    requestNearByPermissions {
                       // checkDeepLinkIsOpenFirst()
                    }
                }
            }
            Log.d(TAG, "checkAllPermisions: below tira")
        }

    }

    private fun getPermissionWithoutSetting() {
        requestVideoPermissions {
            if (it) {
                thread {
                    Thread.sleep(500)
                    requestNotificationPermission {
                        Log.d(TAG, "onCreate: request Notification permission $it")
                    }
                }
            } else {
                requestVideoPermissions {
                    if (it) {
                        requestNearByPermissions() {
                            thread {
                                Thread.sleep(500)
                                requestNotificationPermission {
                                    Log.d(TAG, "onCreate: request Notification permission $it")
                                }
                            }
                            Log.d(TAG, "onCreate: onNearbyPermission $it")
                        }
                        //showToast(this,getString(R.string.txt_permission_required))
                    } else {
                        requestVideoPermissions {
                            thread {
                                Thread.sleep(500)
                                requestNotificationPermission {
                                    Log.d(
                                        TAG,
                                        "onCreate: request Notification permission notification persimission $it"
                                    )
                                    CoroutineScope(Dispatchers.IO).launch {
                                        try {
                                            try {
                                                if (DataStoreHelper.getDeeplinkIsOpenStatus() != null) {
                                                    if (!DataStoreHelper.getDeeplinkIsOpenStatus()) {
                                                        runOnUiThread { getDeepLinkPermission {} }
                                                    } else {

                                                    }
                                                } else {
                                                    runOnUiThread { getDeepLinkPermission {} }
                                                }
                                            } catch (e: Exception) {
                                                runOnUiThread { getDeepLinkPermission {} }

                                            }
                                        } catch (e: Exception) {
                                            runOnUiThread {
                                                getDeepLinkPermission {
                                                    DataStoreHelper.setDeepLinkData(
                                                        false
                                                    )
                                                }
                                            }

                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }

    private fun getDeepLinkPermission(onFinish: () -> Unit) {
            Log.d(TAG, "getDeepLinkPermission: on deeplink dialog")
            val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            dialog.setTitle(getString(R.string.txt_please_enable_the_deeplink))
            dialog.setPositiveButton(getString(R.string.txt_ok),object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    Log.d(TAG, "onClick: ${Build.BRAND.toString()}")
                    try{
                        CoroutineScope(Dispatchers.IO).launch {
                            DataStoreHelper.setDeepLinkData(true)
                        }
                        if (Build.BRAND.toString().trim().lowercase().contains("SAMSUNG".trim().lowercase()))
                        {
                            showCustomSnackbarOnTop("you have to add deeplink manually")
                        }else
                        {
                            val intent = Intent(
                                Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                                Uri.parse("package:${packageName}")
                            )
                            startActivity(intent)
                        }
                    }catch (e:Exception)
                    {
                        showCustomSnackbarOnTop("you have to add deeplink manually")
                    }

                }
            })
            dialog.setOnDismissListener {
                onFinish()
            }
            dialog.create()
            dialog.show()

    }

    fun handleBlankfields() {
        if (binding.etEmail.text.toString().equals("")) {
            binding.tvEmailError.text = getString(R.string.txt_required)
        } else {
            binding.tvEmailError.text = ""
        }
        if (binding.etPassword.text.toString().equals("")) {
            binding.tvPsswdError.text = getString(R.string.txt_required)
        } else {
            binding.tvPsswdError.text = ""
        }
    }


    /* "userName": "gaurav+firstlevel@synkriom.com",
        "password": "PassRecruiter@1"
*/


    fun handleLoginApi(email: String, psswd: String) {
        viewModel.login(
            //encrypt(email), encrypt(psswd) ,
            email, psswd,
            response = { result, data, exception ->
                when (result) {
                    200 -> {
                        Log.d(TAG, "onCreate: success ${data.data?.accessToken}")
                        DataStoreHelper.setToken(data.data?.accessToken!!)
                        // DataStoreHelper.insertValue(email,psswd)
                        DataStoreHelper.insertValue(email, psswd)
                        DataStoreHelper.setLoggedInWithOtp(false)
                        decodeLoginToken(data.data?.accessToken!!, response = { response ->

                            CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

                                if (response != null) {
                                    Log.d(
                                        TAG,
                                        "handleLoginApi: userids response.Name ${response.Name} ${response.UserId.toString()}   ${response.CreatedBy.toString()} "
                                    )
                                    DataStoreHelper.setUserName(response.Name)
                                    DataStoreHelper.setMeetingRecruiterAndUserIds(
                                        response.UserId.toString(),
                                        response.CreatedBy.toString()
                                    )

                                    val intent = Intent(
                                        this@LoginActivity,
                                        UpcomingMeetingActivity::class.java
                                    )
                                    CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                                        intent.putExtra(AppConstants.LOGIN_WITH_OTP, false)
                                    }

                                    Handler(Looper.getMainLooper()).postDelayed({
                                        startActivity(intent)
                                        finish()
                                        overridePendingTransition(
                                            R.anim.slide_in_right,
                                            R.anim.slide_out_left
                                        )

                                    }, 500)

                                } else {
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

                    401 -> {
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
                } else {
                    Log.d(TAG, "handleLoginApi: not visible progress ")
                    dismissProgressDialog()
                }
            })
    }

    fun checkTextFieldsAndPerformAction() {
        CoroutineScope(Dispatchers.IO + exceptionHandler)
            .launch {

                validate(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString()
                ) { email, psswd, isSuccess, error ->
                    if (isSuccess) {
                        if (checkInternet()) {
                            handleLoginApi(email, psswd)
                        } else {
                            showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
                            /*   Snackbar.make(binding.root,getString(R.string.txt_no_internet_connection),
                                   Snackbar.LENGTH_SHORT).show()*/
                        }
//8218090995
                        Log.d("textcheck", "click on success btn $email $psswd ")
                    } else {

                    }
                }
                Log.d("TAG", "onCreate: ${DataStoreHelper.getUserEmail()}")
            }


    }


    private fun selectLangaugeDialog() {
        runOnUiThread {
            val dialog = Dialog(this)

            val dialogBinding =
                LayoutChooseLanguageDialogBinding.inflate(LayoutInflater.from(this))
            dialog.setContentView(dialogBinding.root)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogBinding.btnCross.setOnClickListener {
                dialog.dismiss()
            }
            dialogBinding.btnSubmitButton.setText(getString(R.string.txt_submit))
            val language = mutableListOf<String>()
            val languageStringList = mutableListOf<ModelLanguageSelect>()

            languageStringList.add(ModelLanguageSelect(getString(R.string.txt_english), "en-US"))
            languageStringList.add(ModelLanguageSelect(getString(R.string.txt_spanish), "es"))
            languageStringList.add(ModelLanguageSelect(getString(R.string.txt_french), "fr"))

            language.add(getString(R.string.txt_select_language))
            language.add(getString(R.string.txt_english))
            language.add(getString(R.string.txt_spanish))
            language.add(getString(R.string.txt_french))
            var selectedLanguage: String? = null
            CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                runOnUiThread {

                    // dialogBinding.tvUsername.setText(data.Name)

                    val langAdapter = getArrayAdapterOneItemSelected(language)
                    dialogBinding.spinnerLanguage.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                if (position != 0) {
                                    languageStringList.forEach {
                                        if (it.language.equals(language[position])) {
                                            selectedLanguage = it.langCode
                                        }
                                    }

                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                        }

                    dialogBinding.spinnerLanguage.adapter = langAdapter

                    dialogBinding.btnSubmitButton.setOnClickListener {
                        if (selectedLanguage != null) {
                            dialog.dismiss()
                            setLanguagetoApp(intent,selectedLanguage.toString(),true)
                            finish()
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        } else {
                            dialogBinding.tvError.visibility = View.VISIBLE
                            setHandler().postDelayed({
                                dialogBinding.tvError.visibility = View.INVISIBLE
                            }, 3000)
                            //showCustomSnackbarOnTop(getString(R.string.txt_please_select_language))
                        }
                    }
                }

            }

            dialog.create()
            dialog.show()

        }
    }

    //expiration=0, email=null, userId=0, role=null), errorMessage=null

    fun handleOnEventChanges() {
        binding.etEmail.doOnTextChanged { text, start, before, _ ->
            emailValidator(this, text.toString()) { isOk, email, error ->
                if (isOk) {
                    mEmail = email
                    binding.tvEmailError.visibility = View.INVISIBLE
                } else {
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
                } else {
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