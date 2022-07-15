package com.app.guardian.ui.User.UserHome


import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.app.guardian.ConnectivityChangeReceiver
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.ReusedMethod.Companion.redirectUrl
import com.app.guardian.common.ReusedMethod.Companion.showSubscriptionDialog
import com.app.guardian.common.ReusedMethod.Companion.viewPagerScroll
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentUserHomeBinding
import com.app.guardian.model.HomeBanners.BannerCollection
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.BannerAds.BannerAdsPager
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.HomeBanners.HomeBannersFragment
import com.app.guardian.ui.Login.LoginActivity
import com.app.guardian.ui.User.ContactSupport.ContactSupportFragment
import com.app.guardian.ui.User.RecordPolice.RecordPoliceInteractionFragment
import com.app.guardian.ui.User.ScheduleVirtualWitness.ScheduleVirtualWitnessFragment
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import com.google.gson.Gson
import org.koin.android.viewmodel.ext.android.viewModel


class UserHomeFragment : BaseFragment(), View.OnClickListener {
    private val mViewModel: CommonScreensViewModel by viewModel()
    private lateinit var mBinding: FragmentUserHomeBinding
    var array = ArrayList<BannerCollection>()
    var bannerArray = ArrayList<BannerCollection>()
    var bannerAdsPager: BannerAdsPager? = null

    override fun getInflateResource(): Int {
        return R.layout.fragment_user_home
    }

