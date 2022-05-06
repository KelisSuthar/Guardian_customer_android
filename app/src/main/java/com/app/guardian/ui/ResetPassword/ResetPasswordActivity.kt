package com.app.guardian.ui.ResetPassword

import android.content.Intent
import android.view.View
import com.app.guardian.R
import com.app.guardian.common.IntegratorImpl
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.ReusedMethod.Companion.ShowNoBorders
import com.app.guardian.common.ReusedMethod.Companion.ShowRedBorders
import com.app.guardian.common.ReusedMethod.Companion.isNetworkConnected
import com.app.guardian.common.ValidationView
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.ActivityResetPasswordBinding
import com.app.guardian.model.viewModels.AuthenticationViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.Login.LoginActivity
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel

class ResetPasswordActivity : BaseActivity(), View.OnClickListener {
    private val mViewModel: AuthenticationViewModel by viewModel()
    lateinit var mBinding: ActivityResetPasswordBinding
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_reset_password
    }

    override fun initView() {
        mBinding = getBinding()
//        mBinding.headderResetPass.ivBack.gone()
        mBinding.headderResetPass.tvHeaderText.text = resources.getString(R.string.reset_password)
    }

    override fun initObserver() {
        mViewModel.getResetPAssResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            startActivity(
                                Intent(
                                    this@ResetPasswordActivity,
                                    LoginActivity::class.java
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
        mBinding.headderResetPass.ivBack.setOnClickListener(this)
        mBinding.btnResetPasswordSubmit.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                onBackPressed()
                overridePendingTransition(R.anim.leftto, R.anim.right)
            }
            R.id.btnResetPasswordSubmit -> {
                validations()
            }
        }

    }

    private fun validations() {
        IntegratorImpl.isValidResetPass(
            mBinding.editTextNewPass.text?.trim().toString(),
            mBinding.editTextConPass.text?.trim().toString(),
            object : ValidationView.RestPass {
                override fun passwordMinValidation() {
                    ShowRedBorders(this@ResetPasswordActivity, mBinding.editTextNewPass)
                }

                override fun passwordSpecialValidation() {
                    ShowRedBorders(this@ResetPasswordActivity, mBinding.editTextNewPass)
                }

                override fun con_passwordMinValidation() {
                    ShowRedBorders(this@ResetPasswordActivity, mBinding.editTextConPass)
                }

                override fun con_passwordSpecialValidation() {
                    ShowRedBorders(this@ResetPasswordActivity, mBinding.editTextConPass)
                }

                override fun comparePass() {
                    ShowRedBorders(this@ResetPasswordActivity, mBinding.editTextConPass)
                    ShowRedBorders(this@ResetPasswordActivity, mBinding.editTextNewPass)
                }

                override fun success() {
                    ShowNoBorders(this@ResetPasswordActivity, mBinding.editTextConPass)
                    ShowNoBorders(this@ResetPasswordActivity, mBinding.editTextNewPass)
                    callApi()
                }
            })
    }

    private fun callApi() {
        if (isNetworkConnected(this)) {
            mViewModel.resetPassword(
                true,
                this,
                mBinding.editTextConPass.text?.trim().toString(),
                mBinding.editTextConPass.text?.trim().toString(),
                ""
            )
        } else {
            mBinding.noInternetoginResetPass.llNointernet.visible()
            mBinding.noDataoginResetPass.gone()
            mBinding.nsResetPass.gone()
        }
    }

}