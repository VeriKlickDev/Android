package com.ui.activities.adduserlist

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.data.*
import com.domain.BaseModels.VideoTracksBean
import com.example.twillioproject.databinding.ActivityListOfMembersBinding
import com.ui.listadapters.AddUserListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

@AndroidEntryPoint
class MemberListActivity :AppCompatActivity() {
    private val TAG="checkadduserlist"
    private lateinit var binding:ActivityListOfMembersBinding
    private val usersList= mutableListOf<VideoTracksBean>()
    private lateinit var adapter:AddUserListAdapter
    private lateinit var viewModel:AddUserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityListOfMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel=ViewModelProvider(this).get(AddUserViewModel::class.java)

        binding.rvAddUser.layoutManager=GridLayoutManager(this,2)


        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        Log.d(TAG, "onCreate: current hight $height  $width" )

        CurrentConnectUserList.getListForAddParticipantActivity().observe(this, Observer {listitem->
            listitem?.let {
                adapter= AddUserListAdapter(this,listitem, hight = height, width = width, onClick = { pos, action, data ->

                })
                binding.rvAddUser.adapter=adapter
                adapter.notifyDataSetChanged()
            }
        })
        binding.btnJumpBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnAddUser.setOnClickListener {
            handleAddParticipant()
        }
    }

    private fun handleAddParticipant() {
        val intent= Intent(this@MemberListActivity,ActivityAddParticipant::class.java)
        startActivity(intent)
        Log.d(TAG, "handleAddParticipant: onclicked")
    }


    private fun getEditTextWithFlow(editText: EditText) : Flow<String> {
        return callbackFlow<String> {
            editText.addTextChangedListener {
                trySend(it.toString())
            }
            awaitClose { cancel() }
        }
    }


/*
    @OptIn(FlowPreview::class)
    private fun showAddUserDialog() {
        val dialog = Dialog(this)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogView = LayoutDialogAddParticipantBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogView.root)

        lifecycleScope.launch {
            getEditTextWithFlow(dialogView.etEmail)
                .debounce(1000)
                .collect { text ->
                    Log.d(TAG, "showAddUserDialog: ${text}")

                    emailValidator(this@MemberListActivity,text, validateEmail = { isEmailOk, mEmail, error ->
                        if (isEmailOk)
                        {
                            checkEmail(text)
                            dialogView.tvEmailError.isVisible=false
                        }else
                        {
                            this@MemberListActivity.isEmailOk=false
                            dialogView.tvEmailError.isVisible=true
                            dialogView.tvEmailError.setText(error.toString())
                        }
                    })



                }
        }

        lifecycleScope.launch {
            getEditTextWithFlow(dialogView.etPhoneNumber)
                .debounce(1000)
                .collect { text ->
                    Log.d(TAG, "showAddUserDialog: ${text}")
                    checkPhone(text)
                    if(text.toString().length>9)
                    {
                        checkPhone(text)
                        dialogView.tvPhoneError.isVisible=false
                    }else
                    {
                        isPhoneOk=false
                        dialogView.tvPhoneError.isVisible=true
                        dialogView.tvPhoneError.text="Please Enter Valid No."
                    }
                }
        }

        dialogView.btnSend.setOnClickListener {

            if (isEmailOk == true && isPhoneOk == true) {
                showToast(this, "please Recheck the above fields")
            }
            else
                Log.d(TAG, "showAddUserDialog: sending mail... ")
        }
        dialog.create()
        dialog.show()
        }

    private var isEmailOk=false
    private var isPhoneOk=false
    val interviewId=CurrentMeetingDataSaver.getData().interviewModel?.interviewId
    fun checkEmail(email:String)
    {
    viewModel.getIsEmailAndPhoneExists(interviewId!!,email,"",{
       if(it!=null){
        if (it)
        {
            isEmailOk=true
        }else
        {
            isPhoneOk=false
        }
       }
    })
    }

    fun checkPhone(phone:String)
    {
        viewModel.getIsEmailAndPhoneExists(interviewId!!,"",phone,{
           if (it!=null){
            if (it)
            {
                isPhoneOk=true
            }else
            {
                isPhoneOk=false
            }}
        })
    }


*/

}