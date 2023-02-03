package com.data

//import com.veriKlick.databinding.CustomSnackbarGlobalBinding

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.projection.MediaProjectionManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.araujo.jordan.excuseme.ExcuseMe
import com.domain.BaseModels.ResponseChatFromToken
import com.domain.BaseModels.ResponseJWTTokenLogin
import com.domain.IncomingCallCallback
import com.domain.OnViewClicked
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.veriKlick.R
import com.veriKlick.databinding.CustomSnackbarGlobalBinding
import com.veriKlick.databinding.LayoutPrivacyPolicyBinding
import com.veriKlick.databinding.LayoutProgressBinding
import com.veriKlick.databinding.LayoutSuccessMsgSnackbarPlayerBinding
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.UnsupportedEncodingException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


var ispsswdok = false
var isemailok = false
var error: String = ""
/**working*//*
fun emailValidator(
    context: Context,
    email: String,
    validateEmail: (isEmailOk: Boolean, mEmail: String, error: String?) -> Unit
) {
    var mEmail = email
    val EMAIL_ADDRESS_PATTERN: Pattern =
        Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+")

    var isEmailOk = false

    if (mEmail.isEmpty()) {
        validateEmail(false, "", context.getString(R.string.txt_required))
        error = context.getString(R.string.txt_enter_valid_email)
    } else {
        if (EMAIL_ADDRESS_PATTERN.matcher(mEmail).matches()) {
            isEmailOk = true
            isemailok = true
        } else {
            mEmail = "null"
            isemailok = false
            isEmailOk = false
            validateEmail(isEmailOk, mEmail, context.getString(R.string.txt_enter_valid_email))
            error = context.getString(R.string.txt_enter_valid_email)
        }
    }
    validateEmail(isEmailOk, mEmail, null)
}
*/

fun emailValidator(
    context: Context,
    email: String,
    validateEmail: (isEmailOk: Boolean, mEmail: String, error: String?) -> Unit
) {


    /*

    [a-zA-Z0-9._%+-]+@
([a-zA-Z0-9-]{1,})+(\.[a-zA-Z]{2,})
(\.[a-zA-Z]{2,})?$


^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$

"[a-zA-Z0-9._%+-]+@
([a-zA-Z0-9-]{1,})+(\.[a-zA-Z]{2,})
(\.[a-zA-Z]{2,})?$"

     */

   // val pattrn="[a-zA-Z0-9._%+-]+@([a-zA-Z0-9-]{1,})+(\.[a-zA-Z]{2,})(\.[a-zA-Z]{2,})?$"

  //  val EMAIL_ADDRESS_PATTERN1: Pattern =//Pattern.compile("[a-zA-Z0-9._%+-]+@" +"([a-zA-Z0-9-]{1,})+(\\.[a-zA-Z]{2,})" +"(\\.[a-zA-Z]{2,})?\$")
        //Pattern.compile("[a-zA-Z0-9._%+-]+@([a-zA-Z0-9-]{1,})+(\\.[a-zA-Z]{2,})(\\.[a-zA-Z]{2,})?\$")

    var mEmail = email.trim()
    val emailPattern="^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-+]+)*@"+"[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$"
    val EMAIL_ADDRESS_PATTERN: Pattern =
        //Pattern.compile("[a-zA-Z0-9._%+-]+@" +"([a-zA-Z0-9-]{1,})+(\\.[a-zA-Z]{2,})" +"(\\.[a-zA-Z]{2,})?\$")
      Pattern.compile(emailPattern)
    val doubleDots=Pattern.compile("\\.\\.")
    var isEmailOk = false

    if (mEmail.isEmpty()) {
        validateEmail(false, "", context.getString(R.string.txt_required))
        error = context.getString(R.string.txt_enter_valid_email)
    } else {

        if (android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            isEmailOk = true
            isemailok = true
            validateEmail(isEmailOk, mEmail, null)
        } else {
            mEmail = "null"
            isemailok = false
            isEmailOk = false
            validateEmail(isEmailOk, mEmail, context.getString(R.string.txt_enter_valid_email))
            error = context.getString(R.string.txt_enter_valid_email)
        }
    }
  //  validateEmail(isEmailOk, mEmail, null)
}

