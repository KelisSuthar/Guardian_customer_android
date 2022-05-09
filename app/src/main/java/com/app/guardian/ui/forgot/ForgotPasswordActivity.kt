package com.app.guardian.ui.forgot

import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.IntegratorImpl
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.ReusedMethod.Companion.isNetworkConnected
import com.app.guardian.common.ValidationView
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.ActivityForgotPasswordBinding
import com.app.guardian.model.viewModels.AuthenticationViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.OTP.OTPScreen
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel

class ForgotPasswordActivity : BaseActivity(), View.OnClickListener {
    private val mViewModel: AuthenticationViewModel by viewModel()
    lateinit var mBinding: ActivityForgotPasswordBinding
    var isEmail = true
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
                    ReusedMethod.changePhoneEmailState(
                        this,
                        isEmail,
                        mBinding.emailphoneSelector.edtLoginEmail,
                        mBinding.emailphoneSelector.ccp
                    )
                }
                R.id.rbPhone -> {
                    isEmail = false
                    ReusedMethod.changePhoneEmailState(
                        this,
                        isEmail,
                        mBinding.emailphoneSelector.edtLoginEmail,
                        mBinding.emailphoneSelector.ccp
                    )
                }
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.leftto, R.anim.right)
    }

    override fun initObserver() {
        mViewModel.getForgotPassResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            startActivity(
                                Intent(
                                    this@ForgotPasswordActivity,
                                    OTPScreen::class.java
                                ).putExtra(AppConstants.EXTRA_OTP, data.otp.toString())
                                    .putExtra(AppConstants.EXTRA_IS_EMAIL, isEmail)
                                    .putExtra(
                                        AppConstants.EXTRA_EMAIL_PHONE,
                                        mBinding.emailphoneSelector.edtLoginEmail.text?.trim()
                                            .toString()
                                    )
                            )
                            overridePendingTransition(R.anim.rightto, R.anim.left)
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
        mBinding.headdeForgotPass.ivBack.setOnClickListener(this)
        mBinding.btnSubmit.setOnClickListener(this)
        mBinding.txtDoNotHaveAccount.setOnClickListener(this)
        mBinding.noInternetoginForgotPass.btnTryAgain.setOnClickListener(this)
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
            R.id.txtDoNotHaveAccount -> {
                finish()
                onBackPressed()
                overridePendingTransition(R.anim.leftto, R.anim.right)
            }
            R.id.btnTryAgain -> {
                if (isEmail) {
                    callApi(mBinding.emailphoneSelector.edtLoginEmail.text?.trim().toString())
                } else {
                    callApi(
                        mBinding.emailphoneSelector.ccp.selectedCountryCode.toString() + mBinding.emailphoneSelector.edtLoginEmail.text?.trim()
                            .toString()
                    )
                }
            }
        }
    }

    private fun valiations() {
        IntegratorImpl.isValidForgotPass(isEmail,
            mBinding.emailphoneSelector.edtLoginEmail.text?.trim().toString(),
            object : ValidationView.ForgotPass {
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

                    if (isEmail) {
                        callApi(mBinding.emailphoneSelector.edtLoginEmail.text?.trim().toString())
                    } else {
                        callApi(
                            mBinding.emailphoneSelector.ccp.selectedCountryCode.toString() + mBinding.emailphoneSelector.edtLoginEmail.text?.trim()
                                .toString()
                        )
                    }
                }

            })
    }

    private fun callApi(phone_email: String) {
        if (isNetworkConnected(this)) {
            mViewModel.forgotPass(
                true, this, isEmail,
                phone_email
            )

        } else {
            mBinding.noInternetoginForgotPass.llNointernet.visible()
            mBinding.nsForgotPass.gone()
            mBinding.noDataoginForgotPass.gone()
        }
    }
}