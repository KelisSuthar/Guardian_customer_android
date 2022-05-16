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
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.ActivityLoginBinding
import com.app.guardian.databinding.FragmentSettingsBinding
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.termsandcondtions.TermAndConditionsActivity
import com.app.guardian.ui.ResetPassword.ResetPasswordActivity
import com.app.guardian.ui.SubscriptionPlan.SubScriptionPlanScreen
import com.app.guardian.ui.aboutus.AboutUsActivity
import com.app.guardian.ui.editProfile.EditProfileActivity
import com.app.guardian.ui.virtualWitness.VirtualWitnessActivity


class SettingsFragment : BaseFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentSettingsBinding
    override fun getInflateResource(): Int {
        return R.layout.fragment_settings

    }

    override fun initView() {
        mBinding = getBinding()
        mBinding.headderSettitng.ivBack.visible()
        mBinding.headderSettitng.tvHeaderText.gone()
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
        mBinding.headderSettitng.ivBack.setOnClickListener(this)
    }

    override fun initObserver() {

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
            R.id.btnSignOut -> {


            }
        }


    }
}