fun checkSpecialCharatersInString(
    str: String,
    response: (isValide: Boolean, text: String) -> Unit
) {
    if (str.matches("[A-Za-z][^.]".toRegex())) {
        response(true, str)
    } else {
        response(false, str)
    }
}

fun Context.showCustomToast(str:String)
{
    Handler(Looper.getMainLooper()).post({
        val toast=Toast(this)
        toast.setGravity(Gravity.TOP,0,0)
        val layout = CustomSnackbarGlobalBinding.inflate(LayoutInflater.from(this))
       // toast.setMargin(0f,50f)

        layout.customsnackbarTextviewGlobal.text=str
        toast.view=layout.root
        toast.duration=Toast.LENGTH_LONG
        toast.show()
    })
}


fun validate(
    email: String,
    psswd: String,
    onSuccess: (email: String, psswd: String, isSuccess: Boolean, error: String?) -> Unit
) {

    if (ispsswdok == true && isemailok == true) {
        Log.d("textcheck", "onCreate: success $email $psswd")
        onSuccess(email, psswd, true, null)
    } else {
        Log.d("textcheck", "onCreate: failed $email $psswd")
        onSuccess(email, psswd, false, error)
    }
}


fun passwordValidator(
    context: Context,
    password: String,
    validatePassword: (isPasswordOk: Boolean, mPassword: String?, error: String?) -> Unit
) {
    /* val PASSWORD_PATTERN: Pattern =
         Pattern.compile("^" + "(?=.*[@#$%^&+=])" + "(?=\\S+$)" + ".{4,}" + "$");
 */
    if (password.isEmpty()) {
        validatePassword(false, null, context.getString(R.string.txt_required))
        ispsswdok = false
    } else if (password.length <= 6) {
        validatePassword(
            false,
            null,
            context.getString(R.string.txt_password_must_be_greaterthan_6)
        )
        error = context.getString(R.string.txt_password_must_be_greaterthan_6)
        ispsswdok = false
    }
    /* else if (!PASSWORD_PATTERN.matcher(password).matches()) {
         validatePassword(false, null, context.getString(R.string.txt_weakPassword))
         ispsswdok = false
     }*/
    else {
        validatePassword(true, password, null)
        ispsswdok = true
    }

}

var progressbar: Dialog? = null
fun Context.showProgressDialog() {
    progressbar?.let {
        it.dismiss()
    }
    android.os.Handler(Looper.getMainLooper()).post(Runnable {
        //val layoutView = LayoutInflater.from(this).inflate(R.layout.layout_progress, null, false)
        val layoutView = LayoutProgressBinding.inflate(LayoutInflater.from(this))
        progressbar = AlertDialog.Builder(this)
            .setView(layoutView.root)
            .setCancelable(false)
            .create()
        progressbar?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressbar?.show()
    })
}

fun Context.dismissProgressDialog() {
   try{

       android.os.Handler(Looper.getMainLooper()).post(Runnable {
           progressbar?.let {
               it.dismiss()
           }
       })

   }catch (e:Exception)
   {

   }
}

fun Context.showCustomSnackBar(msg: String) {
    val bindingSnack = LayoutSuccessMsgSnackbarPlayerBinding.inflate(LayoutInflater.from(this))
    val snack = Snackbar.make(
        (this as Activity).findViewById(android.R.id.content),
        msg,
        Snackbar.LENGTH_LONG
    )

    bindingSnack.viewHandler = object : OnViewClicked {
        override fun onViewClicked(view: View) {
            snack.dismiss()
        }
    }

    bindingSnack.tvSnackbarMsg.text = msg.trim()

    snack.view.setBackgroundColor(Color.TRANSPARENT)
    val snackLayout: Snackbar.SnackbarLayout = snack.view as Snackbar.SnackbarLayout
//    val param=snackLayout.layoutParams as FrameLayout.LayoutParams
//    param.gravity=Gravity.CENTER_HORIZONTAL
//    bindingSnack.root.layoutParams=param
    snackLayout.addView(bindingSnack.root)

    snack.show()

}

