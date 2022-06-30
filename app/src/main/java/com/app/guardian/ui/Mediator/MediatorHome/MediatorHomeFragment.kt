package com.app.guardian.ui.Mediator.MediatorHome

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentMediatorHomeBinding
import com.app.guardian.model.HomeBanners.BannerCollection
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.BannerAds.BannerAdsPager
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.HomeBanners.HomeBannersFragment
import com.app.guardian.ui.KnowRight.KnowRightFragment
import com.app.guardian.ui.LawyerList.LawyerListFragment
import com.app.guardian.ui.Login.LoginActivity
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import org.koin.android.viewmodel.ext.android.viewModel


class MediatorHomeFragment : BaseFragment(), View.OnClickListener {
    private val mViewModel: CommonScreensViewModel by viewModel()
    var array = ArrayList<BannerCollection>()
    var bannerArray = ArrayList<BannerCollection>()
    var bannerAdsPager: BannerAdsPager? = null
    lateinit var mBinding: FragmentMediatorHomeBinding
    override fun getInflateResource(): Int {
        return R.layout.fragment_mediator_home
    }

    override fun initView() {
        mBinding = getBinding()
        (activity as HomeActivity).bottomTabVisibility(true)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.lawyer_profile),
            isHeaderVisible = false,
            isBackButtonVisible = false
        )

        setAdapter()
        callApi()
        Log.e("EDIT_APP", SharedPreferenceManager.getUser().toString())
        mBinding.availabilitySwitch.setOnToggledListener { _, isOn ->
            showDialog(isOn)
        }
    }

    private fun showDialog(isOn: Boolean) {
        val dialog = ReusedMethod.setUpDialog(requireContext(), R.layout.dialog_layout, false)
        val OK = dialog.findViewById<MaterialTextView>(R.id.tvPositive)
        val TITLE = dialog.findViewById<TextView>(R.id.tvTitle)
        val MESSAGE = dialog.findViewById<TextView>(R.id.tvMessage)
        val CANCEL = dialog.findViewById<MaterialTextView>(R.id.tvNegative)
        if (isOn) {
            TITLE.text = "Are you sure want to Active?"
        } else {
            TITLE.text = "Are you sure want to De-Active?"
        }

        MESSAGE.gone()
        CANCEL.text = "No"
        OK.text = "Yes"
        CANCEL.isAllCaps = false
        OK.isAllCaps = false
        CANCEL.setOnClickListener {
            dialog.dismiss()
            mBinding.availabilitySwitch.isOn = !isOn
        }
        OK.setOnClickListener {
            dialog.dismiss()
            if (isOn) {
                callChangeStatusAPI(1)
            } else {
                callChangeStatusAPI(0)

            }
        }
        dialog.show()
    }

    private fun callChangeStatusAPI(i: Int) {
        if (ReusedMethod.isNetworkConnected(requireActivity())) {
            mViewModel.setAppUserStatus(true, requireActivity() as BaseActivity, i.toString())
        } else {
            mBinding.noInternetUserHomeFrag.llNointernet.visible()
            mBinding.noDataUserHomeFrag.gone()
            mBinding.cl.gone()
        }
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
                ReusedMethod.redirecttoUrl(requireContext(),array[position].url)
            }

        })
        mBinding.pager.adapter = bannerAdsPager
    }

    override fun onResume() {
        super.onResume()
        mBinding.noInternetUserHomeFrag.llNointernet.gone()
        mBinding.noDataUserHomeFrag.gone()
        mBinding.cl.visible()

        chnagelayout(SharedPreferenceManager.getInt(AppConstants.EXTRA_SH_MEDIATOR_HOME, 1))


    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.cvDialLawyer.setOnClickListener(this)
        mBinding.cvKnowBasicRight.setOnClickListener(this)
        mBinding.rbDialLawyer.setOnClickListener(this)
        mBinding.rbKnowBasicRight.setOnClickListener(this)
        mBinding.txtViewMore.setOnClickListener(this)
    }

    override fun initObserver() {
        mViewModel.getuserHomeBannerResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        SharedPreferenceManager.putBoolean(AppConstants.IS_SUBSCRIBE, true)

                        if (it.status) {
                            mBinding.availabilitySwitch.isOn = data.is_online == 1
                            array.clear()
                            array.addAll(data.top5)
                            bannerAdsPager?.notifyDataSetChanged()
                            bannerArray.clear()
                            bannerArray.addAll(data.bannerCollection)
                            if (data.bannerCollection.isNullOrEmpty()) {
                                mBinding.txtViewMore.gone()
                            }
                            if (array.size > 1) {
                                ReusedMethod.viewPagerScroll(mBinding.pager, array.size)
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
                                        ReusedMethod.displayMessage(context as Activity, it)
                                    }
                                }
                    }
                }
            }
        }
        //SET USER STATUS
        mViewModel.getCommonResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->


                        if (!it.status) {
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
                                ?.let { ReusedMethod.displayMessage(requireActivity(), it) }
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
            R.id.cvKnowBasicRight -> {
                chnagelayout(1)
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    KnowRightFragment(), true, "", MediatorHomeFragment::class.java.name
                )

            }
            R.id.cvDialLawyer -> {
                chnagelayout(2)

                (activity as HomeActivity).lawyerListPageOpen()

            }

            R.id.rbKnowBasicRight -> {
                chnagelayout(1)
                mBinding.cvKnowBasicRight.performClick()
            }
            R.id.rbDialLawyer -> {
                chnagelayout(2)
                mBinding.cvDialLawyer.performClick()
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

    private fun chnagelayout(i: Int) {
        SharedPreferenceManager.putInt(AppConstants.EXTRA_SH_MEDIATOR_HOME, i)
        when (i) {
            1 -> {
                mBinding.rlKnowBasicRight.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.rlDialLawyer.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rbKnowBasicRight.isChecked = true
                mBinding.rbDialLawyer.isChecked = false

                mBinding.rbKnowBasicRight.buttonTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))

                mBinding.rbDialLawyer.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimaryDark
                        )
                    )
            }
            2 -> {
                mBinding.rlKnowBasicRight.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )

                mBinding.rlDialLawyer.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.rbKnowBasicRight.isChecked = false
                mBinding.rbDialLawyer.isChecked = true
                mBinding.rbKnowBasicRight.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimaryDark
                        )
                    )

                mBinding.rbDialLawyer.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
            }
            0 -> {
                mBinding.rlKnowBasicRight.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )

                mBinding.rlDialLawyer.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rbKnowBasicRight.isChecked = false
                mBinding.rbDialLawyer.isChecked = false
                mBinding.rbKnowBasicRight.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimaryDark
                        )
                    )

                mBinding.rbDialLawyer.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimaryDark
                        )
                    )
            }

        }
    }

}