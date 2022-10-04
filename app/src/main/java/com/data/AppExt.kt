package com.data

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.projection.MediaProjectionManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.araujo.jordan.excuseme.ExcuseMe

import com.domain.BaseModels.ResponseChatFromToken
import com.domain.BaseModels.ResponseJWTTokenLogin
import com.example.twillioproject.R
import com.example.twillioproject.databinding.LayoutProgressBinding
import com.google.gson.Gson
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
 var error:String=""
fun emailValidator(
    context: Context,
    email: String,
    validateEmail: (isEmailOk: Boolean, mEmail: String, error: String?) -> Unit
) {
    var mEmail = email
    val EMAIL_ADDRESS_PATTERN: Pattern =
        Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

    var isEmailOk = false

    if (mEmail.isEmpty()) {
        validateEmail(false, "", context.getString(R.string.txt_required))
        error= context.getString(R.string.txt_enter_valid_email)
    }
    else {
        if (EMAIL_ADDRESS_PATTERN.matcher(mEmail).matches()) {
            isEmailOk = true
            isemailok = true
        }
        else {
            mEmail = "null"
            isemailok = false
            isEmailOk = false
            validateEmail(isEmailOk, mEmail, context.getString(R.string.txt_enter_valid_email))
            error= context.getString(R.string.txt_enter_valid_email)
        }
    }
    validateEmail(isEmailOk, mEmail, null)
}

fun validate(
    email: String,
    psswd: String,
    onSuccess: (email: String, psswd: String, isSuccess: Boolean,error:String?) -> Unit
) {

    if (ispsswdok == true && isemailok == true) {
        Log.d("textcheck", "onCreate: success $email $psswd")
        onSuccess(email, psswd, true,null)
    }
    else {
        Log.d("textcheck", "onCreate: failed $email $psswd")
        onSuccess(email, psswd, false,error)
    }
}


