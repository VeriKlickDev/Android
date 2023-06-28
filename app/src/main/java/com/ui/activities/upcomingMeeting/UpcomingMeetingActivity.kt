package com.ui.activities.upcomingMeeting

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.data.*
import com.data.dataHolders.DataStoreHelper
import com.domain.BaseModels.ModelLanguageSelect
import com.ui.activities.createCandidate.FragmentCreateCandidate
import com.ui.activities.login.LoginActivity
import com.ui.activities.upcomingMeeting.CandidateList.CandidateListFragment
import com.ui.activities.upcomingMeeting.UpComingFragment.UpcomingListFragment
import com.veriKlick.*
import com.veriKlick.databinding.ActivityUpcomingMeetingBinding
import com.veriKlick.databinding.LayoutChooseLanguageDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale


@AndroidEntryPoint
class UpcomingMeetingActivity : AppCompatActivity() {

    private val TAG = "upcomingMeeting"

    private lateinit var binding: ActivityUpcomingMeetingBinding
    private lateinit var viewModel: UpComingMeetingViewModel
    private var upcomingFragment: UpcomingListFragment? = null
    private var fragManager: FragmentManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUpcomingMeetingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(UpComingMeetingViewModel::class.java)
        fragManager = supportFragmentManager
        //setInstance()


        try {

            /*if (UpcomingMeetingStatusHolder.getFragment()!=null){
                upcomingFragment=UpcomingMeetingStatusHolder.getFragment() as UpcomingListFragment
            }else
            {
                UpcomingMeetingStatusHolder.setFragement(UpcomingListFragment(getString(R.string.txt_scheduled)))
                upcomingFragment=UpcomingListFragment(getString(R.string.txt_scheduled))
            }*/
            supportFragmentManager.beginTransaction()?.add(
                R.id.fragment_container,
                UpcomingListFragment(getString(R.string.txt_scheduled_meetings))
            )?.commit()
        } catch (e: Exception) {
            Log.d(TAG, "onCreate: exception in 67 ${e.message}")
        }

        /* if (viewModel.getFragManager()!=null)
         {
             fragManager=viewModel.getFragManager()
             upcomingFragment=viewModel.getFragmentsList().get(0) as UpcomingListFragment
             hideAllFragment()
             Log.d(TAG, "onCreate: ids ${fragManager?.fragments?.get(0)?.id}  ${fragManager?.fragments?.get(1)?.id}")
             attatchFragment(0)
         }else
         {
             fragManager?.let {
                 Log.d(TAG, "onCreate: not null fragmanager")
                 it.beginTransaction().add(R.id.fragment_container,UpcomingListFragment.getInstance(),AppConstants.UPCOMING_LIST_FRAGMENT).commit()
                 it.beginTransaction().add(R.id.fragment_container,CandidateListFragment.getInstance(),AppConstants.CANDIDATE_LIST_FRAGMENT).commit()

                 setHandler().post{
                     Log.d(TAG, "onCreate: sizze ${it.fragments.size}")
                     viewModel.createInstanceOfFragments(it)
                     upcomingFragment=viewModel.getFragmentsList().get(0) as UpcomingListFragment
                     hideAllFragment()
                     Log.d(TAG, "onCreate: ids ${fragManager?.fragments?.get(0)?.id}  ${fragManager?.fragments?.get(1)?.id}")
                     attatchFragment(0)
                 }
             }
         }
 */


        // upcomingFragment=viewModel.getFragmentsList().get(0) as UpcomingListFragment

        //UpcomingListFragment.getInstance()
        setDrawabletoMenuItem()


        binding.btnLogout.setOnClickListener {
            logout()
        }

