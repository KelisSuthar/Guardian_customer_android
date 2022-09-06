package com.app.guardian.ui.Home


import android.annotation.SuppressLint
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
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.app.guardian.R
import com.app.guardian.common.*
import com.app.guardian.common.AppConstants.BROADCAST_REC_INTENT
import com.app.guardian.common.SharedPreferenceManager.clearCityState
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
import com.app.guardian.ui.MediatorVideoCallReq.MediatorVideoCallReqFragment
import com.app.guardian.ui.Radar.RadarFragment
import com.app.guardian.ui.SupportGroups.SupportGroupList
import com.app.guardian.ui.User.ContactSupport.ContactSupportFragment
import com.app.guardian.ui.User.LiveVirtualVitness.LiveVirtualVitnessUserFragment
import com.app.guardian.ui.User.RecordPolice.RecordPoliceInteractionFragment
import com.app.guardian.ui.User.RecordPoliceInteraction_2.RecordPoliceInteraction_2Fragment
import com.app.guardian.ui.User.ScheduleVirtualWitness.ScheduleVirtualWitnessFragment
import com.app.guardian.ui.User.UserHome.UserHomeFragment
import com.app.guardian.ui.User.settings.SettingsFragment
import com.app.guardian.ui.VideoCallReq.VideoCallReqFragment
import com.app.guardian.ui.chatting.ChattingFragment
import com.app.guardian.ui.videocalljoin.VideoCallJoinActivity
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import org.json.JSONObject


class HomeActivity : BaseActivity(), View.OnClickListener, onBadgeCounterIntegration {
    lateinit var mBinding: ActivityHomeBinding

    //    private val mVideModel: UserViewModel by viewModel()
    var notification_type = ""
    var notification_id = ""
    var notification_URL = ""
    var notification_meeting_id = ""

    //get Current Location
    private var locationManager: LocationManager? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
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

            val extras = intent.extras

            notification_id = extras!!.getString("sender_id").toString()
            notification_type = extras!!.getString("type").toString()
            notification_URL = extras!!.getString("url").toString()
            notification_meeting_id = extras!!.getString("room_id").toString()

            Log.e("BROADCAST_DATA", extras!!.getString("sender_id").toString())
            Log.e("BROADCAST_DATA", extras!!.getString("type").toString())
            Log.e("BROADCAST_DATA", extras!!.getString("url").toString())
            Log.e("BROADCAST_DATA", extras!!.getString("room_id").toString())

            val getFragment = supportFragmentManager.findFragmentById(R.id.flUserContainer)
            if (getFragment != null) {
                if (getFragment !is SettingsFragment) {
                    if (extras != null) {
                        if (extras.containsKey("data")) {
                            onVisibleBageCounterCounter(extras.getInt("data"))
                        } else if (extras.containsKey("code")) {
                        }
                        Log.e("BROADCAST_DATA", extras.getString("notification_data").toString())
                    }
                }
            } else {
                onHideBadgeCounter()
            }


        }
    }

