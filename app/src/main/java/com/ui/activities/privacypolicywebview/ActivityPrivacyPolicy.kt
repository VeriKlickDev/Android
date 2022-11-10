package com.harvee.yourhealthmate2.ui.privacypolicy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import com.domain.constant.AppConstants
import com.example.twillioproject.R
import com.example.twillioproject.databinding.ActivityPrivacyPolicyBinding

class ActivityPrivacyPolicy : AppCompatActivity() {

    private lateinit var binding:ActivityPrivacyPolicyBinding

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
        binding.wbPrivacyPolicy.webViewClient = WebViewClient()
        binding.wbPrivacyPolicy.loadUrl(intent.getStringExtra(AppConstants.PRIVACY_LINK).toString())
        binding.wbPrivacyPolicy.settings.javaScriptEnabled = true
        binding.wbPrivacyPolicy.settings.setSupportZoom(true)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}