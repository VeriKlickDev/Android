package com.ui.activities.uploadResumeDocument

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.data.*
import com.data.dataHolders.CandidateImageAndAudioHolder
import com.data.dataHolders.CreateProfileDeepLinkHolder
import com.domain.BaseModels.CandidateDeepLinkDataModel
import com.domain.constant.AppConstants
import com.ui.activities.createCandidate.ActivityCreateCandidate
import com.veriKlick.R
import com.veriKlick.databinding.ActivityUploadResumeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


@AndroidEntryPoint
class ActivityResumeDocument : AppCompatActivity() {
    private lateinit var binding: ActivityUploadResumeBinding
    private var viewModel: VMUploadResume? = null
    private var recruiterId: String? = null
    private var tokenId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadResumeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //getAppLanguage()

        binding.etDocument.isEnabled = false
        Log.d(
            TAG,
            "onCreate: intent data from deeplink from holder ${CreateProfileDeepLinkHolder.get()}"
        )


        binding.btnJumpBack.setOnClickListener {
            onBackPressed.handleOnBackPressed()
        }

        requestWriteExternamlStoragePermissions {

        }

        viewModel = ViewModelProvider(this).get(VMUploadResume::class.java)

        binding.btnSelectFile.setOnClickListener {
            getResumeFile()
        }

        binding.btnUploadResume.setOnClickListener {
            if (checkInternet()) {
                uploadResumeFile()
            } else {
                showCustomSnackbarOnTop(getString(R.string.txt_no_internet_connection))
            }

        }

          binding.btnSkip.setOnClickListener {
              val intent=Intent(this, ActivityCreateCandidate::class.java)
              //val intent=Intent(this, ActivityCreateCandidate::class.java)
              //CreateProfileDeepLinkHolder.setCandidateId(candidateId)
              intent.putExtra(AppConstants.CANDIDATE_ID,candidateId)
              intent.putExtra(AppConstants.TOKEN_ID,tokenId)
              startActivity(intent)
              overridePendingTransition(
                  R.anim.slide_in_right,
                  R.anim.slide_out_left
              )
          }

        /*  binding.tvSetPreference.setOnClickListener {
              selectLangaugeDialog()
          }
  */

        binding.btnUploadResume.isEnabled = false

        binding.tvSetPreference.setOnClickListener {
            selectLangaugeDialogGlobal()
        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }

    private var candidateId = ""

    val TAG = "checkLoadRecruiterData"
    private fun loadRecruiterData() {
        try {
            var splitList = CreateProfileDeepLinkHolder.getPathCreateCandidateString()?.split("/")
            var id = splitList?.get(splitList.size - 3)
            var token = splitList?.get(splitList.size - 1)
            candidateId = id.toString()
            tokenId = token.toString()

            Log.d(TAG, "loadRecruiterData: url id $id  token $token")
            runOnUiThread { showProgressDialog() }
            viewModel?.getRecruiterDetails(
                id.toString(),
                token.toString()
            ) { data, isSuccess, errorCode, msg ->
                runOnUiThread { dismissProgressDialog() }
                if (errorCode == 200) {
                    runOnUiThread {


                        binding.btnSkip.isEnabled = true
                        binding.btnSkip.setTextColor(getColor(R.color.skyblue_light1))
                        CandidateImageAndAudioHolder.setDeepLinkData(
                            CandidateDeepLinkDataModel(
                                token,
                                data?.Subscriberid
                            )
                        )
                        recruiterId = data?.Subscriberid
                    }
                    Log.d(TAG, "loadRecruiterData: success data $data")
                } else {
                    runOnUiThread {
                        showCustomSnackbarOnTop(getString(R.string.txt_something_went_wrong))
                        binding.btnSkip.setTextColor(getColor(R.color.white))
                        binding.btnSkip.isEnabled = false
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "loadRecruiterData: exception 92 ${e.message}")
        }
    }

    val onBackPressed = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Log.d(TAG, "handleOnBackPressed: on back pressed")
            finish()
        }
    }


    fun getResumeFile() {

        var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE)
       // chooseFile.type = "*/*"

        val mimeTypes = arrayOf(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        )
        chooseFile.type = "*/*"
        chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)


