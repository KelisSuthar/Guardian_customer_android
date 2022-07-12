package com.app.guardian.ui.Lawyer.LawyerHome

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
import com.app.guardian.common.ReusedMethod.Companion.displayMessage
import com.app.guardian.common.ReusedMethod.Companion.showSubscriptionDialog
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentLawyerHomeBinding
import com.app.guardian.model.HomeBanners.BannerCollection
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.BannerAds.BannerAdsPager
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.HomeBanners.HomeBannersFragment
import com.app.guardian.ui.KnowRight.KnowRightFragment
import com.app.guardian.ui.Lawyer.AskMoreQuestion.AskMoreQuestion
import com.app.guardian.ui.Login.LoginActivity
import com.app.guardian.ui.SeekLegalAdvice.SeekLegalAdviceListFragment
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import org.koin.android.viewmodel.ext.android.viewModel


class LawyerHomeFragment : BaseFragment(), View.OnClickListener {
    private val mViewModel: CommonScreensViewModel by viewModel()
    var array = ArrayList<BannerCollection>()
    var bannerArray = ArrayList<BannerCollection>()
    var bannerAdsPager: BannerAdsPager? = null
    lateinit var mBinding: FragmentLawyerHomeBinding
    override fun getInflateResource(): Int {
        return R.layout.fragment_lawyer_home
    }

    override fun initView() {
        mBinding = getBinding()
        (activity as HomeActivity).bottomTabVisibility(true)
        (activity as HomeActivity).headerTextVisible(
            "",
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
                ReusedMethod.redirectUrl(requireActivity(), array[position].url)
            }

        })
        mBinding.pager.adapter = bannerAdsPager
    }

    override fun onResume() {
        super.onResume()
        mBinding.noInternetUserHomeFrag.llNointernet.gone()
        mBinding.noDataUserHomeFrag.gone()
        mBinding.cl.visible()
        chnagelayout(SharedPreferenceManager.getInt(AppConstants.EXTRA_SH_LAWYER_HOME, 1))
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.cvKnowRights.setOnClickListener(this)
        mBinding.cvAskQuestion.setOnClickListener(this)
        mBinding.cvSeekAdv.setOnClickListener(this)
        mBinding.btnKnowRights.setOnClickListener(this)
        mBinding.btnAskQuestions.setOnClickListener(this)
        mBinding.btnSeekAdv.setOnClickListener(this)
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
                            mBinding.availabilitySwitch.isOn = data.is_online == 1
                            array.clear()
                            array.addAll(data.top5)
                            bannerAdsPager?.notifyDataSetChanged()
                            bannerArray.clear()
                            bannerArray.addAll(data.bannerCollection)
                            if (data.bannerCollection.isNullOrEmpty()) {
                                mBinding.txtViewMore.gone()
                            }
                            if (data.top5.isNullOrEmpty()) {
                                mBinding.cl1.gone()
                            } else {
                                mBinding.cl1.visible()
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
                            ReusedMethod.displayMessage(requireActivity(), it.message.toString())
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
            R.id.cvKnowRights -> {
                chnagelayout(1)
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    KnowRightFragment(),
                    true,
                    HomeActivity::class.java.name,
                    HomeActivity::class.java.name
                )
            }
            R.id.cvAskQuestion -> {
                chnagelayout(2)
//                ReplaceFragment.replaceFragment(
//                    requireActivity(),
//                    AskMoreQuestion(),
//                    true,
//                    LawyerHomeFragment::class.java.name,
//                    LawyerHomeFragment::class.java.name
//                )
                displayMessage(requireActivity(), resources.getString(R.string.come_soon))
            }
            R.id.cvSeekAdv -> {
                chnagelayout(3)
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    SeekLegalAdviceListFragment(
                        true,
                        SharedPreferenceManager.getUser()!!.id!!
                    ),
                    true,
                    HomeActivity::class.java.name,
                    HomeActivity::class.java.name
                )
            }
            R.id.btnKnowRights -> {
//                chnagelayout(1)
//                ReplaceFragment.replaceFragment(
//                    requireActivity(),
//                    KnowRightFragment(),
//                    true,
//                    "",
//                    HomeActivity::class.java.name
//                )
                mBinding.cvKnowRights.performClick()
            }
            R.id.btnAskQuestions -> {
                mBinding.cvAskQuestion.performClick()
            }
            R.id.btnSeekAdv -> {
//                ReplaceFragment.replaceFragment(
//                    requireActivity(),
//                    SeekLegalAdviceListFragment(true,SharedPreferenceManager.getUser()!!.user.id!!),
//                    false,
//                    "",
//                    HomeActivity::class.java.name
//                )
//                chnagelayout(3)
                mBinding.cvSeekAdv.performClick()
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
        SharedPreferenceManager.putInt(AppConstants.EXTRA_SH_LAWYER_HOME, i)
        when (i) {
            0 -> {
                mBinding.rlKnowRights.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlAskQuestions.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlSeekAdv.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.btnKnowRights.isChecked = false
                mBinding.btnAskQuestions.isChecked = false
                mBinding.btnSeekAdv.isChecked = false
                mBinding.btnKnowRights.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimaryDark
                        )
                    )
                mBinding.btnAskQuestions.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimaryDark
                        )
                    )
                mBinding.btnSeekAdv.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimaryDark
                        )
                    )
            }
            1 -> {
                mBinding.rlKnowRights.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.rlAskQuestions.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlSeekAdv.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.btnKnowRights.isChecked = true
                mBinding.btnAskQuestions.isChecked = false
                mBinding.btnSeekAdv.isChecked = false
                mBinding.btnKnowRights.buttonTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                mBinding.btnAskQuestions.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimaryDark
                        )
                    )
                mBinding.btnSeekAdv.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimaryDark
                        )
                    )
            }
            2 -> {
                mBinding.rlKnowRights.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlAskQuestions.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.rlSeekAdv.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.btnKnowRights.isChecked = false
                mBinding.btnAskQuestions.isChecked = true
                mBinding.btnSeekAdv.isChecked = false
                mBinding.btnKnowRights.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimaryDark
                        )
                    )
                mBinding.btnAskQuestions.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                mBinding.btnSeekAdv.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimaryDark
                        )
                    )
            }
            3 -> {
                mBinding.rlKnowRights.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlAskQuestions.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlSeekAdv.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.btnKnowRights.isChecked = false
                mBinding.btnAskQuestions.isChecked = false
                mBinding.btnSeekAdv.isChecked = true
                mBinding.btnKnowRights.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimaryDark
                        )
                    )
                mBinding.btnAskQuestions.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimaryDark
                        )
                    )
                mBinding.btnSeekAdv.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
            }
        }
    }


}