fun Context.showCustomSnackbarOnTop(msg: String) {
    val snackbar = Snackbar.make(
        (this as Activity).findViewById(android.R.id.content),
        "",
        Snackbar.LENGTH_SHORT
    )
    snackbar.view.setBackgroundColor(Color.TRANSPARENT)
    val layout =
        LayoutInflater.from(applicationContext).inflate(R.layout.custom_snackbar_global, null)
    val textView = layout.findViewById<TextView>(R.id.customsnackbar_textview_global)
    textView.text = msg
    val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
    val param = snackbarLayout.layoutParams as FrameLayout.LayoutParams
    param.gravity = Gravity.CENTER_HORIZONTAL
    layout.layoutParams = param
    layout.setPadding(0, 100, 0, 0)
    snackbarLayout.addView(layout)
    snackbar.show()

}

fun Context.checkIncomingCall(incomingCallStatus: IncomingCallCallback) {
    androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this).registerReceiver(
        object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                incomingCallStatus.onRecieved(true)
            }
        },
        IntentFilter(com.domain.constant.AppConstants.IN_COMING_CALL_ACTION)
    )
}


fun Context.showPrivacyPolicy(
    parent: ViewGroup,
    onClicked: (action: Boolean, dialog: Dialog) -> Unit,
    onClickedText: (url: String, action: Int) -> Unit
) {
    val dialog = Dialog(this)
    val dialogBinding = LayoutPrivacyPolicyBinding.inflate(LayoutInflater.from(this), parent, false)
    dialog.setContentView(dialogBinding.root)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    dialogBinding.btnAgree.setOnClickListener {
        onClicked(true, dialog)

    }
    dialogBinding.btnCancel.setOnClickListener {
        onClicked(false, dialog)
    }

    dialogBinding.tvPrivacyPolicy.text =
        "I have read and agree to the Terms and Conditions  and Privacy Policy. This meeting may be recorded. By joining the meeting you are also giving your consent to record this meeting otherwise click cancel to leave the meeting."
    dialogBinding.tvPrivacyPolicy.makeLinks(Pair("Terms and Conditions", View.OnClickListener {
        onClickedText("https://veriklick.com/terms-of-use/", 1)
    }))
    dialogBinding.tvPrivacyPolicy.makeLinks(Pair("Privacy Policy", View.OnClickListener {
        onClickedText("https://veriklick.com/privacy-policy/", 2)
    }))

    dialog.setCancelable(false)
    dialog.create()
    dialog.show()
}


fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {

        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                // use this to change the link color
                textPaint.color = textPaint.linkColor
                // toggle below value to enable/disable
                // the underline shown below the clickable text
                textPaint.isUnderlineText = true
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
//      if(startIndexOfLink == -1) continue // todo if you want to verify your texts contains links text
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}


fun Context.requestNearByPermissions(isGrant: (isGranted: Boolean) -> Unit) {
    ExcuseMe.couldYouGive(this).permissionFor(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH_CONNECT,

    ) {

        Log.d("permissioncheck", "requestVideoPermissions: $it ")
        if (  it.granted.contains(Manifest.permission.ACCESS_FINE_LOCATION) &&
             it.granted.contains(Manifest.permission.BLUETOOTH_CONNECT)
        ) {
            isGrant(true)
        } else {
            isGrant(false)
        }
    }
}





