package com.app.guardian.ui.Home


import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.guardian.R
import com.app.guardian.common.*
import com.app.guardian.common.SharedPreferenceManager.removeSeletionData
import com.app.guardian.common.extentions.checkLoationPermission
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.ActivityHomeBinding
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.ContactedHistory.ContectedHistoryFragment
import com.app.guardian.ui.Lawyer.LawyerHome.LawyerHomeFragment
import com.app.guardian.ui.LawyerList.LawyerListFragment
import com.app.guardian.ui.Login.LoginActivity
import com.app.guardian.ui.Mediator.MediatorHome.MediatorHomeFragment
import com.app.guardian.ui.Radar.RadarFragment
import com.app.guardian.ui.SupportGroups.SupportGroupList
import com.app.guardian.ui.User.ContactSupport.ContactSupportFragment
import com.app.guardian.ui.User.LiveVirtualVitness.LiveVirtualVitnessUserFragment
import com.app.guardian.ui.User.RecordPolice.RecordPoliceInteractionFragment
import com.app.guardian.ui.User.RecordPoliceInteraction_2.RecordPoliceInteraction_2Fragment
import com.app.guardian.ui.User.ScheduleVirtualWitness.ScheduleVirtualWitnessFragment
import com.app.guardian.ui.User.UserHome.UserHomeFragment
import com.app.guardian.ui.User.settings.SettingsFragment
import com.app.guardian.ui.chatting.ChattingFragment
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView


class HomeActivity : BaseActivity(), View.OnClickListener, onBadgeCounterIntegration {
    lateinit var mBinding: ActivityHomeBinding

    //get Current Location
    private var locationManager: LocationManager? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    val notification_type = ""
    val notification_id_ = ""
    var main_layoutBageCounter: RelativeLayout? = null
    private var notificationBadge: View? = null
    var txtBagecount: TextView? = null


    companion object {
        var bage_counter_notification: Int = 0
        val intentAction = "com.parse.push.intent.RECEIVE"
    }

    //todo : broadcast for chat notification
    private val mBroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            val extras = intent.getExtras()
            val getFragment = supportFragmentManager.findFragmentById(R.id.flUserContainer)
            if (getFragment != null) {
                if (getFragment !is SettingsFragment) {
                    if (extras != null) {
                        if (extras.containsKey("data")) {
                            onVisibleBageCounterCounter(extras.getInt("data"))
                        } else if (extras.containsKey("code")) {

                        }
                    }
                }
            } else {
                onHideBadgeCounter()
            }


        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val i = getIntent()
        val extras = i.extras
        if (extras != null) {
            for (key in extras.keySet()) {
                val value = extras[key]
                Log.d(
                    "Notification",
                    "Extras received at onNewIntent:  Key: $key Value: $value"
                )
            }
            val title = extras.getString("title")
            val message = extras.getString("body")
            if (message != null && message.length > 0) {
                getIntent().removeExtra("body")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val i = getIntent()
        val extras = i.extras
        if (extras != null) {
            for (key in extras.keySet()) {
                val value = extras[key]
                Log.d(
                    "Notification",
                    "Extras received at onCreate:  Key: $key Value: $value"
                )
            }
            val title = extras.getString("title")
            val message = extras.getString("body")
            if (message != null && message.length > 0) {
                getIntent().removeExtra("body")
            }
        }
    }
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_home
    }