        hideDrawerViews()
        setDrawerListener()
        //checkAllPermisions()
        // checkPermissionNotificaton()
    }

    private fun checkDeepLinkIsOpenFirst() {
        CoroutineScope(Dispatchers.IO).launch {

            try {
                if (DataStoreHelper.getDeeplinkIsOpenStatus() != null && !DataStoreHelper.getDeeplinkIsOpenStatus()) {
                    runOnUiThread { getDeepLinkPermission { } }

                } else {
                    runOnUiThread { }

                }
            } catch (e: Exception) {
                runOnUiThread { getDeepLinkPermission { } }

            }
        }

    }

    private fun checkAllPermisions() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            Log.d(TAG, "checkAllPermisions: up q")
            requestCameraAndMicPermissionsTiramishu {
                requestWriteExternamlStoragePermissions {
                    requestNearByPermissions {
                       // checkDeepLinkIsOpenFirst()
                    }
                }
            }
        } else {
            requestCameraAndMicPermissions {
                requestWriteExternamlStoragePermissions {
                    requestNearByPermissions {
                       // checkDeepLinkIsOpenFirst()
                    }
                }
            }
            Log.d(TAG, "checkAllPermisions: below tira")
        }

    }


    /*
private fun checkPermissionNotificaton()
{

        if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)==PackageManager.PERMISSION_GRANTED)
        {
            Log.d(TAG, "onCreate: notifi granted")
        }else
        {
            Log.d(TAG, "onCreate: notifi not granted")
        }

        requestNotificationPermission {
            Log.d(TAG, "onCreate: request Notification permission $it")
            //requestNearByPermissions {

            // }
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    if (DataStoreHelper.getDeeplinkIsOpenStatus() != null) {
                        if (!DataStoreHelper.getDeeplinkIsOpenStatus()) {
                            runOnUiThread { getDeepLinkPermission {} }
                        } else {

                        }
                    } else {
                        runOnUiThread { getDeepLinkPermission {} }
                    }
                } catch (e: Exception) {
                    runOnUiThread { getDeepLinkPermission {} }

                }
            }
        }


}
     */

    private fun getDeepLinkPermission(onFinish: () -> Unit) {
        Log.d(TAG, "getDeepLinkPermission: on deeplink dialog")
        val dialog = android.app.AlertDialog.Builder(this)
        dialog.setTitle(getString(R.string.txt_please_enable_the_deeplink))
        dialog.setPositiveButton(getString(R.string.txt_ok),
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    CoroutineScope(Dispatchers.IO).launch {
                        DataStoreHelper.setDeepLinkData(true)
                    }
                    val intent = Intent(
                        Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                        Uri.parse("package:${packageName}")
                    )
                    startActivity(intent)
                }
            })
        dialog.setOnDismissListener {
            onFinish()
        }
        dialog.create()
        dialog.show()


    }

    override fun onResume() {
        // if (fragManager?.fragments?.size==0)
        // setInstance()

        // dismissProgressDialog()
        super.onResume()
    }

    fun setDrawabletoMenuItem() {
        binding.tvAppVersion.setText(getString(R.string.txt_app_version) + " 1.1")
        /*  binding.navView.menu.getItem(0)?.setActionView(R.layout.drawer_user_icon_layout)
          binding.navView.menu.getItem(1)?.setActionView(R.layout.drawer_user_icon_layout)
          binding.navView.menu.getItem(2)?.setActionView(R.layout.drawer_user_icon_layout)
          binding.navView.menu.getItem(3)?.setActionView(R.layout.drawer_user_icon_layout)
          binding.navView.menu.getItem(4)?.setActionView(R.layout.drawer_user_icon_layout)
          binding.navView.menu.getItem(5)?.setActionView(R.layout.drawer_user_icon_layout)
          binding.navView.menu.getItem(6)?.setActionView(R.layout.drawer_user_icon_layout)*/
    }

    fun hideDrawerViews() {
        CoroutineScope(Dispatchers.Main).launch {
            if (DataStoreHelper.getLoggedInStatus()) {
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        }

    }


    fun closeDrawer() {
        binding.drawerLayout.closeDrawer(Gravity.LEFT)
    }

    fun getViewModel() = viewModel

    fun setDrawerListener() {
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navdrawerCandidateId -> {
                    Log.d(TAG, "onNavigationItemSelected: candidate list clicked")
                    //hideAllFragment()
                    //attatchFragment(1)
                    supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.fragment_container, CandidateListFragment.getInstance())
                        ?.commit()
                    closeDrawer()
                }

                R.id.navdrawerall_meetings -> {

                    // hideAllFragment()
                    // attatchFragment(0)
                    //(UpcomingMeetingStatusHolder.getFragment() as UpcomingListFragment)?.getMeetingList(0)
                    Log.d(
                        TAG,
                        "setDrawerListener: fralistt ${supportFragmentManager.fragments.size}"
                    )
                    supportFragmentManager.fragments.forEach {
                        Log.d(
                            TAG,
                            "setDrawerListener: fralistt ${it.id}"
                        )

                    }

                    supportFragmentManager?.beginTransaction()?.replace(
                        R.id.fragment_container,
                        UpcomingListFragment(getString(R.string.txt_all_meetings))
                    )?.commit()
                    closeDrawer()
                }

                R.id.navdrawerscheduled_meetings -> {
                    //  replaceFragment(viewModel.getFragmentsList().get(0))
                    // hideAllFragment()
                    // attatchFragment(0)
                    supportFragmentManager?.beginTransaction()?.replace(
                        R.id.fragment_container,
                        UpcomingListFragment(getString(R.string.txt_scheduled))
                    )?.commit()
                    // (UpcomingMeetingStatusHolder.getFragment() as UpcomingListFragment)?.getMeetingList(2)
                    closeDrawer()
                }

                R.id.navdrawerattended_meetings -> {
                    // replaceFragment(viewModel.getFragmentsList().get(0))
                    //hideAllFragment()
                    //attatchFragment(0)
                    supportFragmentManager?.beginTransaction()?.replace(
                        R.id.fragment_container,
                        UpcomingListFragment(getString(R.string.txt_attended))
                    )?.commit()
                    //upcomingFragment?.getMeetingList(1)
                    closeDrawer()
                }

                R.id.navdrawermissed -> {
                    // replaceFragment(viewModel.getFragmentsList().get(0))
                    // hideAllFragment()
                    // attatchFragment(0)
                    supportFragmentManager?.beginTransaction()?.replace(
                        R.id.fragment_container,
                        UpcomingListFragment(getString(R.string.txt_missed))
                    )?.commit()
                    // (UpcomingMeetingStatusHolder.getFragment() as UpcomingListFragment)?.getMeetingList(3)
                    closeDrawer()
                }

                R.id.navdrawercancel_meetings -> {
                    //  replaceFragment(viewModel.getFragmentsList().get(0))
                    // hideAllFragment()
                    // attatchFragment(0)
                    supportFragmentManager?.beginTransaction()?.replace(
                        R.id.fragment_container,
                        UpcomingListFragment(getString(R.string.txt_cancelled))
                    )?.commit()
                    // (UpcomingMeetingStatusHolder.getFragment() as UpcomingListFragment)?.getMeetingList(4)
                    closeDrawer()
                }

                R.id.navdrawercreateCandidate -> {
                    closeDrawer()
                    supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.fragment_container, FragmentCreateCandidate())?.commit()
                    setHandler().postDelayed({
                        // startActivity(Intent(this,FragementCreateCandidate::class.java))
                    }, 200)
                }
                R.id.navdrawerselectLanguage->{
                selectLangaugeDialog()
                }
                /*R.id.navdrawerQuestonnaireScreen -> {
                    closeDrawer()
                    startActivity(Intent(this,ActivityCandidateQuestinnaire::class.java))
                }*/
            }


            true
        }

    }


    fun openDrawer() {
        Log.d(TAG, "openDrawer: open")
        hideKeyboard(this)
        binding.drawerLayout.openDrawer(Gravity.LEFT)
    }

    private fun logout() {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage(getString(R.string.txt_do_you_want_to_logout))
        dialog.setPositiveButton(
            getString(R.string.txt_ok),
            DialogInterface.OnClickListener { dialogInterface, i ->
                //  DataStoreHelper.clearData()
                checkDeepLinkExists()
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

    private fun selectLangaugeDialog() {
        runOnUiThread {
            val dialog = Dialog(this)

            val dialogBinding =
                LayoutChooseLanguageDialogBinding.inflate(LayoutInflater.from(this))
            dialog.setContentView(dialogBinding.root)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogBinding.btnCross.setOnClickListener {
                dialog.dismiss()
            }
            dialogBinding.btnSubmitButton.setText(getString(R.string.txt_submit))
            val language = mutableListOf<String>()
            val languageStringList = mutableListOf<ModelLanguageSelect>()

            languageStringList.add(ModelLanguageSelect(getString(R.string.txt_english), "en-US"))
            languageStringList.add(ModelLanguageSelect(getString(R.string.txt_spanish), "es"))
            languageStringList.add(ModelLanguageSelect(getString(R.string.txt_french), "fr"))

            language.add(getString(R.string.txt_select_language))
            language.add(getString(R.string.txt_english))
            language.add(getString(R.string.txt_spanish))
            language.add(getString(R.string.txt_french))
            var selectedLanguage: String? = null
            CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                runOnUiThread {

                    // dialogBinding.tvUsername.setText(data.Name)

                    val langAdapter = getArrayAdapterOneItemSelected(language)
                    dialogBinding.spinnerLanguage.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                if (position != 0) {
                                    languageStringList.forEach {
                                        if (it.language.equals(language[position])) {
                                            selectedLanguage = it.langCode
                                        }
                                    }

                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                        }

                    dialogBinding.spinnerLanguage.adapter = langAdapter

                    dialogBinding.btnSubmitButton.setOnClickListener {
                        if (selectedLanguage != null) {
                            dialog.dismiss()
                            setLanguagetoApp(selectedLanguage.toString())
                        } else {
                            dialogBinding.tvError.visibility = View.VISIBLE
                            setHandler().postDelayed({
                                dialogBinding.tvError.visibility = View.INVISIBLE
                            }, 3000)
                            //showCustomSnackbarOnTop(getString(R.string.txt_please_select_language))
                        }
                    }
                }

            }

            dialog.create()
            dialog.show()

        }
    }

    private fun setLanguagetoApp(langCode:String)
    {
        val local= Locale(langCode)
        Locale.setDefault(local)
        val config=resources.configuration
        config.setLocale(local)
        resources.updateConfiguration(config,resources.displayMetrics)
        startActivity(intent)
        CoroutineScope(Dispatchers.IO).launch {
            DataStoreHelper.setAppLanguage(langCode)
        }
    }

    private fun checkDeepLinkExists()
    {
        CoroutineScope(Dispatchers.IO).launch {
            try {
            if (DataStoreHelper.getDeeplinkIsOpenStatus()){
                DataStoreHelper.clearData()
                DataStoreHelper.setDeepLinkData(true)
            }else
            {
                DataStoreHelper.clearData()
            }
            }catch (e:Exception)
            {
                DataStoreHelper.clearData()
                Log.d(TAG, "checkDeepLinkExists: exception in checking deeplink")
            }
        }

    }

    private fun setupDrawer() {
        binding.drawerLayout
    }

    override fun onDestroy() {
        dismissProgressDialog()
        super.onDestroy()
    }

    var isBackPressed = 0

    override fun onBackPressed() {

        // Log.d(TAG, "onBackPressed: ${viewModel.getFragmentsList().get(0).tag} ${binding.fragmentContainer.tag} ")
        // Log.d(TAG, "onBackPressed: navid ${binding.navViewDrawer.findFragment<CandidateListFragment>().id}  viewmodelId ${viewModel.getFragmentsList().get(0).id}")
        Log.d(TAG, "onBackPressed: if part")
        showCustomSnackbarOnTop(getString(R.string.txt_press_back_again_to_exit))
        isBackPressed = isBackPressed + 1
        setHandler().postDelayed({
            isBackPressed = 0
        }, 2000)

        if (isBackPressed == 2){
            finishAffinity()
            super.onBackPressed()

        }


        // super.onBackPressed()
    }

    fun attatchFragment(fragNo: Int) {

        try {
            if (!viewModel.getFragmentsList().isNullOrEmpty()) {

                val fragTrans = fragManager?.beginTransaction()
                //fragTrans?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                fragTrans?.show(viewModel.getFragmentsList().get(fragNo))?.commit()
                Log.d(TAG, "replaceFragment: no null detached")
            } else {
                Log.d(TAG, "replaceFragment: not detached")
            }

        } catch (e: Exception) {
            Log.d(TAG, "replaceFragment: exception ${e.message}")
        }

        Log.d(TAG, "replaceFragment: frag replace ${fragManager?.fragments?.size} ")
    }

    fun replaceFragment(fragNo: Int) {
        try {
            if (!viewModel.getFragmentsList().isNullOrEmpty()) {
                fragManager?.beginTransaction()?.attach(viewModel.getFragmentsList().get(fragNo))

                Log.d(TAG, "replaceFragment: no null detached")
            } else {
                Log.d(TAG, "replaceFragment: not detached")
            }

        } catch (e: Exception) {
            Log.d(TAG, "replaceFragment: exception ${e.message}")
        }

        Log.d(TAG, "replaceFragment: frag replace ${fragManager?.fragments?.size} ")
    }


    fun hideAllFragment() {
        try {
            if (!viewModel.getFragmentsList().isNullOrEmpty()) {
                viewModel.getFragmentsList().forEach { fr ->
                    fragManager?.beginTransaction()?.hide(fr)?.commit()
                }
            } else {
                Log.d(TAG, "replaceFragment: not detached")
            }

        } catch (e: Exception) {
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