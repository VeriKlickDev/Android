package com.ui.activities.meetingmemberslist

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.data.checkInternet
import com.data.dataHolders.CurrentConnectUserList
import com.data.dataHolders.CurrentMeetingDataSaver
import com.domain.BaseModels.VideoTracksBean
import com.example.twillioproject.R
import com.example.twillioproject.databinding.ActivityListOfMembersBinding
import com.google.android.material.snackbar.Snackbar
import com.ui.activities.adduserlist.ActivityAddParticipant
import com.ui.listadapters.ConnectedMemberListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MemberListActivity : AppCompatActivity() {
    private val TAG = "checkadduserlist"
    private lateinit var binding: ActivityListOfMembersBinding
    private val usersList = mutableListOf<VideoTracksBean>()
    private lateinit var adapter: ConnectedMemberListAdapter
    private lateinit var viewModel: MemberListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListOfMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(MemberListViewModel::class.java)

        binding.rvAddUser.layoutManager = GridLayoutManager(this, 2)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        if (CurrentMeetingDataSaver.getData().isPresenter == true) {
            binding.btnAddUser.visibility = View.VISIBLE
        }
        else {
            binding.btnAddUser.visibility = View.INVISIBLE
        }




        Log.d(TAG, "onCreate: current hight $height  $width")

        CurrentConnectUserList.getListForAddParticipantActivity()
            .observe(this, Observer { listitem ->
                listitem?.let {
                    adapter = ConnectedMemberListAdapter(
                        this,
                        listitem,
                        hight = height,
                        width = width,
                        onClick = { pos, action, data ->
                            when (action) {
                                1 -> {
                                    if (checkInternet())
                                    {
                                        leftUserFromMeeting(data)
                                    }else
                                    {
                                        Snackbar.make(binding.root,getString(com.example.twillioproject.R.string.txt_no_internet_connection),
                                            Snackbar.LENGTH_SHORT).show()
                                    }
                                }

                            }
                        })
                    binding.rvAddUser.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            })

        binding.btnJumpBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnAddUser.setOnClickListener {
            handleAddParticipant()
        }

      /*  CurrentMeetingDataSaver.getIsRoomDisconnected().observe(this, Observer {
            if (it!=null)
            if (it==true){
                finish()
            }
        })
*/

    }

    private fun  leftUserFromMeeting(data:VideoTracksBean)
    {
        viewModel.leftUser(
            data.remoteParticipant?.sid!!,
            CurrentMeetingDataSaver.getRoomData()
                .roomName!!,
            onDataResponse = { data, status ->
                when (status) {
                    200 -> {
                        Log.d(
                            TAG,
                            "onCreate: data response success $data "
                        )
                    }
                    400 -> {
                        Log.d(
                            TAG,
                            "onCreate: data response success $data "
                        )
                    }
                    404 -> {
                        Log.d(TAG, "onCreate: data not found")
                    }
                }
            })
    }

    private fun handleAddParticipant() {
        val intent = Intent(this@MemberListActivity, ActivityAddParticipant::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        Log.d(TAG, "handleAddParticipant: onclicked")
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
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