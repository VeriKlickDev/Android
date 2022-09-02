package com.ui.activities.documentviewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.data.CurrentMeetingDataSaver
import com.data.dismissProgressDialog
import com.data.showProgressDialog
import com.data.showToast
import com.example.twillioproject.R
import com.example.twillioproject.databinding.ActivityDocumentViewerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DocumentViewerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDocumentViewerBinding
    private lateinit var viewModel: DocumentViewerViewModel
    private val TAG = "checkDocument"

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
        binding.swipetorefresh.setOnRefreshListener {
            getResume()
        }


    }

    fun loadResume(link: String, fileName: String, action: Int) {
        Log.d(TAG, "loadResume: url is $link")

        Handler(Looper.getMainLooper()).post(kotlinx.coroutines.Runnable {

/*            val wv = binding.wbDocumentViewer
            wv.settings.javaScriptEnabled = true
            //wv.settings..setPluginsEnabled(true)
            wv.settings.allowFileAccess = true
            //wv.loadUrl("https://docs.google.com/gview?url=https://www.mtsac.edu/webdesign/accessible-docs/word/example03.docx")
            wv.loadUrl(link)
*/


            binding.wbDocumentViewer.setWebViewClient(WebViewClientClass())
            binding.wbDocumentViewer.getSettings().setJavaScriptEnabled(true)
            binding.wbDocumentViewer.getSettings().setUseWideViewPort(true)
            binding.wbDocumentViewer.loadUrl(
                "http://docs.google.com/gview?embedded=true&url="
                        + link
            )


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
                    binding.swipetorefresh.isRefreshing = false
                }
                400 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "getResume: not success ondata 400")
                    showToast(this, getString(R.string.txt_something_went_wrong))
                    binding.swipetorefresh.isRefreshing = false
                }
                404 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "getResume: not success ondata 404")
                    showToast(this, getString(R.string.txt_something_went_wrong))
                    binding.swipetorefresh.isRefreshing = false
                }
            }
        }, onResumeResponse = { resumeData, fileName, action ->

            when (action) {
                200 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "getResume: success 200")
                    binding.swipetorefresh.isRefreshing = false
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
                    binding.swipetorefresh.isRefreshing = false
                }
                404 -> {
                    dismissProgressDialog()
                    Log.d(TAG, "getResume: not success 404")
                    showToast(this, resumeData?.errorMessage.toString())
                    binding.swipetorefresh.isRefreshing = false

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