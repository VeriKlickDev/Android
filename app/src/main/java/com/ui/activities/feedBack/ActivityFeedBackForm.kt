package com.ui.activities.feedBack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.twillioproject.R
import com.example.twillioproject.databinding.ActivityFeedBackFormBinding

class ActivityFeedBackForm : AppCompatActivity() {

    private lateinit var binding:ActivityFeedBackFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityFeedBackFormBinding.inflate(layoutInflater)
        setContentView(binding.root)






    }
}