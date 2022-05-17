package com.app.guardian.ui.OTP

import `in`.aabhasjindal.otptextview.OTPListener
import android.content.Intent
import android.view.View
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.ActivityOtpscreenBinding
import com.app.guardian.model.viewModels.AuthenticationViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.ResetPassword.ResetPasswordActivity
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel

class OTPScreen : BaseActivity(), View.OnClickListener {
    private val mViewModel: AuthenticationViewModel by viewModel()
    lateinit var mBinding: ActivityOtpscreenBinding
    var OTP = ""
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_otpscreen
    }

    override fun initView() {
        mBinding = getBinding()
        checkInputData()
        if (intent.extras != null || intent != null) {
            mBinding.otpTextView.otp = intent.getStringExtra(AppConstants.EXTRA_OTP)
        }
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

        //VerifyOTP RESP
        mViewModel.getVerifyOTPResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {

                            finish()
                            startActivity(
                                Intent(
                                    this@OTPScreen,
                                    ResetPasswordActivity::class.java
                                ).putExtra(AppConstants.EXTRA_USER_ID,data.id.toString())
                            )
                            overridePendingTransition(R.anim.rightto, R.anim.left)
                            ReusedMethod.displayMessage(this, it.message.toString())
                        }else{
                            ReusedMethod.displayMessage(this, it.message.toString())
                        }
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            ReusedMethod.displayMessage(
                                this,
                                getString(R.string.text_error_network)
                            )

                        Config.CUSTOM_ERROR ->
                            errorObj.customMessage
                                ?.let { }
                    }
                }
            }
        }
        //RESEND OTP RESP
        mViewModel.getForgotPassResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            mBinding.otpTextView.otp = data.otp.toString()
                        }else{
                            ReusedMethod.displayMessage(this, it.message.toString())
                        }
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            ReusedMethod.displayMessage(
                                this,
                                getString(R.string.text_error_network)
                            )

                        Config.CUSTOM_ERROR ->
                            errorObj.customMessage
                                ?.let { ReusedMethod.displayMessage(this, it) }
                    }
                }
            }
        }
    }

    override fun handleListener() {
        mBinding.btnVerifyOTP.setOnClickListener(this)
        mBinding.headderOTP.ivBack.setOnClickListener(this)
        mBinding.txtResendOTP.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.txtResendOTP -> {
                if (intent.getBooleanExtra(AppConstants.EXTRA_IS_EMAIL, false)) {
                    callResendOTPAPI(intent.getStringExtra(AppConstants.EXTRA_EMAIL_PHONE)!!)
                } else {
                    callResendOTPAPI(
                        intent.getStringExtra(AppConstants.EXTRA_CCP) + intent.getStringExtra(
                            AppConstants.EXTRA_EMAIL_PHONE
                        )
                    )
                }

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

    private fun callResendOTPAPI(email_phone: String) {
        if (ReusedMethod.isNetworkConnected(this)) {
            mViewModel.forgotPass(
                true, this, intent.getBooleanExtra(AppConstants.EXTRA_IS_EMAIL, false),
                intent.getStringExtra(AppConstants.EXTRA_CCP).toString(),
                email_phone
            )

        } else {
            mBinding.noInternetOTP.llNointernet.visible()
            mBinding.clOtp.gone()
            mBinding.noDataOTP.gone()
        }
    }

    private fun callVerifyOtpApi() {
        if (ReusedMethod.isNetworkConnected(this)) {
            mViewModel.verifyOTP(
                true,
                this,
                intent.getBooleanExtra(AppConstants.EXTRA_IS_EMAIL, false),
                intent.getStringExtra(AppConstants.EXTRA_EMAIL_PHONE)!!,
                intent.getStringExtra(AppConstants.EXTRA_CCP)!!,
                mBinding.otpTextView.otp.toString(),
            )
        } else {
            mBinding.clOtp.gone()
            mBinding.noInternetOTP.llNointernet.visible()
            mBinding.noDataOTP.gone()
        }
    }
}