fun Context.requestVideoPermissions(isGrant: (isGranted: Boolean) -> Unit) {
    ExcuseMe.couldYouGive(this).permissionFor(
        android.Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
//        Manifest.permission.ACCESS_FINE_LOCATION,
//        Manifest.permission.BLUETOOTH_CONNECT,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_PHONE_STATE,
    ) {

        Log.d("permissioncheck", "requestVideoPermissions: $it ")
        if (it.granted.contains(android.Manifest.permission.CAMERA) &&
            it.granted.contains(Manifest.permission.RECORD_AUDIO) &&
            it.granted.contains(Manifest.permission.READ_PHONE_STATE) &&
          //  it.granted.contains(Manifest.permission.ACCESS_FINE_LOCATION) &&
           // it.granted.contains(Manifest.permission.BLUETOOTH_CONNECT) &&
            it.granted.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
            it.granted.contains(Manifest.permission.READ_EXTERNAL_STORAGE) &&
            it.granted.contains(Manifest.permission.READ_PHONE_STATE)
        ) {
            isGrant(true)
        } else {
            isGrant(false)
        }

    }



}


fun Context.requestWriteExternamlStoragePermissions(isGrant: (isGranted: Boolean) -> Unit) {
    ExcuseMe.couldYouGive(this).permissionFor(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
    ) {
        Log.d("permissioncheck", "requestVideoPermissions: $it ")
        if (it.granted.contains(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ) {
            isGrant(true)
        } else {
            isGrant(false)
        }
    }
}


fun Context.checkInternet(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null) {
            return networkInfo.isConnected
        }
    } else {
        val network = connectivityManager.activeNetwork
        if (network != null) {
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(network)
            if (networkCapabilities != null) {
                return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || networkCapabilities.hasTransport(
                    NetworkCapabilities.TRANSPORT_WIFI
                )
            }
        }
    }
    return false
}


fun Context.showToast(context: Context, txt: String) {
    Handler(Looper.getMainLooper()).post(kotlinx.coroutines.Runnable {
        Toast.makeText(context, txt, Toast.LENGTH_SHORT).show()
    })

}

fun View.showSnackBar(txt: String) {
    Handler(Looper.getMainLooper()).post(kotlinx.coroutines.Runnable {
        Snackbar.make(this, txt, Snackbar.LENGTH_LONG).show()
    })
}

fun change24to12hoursFormat(time: String): String {
    val sdf = SimpleDateFormat("HH:mm")
    val datef = sdf.parse(time)
    val sdf2 = SimpleDateFormat("hh:mm a")
    val dateFinal = sdf2.format(datef)
    return dateFinal.toString()
}

fun changeDatefrom_yyyymmdd_to_mmddyyyy(date: String): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val datef = sdf.parse(date)
    val sdf2 = SimpleDateFormat("MMM-dd-yyy")
    val dateFinal = sdf2.format(datef)
    return dateFinal.toString()
}

fun Context.requestScreenCapturePermission(
    context: Context,
    onResult: (intent: Intent, reqCode: Int) -> Unit
) {
    Log.d("screenreco", "Requesting permission to capture screen")
    val mediaProjectionManager =
        context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager?
    val ob = mediaProjectionManager!!.createScreenCaptureIntent()
    onResult(ob, 1011)
    // This initiates a prompt dialog for the user to confirm screen projection.

}

/*
object getNavController{
    fun getInstance(fragmentManager: FragmentManager): NavController
    {
        return fragmentManager.findFragmentById(R.id.nav_host_fragment)?.findNavController()!!
    }
}
*/
fun Context.setHandler() = Handler(Looper.getMainLooper())

/*
fun Context.handleGesture(event: MotionEvent?, swipe: (swipe: Boolean) -> Unit) {
    var x1 = 0f
    var x2 = 0f

    when (event?.action) {
        0 -> {
            x1 = event.x
        }
        1 -> {
            x2 = event.y
            val valueX = x2 - x1

            if (abs(valueX.toDouble()) > MIN_DISTANCE) {
                swipe(true)
                showToast(this, "swipe left")
            }
            else {
                swipe(false)
                showToast(this, "swipe right")
            }

        }
    }
}
*/




