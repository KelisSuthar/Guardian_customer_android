package com.app.guardian.ui.forgot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.app.guardian.R
import com.app.guardian.common.IntegratorImpl
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.ReusedMethod.Companion.ShowNoBorders
import com.app.guardian.common.ReusedMethod.Companion.ShowRedBorders
import com.app.guardian.common.ReusedMethod.Companion.isNetworkConnected
import com.app.guardian.common.ValidationView
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.ActivityForgotPasswordBinding
import com.app.guardian.databinding.ActivityOtpscreenBinding
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.OTP.OTPScreen

class ForgotPasswordActivity : BaseActivity(), View.OnClickListener {
    lateinit var mBinding: ActivityForgotPasswordBinding
    var isEmail = false
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_forgot_password
    }

    override fun initView() {
        mBinding = getBinding()
        setPhoneEmailSelector()
    }
    private fun setPhoneEmailSelector() {
        mBinding.emailphoneSelector.rgLogin.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {
                R.id.rbEmail -> {
                    isEmail = true
                    mBinding.emailphoneSelector.rbPhone.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.white
                        )
                    )
                    mBinding.emailphoneSelector.rbEmail.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.black
                        )
                    )
                    mBinding.emailphoneSelector.ccp.gone()

                    mBinding.emailphoneSelector.edtLoginEmail.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_mail,
                        0,
                        0,
                        0
                    )
                    mBinding.emailphoneSelector.edtLoginEmail.compoundDrawablePadding =
                        resources.getDimension(com.intuit.sdp.R.dimen._10sdp).toInt()
                    mBinding.emailphoneSelector.edtLoginEmail.setText("")
                }
                R.id.rbPhone -> {
                    isEmail = false
                    mBinding.emailphoneSelector.rbPhone.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.black
                        )
                    )
                    mBinding.emailphoneSelector.rbEmail.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.white
                        )
                    )
                    mBinding.emailphoneSelector.ccp.visible()
                    mBinding.emailphoneSelector.edtLoginEmail.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        0,
                        0
                    );
                    mBinding.emailphoneSelector.edtLoginEmail.setText("")

                }
            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.leftto, R.anim.right)
    }

    override fun initObserver() {

    }

    override fun handleListener() {
        mBinding.headdeForgotPass.ivBack.setOnClickListener(this)
        mBinding.btnSubmit.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnSubmit -> {
                valiations()
            }
            R.id.ivBack -> {
                onBackPressed()
                overridePendingTransition(R.anim.leftto, R.anim.right)
            }
        }
    }

    private fun valiations() {
        IntegratorImpl.isValidForgotPass(isEmail,
            mBinding.emailphoneSelector.edtLoginEmail.text?.trim().toString(),object :ValidationView.ForgotPass{
            override fun emailValidation() {
                mBinding.emailphoneSelector.cl1.background = ContextCompat.getDrawable(
                    this@ForgotPasswordActivity,
                    R.drawable.normal_rounded_light_blue_red_borders
                )
            }

            override fun phoneValidation() {
                mBinding.emailphoneSelector.cl1.background = ContextCompat.getDrawable(
                    this@ForgotPasswordActivity,
                    R.drawable.normal_rounded_light_blue_red_borders
                )
            }

            override fun success() {
                mBinding.emailphoneSelector.cl1.background = ContextCompat.getDrawable(
                    this@ForgotPasswordActivity,
                    R.drawable.normal_rounded_light_blue_red_borders
                )
                callApi()
            }

        })
    }

    private fun callApi() {
        if(isNetworkConnected(this)){
            startActivity(
                Intent(
                    this@ForgotPasswordActivity,
                    OTPScreen::class.java
                )
            )
            overridePendingTransition(R.anim.rightto, R.anim.left)
        }else{
            mBinding.noInternetoginForgotPass.visible()
            mBinding.nsForgotPass.gone()
            mBinding.noDataoginForgotPass.gone()
        }
    }
}