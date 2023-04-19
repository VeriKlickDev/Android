package com.ui.activities.createCandidate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.data.exceptionHandler
import com.data.setHandler
import com.veriKlick.R
import com.veriKlick.databinding.ActivityCreateCandidateBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ActivityCreateCandidate : AppCompatActivity() {
    private var binding:ActivityCreateCandidateBinding?=null
    private var viewModel:VMCreateCandidate?=null
    var fragementNo=0
    val TAG="acitivityCreateCan"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCreateCandidateBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        viewModel=ViewModelProvider(this).get(VMCreateCandidate::class.java)




    }




}