@Throws(java.lang.Exception::class)
fun decodeLoginToken(JWTEncoded: String, response: (ob: ResponseJWTTokenLogin?) -> Unit) {
    try {
        val split = JWTEncoded.split(".") //exception
        var tokenArray = mutableListOf<String>()
        for (i in split) {
            val index = split.indexOf(i)
            tokenArray.add(i)
            if (index == 1) {
                break
            }
        }
        if (tokenArray[0]!=null || !tokenArray[0].equals(""))
        {
            var jsonData:String?=null
            decodeBase64(tokenArray[1])?.let {
                jsonData=it
            }
            Log.d("TAGDE", "decodeLoginToken: decoded token ${jsonData}")
            if (jsonData!=null){
                jsonData?.let {
                    response(Gson().fromJson(decodeBase64(tokenArray[1]), ResponseJWTTokenLogin::class.java))
                    // response(Gson().fromJson(decoded, ResponseJWTTokenLogin::class.java))
                }
            }else
            {
                response(null)
            }
        }else
        {
            Log.d("TAGDE", "decodeLoginToken: decoded token null response")
            response(null)
        }
        Log.d("TAGDE", "decodeLoginToken: typeArray ${tokenArray[0]}")
        /*

        Log.d("JWT_DECODED", "Body: token in json data ${jsonData}   ")

        Log.d("JWT_DECODED", "Body: token in json form ${decodeBase64(tokenArray[1])}   ")
        Log.d("JWT_DECODED", "Body: token in json data ${jsonData}   ")
*/
    } catch (e: UnsupportedEncodingException) {
        response(null)
        Log.d("JWT_DECODED", "getJsonFromJWTToken: decoding exception ${e.printStackTrace()} ")
    }
}


@Throws(java.lang.Exception::class)
fun decodeChatToken(JWTEncoded: String, response: (ob: ResponseChatFromToken) -> Unit) {
    try {
        val split = JWTEncoded.split(".") //exception
        var tokenArray = mutableListOf<String>()
        for (i in split) {
            val index = split.indexOf(i)
            tokenArray.add(i)
            if (index == 1) {
                break
            }
        }

        val jsonData = decodeBase64(tokenArray[1])

        response(Gson().fromJson(decodeBase64(tokenArray[1]), ResponseChatFromToken::class.java))
        Log.d("JWT_DECODED", "Body: token in json form ${decodeBase64(tokenArray[1])}   ")
        Log.d("JWT_DECODED", "Body: token in json data ${jsonData}   ")

    } catch (e: UnsupportedEncodingException) {

        Log.d("JWT_DECODED", "getJsonFromJWTToken: decoding exception ${e.printStackTrace()} ")
    }catch (e:Exception)
    {

    }
}


fun decodeBase64(str: String): String? {
    var decoder: Base64.Decoder
    var decoded:String?=null
    try {
         decoder = Base64.getDecoder()
        decoded = String(decoder.decode(str))
        println("Decoded Data: $decoded")
    }catch (e:Exception)
    {
        Log.d("TAG", "decodeBase64: exception ${e.message} ")

    }
    return decoded!!
}


fun hideKeyboard(activity: Activity) {
    val view = activity.currentFocus
    if (view != null) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}


fun Context.getCurrentUtcFormatedDate(): String {

    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"))
    var utcTime1: String = sdf.format(Date())
    sdf.format(Date())

    val date = sdf.calendar.get(Calendar.MONTH) + 1
    val day = sdf.calendar.get(Calendar.DAY_OF_MONTH)
    val year = sdf.calendar.get(Calendar.YEAR)

    var timeUtclenght = utcTime1.length
    var timeUtc = utcTime1.subSequence(10, timeUtclenght)
    val finalUtcTime =
        year.toString() + "-" + date.toString() + "-" + (day).toString() + timeUtc.toString()
    Log.d("utcformatted", "getCurrentUtcFormatedDate:  $finalUtcTime")


    /* var current = LocalDateTime.now()
     var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
     var utcTime = SimpleTimeZone.getTimeZone("UTC").toZoneId()
     val formatted = current.format(formatter)*/
    return finalUtcTime
}


