package com.app.guardian.ui.Home


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.FragmentManager
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.checkLoationPermission
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.ActivityHomeBinding
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.ContactedHistory.ContectedHistoryFragment
import com.app.guardian.ui.KnowRight.KnowRightFragment
import com.app.guardian.ui.Lawyer.LawyerHome.LawyerHomeFragment
import com.app.guardian.ui.LawyerList.LawyerListFragment
import com.app.guardian.ui.Mediator.MediatorHome.MediatorHomeFragment
import com.app.guardian.ui.Radar.RadarFragment
import com.app.guardian.ui.User.UserHome.UserHomeFragment
import com.app.guardian.ui.User.settings.SettingsFragment
import com.google.android.gms.location.*
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import java.util.*


class HomeActivity : BaseActivity(), View.OnClickListener {
    lateinit var mBinding: ActivityHomeBinding


    //get Current Location
    private var locationManager: LocationManager? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null

    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_home
    }

    override fun initView() {
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
        bottomTabVisibility(true)
        loadHomeScreen()

        locationManager = getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest?.interval = 20 * 1000


        headerTextVisible(resources.getString(R.string.seek_legal_advice), false, true)
    }

    fun historyPageOpen() {
        mBinding.bottomNavigationUser.selectedItemId = R.id.menu_history;
    }


    override fun onResume() {
        super.onResume()

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
        SharedPreferenceManager.clearCityState()
        val fm: FragmentManager = supportFragmentManager
        var getCurrentFragment = supportFragmentManager.fragments
        Log.e("BackStack", "Current fragment Name : " + getCurrentFragment.toString())
        var getFragment = supportFragmentManager.findFragmentById(R.id.flUserContainer)
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

                            Log.i("THIS_APP", location.latitude.toString())
                            Log.i("THIS_APP", location.longitude.toString())

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




}