//
//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//
//        Log.e(
//            "NOTIFICATION_SPLASh",
//            "onNewIntent call"
//        )
//
//
//    }


    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_home
    }


    override fun initView() {

        val count = SharedPreferenceManager.getInt(AppConstants.NOTIFICATION_BAGE, 0)
        Log.e("bageCount", "BageCount initView : " + count.toString())
        if (count > 0) {
            onVisibleBageCounterCounter(count)
        }
        mBinding = getBinding()




        locationManager = getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest?.interval = 20 * 1000




        applybadgeview()

        clearFragmentBackStack()
        Log.e(
            "THIS_APP_NOTI",
            intent.getBooleanExtra(AppConstants.IS_NOTIFICATION, false).toString()
        )
        if (!intent.getBooleanExtra(AppConstants.IS_NOTIFICATION, false)) {
            loadHomeScreen()
        }


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
//            menuView = mBinding.bottomNavigationUser.getChildAt(0) as BottomNavigationMenuView
            menuView = mBinding.bottomNavigationUser.getChildAt(0) as BottomNavigationMenuView
            itemView =
                if (SharedPreferenceManager.getLoginUserRole() == AppConstants.APP_ROLE_USER) {
                    menuView.getChildAt(4) as BottomNavigationItemView
                } else {

                    menuView.getChildAt(0) as BottomNavigationItemView
                }

//            itemView = menuView.findViewById(R.id.menu_setting) as BottomNavigationItemView

            Log.e("menuItem", "Update menu item : ${menuView.getChildAt(1).id}   :  $itemView")
            Log.e("menuItem", "Update menu item : ${menuView[4]}   :  $itemView")
//            notificationBadge = LayoutInflater.from(this)
//                .inflate(R.layout.item_notification_layout, menuView, false)
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


    fun visibleBageCount() {
        val count = SharedPreferenceManager.getInt(AppConstants.NOTIFICATION_BAGE, 0)
        Log.e("bageCount", "BageCount visibleBageCount : " + count.toString())
        if (count > 0) {
            onVisibleBageCounterCounter(count)
        }
    }


    @SuppressLint("LogNotTimber")
    override fun onResume() {
        super.onResume()
        val count = SharedPreferenceManager.getInt(AppConstants.NOTIFICATION_BAGE, 0)
        Log.e("bageCount", "BageCount onResume : " + count.toString())
        if (count > 0) {
            onVisibleBageCounterCounter(count)
        }
        removeSeletionData()

        LocalBroadcastManager.getInstance(this).registerReceiver(
            mBroadcastReceiver, IntentFilter(
                BROADCAST_REC_INTENT
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
        Log.i(
            "NOTIFICATION_H",
            SharedPreferenceManager.getString("NOTIFICATION", "").toString()
        )
        headerTextVisible(
            resources.getString(R.string.seek_legal_advice),
            isHeaderVisible = false,
            isBackButtonVisible = true
        )
        setNotificationRedirection()

    }

    private fun setNotificationRedirection() {
        val i = intent
        val extras = i.extras
        if (extras != null) {
            for (key in extras.keySet()) {
                val value = extras[key]
                Log.d(
                    "Notification",
                    "Extras received at initView splash:  Key: $key Value: $value"
                )
            }
            notification_type =
                intent.extras!!.getString("type").toString()
            notification_id = intent.extras!!.getString("sender_id").toString()
            notification_URL =
                intent.extras!!.getString("url").toString()
            notification_meeting_id = intent.extras!!.getString("room_id").toString()
        }
        when (SharedPreferenceManager.getLoginUserRole()) {
            AppConstants.APP_ROLE_LAWYER -> {
                if (notification_type == AppConstants.EXTRA_VIDEOCALLREQ_PAYLOAD) {
                    ReplaceFragment.replaceFragment(
                        this,
                        VideoCallReqFragment(),
                        true,
                        SettingsFragment::class.java.name,
                        SettingsFragment::class.java.name
                    )
                } else if (!notification_type.isNullOrEmpty() || !notification_id.isNullOrEmpty()) {
                    Log.e("THIS_APP_GUAR", notification_type.toString())
                    Log.e("THIS_APP_GUAR", notification_id.toString())
                    checkNotificationRedirection(
                        notification_type,
                        notification_id
                    )
                }
            }
            AppConstants.APP_ROLE_MEDIATOR -> {
                if (notification_type == AppConstants.EXTRA_MEDIATOR_PAYLOAD) {
                    ReplaceFragment.replaceFragment(
                        this,
                        MediatorVideoCallReqFragment(),
                        true,
                        SettingsFragment::class.java.name,
                        SettingsFragment::class.java.name
                    )
                } else if (!notification_type.isNullOrEmpty() || !notification_id.isNullOrEmpty()) {
                    Log.e("THIS_APP_GUAR", notification_type.toString())
                    Log.e("THIS_APP_GUAR", notification_id.toString())
                    checkNotificationRedirection(
                        notification_type,
                        notification_id
                    )
                }
            }
            AppConstants.APP_ROLE_USER -> {
                if (notification_type == AppConstants.EXTRA_VIDEOCALLREQ_PAYLOAD) {
                    if (!notification_id.isNullOrEmpty()) {
                        joinMeeting(notification_meeting_id.toString())
                    }

                } else if (!notification_type.isNullOrEmpty() || !notification_id.isNullOrEmpty()) {
                    Log.e("THIS_APP_GUAR", notification_type.toString())
                    Log.e("THIS_APP_GUAR", notification_id.toString())
                    checkNotificationRedirection(
                        notification_type,
                        notification_id
                    )
                }
            }
        }


    }

    private fun joinMeeting(meeting_Id: String) {
        AndroidNetworking.post("https://api.videosdk.live/v1/meetings/$meeting_Id")
            .addHeaders("Authorization", resources.getString(R.string.video_call_auth))
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    val meetingId = response.getString("meetingId")
                    Log.e("VIDEO_CALL", "JOIN_MEATING_RESP:    $response")
                    val intent =
                        Intent(this@HomeActivity, VideoCallJoinActivity::class.java)
                    intent.putExtra("token", resources.getString(R.string.video_call_auth))
                    intent.putExtra("meetingId", meetingId)
                    intent.putExtra(
                        AppConstants.EXTRA_NAME,
                        SharedPreferenceManager.getUser()?.full_name
                    )
                    intent.putExtra(
                        AppConstants.IS_JOIN,
                        true
                    )
                    intent.putExtra(
                        AppConstants.EXTRA_URL,
                        "https://api.videosdk.live/v1/meetings/$meetingId"
                    )
                    intent.putExtra(AppConstants.EXTRA_ROOM_ID, meetingId)
                    startActivity(intent)

                }

                override fun onError(anError: ANError) {
                    anError.printStackTrace()
                    ReusedMethod.displayMessage(
                        this@HomeActivity,
                        anError.message.toString()
                    )
                    ReusedMethod.displayMessage(this@HomeActivity, "Your Token Has Expired")
                    Log.e("VIDEO_CALL", "JOIN_MEATING_ERRRO:    " + anError.errorBody)

                }
            })
    }


    fun checkNotificationRedirection(notification_type: String, id: String) {
        SharedPreferenceManager.removeNotificationData()
        when (notification_type) {
            AppConstants.EXTRA_CHAT_MESSAGE_PAYLOAD -> {
                Log.i(
                    "NOTIFICATION_DATA_H",
                    notification_type
                )
                ReplaceFragment.replaceFragment(
                    this,
                    ChattingFragment(
                        id.toInt(),
                        true
                    ),
                    true,
                    HomeActivity::class.java.name,
                    HomeActivity::class.java.name
                )
            }
            AppConstants.EXTRA_MEDIATOR_PAYLOAD -> {
                Log.i(
                    "NOTIFICATION_DATA_H",
                    notification_type
                )
            }
            AppConstants.EXTRA_VIRTUAL_WITNESS_PAYLOAD -> {
                Log.i(
                    "NOTIFICATION_DATA_H",
                    notification_type
                )

            }
        }
    }

    override fun initObserver() {


    }


    override fun handleListener() {
    }

    override fun onBackPressed() {
        //  super.onBackPressed()
        SharedPreferenceManager.removeNotificationData()
        var getFragment = supportFragmentManager.findFragmentById(R.id.flUserContainer)
        if (getFragment != null) {
            if (getFragment is ChattingFragment) {
                ChattingFragment().stopTimers()
            }
        }
        selecionReset(getFragment)
        clearCityState()
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
            SharedPreferenceManager.getLoginUserRole() == AppConstants.APP_ROLE_USER -> {

                ReplaceFragment.replaceFragment(
                    this,
                    UserHomeFragment(),
                    false,
                    "",
                    HomeActivity::class.java.name
                )
            }
            SharedPreferenceManager.getLoginUserRole() == AppConstants.APP_ROLE_LAWYER -> {
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
            SharedPreferenceManager.getLoginUserRole() == AppConstants.APP_ROLE_MEDIATOR -> {
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


//    fun callUploadOfflienVideoAPI(URL: String) {
//
//        mVideModel.uploadOfflineVideos(true, this, URL)
//
//    }
//    fun removeSettingBage() {
//        onHideBadgeCounter()
//    }



}