fun getCurrentDateToAMPM(): String {
    val cal=Calendar.getInstance()
    val date=cal.time
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

    //val date1 = sdf.format(date.time)
    val sdf2 = SimpleDateFormat("hh:mm a")
    val time1 = sdf2.format(date)
    return time1
}


fun getUtcDateToAMPM(date: String): String {

    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"))
    //var utcTime1: String = sdf.format(Date())

    val date1 = sdf.parse(date)
    val sdf2 = SimpleDateFormat("hh:mm a")

    val time1 = sdf2.format(date1)
//    val finaltime=hour.toString()+":"+minutes.toString()+" "+zone

//    Log.d("checkdate", "getUtcDateToAMPM: ${finaltime}")

    return time1
}


fun Context.getCurrentUtcFormatedDateIntervalofMonth(): String {

    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"))
    var utcTime1: String = sdf.format(Date())
    sdf.format(Date())

    val date = sdf.calendar.get(Calendar.MONTH) + 1
    val day = sdf.calendar.get(Calendar.DAY_OF_MONTH)
    val year = sdf.calendar.get(Calendar.YEAR)

    var timeUtclenght = utcTime1.length
    var timeUtc = utcTime1.subSequence(10, timeUtclenght)
    val finalUtcTime =
        year.toString() + "-" + (date + 1).toString() + "-" + (day).toString() + timeUtc.toString()
    Log.d("utcformatted", "getCurrentUtcFormatedDate:  $finalUtcTime")


    /* var current = LocalDateTime.now()
     var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
     var utcTime = SimpleTimeZone.getTimeZone("UTC").toZoneId()
     val formatted = current.format(formatter)*/
    return finalUtcTime
}


fun Context.getCurrentDate(): String? {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var datetime: String = sdf.format(Date())
    val datefull = sdf.format(Date())

    val date = sdf.calendar.get(Calendar.MONTH) + 1
    val day = sdf.calendar.get(Calendar.DAY_OF_MONTH)
    val year = sdf.calendar.get(Calendar.YEAR)

    val datesubstring = datetime.subSequence(10, datefull.length)

    val finalUtcTime =
        year.toString() + "-" + (date).toString() + "-" + (day).toString() + datesubstring.toString()


    return finalUtcTime
}

/*
fun Context.getPastWeekDate(): String? {

    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var datetime: String = sdf.format(Date())
    val datefull=sdf.format(Date())
    val date = sdf.calendar.get(Calendar.MONTH) + 1
    val day = sdf.calendar.get(Calendar.DAY_OF_MONTH)-getWeekDay()
    val year = sdf.calendar.get(Calendar.YEAR)

    val datesubstring=datetime.subSequence(10,datefull.length)

    val finalUtcTime=year.toString()+"-"+(date).toString()+"-"+(day).toString()+datesubstring.toString()

    minusWeekDay()
    return finalUtcTime
}

fun getPreviousWeek() :String {

    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val calendar =Calendar.getInstance()

    calendar.firstDayOfWeek=Calendar.DAY_OF_MONTH
    calendar.add(Calendar.DATE, getWeekDay())
    val date=calendar.time
    var datetime: String = sdf.format(calendar.time)

    minusWeekDay()
    Log.d("loginActivitytest", "getPreviousWeek: date 2 ${datetime.toString()} daycount ${getWeekDay()}")
    return datetime
}
*/

