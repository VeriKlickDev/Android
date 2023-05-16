package com.ui.activities.upcomingMeeting

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.data.*
import com.data.dataHolders.DataStoreHelper
import com.domain.constant.AppConstants
import com.google.android.material.navigation.NavigationView
import com.ui.activities.candidateQuestionnaire.ActivityCandidateQuestinnaire
import com.ui.activities.createCandidate.ActivityCreateCandidate
import com.ui.activities.login.LoginActivity
import com.ui.activities.upcomingMeeting.CandidateList.CandidateListFragment
import com.ui.activities.upcomingMeeting.UpComingFragment.UpcomingListFragment
import com.ui.activities.uploadProfilePhoto.ActivityUploadProfilePhoto
import com.veriKlick.*
import com.veriKlick.databinding.ActivityUpcomingMeetingBinding
import com.veriKlick.databinding.ActivityUploadProfilePhotoBinding
import com.veriKlick.databinding.DrawerUserIconLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.concurrent.thread


@AndroidEntryPoint
class UpcomingMeetingActivity : AppCompatActivity() {

    private val TAG = "upcomingMeeting"

    private lateinit var binding: ActivityUpcomingMeetingBinding
    private lateinit var viewModel: UpComingMeetingViewModel
    private var upcomingFragment:UpcomingListFragment?=null
    private var fragManager:FragmentManager?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUpcomingMeetingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(UpComingMeetingViewModel::class.java)
        fragManager=supportFragmentManager


        thread {
            Thread.sleep(500)
            requestNotficationPermission {
                Log.d(TAG, "onCreate: request Notification permission $it")
                requestNearByPermissions {

                }
            }


        }

        fragManager?.let {
            Log.d(TAG, "onCreate: not null fragmanager")
            it.beginTransaction().add(R.id.fragment_container,UpcomingListFragment.getInstance(),AppConstants.UPCOMING_LIST_FRAGMENT).commit()
            it.beginTransaction().add(R.id.fragment_container,CandidateListFragment.getInstance(viewModel),AppConstants.CANDIDATE_LIST_FRAGMENT).commit()

            setHandler().post{
                Log.d(TAG, "onCreate: sizze ${it.fragments.size}")
                viewModel.createInstanceOfFragments(it)
                upcomingFragment=viewModel.getFragmentsList().get(0) as UpcomingListFragment
                hideAllFragment()
                Log.d(TAG, "onCreate: ids ${fragManager?.fragments?.get(0)?.id}  ${fragManager?.fragments?.get(1)?.id}")
                attatchFragment(0)
            }
        }
       // upcomingFragment=viewModel.getFragmentsList().get(0) as UpcomingListFragment

        //UpcomingListFragment.getInstance()
        setDrawabletoMenuItem()


