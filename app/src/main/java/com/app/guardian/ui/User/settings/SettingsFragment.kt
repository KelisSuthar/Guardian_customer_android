package com.app.guardian.ui.User.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.loadImage
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.ActivityLoginBinding
import com.app.guardian.databinding.FragmentSettingsBinding
import com.app.guardian.model.viewModels.AuthenticationViewModel
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.termsandcondtions.TermAndConditionsActivity
import com.app.guardian.ui.BannerAds.BannerAdsPager
import com.app.guardian.ui.ContactedHistory.ContectedHistoryFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.Lawyer.AddBaner.AddBannerFragment
import com.app.guardian.ui.Lawyer.LawyerHome.LawyerHomeFragment
import com.app.guardian.ui.Login.LoginActivity
import com.app.guardian.ui.Mediator.MediatorHome.MediatorHomeFragment
import com.app.guardian.ui.ResetPassword.ResetPasswordActivity
import com.app.guardian.ui.SubscriptionPlan.SubScriptionPlanScreen
import com.app.guardian.ui.User.UserHome.UserHomeFragment
import com.app.guardian.ui.aboutus.AboutUsActivity
import com.app.guardian.ui.editProfile.EditProfileActivity
import com.app.guardian.ui.virtualWitness.VirtualWitnessActivity
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel


class SettingsFragment : BaseFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentSettingsBinding
    private val mViewModel: CommonScreensViewModel by viewModel()
    override fun getInflateResource(): Int {
        return R.layout.fragment_settings

    }

    override fun initView() {
        mBinding = getBinding()
        setViews()
        mBinding.imgProfile.loadImage(SharedPreferenceManager.getUser()?.user?.profile_avatar)
        mBinding.txtUName.text = SharedPreferenceManager.getUser()?.user?.full_name
    }

    private fun setViews() {
        (activity as HomeActivity).bottomTabVisibility(true)
        (activity as HomeActivity).headerTextVisible(requireActivity().resources.getString(R.string.menu_setting),true,false)

        when {
            SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                AppConstants.APP_ROLE_USER
            ) == AppConstants.APP_ROLE_USER -> {
                mBinding.tvSeekLegaladv.gone()
                mBinding.imgSeekLegaladv.gone()
                mBinding.viewSeekLegaladv.gone()

                mBinding.imgSubscription2.gone()
                mBinding.tvSubscription2.gone()
                mBinding.viewSubscription2.gone()

                mBinding.tvBanneradds.gone()
                mBinding.imgBanneradds.gone()
                mBinding.viewBanneradds.gone()

            }
            SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                AppConstants.APP_ROLE_USER
            ) == AppConstants.APP_ROLE_LAWYER -> {
                mBinding.tvSubScription.gone()
                mBinding.imgSubscription.gone()
                mBinding.view.gone()

                mBinding.imgVirtualWitness.gone()
                mBinding.tvVirtualWitness.gone()
                mBinding.view6.gone()
            }
            SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                AppConstants.APP_ROLE_USER
            ) == AppConstants.APP_ROLE_MEDIATOR -> {
                mBinding.tvSubScription.gone()
                mBinding.imgSubscription.gone()
                mBinding.view.gone()

                mBinding.tvSeekLegaladv.gone()
                mBinding.imgSeekLegaladv.gone()
                mBinding.viewSeekLegaladv.gone()

                mBinding.imgVirtualWitness.gone()
                mBinding.tvVirtualWitness.gone()
                mBinding.view6.gone()

                mBinding.imgSubscription2.gone()
                mBinding.tvSubscription2.gone()
                mBinding.viewSubscription2.gone()

                mBinding.tvBanneradds.gone()
                mBinding.imgBanneradds.gone()
                mBinding.viewBanneradds.gone()
            }
        }
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.tvSubScription.setOnClickListener(this)
        mBinding.tvAbout.setOnClickListener(this)
        mBinding.tvTerms.setOnClickListener(this)
        mBinding.tvVirtualWitness.setOnClickListener(this)
        mBinding.tvChangePwd.setOnClickListener(this)
        mBinding.btnEditProfile.setOnClickListener(this)
        mBinding.btnSignOut.setOnClickListener(this)
//        mBinding.headderSettitng.ivBack.setOnClickListener(this)
        mBinding.tvBanneradds.setOnClickListener(this)
        mBinding.tvContactHistory.setOnClickListener(this)
    }

    override fun initObserver() {
        mViewModel.getcheckSubResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            if (data.is_subscribe == 0) {
                                startActivity(
                                    Intent(
                                        requireActivity(),
                                        SubScriptionPlanScreen::class.java
                                    ).putExtra(AppConstants.EXTRA_IS_LAWYER,true)
                                )
                                requireActivity().overridePendingTransition(
                                    R.anim.rightto,
                                    R.anim.left
                                )
                            } else {
                                ReplaceFragment.replaceFragment(
                                    requireActivity(),
                                    AddBannerFragment(),
                                    false,
                                    "",
                                    HomeActivity::class.java.name
                                )
                                requireActivity().overridePendingTransition(
                                    R.anim.rightto,
                                    R.anim.left
                                )
                            }

                        } else {
                            ReusedMethod.displayMessage(requireActivity(), it.message.toString())
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
                                ?.let {}
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvSubScription -> {
                startActivity(Intent(context, SubScriptionPlanScreen::class.java))
                requireActivity().overridePendingTransition(R.anim.rightto, R.anim.left)
            }
            R.id.tvAbout -> {
                startActivity(Intent(context, AboutUsActivity::class.java))
                requireActivity().overridePendingTransition(R.anim.rightto, R.anim.left)
            }
            R.id.tvTerms -> {
                startActivity(Intent(context, TermAndConditionsActivity::class.java))
                requireActivity().overridePendingTransition(R.anim.rightto, R.anim.left)
            }
            R.id.tvVirtualWitness -> {
                startActivity(Intent(context, VirtualWitnessActivity::class.java))
                requireActivity().overridePendingTransition(R.anim.rightto, R.anim.left)
            }
            R.id.tvChangePwd -> {
                startActivity(Intent(context, ResetPasswordActivity::class.java))
                requireActivity().overridePendingTransition(R.anim.rightto, R.anim.left)
            }
            R.id.btnEditProfile -> {
                startActivity(Intent(context, EditProfileActivity::class.java))
                requireActivity().overridePendingTransition(R.anim.rightto, R.anim.left)

            }
            R.id.tvBanneradds -> {
                callCheckSubscriptionApi()

            }
            R.id.tvContactHistory -> {
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    ContectedHistoryFragment(),
                    false,
                    "",
                    HomeActivity::class.java.name
                )
                requireActivity().overridePendingTransition(R.anim.rightto, R.anim.left)
            }
            R.id.btnSignOut -> {


            }
        }


    }

    private fun callCheckSubscriptionApi() {
        if (ReusedMethod.isNetworkConnected(requireActivity())) {
            mViewModel.checkSubscritpion(true, context as BaseActivity)
        } else {
            ReusedMethod.displayMessage(requireActivity(), getString(R.string.text_error_network))
        }
    }
}