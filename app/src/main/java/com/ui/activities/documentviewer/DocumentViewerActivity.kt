package com.ui.activities.documentviewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.twillioproject.databinding.ActivityDocumentViewerBinding


class DocumentViewerActivity : AppCompatActivity() {
    lateinit var binding:ActivityDocumentViewerBinding
    val  doc:String="<iframe src='http://docs.google.com/viewer?url=http://www.iasted.org/conferences/formatting/presentations-tips.ppt&embedded=true'width='100%' height='100%'style='border: none;'></iframe>";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDocumentViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val wv = binding.wbDocumentViewer
        wv.settings.javaScriptEnabled = true
        //wv.settings..setPluginsEnabled(true)
        wv.settings.allowFileAccess = true
        wv.loadUrl(doc)
        //wv.loadData( doc, "text/html",  "UTF-8");

    }

    companion object{
        fun start(context: Context)
        {
            val intent= Intent(context,DocumentViewerActivity::class.java)
            context.startActivity(intent)
        }

    }
}