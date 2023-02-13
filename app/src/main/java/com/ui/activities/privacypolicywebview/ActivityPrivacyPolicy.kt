package com.harvee.yourhealthmate2.ui.privacypolicy

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.data.dismissProgressDialog
import com.data.showProgressDialog
import com.domain.constant.AppConstants
import com.veriKlick.*
import com.veriKlick.databinding.ActivityPrivacyPolicyBinding

class ActivityPrivacyPolicy : AppCompatActivity() {

    private lateinit var binding: ActivityPrivacyPolicyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when(intent.getIntExtra(AppConstants.PRIVACY_ACTION,0))
        {
            1->{
                binding.tvHeader.text=getString(R.string.txt_term_and_condition)
            }
            2->{
                binding.tvHeader.text=getString(R.string.txt_privacy_policy)
            }
        }

        privacyPolicy()

        binding.btnJumpBackAddmember.setOnClickListener {
            onBackPressed()
        }

    }

    private fun privacyPolicy()
    {
        showProgressDialog()
        binding.wbPrivacyPolicy.webViewClient = WebViewClient()
        binding.wbPrivacyPolicy.webViewClient=webViewClient
        binding.wbPrivacyPolicy.loadUrl(intent.getStringExtra(AppConstants.PRIVACY_LINK).toString())
        binding.wbPrivacyPolicy.settings.javaScriptEnabled = true
        binding.wbPrivacyPolicy.settings.setSupportZoom(true)
    }

    private val webViewClient=object : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            dismissProgressDialog()
        }
    }

    override fun onDestroy() {
        dismissProgressDialog()
        super.onDestroy()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}