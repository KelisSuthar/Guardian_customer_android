package com.app.guardian.ui.OTP

import `in`.aabhasjindal.otptextview.OTPListener
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.ActivityOtpscreenBinding
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.ResetPassword.ResetPasswordActivity

class OTPScreen : BaseActivity(), View.OnClickListener {
    lateinit var mBinding: ActivityOtpscreenBinding
    var OTP = ""
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_otpscreen
    }

    override fun initView() {
        mBinding = getBinding()
        checkInputData()
    }

    private fun checkInputData() {
        mBinding.otpTextView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
            }

            override fun onOTPComplete(otp: String) {
                OTP = otp
                mBinding.otpTextView.resetState()
            }
        }
    }

    override fun initObserver() {

    }

    override fun handleListener() {
        mBinding.btnVerifyOTP.setOnClickListener(this)
        mBinding.headderOTP.ivBack.setOnClickListener(this)
        mBinding.txtResendOTP.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.txtResendOTP -> {
                callResendOTPAPI()
            }
            R.id.ivBack -> {
                onBackPressed()
                overridePendingTransition(R.anim.leftto, R.anim.right)
            }
            R.id.btnVerifyOTP -> {
                when {
                    OTP.length < 4 -> {
                        mBinding.otpTextView.showError()
                    }
                    else -> {
                        callVerifyOtpApi()
                    }
                }
            }
        }
    }

    private fun callResendOTPAPI() {
        if (ReusedMethod.isNetworkConnected(this)) {
            startActivity(
                Intent(
                    this@OTPScreen,
                    ResetPasswordActivity::class.java
                )
            )
            overridePendingTransition(R.anim.rightto, R.anim.left)
        } else {

        }

    }

    private fun callVerifyOtpApi() {
        if (ReusedMethod.isNetworkConnected(this)) {

        } else {
            mBinding.clOtp.gone()
            mBinding.noInternetOTP.visible()
            mBinding.noDataOTP.gone()
        }
    }
}