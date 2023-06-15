package com.ui.activities.createCandiateForm

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.lifecycle.MutableLiveData
import com.data.dataHolders.CreateProfileDeepLinkHolder
import com.data.dismissProgressDialog
import com.data.showProgressDialog
import com.google.gson.Gson
import com.veriKlick.databinding.ActivityCreateCandidateFormBinding

class ActivityCreateCandidateWebForm : AppCompatActivity() {
    private lateinit var binding:ActivityCreateCandidateFormBinding
    val list= mutableListOf<String>()
    val requestListdata=MutableLiveData<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCreateCandidateFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(binding.wvCreateCandidate, true);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
        }

        loadUrl()

        requestListdata.observe(this){
            if (it!=null)
            {
                list.add(it)
            }
        }

        binding.tvHeader.setOnClickListener {
            printList()
        }

    }

    fun printList()
    {
        Log.d(TAG, "printList: ${Gson().to(list)}")
    }

    private val TAG="webviewPages"
    private fun loadUrl()
    {
        showProgressDialog()
        binding.wvCreateCandidate.webViewClient = WebViewClient()
        binding.wvCreateCandidate.webViewClient=webViewClient
        binding.wvCreateCandidate.loadUrl(CreateProfileDeepLinkHolder.get().toString())
        binding.wvCreateCandidate.settings.javaScriptEnabled = true
        binding.wvCreateCandidate.settings.domStorageEnabled = true
        binding.wvCreateCandidate.settings.setSupportZoom(true)
        binding.wvCreateCandidate.settings.allowContentAccess=true

    }



    private val webViewClient=object : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

        }

        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            requestListdata.postValue(request?.url.toString())
            return super.shouldInterceptRequest(view, request)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            dismissProgressDialog()
        }
    }

}