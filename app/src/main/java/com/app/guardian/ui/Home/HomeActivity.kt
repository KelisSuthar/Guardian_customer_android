package com.app.guardian.ui.Home


import android.view.View
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.ActivityHomeBinding
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.Lawyer.LawyerHome.LawyerHomeFragment
import com.app.guardian.ui.Lawyer.LawyerListFragment
import com.app.guardian.ui.Mediator.MediatorHome.MediatorHomeFragment
import com.app.guardian.ui.User.UserHome.UserHomeFragment
import com.app.guardian.ui.User.settings.SettingsFragment


class HomeActivity : BaseActivity(),View.OnClickListener {
    lateinit var mBinding: ActivityHomeBinding

    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_home
    }

    override fun initView() {
        mBinding = getBinding()
        mBinding.bottomNavigationUser.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> {
                    loadHomeScreen()

                }
                R.id.menu_lawyer -> {ReplaceFragment.homeFragmentReplace(this,LawyerListFragment(),null);}
                R.id.menu_radar -> {}
                R.id.menu_history ->{}
                R.id.menu_setting ->ReplaceFragment.homeFragmentReplace(this, SettingsFragment(), null);
            }
            true
        }


//        navView = findViewById(R.id.bottomNavigationUser)
//        navController = Navigation.findNavController(this, R.id.flUserContainer)
//        navView.itemIconTintList = null
//        NavigationUI.setupWithNavController(navView, navController!!)

        loadHomeScreen()
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

    private fun loadHomeScreen() {
        when {
            SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                AppConstants.APP_ROLE_USER
            ) == AppConstants.APP_ROLE_USER -> {

                ReplaceFragment.homeFragmentReplace(this, UserHomeFragment(), null);
            }
            SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                AppConstants.APP_ROLE_USER
            ) == AppConstants.APP_ROLE_LAWYER -> {
                ReplaceFragment.homeFragmentReplace(this, LawyerHomeFragment(), null);
            }
            SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                AppConstants.APP_ROLE_USER
            ) == AppConstants.APP_ROLE_MEDIATOR -> {
                ReplaceFragment.homeFragmentReplace(this, MediatorHomeFragment(), null);
            }
        }
    }

    fun headerTextVisible(
        headerTitle: String,
        isHeaderVisible: Boolean,
        isBackButtonVisible: Boolean
    ) {
        if (isHeaderVisible) {
            mBinding.headerToolbar.tvHeaderText.text =
                resources.getString(R.string.select_user_role)

            if (isBackButtonVisible)
                mBinding.headerToolbar.ivBack.visible()
            else
                mBinding.headerToolbar.ivBack.gone()
        } else {
            mBinding.headerToolbar.clHeadder.gone()
        }

    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}