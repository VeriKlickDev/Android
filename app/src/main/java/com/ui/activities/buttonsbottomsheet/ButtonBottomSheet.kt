package com.ui.activities.buttonsbottomsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.veriklick.R

import com.veriklick.databinding.LayoutBottombuttonsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class ButtonBottomSheet(context: Context) :BottomSheetDialog(context,R.style.bottomsheet_buttons) {

    lateinit var binding:LayoutBottombuttonsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_bottombuttons,null,false)
        setContentView(binding.root)
    }

}