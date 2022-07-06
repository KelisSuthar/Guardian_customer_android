package com.app.guardian.ui.ResetPassword

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.app.guardian.R
import com.app.guardian.common.*
import com.app.guardian.common.ReusedMethod.Companion.ShowNoBorders
import com.app.guardian.common.ReusedMethod.Companion.ShowRedBorders
import com.app.guardian.common.ReusedMethod.Companion.isNetworkConnected
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.ActivityResetPasswordBinding
import com.app.guardian.model.viewModels.AuthenticationViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.Login.LoginActivity
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import com.google.android.material.textfield.TextInputEditText
import org.koin.android.viewmodel.ext.android.viewModel

class ResetPasswordActivity : BaseActivity(), View.OnClickListener {
    private val mViewModel: AuthenticationViewModel by viewModel()
    private val authViewModel: AuthenticationViewModel by viewModel()

    lateinit var mBinding: ActivityResetPasswordBinding
    var is_ChangePass = false
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_reset_password
    }

    override fun initView() {
        mBinding = getBinding()
//        mBinding.headderResetPass.ivBack.gone()
        mBinding.headderResetPass.tvHeaderText.text = resources.getString(R.string.reset_password)

        if (intent != null && intent.extras != null) {
            is_ChangePass = intent.getBooleanExtra(AppConstants.IS_CHANGE_PASS, false)
            if (is_ChangePass) {
                mBinding.textInputLayout3.visible()
            }
        }
        setFocus(mBinding.editTextOldPass)
        setFocus(mBinding.editTextNewPass)
        setFocus(mBinding.editTextConPass)
    }

    private fun setFocus(editText: TextInputEditText) {
        editText.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val value = editText.text?.trim().toString()
                if (!hasFocus) {

                    if (value.length >= 8 && !TextUtils.isEmpty(value) && SmartUtils.checkSpecialPasswordValidation(
                            value
                        )
                    ) {
                        ShowNoBorders(this@ResetPasswordActivity, editText)
                    }
                }
            }

    }

    override fun initObserver() {
        mViewModel.getResetPAssResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            finish()
                            startActivity(
                                Intent(
                                    this@ResetPasswordActivity,
                                    LoginActivity::class.java
                                )
                            )
                            overridePendingTransition(R.anim.rightto, R.anim.left)
                            ReusedMethod.displayMessage(this, it.message.toString())
                        } else {
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
                                ?.let {
                                    if (errorObj.code == ApiConstant.API_401) {
                                        authViewModel.signOUT(true, this as BaseActivity)

                                        ReusedMethod.displayMessage(this, it)
                                        startActivity(
                                            Intent(
                                                this@ResetPasswordActivity,
                                                LoginActivity::class.java
                                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        )
                                        overridePendingTransition(R.anim.rightto, R.anim.left)
                                    } else {
                                        ReusedMethod.displayMessage(this as Activity, it)
                                    }
                                }
                    }
                }
            }
        }

        //Chnage Pass word resp
        mViewModel.getchangePassResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            onBackPressed()
                            overridePendingTransition(R.anim.leftto, R.anim.right)
                            ReusedMethod.displayMessage(this, it.message.toString())
                        } else {
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
                                ?.let {
                                    if (errorObj.code == ApiConstant.API_401) {
                                        authViewModel.signOUT(true, this as BaseActivity)
                                        startActivity(
                                            Intent(
                                                this@ResetPasswordActivity,
                                                LoginActivity::class.java
                                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        )
                                        overridePendingTransition(R.anim.rightto, R.anim.left)
                                    } else {
                                        ReusedMethod.displayMessage(this as Activity, it)

                                    }

                                }
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
            is_ChangePass,
            mBinding.editTextOldPass.text?.trim().toString(),
            mBinding.editTextNewPass.text?.trim().toString(),
            mBinding.editTextConPass.text?.trim().toString(),
            object : ValidationView.RestPass {
                override fun empty_old_pass() {
                    ReusedMethod.displayMessageDialog(
                        this@ResetPasswordActivity,
                        "",
                        resources.getString(R.string.empty_old_pass),
                        false,
                        "Ok",
                        ""
                    )
                    ShowRedBorders(this@ResetPasswordActivity, mBinding.editTextOldPass)
                }

                override fun oldpasswordMinValidation() {
                    ReusedMethod.displayMessageDialog(
                        this@ResetPasswordActivity,
                        "",
                        resources.getString(R.string.valid_old_pass),
                        false,
                        "Ok",
                        ""
                    )
                    ShowRedBorders(this@ResetPasswordActivity, mBinding.editTextOldPass)
                }

                override fun oldpasswordSpecialValidation() {
                    ReusedMethod.displayMessageDialog(
                        this@ResetPasswordActivity,
                        "",
                        resources.getString(R.string.valid_old_pass),
                        false,
                        "Ok",
                        ""
                    )
                    ShowRedBorders(this@ResetPasswordActivity, mBinding.editTextOldPass)
                }

                override fun empty_pass() {
                    ReusedMethod.displayMessageDialog(
                        this@ResetPasswordActivity,
                        "",
                        resources.getString(R.string.new_password),
                        false,
                        "Ok",
                        ""
                    )
                    ShowNoBorders(this@ResetPasswordActivity, mBinding.editTextOldPass)
                    ShowRedBorders(this@ResetPasswordActivity, mBinding.editTextNewPass)
                }

                override fun passwordMinValidation() {
                    ReusedMethod.displayMessageDialog(
                        this@ResetPasswordActivity,
                        "",
                        resources.getString(R.string.valid_new_pass),
                        false,
                        "Ok",
                        ""
                    )
                    ShowNoBorders(this@ResetPasswordActivity, mBinding.editTextOldPass)
                    ShowRedBorders(this@ResetPasswordActivity, mBinding.editTextNewPass)
                }

                override fun passwordSpecialValidation() {
                    ReusedMethod.displayMessageDialog(
                        this@ResetPasswordActivity,
                        "",
                        resources.getString(R.string.valid_new_pass),
                        false,
                        "Ok",
                        ""
                    )
                    ShowNoBorders(this@ResetPasswordActivity, mBinding.editTextOldPass)
                    ShowRedBorders(this@ResetPasswordActivity, mBinding.editTextNewPass)
                }

                override fun empty_con_Pass() {
                    ReusedMethod.displayMessageDialog(
                        this@ResetPasswordActivity,
                        "",
                        resources.getString(R.string.valid_con_pass),
                        false,
                        "Ok",
                        ""
                    )
                    ShowNoBorders(this@ResetPasswordActivity, mBinding.editTextOldPass)
                    ShowNoBorders(this@ResetPasswordActivity, mBinding.editTextNewPass)
                    ShowRedBorders(this@ResetPasswordActivity, mBinding.editTextConPass)
                }

                override fun con_passwordMinValidation() {
                    ReusedMethod.displayMessageDialog(
                        this@ResetPasswordActivity,
                        "",
                        resources.getString(R.string.valid_con_pass),
                        false,
                        "Ok",
                        ""
                    )
                    ShowNoBorders(this@ResetPasswordActivity, mBinding.editTextOldPass)
                    ShowNoBorders(this@ResetPasswordActivity, mBinding.editTextNewPass)
                    ShowRedBorders(this@ResetPasswordActivity, mBinding.editTextConPass)
                }

                override fun con_passwordSpecialValidation() {
                    ReusedMethod.displayMessageDialog(
                        this@ResetPasswordActivity,
                        "",
                        resources.getString(R.string.valid_con_pass),
                        false,
                        "Ok",
                        ""
                    )
                    ShowNoBorders(this@ResetPasswordActivity, mBinding.editTextOldPass)
                    ShowNoBorders(this@ResetPasswordActivity, mBinding.editTextNewPass)
                    ShowRedBorders(this@ResetPasswordActivity, mBinding.editTextConPass)
                }

                override fun comparePass() {
                    ReusedMethod.displayMessageDialog(
                        this@ResetPasswordActivity,
                        "",
                        resources.getString(R.string.same_old_new_new_pass),
                        false,
                        "Ok",
                        ""
                    )
                    ShowRedBorders(this@ResetPasswordActivity, mBinding.editTextConPass)
                    ShowRedBorders(this@ResetPasswordActivity, mBinding.editTextNewPass)
                }

                override fun success() {
                    ShowNoBorders(this@ResetPasswordActivity, mBinding.editTextConPass)
                    ShowNoBorders(this@ResetPasswordActivity, mBinding.editTextNewPass)
                    ShowNoBorders(this@ResetPasswordActivity, mBinding.editTextOldPass)
                    if (is_ChangePass) {
                        callChangePassAPI()
                    } else {
                        callApi()
                    }

                }
            })
    }

    private fun callApi() {
        if (isNetworkConnected(this)) {
            mViewModel.resetPassword(
                true,
                this,
                mBinding.editTextNewPass.text?.trim().toString(),
                mBinding.editTextConPass.text?.trim().toString(),
                intent.getStringExtra(AppConstants.EXTRA_USER_ID).toString()
            )
        } else {
            mBinding.noInternetoginResetPass.llNointernet.visible()
            mBinding.noDataoginResetPass.gone()
            mBinding.nsResetPass.gone()
        }
    }

    private fun callChangePassAPI() {
        if (isNetworkConnected(this)) {
            mViewModel.changePassword(
                true,
                this,
                mBinding.editTextOldPass.text?.trim().toString(),
                mBinding.editTextNewPass.text?.trim().toString(),
                mBinding.editTextConPass.text?.trim().toString(),
            )
        } else {
            mBinding.noInternetoginResetPass.llNointernet.visible()
            mBinding.noDataoginResetPass.gone()
            mBinding.nsResetPass.gone()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.leftto, R.anim.right)

    }

}