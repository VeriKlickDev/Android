package com.ui.activities.documentviewer


import android.R
import android.animation.ValueAnimator
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.data.dataHolders.CurrentMeetingDataSaver
import com.data.dismissProgressDialog
import com.data.requestWriteExternamlStoragePermissions
import com.data.showProgressDialog
import com.data.showToast
import com.example.twillioproject.databinding.ActivityDocumentViewerBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class DocumentViewerActivity : AppCompatActivity() {
    private var fileName=""
    private lateinit var binding: ActivityDocumentViewerBinding
    private lateinit var viewModel: DocumentViewerViewModel
    private val TAG = "checkDocument"
    val docUrl="https://docs.google.com/gview?url=https%3a%2f%2fveriklick-dev-app-resumes.s3.us-east-2.amazonaws.com%2fAlfonsoRiggResume_2018v4-converted_1629636205937.docx%3fX-Amz-Expires%3d10800%26X-Amz-Algorithm%3dAWS4-HMAC-SHA256%26X-Amz-Credential%3dAKIAUPUNSA4TYD2ZVOSX%2f20220903%2fus-east-2%2fs3%2faws4_request%26X-Amz-Date%3d20220903T131606Z%26X-Amz-SignedHeaders%3dhost%26X-Amz-Signature%3d60ebca908546dc65ea1efe8c6ab608b9b4c81aafd3f2fbf979e9c202fc582f08"
    val docUrl2="https://docs.google.com/gview?url=https%3A%2F%2Fveriklick-dev-app-resumes.s3.us-east-2.amazonaws.com%2FChaitanya%2520Vooradi_1649799349498_1661856080358.docx%3FX-Amz-Expires%3D10800%26X-Amz-Algorithm%3DAWS4-HMAC-SHA256%26X-Amz-Credential%3DAKIAUPUNSA4TYD2ZVOSX%2F20220903%2Fus-east-2%2Fs3%2Faws4_request%26X-Amz-Date%3D20220903T111904Z%26X-Amz-SignedHeaders%3Dhost%26X-Amz-Signature%3D81b08889cd93198534cfa8986c66331e9122f2729e546828ec267c76dbd01013&embedded=true"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(DocumentViewerViewModel::class.java)


        val candidateId = CurrentMeetingDataSaver.getData().interviewModel?.candidateId
        Log.d("documentcheck", "onCreate: candi Id $candidateId")

        binding.btnJumpBack.setOnClickListener {
            onBackPressed()
        }

        getResume()
        /* binding.swipetorefresh.setOnRefreshListener {
             getResume()
         }*/



        /* CurrentMeetingDataSaver.getIsRoomDisconnected().observe(this, Observer {
             if (it!=null)
                 if (it==true){
                     finish()
                 }
         })
 */

        binding.btnLayout.setOnClickListener {
            showDocFile(fileName)
        }

    }

    fun giveStoragePermissionAndDownloadResume(link:String,fileName: String)
    {

        requestWriteExternamlStoragePermissions {
            Log.d(TAG, "giveStoragePermissionAndDownloadResume: permission $it")
            if (it)
            {
                Log.d(TAG, "giveStoragePermissionAndDownloadResume: permission granted")
                downloadFile(fileName,"VeriKlickFiles",link)

            }
            else{
                Log.d(TAG, "giveStoragePermissionAndDownloadResume: file notwritten")
                val dialog=AlertDialog.Builder(this)
                dialog.setMessage("Grant the permission to download Resume")
                dialog.setPositiveButton("GiveAccess",object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        giveStoragePermissionAndDownloadResume(link,fileName)
                    }
                })
                dialog.setNegativeButton("cancel",object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {

                    }
                })
                dialog.create()
                dialog.show()
            }
        }
    }

    fun showDocFile(fileName: String)
    {
        val file =File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/"+fileName).toString())
        val uri=FileProvider.getUriForFile(this,"com.example.twillioproject"+".provider",file)
        val i = Intent(Intent.ACTION_VIEW)
        this.fileName=fileName
        if (fileName.contains(".pdf"))
        {
            i.setDataAndType(uri, "application/pdf")
        }
        if (fileName.contains(".docx") || fileName.contains(".doc") )
        {
            i.setDataAndType(uri, "application/msword")
        }

        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_GRANT_READ_URI_PERMISSION
          startActivity(i)
    }



    private fun downloadFile(fileName : String, desc :String, url : String){

        // fileName -> fileName with extension
        val request = DownloadManager.Request(Uri.parse(url))
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setTitle(fileName)
            .setDescription(desc)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName)
        val downloadManager= getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadID = downloadManager.enqueue(request)
        Log.d(TAG, "downloadFile: download id $downloadID")
        showDocFile(fileName)
    }


    fun loadResume(link: String, fileName: String, action: Int) {
        Log.d(TAG, "loadResume: url is $link")
        Log.d(TAG, "loadResume: url method")
        Log.d(TAG, "loadResume: url method FILE NAME $fileName")

        Handler(Looper.getMainLooper()).post(kotlinx.coroutines.Runnable {
            giveStoragePermissionAndDownloadResume(link,fileName)


/*            val wv = binding.wbDocumentViewer
            wv.settings.javaScriptEnabled = true
            //wv.settings..setPluginsEnabled(true)
            wv.settings.allowFileAccess = true
            //wv.loadUrl("https://docs.google.com/gview?url=https://www.mtsac.edu/webdesign/accessible-docs/word/example03.docx")
            wv.loadUrl(link)
*/

            /*     val MyURL = "this is your PDF URL"
                 val url = "http://docs.google.com/gview?embedded=true&url=$link"
                 Log.i(TAG, "Opening PDF: $url")
                 binding.wbDocumentViewer.getSettings().setJavaScriptEnabled(true)
                 binding.wbDocumentViewer.loadUrl(docUrl)
     */

/*
            binding.wbDocumentViewer.setWebViewClient(WebViewClientClass())
            binding.wbDocumentViewer.getSettings().setJavaScriptEnabled(true)
            binding.wbDocumentViewer.getSettings().setUseWideViewPort(true)
            binding.wbDocumentViewer.loadUrl(docUrl2)*/
            /*  binding.wbDocumentViewer.loadUrl(
                  "http://docs.google.com/gview?embedded=true&url="
                          + link
              )
  */

            //            wv.loadData("<iframe>https://docs.google.com/gview?url=$link></iframe>","text/html","UTF-8")
            //wv.loadData( doc, "text/html",  "UTF-8");

        })
        /*         when (action) {
                     1 -> {
                         //doc

                     }
                     2 -> {
                         //docx

                     }
                     3 -> {
                         //pdf
                         // Read a pdf file from Uri
                         val docString : String = DocumentReaderUtil.readPdfFromUri(Uri.parse(link), applicationContext)

                     }
                 }


                startActivity(
                     // Use 'launchPdfFromPath' if you want to use assets file (enable "fromAssets" flag) / internal directory
                     PdfViewerActivity.launchPdfFromUrl(           //PdfViewerActivity.Companion.launchPdfFromUrl(..   :: incase of JAVA
                         this,
                         link,                                // PDF URL in String format
                         fileName,                        // PDF Name/Title in String format
                         "",                  // If nothing specific, Put "" it will save to Downloads
                         enableDownload = false                    // This param is true by defualt.
                     )
                 )*/
    }

    fun getResume() {
        viewModel.getDocument(onDataResponse = { data, action ->
            showProgressDialog()
            when (action) {
                200 -> {
                    Log.d(TAG, "getResume: success ondata response")
                    //   binding.swipetorefresh.isRefreshing = false
                }
                400 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "getResume: not success ondata 400")
                    showToast(this, getString(com.example.twillioproject.R.string.txt_something_went_wrong))
                    // binding.swipetorefresh.isRefreshing = false
                }
                404 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "getResume: not success ondata 404")
                    showToast(this, getString(com.example.twillioproject.R.string.txt_something_went_wrong))
                    // binding.swipetorefresh.isRefreshing = false
                }
            }
        }, onResumeResponse = { resumeData, fileName, action ->

            when (action) {
                200 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "getResume: success 200")
                    //  binding.swipetorefresh.isRefreshing = false
                    if (fileName.contains(".doc")) {
                        loadResume(resumeData?.data?.fileName.toString(), fileName, 1)
                    }
                    if (fileName.contains(".docx")) {
                        loadResume(resumeData?.data?.fileName.toString(), fileName, 2)
                    }
                    if (fileName.contains(".pfd")) {
                        loadResume(resumeData?.data?.fileName.toString(), fileName, 3)
                    }

                }
                400 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "getResume: not success 400")
                    showToast(this, resumeData?.errorMessage.toString())
                    //   binding.swipetorefresh.isRefreshing = false
                }
                404 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "getResume: not success 404")
                    showToast(this, resumeData?.errorMessage.toString())
                    //  binding.swipetorefresh.isRefreshing = false

                }
            }

        })

    }


    companion object {
        fun start(context: Context) {
            val intent = Intent(context, DocumentViewerActivity::class.java)
            context.startActivity(intent)
        }
    }

    inner class WebViewClientClass : WebViewClient()
    {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            Log.d(TAG, "shouldOverrideUrlLoading: ")
            view?.loadUrl(url!!)
            return super.shouldOverrideUrlLoading(view, url)
        }

        override fun onPageFinished(view: WebView?, url: String?) {

            super.onPageFinished(view, url)
        }


    }

}