/*
fun Context.getPastWeekDateTemp(): String? {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var datetime: String = sdf.format(Date())
    val datefull=sdf.format(Date())





    val cal = Calendar.getInstance()
    cal.firstDayOfWeek = Calendar.SUNDAY
    cal[Calendar.DAY_OF_WEEK] = Calendar.SUNDAY


    val date = sdf.calendar.get(Calendar.MONTH) + 1
    val day = sdf.calendar.get(Calendar.DAY_OF_MONTH)-getWeekDay()
    val year = sdf.calendar.get(Calendar.YEAR)

    val datesubstring=datetime.subSequence(10,datefull.length)

    val finalUtcTime=year.toString()+"-"+(date).toString()+"-"+(day).toString()+datesubstring.toString()

    minusWeekDay()
    return finalUtcTime
}



fun Context.getCurrentUtcFormatedDateOfPastWeek(): String {

    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"))
    var utcTime1: String = sdf.format(Date())
    sdf.format(Date())

    val date = sdf.calendar.get(Calendar.MONTH) + 1
    val day = sdf.calendar.get(Calendar.DAY_OF_MONTH)- getWeekDay()
    val year = sdf.calendar.get(Calendar.YEAR)

    var timeUtclenght=utcTime1.length
    var timeUtc=utcTime1.subSequence(10,timeUtclenght)
    val finalUtcTime=year.toString()+"-"+date.toString()+"-"+(day-1).toString()+timeUtc.toString()
    Log.d("utcformatted", "getCurrentUtcFormatedDate:  $finalUtcTime")


    /* var current = LocalDateTime.now()
     var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
     var utcTime = SimpleTimeZone.getTimeZone("UTC").toZoneId()
     val formatted = current.format(formatter)*/
    minusWeekDay()
    return finalUtcTime
}
*/

fun EditText.getEditTextWithFlow(): Flow<String> {
    return callbackFlow<String> {
        this@getEditTextWithFlow.addTextChangedListener {
            trySend(it.toString())
        }
        awaitClose { cancel() }
    }
}

fun Context.getIntervalMonthDate(): String? {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var datetime: String = sdf.format(Date())
    val datefull = sdf.format(Date())

    val date = sdf.calendar.get(Calendar.MONTH) + 1
    val day = sdf.calendar.get(Calendar.DAY_OF_MONTH)
    val year = sdf.calendar.get(Calendar.YEAR)

    val datesubstring = datetime.subSequence(10, datefull.length)

    val finalUtcTime =
        year.toString() + "-" + (date + 1).toString() + "-" + (day).toString() + datesubstring.toString()


    return finalUtcTime
}

fun Context.getCurrentDayOfYear(): Int {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var datetime: String = sdf.format(Date())
    val datefull = sdf.format(Date())

    val date = sdf.calendar.get(Calendar.MONTH) + 1
    val day = sdf.calendar.get(Calendar.DAY_OF_MONTH)
    val year = sdf.calendar.get(Calendar.YEAR)
    val dayofYear = sdf.calendar.get(Calendar.DAY_OF_YEAR)

    val datesubstring = datetime.subSequence(10, datefull.length)

    //val finalUtcTime=year.toString()+"-"+(date+1).toString()+"-"+(day).toString()+datesubstring.toString()


    return dayofYear
}

fun getDateWithMonthName(date: String, action: Int): String {
    var sdfOb: SimpleDateFormat
    if (date.contains("Z")) {
        sdfOb = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    } else {
        sdfOb = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    }

    val sdfFormated: SimpleDateFormat
    if (action == 1) {
        sdfFormated = SimpleDateFormat("dd-MMM")
    } else {
        sdfFormated = SimpleDateFormat("dd-MMM-yyyy")
    }

    val date1 = sdfOb.parse(date)

    val time1 = sdfFormated.format(date1)
    return time1
}


const val MIN_DISTANCE = 150

/**
 * Enum class - Rest APIS Status*/
enum class Status {
    SUCCESS,
    FAILED,
    PROCESSING
}


