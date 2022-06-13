package com.app.guardian.ui.User.settings

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.view.View
import android.view.Window
import android.widget.TextView
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.databinding.FragmentSettingsBinding
import com.app.guardian.model.viewModels.AuthenticationViewModel
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.termsandcondtions.TermAndConditionsActivity
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.Lawyer.AddBaner.AddBannerFragment
import com.app.guardian.ui.LawyerSpecialization.LawyerSpecializationFragment
import com.app.guardian.ui.Login.LoginActivity
import com.app.guardian.ui.ResetPassword.ResetPasswordActivity
import com.app.guardian.ui.SubscriptionPlan.SubScriptionPlanScreen
import com.app.guardian.ui.SupportGroups.SupportGroupList
import com.app.guardian.ui.aboutus.AboutUsActivity
import com.app.guardian.ui.editProfile.EditProfileActivity
import com.app.guardian.ui.notification.NotificationListFragment
import com.app.guardian.ui.virtualWitness.VirtualWitnessFragment
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import org.koin.android.viewmodel.ext.android.viewModel


class SettingsFragment : BaseFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentSettingsBinding
    private val mViewModel: CommonScreensViewModel by viewModel()
    private val authViewModel: AuthenticationViewModel by viewModel()
    override fun getInflateResource(): Int {
        return R.layout.fragment_settings

    }

    override fun initView() {
        mBinding = getBinding()
        setViews()

        Glide.with(requireActivity())
            .load(SharedPreferenceManager.getUser()?.user?.profile_avatar)
            .placeholder(R.drawable.profile)
            .into(mBinding.imgProfile)
        mBinding.txtUName.text = SharedPreferenceManager.getUser()?.user?.full_name
    }

    private fun setViews() {
        (activity as HomeActivity).bottomTabVisibility(true)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.menu_setting),
            true,
            false
        )

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

    override fun onResume() {
        super.onResume()
        callCMSAPI()
    }

    private fun callCMSAPI() {
        if (ReusedMethod.isNetworkConnected(requireActivity())) {
            mViewModel.getCMSData(true, context as BaseActivity)
        } else {
            ReusedMethod.displayMessage(
                requireActivity(),
                resources.getString(R.string.text_error_network)
            )
        }
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
        mBinding.tvNotification.setOnClickListener(this)
        mBinding.tvSubscription2.setOnClickListener(this)
        mBinding.tvSpecialization.setOnClickListener(this)
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
                                    )
                                )
                                requireActivity().overridePendingTransition(
                                    R.anim.rightto,
                                    R.anim.left
                                )
                            } else {
                                ReplaceFragment.replaceFragment(
                                    requireActivity(),
                                    AddBannerFragment(),
                                    true,
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

        //CMS DATA RESP
        mViewModel.getCMSResp().observe(this) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            val gson = Gson()
                            val json = gson.toJson(data)
                            SharedPreferenceManager.putString(AppConstants.CMS_DETAIL, json)
                        } else {
                            ReusedMethod.displayMessage(
                                requireActivity(),
                                it.message.toString()
                            )
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
        //Logout Resp
        authViewModel.getSignOutResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            SharedPreferenceManager.removeAllData()

                            startActivity(
                                Intent(
                                    requireActivity(),
                                    LoginActivity::class.java
                                )
                            )
                            requireActivity().overridePendingTransition(R.anim.rightto, R.anim.left)
                            requireActivity().finish()
                            ReusedMethod.displayMessage(requireActivity(), it.message.toString())
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
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvSubScription -> {
                startActivity(Intent(context, SubScriptionPlanScreen::class.java))
                requireActivity().overridePendingTransition(R.anim.rightto, R.anim.left)
            }
            R.id.tvSubscription2 -> {
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
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    VirtualWitnessFragment(),
                    true,
                    SettingsFragment::class.java.name,
                    SettingsFragment::class.java.name
                );
            }
            R.id.tvChangePwd -> {
                startActivity(
                    Intent(context, ResetPasswordActivity::class.java)
                        .putExtra(AppConstants.IS_CHANGE_PASS, true)
                )
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
//                ReplaceFragment.replaceFragment(
//                    requireActivity(),
//                    ContectedHistoryFragment(),
//                    false,
//                    "",
//                    HomeActivity::class.java.name
//                )
//                requireActivity().overridePendingTransition(R.anim.rightto, R.anim.left)
                (activity as HomeActivity).historyPageOpen()

            }
            R.id.btnSignOut -> {
                logoutDialog()

            }
            R.id.tvNotification -> {
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    NotificationListFragment(),
                    true,
                    "",
                    HomeActivity::class.java.name
                )
                requireActivity().overridePendingTransition(R.anim.rightto, R.anim.left)

            }
            R.id.tvSpecialization -> {
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    LawyerSpecializationFragment(),
                    true,
                    "",
                    HomeActivity::class.java.name
                )
                requireActivity().overridePendingTransition(R.anim.rightto, R.anim.left)

            }
        }


    }

    private fun logoutDialog() {
        val dialog = Dialog(
            requireActivity(),
            com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_layout)
        dialog.setCancelable(false)

        val OK = dialog.findViewById<MaterialTextView>(R.id.tvPositive)
        val TITLE = dialog.findViewById<TextView>(R.id.tvTitle)
        val MESSAGE = dialog.findViewById<TextView>(R.id.tvMessage)
        val CANCEL = dialog.findViewById<MaterialTextView>(R.id.tvNegative)
        TITLE.text = resources.getString(R.string.want_to_signout)
        MESSAGE.gone()

        OK.text = "Yes"

        CANCEL.text = "No"

        CANCEL.setOnClickListener {
            dialog.dismiss()
        }
        OK.setOnClickListener {
            callSignoutDialog(dialog)
        }
        dialog.show()
    }

    private fun callSignoutDialog(dialog: Dialog) {
        if (ReusedMethod.isNetworkConnected(requireActivity())) {
            authViewModel.signOUT(true, context as BaseActivity)
            dialog.dismiss()
        } else {
            ReusedMethod.displayMessage(requireActivity(), getString(R.string.text_error_network))
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