        binding.btnLogout.setOnClickListener {
            logout()
        }
        setDrawerListener()

    }

    override fun onResume() {
        dismissProgressDialog()
        super.onResume()
    }

    fun setDrawabletoMenuItem(){
        binding.tvAppVersion.setText(getString(R.string.txt_app_version)+ " 1.1")
        binding.navView.menu.getItem(0)?.setActionView(R.layout.drawer_user_icon_layout)
        binding.navView.menu.getItem(1)?.setActionView(R.layout.drawer_user_icon_layout)
        binding.navView.menu.getItem(2)?.setActionView(R.layout.drawer_user_icon_layout)
        binding.navView.menu.getItem(3)?.setActionView(R.layout.drawer_user_icon_layout)
        binding.navView.menu.getItem(4)?.setActionView(R.layout.drawer_user_icon_layout)
        binding.navView.menu.getItem(5)?.setActionView(R.layout.drawer_user_icon_layout)
        binding.navView.menu.getItem(6)?.setActionView(R.layout.drawer_user_icon_layout)
    }


    fun closeDrawer()
    {
        binding.drawerLayout.closeDrawer(Gravity.LEFT)

    }

    fun setDrawerListener()
    {
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navdrawerCandidateId -> {
                    Log.d(TAG, "onNavigationItemSelected: candidate list clicked")
                    //CandidateListFragment.newInstance()
                    hideAllFragment()
                    attatchFragment(1)
                    closeDrawer()
                }
                R.id.navdrawerall_meetings -> {
                    //  replaceFragment(viewModel.getFragmentsList().get(0))
                    hideAllFragment()
                    attatchFragment(0)
                    upcomingFragment?.getMeetingList(0)
                    closeDrawer()
                }
                R.id.navdrawerscheduled_meetings -> {
                    //  replaceFragment(viewModel.getFragmentsList().get(0))
                    hideAllFragment()
                    attatchFragment(0)
                    upcomingFragment?.getMeetingList(2)
                    closeDrawer()
                }
                R.id.navdrawerattended_meetings -> {
                    // replaceFragment(viewModel.getFragmentsList().get(0))
                    hideAllFragment()
                    attatchFragment(0)
                    upcomingFragment?.getMeetingList(1)
                    closeDrawer()
                }
                R.id.navdrawermissed -> {
                    // replaceFragment(viewModel.getFragmentsList().get(0))
                    hideAllFragment()
                    attatchFragment(0)
                    upcomingFragment?.getMeetingList(3)
                    closeDrawer()
                }
                R.id.navdrawercancel_meetings -> {
                    //  replaceFragment(viewModel.getFragmentsList().get(0))
                    hideAllFragment()
                    attatchFragment(0)
                    upcomingFragment?.getMeetingList(4)
                    closeDrawer()
                }
                 R.id.navdrawercreateCandidate -> {
                     closeDrawer()
                     setHandler().postDelayed({
                         startActivity(Intent(this,ActivityCreateCandidate::class.java))
                     },200)
                 }
                /*R.id.navdrawerQuestonnaireScreen -> {
                    closeDrawer()
                    startActivity(Intent(this,ActivityCandidateQuestinnaire::class.java))
                }*/
            }
            true
        }

    }


    fun openDrawer()
    {
        Log.d(TAG, "openDrawer: open")
        binding.drawerLayout.openDrawer(Gravity.LEFT)

    }

    private fun logout()
    {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage(getString(R.string.txt_do_you_want_to_logout))
        dialog.setPositiveButton(getString(R.string.txt_ok), DialogInterface.OnClickListener { dialogInterface, i ->
            DataStoreHelper.clearData()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        })
        dialog.setNegativeButton(
            getString(R.string.txt_cancel),
            DialogInterface.OnClickListener { dialogInterface, i ->

            })
        dialog.show()
        dialog.create()
    }

    private fun setupDrawer() {
        binding.drawerLayout
    }

    override fun onDestroy() {
        dismissProgressDialog()
        super.onDestroy()
    }

    var isBackPressed=0

    override fun onBackPressed() {

        Log.d(TAG, "onBackPressed: ${viewModel.getFragmentsList().get(0).tag} ${binding.fragmentContainer.tag} ")
       // Log.d(TAG, "onBackPressed: navid ${binding.navViewDrawer.findFragment<CandidateListFragment>().id}  viewmodelId ${viewModel.getFragmentsList().get(0).id}")
            Log.d(TAG, "onBackPressed: if part")
            showCustomSnackbarOnTop(getString(R.string.txt_press_back_again_to_exit))
            isBackPressed=isBackPressed+1
            setHandler().postDelayed({
            isBackPressed=0
            },2000)

        if (isBackPressed==2)
            super.onBackPressed()


       // super.onBackPressed()
    }

    fun attatchFragment(fragNo:Int)
    {

        try {
            if (!viewModel.getFragmentsList().isNullOrEmpty())
            {

               val fragTrans= fragManager?.beginTransaction()
                //fragTrans?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                   fragTrans?.show(viewModel.getFragmentsList().get(fragNo))?.commit()
                Log.d(TAG, "replaceFragment: no null detached")
            }else
            {
                Log.d(TAG, "replaceFragment: not detached")
            }

        }catch (e:Exception)
        {
            Log.d(TAG, "replaceFragment: exception ${e.message}")
        }

        Log.d(TAG, "replaceFragment: frag replace ${fragManager?.fragments?.size} ")
    }

    fun replaceFragment(fragNo:Int)
    {
        try {
            if (!viewModel.getFragmentsList().isNullOrEmpty())
            {
                fragManager?.beginTransaction()?.attach(viewModel.getFragmentsList().get(fragNo))

                Log.d(TAG, "replaceFragment: no null detached")
            }else
            {
                Log.d(TAG, "replaceFragment: not detached")
            }

        }catch (e:Exception)
        {
            Log.d(TAG, "replaceFragment: exception ${e.message}")
        }

        Log.d(TAG, "replaceFragment: frag replace ${fragManager?.fragments?.size} ")
    }



    fun hideAllFragment()
    {
        try {
            if (!viewModel.getFragmentsList().isNullOrEmpty())
            {
                viewModel.getFragmentsList().forEach {fr->
                    fragManager?.beginTransaction()?.hide(fr)?.commit()
                }
            }else
            {
                Log.d(TAG, "replaceFragment: not detached")
            }

        }catch (e:Exception)
        {
            Log.d(TAG, "replaceFragment: exception ${e.message}")
        }
    }

}

/*

fromdate = getCurrentDate().toString(),
            todate = getIntervalMonthDate().toString(),
            from = getCurrentUtcFormatedDate(),
            to = getCurrentUtcFormatedDateIntervalofMonth(),



 */