    override fun initView() {

        val count = SharedPreferenceManager.getInt(AppConstants.NOTIFICATION_BAGE, 0)
        Log.e("bageCount","BageCount initView : "+count.toString())
        if(count>0){
            onVisibleBageCounterCounter(count)
        }
        mBinding = getBinding()

        mBinding.bottomNavigationUser.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> {
                    clearFragmentBackStack()
                    loadHomeScreen()
//                    ReplaceFragment.replaceFragment(this,KnowRightFragment(),false,"",HomeActivity::class.java.name)
                }
                R.id.menu_lawyer -> {
                    clearFragmentBackStack()
//                    ReplaceFragment.replaceFragment(
//                        this,
//                        ChattingFragment(),
//                        false,
//                        "",
//                        HomeActivity::class.java.name
//                    )
                    ReplaceFragment.replaceFragment(
                        this,
                        LawyerListFragment(false),
                        false,
                        "",
                        HomeActivity::class.java.name
                    )
                }
                R.id.menu_radar -> {
                    clearFragmentBackStack()
                    ReplaceFragment.replaceFragment(
                        this,
                        RadarFragment(),
                        false,
                        "",
                        HomeActivity::class.java.name
                    )

                }
                R.id.menu_history -> {
                    clearFragmentBackStack()
                    ReplaceFragment.replaceFragment(
                        this,
                        ContectedHistoryFragment(),
                        false,
                        "",
                        HomeActivity::class.java.name
                    )
                }
                R.id.menu_setting -> {
                    onHideBadgeCounter()
                    clearFragmentBackStack()
                    ReplaceFragment.replaceFragment(
                        this,
                        SettingsFragment(),
                        false,
                        "",
                        HomeActivity::class.java.name
                    )
                }
            }
            true
        }


        mBinding.headerToolbar.ivBack.setOnClickListener()
        {
            onBackPressed()
        }
        //bottom navigation click listener


        if (intent != null && intent.extras != null) {
            Log.i(
                "NOTIFICATION_DATA_H",
                intent.getStringExtra(AppConstants.EXTRA_NOTIFICATION_DATA_TYPE)!!
            )
            if (intent.getStringExtra(AppConstants.EXTRA_NOTIFICATION_DATA_TYPE) == AppConstants.EXTRA_CHAT_MESSAGE_PAYLOAD) {
                Log.i(
                    "NOTIFICATION_DATA_H",
                    intent.getStringExtra(AppConstants.EXTRA_NOTIFICATION_DATA_TYPE)!!
                )
                ReplaceFragment.replaceFragment(
                    this,
                    ChattingFragment(
                        intent.getStringExtra(AppConstants.EXTRA_NOTIFICATION_DATA_ID)!!.toInt(),
                        true
                    ),
                    true,
                    HomeActivity::class.java.name,
                    HomeActivity::class.java.name
                )
            } else if (intent.getStringExtra(AppConstants.EXTRA_NOTIFICATION_DATA_TYPE) == AppConstants.EXTRA_MEDIATOR_PAYLOAD) {
                Log.i(
                    "NOTIFICATION_DATA_H",
                    intent.getStringExtra(AppConstants.EXTRA_NOTIFICATION_DATA_TYPE)!!
                )
            } else if (intent.getStringExtra(AppConstants.EXTRA_NOTIFICATION_DATA_TYPE) == AppConstants.EXTRA_VIRTUAL_WITNESS_PAYLOAD) {
                Log.i(
                    "NOTIFICATION_DATA_H",
                    intent.getStringExtra(AppConstants.EXTRA_NOTIFICATION_DATA_TYPE)!!
                )

            }
        } else {
            bottomTabVisibility(true)
            loadHomeScreen()
        }

        locationManager = getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest?.interval = 20 * 1000


        headerTextVisible(resources.getString(R.string.seek_legal_advice), false, true)

        applybadgeview()

    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);

    }

    //todo : other function
    //use for notification badge counter display ( only sendbird chat notification get in this fun )
    fun applybadgeview() {
        try {
            var menuView: BottomNavigationMenuView? = null
            var itemView: BottomNavigationItemView? = null
            menuView = mBinding.bottomNavigationUser.getChildAt(0) as BottomNavigationMenuView
            itemView = menuView.getChildAt(4) as BottomNavigationItemView

            Log.e("menuItem", "Update menu item : $menuView   :  $itemView")
            notificationBadge = LayoutInflater.from(this)
                .inflate(R.layout.item_notification_layout, menuView, false)
            itemView.addView(notificationBadge)

            main_layoutBageCounter = notificationBadge?.findViewById(R.id.ly_badge_counter)
            txtBagecount = notificationBadge?.findViewById(R.id.txt_badge_count)

            main_layoutBageCounter?.visibility = View.INVISIBLE
            txtBagecount?.visibility = View.INVISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun historyPageOpen() {
        mBinding.bottomNavigationUser.selectedItemId = R.id.menu_history;
    }

    fun lawyerListPageOpen() {
        mBinding.bottomNavigationUser.selectedItemId = R.id.menu_lawyer;
    }


    fun visibleBageCount(){
        val count = SharedPreferenceManager.getInt(AppConstants.NOTIFICATION_BAGE, 0)
        Log.e("bageCount","BageCount visibleBageCount : "+count.toString())
        if(count>0){
            onVisibleBageCounterCounter(count)
        }
    }



    override fun onResume() {
        super.onResume()
        val count = SharedPreferenceManager.getInt(AppConstants.NOTIFICATION_BAGE, 0)
        Log.e("bageCount","BageCount onResume : "+count.toString())
        if(count>0){
            onVisibleBageCounterCounter(count)
        }
        removeSeletionData()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mBroadcastReceiver, IntentFilter(
                intentAction
            )
        )
        getLatLong()
        if (checkLoationPermission(this)) {
            if (locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!) {

                mFusedLocationClient?.requestLocationUpdates(
                    locationRequest!!,
                    locationCallback!!,
                    Looper.getMainLooper()
                )
            } else {

                ReusedMethod.setLocationDialog(this)
            }
        }

    }


    override fun initObserver() {

        //Logout Resp
//        authViewModel.getSignOutResp().observe(this) { response ->
//            response?.let { requestState ->
//                showLoadingIndicator(requestState.progress)
//                requestState.apiResponse?.let {
//                    it.data?.let { data ->
//                        if (it.status) {
//                            SharedPreferenceManager.removeAllData()
////        authViewModel.signOUT(true, this as BaseActivity)
//                            startActivity(
//                                Intent(
//                                    this@HomeActivity,
//                                    LoginActivity::class.java
//                                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                            )
//                            overridePendingTransition(R.anim.rightto, R.anim.left)
//                            finish()
//                        } else {
//                            ReusedMethod.displayMessage(this, it.message.toString())
//                        }
//                    }
//                }
//                requestState.error?.let { errorObj ->
//                    when (errorObj.errorState) {
//                        Config.NETWORK_ERROR ->
//                            ReusedMethod.displayMessage(
//                               this,
//                                getString(R.string.text_error_network)
//                            )
//
//                        Config.CUSTOM_ERROR ->
//                            errorObj.customMessage
//                                ?.let {
//                                    if (errorObj.code == ApiConstant.API_401) {
//                                        ReusedMethod.displayMessage(this, it)
//                                        HomeActivity().unAuthorizedNavigation()
//                                    } else {
//                                        ReusedMethod.displayMessage(this as Activity, it)
//                                    }
//                                }
//                    }
//                }
//            }
//        }

    }


    override fun handleListener() {
    }


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_home)
//
//
//        loadHomeScreen()
//
//    }

    override fun onBackPressed() {
        //  super.onBackPressed()
        var getFragment = supportFragmentManager.findFragmentById(R.id.flUserContainer)
        if (getFragment != null) {
            if (getFragment is ChattingFragment) {
                ChattingFragment().stopTimers()
            }
        }
        selecionReset(getFragment)
        SharedPreferenceManager.clearCityState()
        val fm: FragmentManager = supportFragmentManager
        val getCurrentFragment = supportFragmentManager.fragments
        Log.e("BackStack", "Current fragment Name : " + getCurrentFragment.toString())


        when {
            SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                AppConstants.APP_ROLE_USER
            ) == AppConstants.APP_ROLE_USER -> {
                if (getFragment is UserHomeFragment || getFragment is LawyerListFragment || getFragment is ContectedHistoryFragment || getFragment is
                            RadarFragment || getFragment is SettingsFragment
                ) {
                    super.onBackPressed()
                } else {
                    fm.popBackStack()
                }

            }
            SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                AppConstants.APP_ROLE_USER
            ) == AppConstants.APP_ROLE_LAWYER -> {
                if (getFragment != null) {
                    if (getFragment is LawyerHomeFragment || getFragment is LawyerListFragment || getFragment is ContectedHistoryFragment || getFragment is SettingsFragment) {
                        super.onBackPressed()
                    } else {
                        fm.popBackStack()
                    }
                }
            }
            SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                AppConstants.APP_ROLE_USER
            ) == AppConstants.APP_ROLE_MEDIATOR -> {

                if (getFragment != null) {
                    if (getFragment is MediatorHomeFragment || getFragment is LawyerListFragment || getFragment is ContectedHistoryFragment || getFragment is SettingsFragment) {
                        super.onBackPressed()
                    } else {
                        fm.popBackStack()
                    }
                }

            }
        }

    }

    private fun selecionReset(fragment: Fragment?) {
        if (fragment != null) {
            when (fragment) {
                is RecordPoliceInteractionFragment -> {
                    SharedPreferenceManager.putInt(
                        AppConstants.EXTRA_SH_RECORD_POLICE_INTERACTION,
                        0
                    )
                }
                is RecordPoliceInteraction_2Fragment -> {
                    SharedPreferenceManager.putInt(
                        AppConstants.EXTRA_SH_RECORD_POLICE_INTERACTION_2,
                        0
                    )
                }
                is LiveVirtualVitnessUserFragment -> {
                    SharedPreferenceManager.putInt(AppConstants.EXTRA_SH_LIVE_VIRTUAL_WITNESS, 0)
                }
                is ScheduleVirtualWitnessFragment -> {
                    SharedPreferenceManager.putInt(
                        AppConstants.EXTRA_SH_SCHEDUAL_VIRTUAL_WITNESS,
                        0
                    )
                }
                is ContactSupportFragment -> {
                    SharedPreferenceManager.putInt(AppConstants.EXTRA_SH_CONTACT_SUPPORT, 0)
                }
                is SupportGroupList -> {
                    SharedPreferenceManager.putInt(AppConstants.EXTRA_SH_SUPPORT_GROUP_LIST, 0)
                }
            }

        }
    }

    fun clearFragmentBackStack() {
        val fm: FragmentManager = supportFragmentManager

        for (fragment in supportFragmentManager.fragments) {
            supportFragmentManager.beginTransaction().remove(fragment).commit()
        }


    }

    private fun loadHomeScreen() {
        when {
            SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                AppConstants.APP_ROLE_USER
            ) == AppConstants.APP_ROLE_USER -> {

                ReplaceFragment.replaceFragment(
                    this,
                    UserHomeFragment(),
                    false,
                    "",
                    HomeActivity::class.java.name
                )
            }
            SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                AppConstants.APP_ROLE_USER
            ) == AppConstants.APP_ROLE_LAWYER -> {
                mBinding.bottomNavigationUser.menu.getItem(2).isVisible = false
                mBinding.bottomNavigationUser.menu.getItem(3).setIcon(R.drawable.ic_history_2)
                mBinding.bottomNavigationUser.menu.getItem(3).title = "Contact history"
                ReplaceFragment.replaceFragment(
                    this,
                    LawyerHomeFragment(),
                    false,
                    "",
                    HomeActivity::class.java.name
                );
            }
            SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                AppConstants.APP_ROLE_USER
            ) == AppConstants.APP_ROLE_MEDIATOR -> {
                mBinding.bottomNavigationUser.menu.getItem(2).isVisible = false
                mBinding.bottomNavigationUser.menu.getItem(3).setIcon(R.drawable.ic_history_2)
                mBinding.bottomNavigationUser.menu.getItem(3).title = "Contact history"
                ReplaceFragment.replaceFragment(
                    this,
                    MediatorHomeFragment(),
                    false,
                    "",
                    HomeActivity::class.java.name
                );
            }
        }
    }

    fun headerTextVisible(
        headerTitle: String,
        isHeaderVisible: Boolean,
        isBackButtonVisible: Boolean
    ) {
        if (isHeaderVisible) {
            mBinding.headerToolbar.clHeadder.visible()

            mBinding.headerToolbar.tvHeaderText.text = headerTitle

            if (isBackButtonVisible)
                mBinding.headerToolbar.ivBack.visible()
            else
                mBinding.headerToolbar.ivBack.gone()
        } else {
            mBinding.headerToolbar.clHeadder.gone()
        }

    }

    fun bottomTabVisibility(isBottomVisible: Boolean) {
        if (isBottomVisible) {
            mBinding.bottomNavigationUser.visible()
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        } else {
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            mBinding.bottomNavigationUser.gone()
        }
    }

    override fun onClick(v: View?) {

    }

    private fun getLatLong() {

        if (checkLoationPermission(this)) {

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0!!)

                    if (p0.equals(null)) {

                        return
                    }
                    for (location in p0.locations) {
                        if (location != null) {

                            SharedPreferenceManager.putString(
                                AppConstants.EXTRA_LAT,
                                location.latitude.toString()
                            )
                            SharedPreferenceManager.putString(
                                AppConstants.EXTRA_LONG,
                                location.longitude.toString()
                            )

                            if (mFusedLocationClient != null) {
                                mFusedLocationClient?.removeLocationUpdates(locationCallback!!)
                            }
                        }
                    }
                }
            }
        }
    }

    fun unAuthorizedNavigation() {
        SharedPreferenceManager.removeAllData()
//        authViewModel.signOUT(true, this as BaseActivity)
        startActivity(
            Intent(
                this@HomeActivity,
                LoginActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        overridePendingTransition(R.anim.rightto, R.anim.left)
        finish()

    }

    override fun onVisibleBageCounterCounter(i: Int) {
        try {
            Log.e("Brodcast value ", "Notification Counter visible :" + i.toString())

            main_layoutBageCounter?.visibility = View.VISIBLE
            txtBagecount?.visibility = View.VISIBLE
            txtBagecount?.text = i.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Bage count", "Bage count e:" + e.printStackTrace())
        }
    }

    override fun onHideBadgeCounter() {
        try {

            bage_counter_notification = 0
            main_layoutBageCounter?.visibility = View.INVISIBLE
            txtBagecount?.visibility = View.INVISIBLE

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Bage count", "Bage count e:" + e.printStackTrace())
        }
    }

//    fun removeSettingBage() {
//        onHideBadgeCounter()
//    }

}