    override fun initView() {
        mBinding = getBinding()
        setAdapter()

        callApi()
        Log.e("EDIT_APP", SharedPreferenceManager.getUser().toString())
        (activity as HomeActivity).bottomTabVisibility(true)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.know_your_basic_rights),
            false,
            true
        )


    }

    private fun callApi() {
        if (ReusedMethod.isNetworkConnected(requireActivity())) {
            if (!SharedPreferenceManager.getBoolean(AppConstants.IS_LOGIN_ONCE, false)) {
                mViewModel.getUserDetials(true, requireActivity() as BaseActivity)
            }
            mViewModel.getuserHomeBanners(true, requireActivity() as BaseActivity)
        } else {
            mBinding.noInternetUserHomeFrag.llNointernet.visible()
            mBinding.noDataUserHomeFrag.gone()
            mBinding.cl.gone()
        }
    }


    private fun setAdapter() {
        bannerAdsPager = BannerAdsPager(requireActivity(), array, object
            : BannerAdsPager.onItemClicklisteners {
            override fun onItemClick(position: Int) {
                redirectUrl(requireActivity(), array[position].url)
            }

        })
        mBinding.pager.adapter = bannerAdsPager
    }

    override fun postInit() {

    }


    override fun onResume() {
        super.onResume()
//        context?.startService(Intent(requireContext(), BackgroundService::class.java))
        mBinding.noInternetUserHomeFrag.llNointernet.gone()
        mBinding.noDataUserHomeFrag.gone()
        mBinding.cl.visible()

        changeLayout(SharedPreferenceManager.getInt(AppConstants.EXTRA_SH_USER_HOME, 1))
        SharedPreferenceManager.putInt(AppConstants.EXTRA_SH_RECORD_POLICE_INTERACTION, 0)
        SharedPreferenceManager.putInt(AppConstants.EXTRA_SH_RECORD_POLICE_INTERACTION_2, 0)
        SharedPreferenceManager.putInt(AppConstants.EXTRA_SH_LIVE_VIRTUAL_WITNESS, 0)
        SharedPreferenceManager.putInt(AppConstants.EXTRA_SH_SCHEDUAL_VIRTUAL_WITNESS, 0)
        SharedPreferenceManager.putInt(AppConstants.EXTRA_SH_CONTACT_SUPPORT, 0)
        SharedPreferenceManager.putInt(AppConstants.EXTRA_SH_SUPPORT_GROUP_LIST, 0)
//        if(SharedPreferenceManager.getInt(AppConstants.EXTRA_SH_USER_HOME, 1) == 0)
//        {
//            changeLayout(1)
//        }
        if (SharedPreferenceManager.getBoolean(AppConstants.IS_OFFLINE_VIDEO_UPLOAD, false)) {
            IntentFilter().apply {
                addAction("android.intent.action.CUSTOM_ACTION")
                requireActivity().registerReceiver(ConnectivityChangeReceiver(), this)
            }
            val i = Intent()
            i.action = "android.intent.action.CUSTOM_ACTION"
            requireActivity().sendBroadcast(i)
        }
    }

    override fun handleListener() {
        mBinding.cvRecord.setOnClickListener(this)
        mBinding.cvScheduleVirtualWitness.setOnClickListener(this)
        mBinding.cvSupportService.setOnClickListener(this)
        mBinding.rbRecord.setOnClickListener(this)
        mBinding.rbScheduleVirtualWitness.setOnClickListener(this)
        mBinding.rbSupportService.setOnClickListener(this)
        mBinding.noInternetUserHomeFrag.btnTryAgain.setOnClickListener(this)
        mBinding.txtViewMore.setOnClickListener(this)
    }

    override fun initObserver() {
        mViewModel.getuserHomeBannerResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            showSubscriptionDialog(requireActivity(), data)
                            array.clear()
                            bannerArray.clear()
                            array.addAll(data.top5)
                            bannerArray.addAll(data.bannerCollection)
                            if (data.bannerCollection.isNullOrEmpty()) {
                                mBinding.txtViewMore.gone()
                            }
                            if (data.top5.isNullOrEmpty()) {
                                mBinding.cl1.gone()
                            } else {
                                mBinding.cl1.visible()
                            }

                            bannerAdsPager?.notifyDataSetChanged()
                            if (array.size > 1) {
                                viewPagerScroll(mBinding.pager, array.size)
                            }
                        } else {
                            mBinding.cl.gone()
                            mBinding.noDataUserHomeFrag.visible()
                            mBinding.noInternetUserHomeFrag.llNointernet.gone()
                        }

                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            ReusedMethod.displayMessage(
                                requireActivity(),
                                getString(R.string.text_error_network)
                            )

                        Config.CUSTOM_ERROR ->
                            errorObj.customMessage
                                ?.let {
                                    if (errorObj.code == ApiConstant.API_401) {
                                        ReusedMethod.displayMessage(requireActivity(), it)
                                        (activity as HomeActivity).unAuthorizedNavigation()
                                    } else {
                                        ReusedMethod.displayMessage(requireActivity(), it)
                                    }
                                }
                    }
                }
            }
        }
        //GET USER DETAILS RESP
        mViewModel.getuserDetailsResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            val gson = Gson()
                            val json = gson.toJson(data)
                            SharedPreferenceManager.putString(
                                AppConstants.USER_DETAIL_LOGIN,
                                json
                            )
                            SharedPreferenceManager.putBoolean(AppConstants.IS_LOGIN_ONCE, true)
                        }
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            ReusedMethod.displayMessage(
                                requireActivity(),
                                getString(R.string.text_error_network)
                            )

                        Config.CUSTOM_ERROR ->
                            errorObj.customMessage
                                ?.let {
                                    if (errorObj.code == ApiConstant.API_401) {
                                        startActivity(
                                            Intent(
                                                requireContext(),
                                                LoginActivity::class.java
                                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        )
                                        requireActivity().overridePendingTransition(
                                            R.anim.rightto,
                                            R.anim.left
                                        )
                                    } else {
                                        ReusedMethod.displayMessage(this as Activity, it)
                                    }
                                }
                    }
                }
            }
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cvRecord -> {
                changeLayout(1)
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    RecordPoliceInteractionFragment(),
                    true,
                    HomeActivity::class.java.name,
                    HomeActivity::class.java.name
                );
            }
            R.id.cvScheduleVirtualWitness -> {
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    ScheduleVirtualWitnessFragment(),
                    true,
                    HomeActivity::class.java.name,
                    HomeActivity::class.java.name
                );
                changeLayout(2)
            }
            R.id.cvSupportService -> {
                changeLayout(3)
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    ContactSupportFragment(),
                    true,
                    HomeActivity::class.java.name,
                    HomeActivity::class.java.name
                );
            }
            R.id.rbRecord -> {
                changeLayout(1)
                mBinding.cvRecord.performClick()
            }
            R.id.rbScheduleVirtualWitness -> {
                changeLayout(2)
                mBinding.cvScheduleVirtualWitness.performClick()
            }
            R.id.rbSupportService -> {
                changeLayout(3)
                mBinding.cvSupportService.performClick()
            }
            R.id.btnTryAgain -> {
                callApi()
            }
            R.id.txtViewMore -> {
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    HomeBannersFragment(bannerArray),
                    true,
                    HomeActivity::class.java.name,
                    HomeActivity::class.java.name
                );
            }
        }
    }

    private fun changeLayout(i: Int) {
        SharedPreferenceManager.putInt(AppConstants.EXTRA_SH_USER_HOME, i)
        when (i) {
            0 -> {
                mBinding.rlRecord.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlScheduleVirtualWitness.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlSupportService.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rbRecord.isChecked = false
                mBinding.rbScheduleVirtualWitness.isChecked = false
                mBinding.rbSupportService.isChecked = false

            }
            1 -> {
                mBinding.rlRecord.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.rlScheduleVirtualWitness.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlSupportService.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rbRecord.isChecked = true
                mBinding.rbScheduleVirtualWitness.isChecked = false
                mBinding.rbSupportService.isChecked = false


            }
            2 -> {
                mBinding.rlRecord.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlScheduleVirtualWitness.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.rlSupportService.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rbRecord.isChecked = false
                mBinding.rbScheduleVirtualWitness.isChecked = true
                mBinding.rbSupportService.isChecked = false
            }
            3 -> {
                mBinding.rlRecord.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlScheduleVirtualWitness.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlSupportService.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.rbRecord.isChecked = false
                mBinding.rbScheduleVirtualWitness.isChecked = false
                mBinding.rbSupportService.isChecked = true
            }

        }
    }

}