fun passwordValidator(
    context: Context,
    password: String,
    validatePassword: (isPasswordOk: Boolean, mPassword: String?, error: String?) -> Unit
) {
    val PASSWORD_PATTERN: Pattern =
        Pattern.compile("^" + "(?=.*[@#$%^&+=])" + "(?=\\S+$)" + ".{4,}" + "$");

    if (password.isEmpty()) {
        validatePassword(false, null, context.getString(R.string.txt_required))
        ispsswdok = false
    }
    else if (password.length <= 6) {
        validatePassword(
            false,
            null,
            context.getString(R.string.txt_password_must_be_greaterthan_6)
        )
        error=context.getString(R.string.txt_password_must_be_greaterthan_6)
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

 var progressbar: Dialog?=null
fun Context.showProgressDialog() {
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
    android.os.Handler(Looper.getMainLooper()).post(Runnable {
        progressbar?.let{
        it.dismiss()
    }
    })

}



fun Context.requestVideoPermissions(isGrant: (isGranted: Boolean) -> Unit) {
    ExcuseMe.couldYouGive(this).permissionFor(
        android.Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    ) {
        Log.d("permissioncheck", "requestVideoPermissions: $it ")
        if (it.granted.contains(android.Manifest.permission.CAMERA) && it.granted.contains(Manifest.permission.RECORD_AUDIO)) {
            isGrant(true)
        }
        else {
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
        if (it.granted.contains(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            isGrant(true)
        }
        else {
            isGrant(false)
        }
    }
}




fun Context.checkInternet(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connectivityManager != null) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            val networkInfo = connectivityManager.activeNetworkInfo
            if (networkInfo != null) {
                return networkInfo.isConnected
            }
        }
        else {
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
    }
    return false
}


fun Context.showToast(context: Context, txt: String) {
    Handler(Looper.getMainLooper()).post(kotlinx.coroutines.Runnable {
        Toast.makeText(context, txt, Toast.LENGTH_SHORT).show()
    })

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
fun decodeLoginToken(JWTEncoded: String, response: (ob: ResponseJWTTokenLogin) -> Unit) {
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


        response(Gson().fromJson(decodeBase64(tokenArray[1]), ResponseJWTTokenLogin::class.java))
        Log.d("JWT_DECODED", "Body: token in json form ${decodeBase64(tokenArray[1])}   ")
        Log.d("JWT_DECODED", "Body: token in json data ${jsonData}   ")

    } catch (e: UnsupportedEncodingException) {
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
    }
}



fun decodeBase64(str: String): String {
    val decoder: Base64.Decoder = Base64.getDecoder()
    val decoded = String(decoder.decode(str))
    println("Decoded Data: $decoded")
    return decoded
}


object InputUtils {
    fun hideKeyboard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
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

    var timeUtclenght=utcTime1.length
    var timeUtc=utcTime1.subSequence(10,timeUtclenght)
    val finalUtcTime=year.toString()+"-"+date.toString()+"-"+(day).toString()+timeUtc.toString()
    Log.d("utcformatted", "getCurrentUtcFormatedDate:  $finalUtcTime")


   /* var current = LocalDateTime.now()
    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    var utcTime = SimpleTimeZone.getTimeZone("UTC").toZoneId()
    val formatted = current.format(formatter)*/
    return finalUtcTime
}

fun getUtcDateToAMPM(date:String):String
{

    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"))
    var utcTime1: String = sdf.format(Date())

    val date =  sdf.parse(date)
    val sdf2 = SimpleDateFormat("hh:mm a")

    val time =sdf2.format(date)
//    val finaltime=hour.toString()+":"+minutes.toString()+" "+zone

//    Log.d("checkdate", "getUtcDateToAMPM: ${finaltime}")

    return time
}



fun Context.getCurrentUtcFormatedDateIntervalofMonth(): String {

    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"))
    var utcTime1: String = sdf.format(Date())
    sdf.format(Date())

    val date = sdf.calendar.get(Calendar.MONTH) + 1
    val day = sdf.calendar.get(Calendar.DAY_OF_MONTH)
    val year = sdf.calendar.get(Calendar.YEAR)

    var timeUtclenght=utcTime1.length
    var timeUtc=utcTime1.subSequence(10,timeUtclenght)
    val finalUtcTime=year.toString()+"-"+(date+1).toString()+"-"+(day).toString()+timeUtc.toString()
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
    val datefull=sdf.format(Date())

    val date = sdf.calendar.get(Calendar.MONTH) + 1
    val day = sdf.calendar.get(Calendar.DAY_OF_MONTH)
    val year = sdf.calendar.get(Calendar.YEAR)

    val datesubstring=datetime.subSequence(10,datefull.length)

    val finalUtcTime=year.toString()+"-"+(date).toString()+"-"+(day).toString()+datesubstring.toString()


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

fun EditText.getEditTextWithFlow() : Flow<String> {
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
    val datefull=sdf.format(Date())

    val date = sdf.calendar.get(Calendar.MONTH) + 1
    val day = sdf.calendar.get(Calendar.DAY_OF_MONTH)
    val year = sdf.calendar.get(Calendar.YEAR)

    val datesubstring=datetime.subSequence(10,datefull.length)

    val finalUtcTime=year.toString()+"-"+(date+1).toString()+"-"+(day).toString()+datesubstring.toString()


    return finalUtcTime
}

fun Context.getCurrentDayOfYear(): Int {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var datetime: String = sdf.format(Date())
    val datefull=sdf.format(Date())

    val date = sdf.calendar.get(Calendar.MONTH) + 1
    val day = sdf.calendar.get(Calendar.DAY_OF_MONTH)
    val year = sdf.calendar.get(Calendar.YEAR)
    val dayofYear=sdf.calendar.get(Calendar.DAY_OF_YEAR)

    val datesubstring=datetime.subSequence(10,datefull.length)

    val finalUtcTime=year.toString()+"-"+(date+1).toString()+"-"+(day).toString()+datesubstring.toString()


    return dayofYear
}

fun getDateWithMonthName(date:String,action:Int):String {
    var sdfOb: SimpleDateFormat
    if (date.contains("Z")) {
        sdfOb = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    }
    else {
        sdfOb = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    }

    val sdfFormated:SimpleDateFormat
    if (action == 1) {
        sdfFormated = SimpleDateFormat("dd-MMM")
    }
    else
    {
        sdfFormated = SimpleDateFormat("dd-MMM-yyyy")
    }

    val date =  sdfOb.parse(date)

   val time = sdfFormated.format(date)
  return time
}



const val MIN_DISTANCE = 150

/**
 * Enum class - Rest APIS Status*/
enum class Status {
    SUCCESS,
    FAILED,
    PROCESSING
}