//        chooseFile = Intent.createChooser(chooseFile, "Choose a file")
        contractorResumeFile.launch(chooseFile)

    }

    private var resumeFile:File?=null
    private val contractorResumeFile =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            binding.btnUploadResume.isEnabled=true
            try {
//                Log.d(TAG, "file uri: uri ${Gson().toJson(it.data)}")
                val uri = it?.data?.data
                Log.d(TAG, "file uri: uri $uri file cte ")
                val src = uri?.path
                if (uri != null) {
                    getFileNameFromUri(uri)
                }
//               val cte = uri?.let { it1 ->
//                   RealPathUtil.getRealPath(this@ActivityResumeDocument,
//                       it1
//                   )
//               }
                binding.etDocument.setText(uri?.let { it1 -> getFileNameFromUri(it1) })
                Log.d(TAG, "file uri: $src uri $uri file cte  ")
                resumeFile= uri?.let { it1 -> getFile(this@ActivityResumeDocument, it1) }

                Log.d(TAG, "file uri: $src uri $uri file cte  ")

            } catch (e: Exception) {
                Log.d(TAG, "error: exception  ${e.message}")
            }

        }


    @SuppressLint("Range")
    fun getFileNameFromUriPath(uri: Uri): String? {
        val fileName: String?
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        fileName = cursor?.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE+"=?")
            ?.let { cursor?.getString(it) }
        cursor?.close()
        return fileName
    }

    @Throws(IOException::class)
    fun getFile(context: Context, uri: Uri): File? {
        val destinationFilename: File = File(
            (context.getFilesDir().getPath() + File.separatorChar).toString() + queryName(
                context,
                uri
            )
        )
        try {
            context.getContentResolver().openInputStream(uri).use { ins ->
                if (ins != null) {
                    createFileFromStream(
                        ins,
                        destinationFilename
                    )
                }
            }
        } catch (ex: java.lang.Exception) {
            Log.e("Save File", ex.message!!)
            ex.printStackTrace()
        }
        return destinationFilename
    }

    fun createFileFromStream(ins: InputStream, destination: File?) {
        try {
            FileOutputStream(destination).use { os ->
                val buffer = ByteArray(4096)
                var length: Int
                while (ins.read(buffer).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
            }
        } catch (ex: java.lang.Exception) {
            Log.e("Save File", ex.message!!)
            ex.printStackTrace()
        }
    }

    private fun queryName(context: Context, uri: Uri): String {
        val returnCursor: Cursor = context.getContentResolver().query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }
    @SuppressLint("Range")
    fun getFileNameFromUri(uri: Uri): String? {
        val fileName: String?
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        fileName = cursor?.getString(cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)!!)
        cursor?.close()
        return fileName
    }


    private fun uploadResumeFile() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                if (resumeFile!=null) {

                    runOnUiThread { showProgressDialog() }
                    viewModel?.updateUserResumeWithoutAuth(resumeFile!!,CandidateImageAndAudioHolder.getDeepLinkData()?.subscriberId?.toString()!!) { isSuccess, code, msg ->
                        when (code) {
                            200 -> {
                                runOnUiThread { dismissProgressDialog() }
                                Log.d(TAG, "uploadProfilePhoto: $msg")
                                runOnUiThread { showCustomSnackbarOnTop(msg) }
                                binding.btnUploadResume.isEnabled = false

                            }

                            400 -> {
                                runOnUiThread { dismissProgressDialog() }
                                runOnUiThread { showCustomSnackbarOnTop(msg) }
                                Log.d(TAG, "uploadProfilePhoto: $msg $isSuccess $code")
                            }

                            401 -> {
                                runOnUiThread { dismissProgressDialog() }
                                runOnUiThread { showCustomSnackbarOnTop(msg) }
                                Log.d(TAG, "uploadProfilePhoto: $msg $isSuccess $code")
                            }

                            500 -> {
                                runOnUiThread { dismissProgressDialog() }
                                runOnUiThread { showCustomSnackbarOnTop(msg) }
                                Log.d(TAG, "uploadProfilePhoto: $msg $isSuccess $code")
                            }

                            501 -> {
                                runOnUiThread { dismissProgressDialog() }
                                runOnUiThread { showCustomSnackbarOnTop(msg) }
                                Log.d(TAG, "uploadProfilePhoto: $msg $isSuccess $code")
                            }

                            404 -> {
                                runOnUiThread { dismissProgressDialog() }
                                Log.d(TAG, "uploadProfilePhoto: $msg $isSuccess $code")
                            }

                            503 -> {
                                runOnUiThread { dismissProgressDialog() }
                                runOnUiThread { showCustomSnackbarOnTop(msg) }
                                Log.d(TAG, "uploadProfilePhoto: $msg $isSuccess $code")
                            }

                            502 -> {
                                runOnUiThread { dismissProgressDialog() }
                                // runOnUiThread { showCustomSnackbarOnTop(msg) }
                                Log.d(TAG, "uploadProfilePhoto: $msg $isSuccess $code")
                            }

                        }
                    }
                }else
                {
                    runOnUiThread { showCustomSnackbarOnTop(getString(R.string.txt_please_select_resume_file_first)) }
                }
            } catch (e: Exception) {
                runOnUiThread { dismissProgressDialog() }
                Log.d(TAG, "uploadProfilePhoto: ")
            }
        }

    }

}

