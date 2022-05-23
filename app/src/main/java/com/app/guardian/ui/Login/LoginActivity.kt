package com.app.guardian.ui.Login

import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import com.app.guardian.R
import com.app.guardian.common.*
import com.app.guardian.common.ReusedMethod.Companion.ShowNoBorders
import com.app.guardian.common.ReusedMethod.Companion.ShowRedBorders
import com.app.guardian.common.ReusedMethod.Companion.changePhoneEmailState
import com.app.guardian.common.ReusedMethod.Companion.displayMessage
import com.app.guardian.common.ReusedMethod.Companion.displayMessageDialog
import com.app.guardian.common.ReusedMethod.Companion.isNetworkConnected
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.ActivityLoginBinding
import com.app.guardian.model.viewModels.AuthenticationViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.SubscriptionPlan.SubScriptionPlanScreen
import com.app.guardian.ui.forgot.ForgotPasswordActivity
import com.app.guardian.ui.signup.SignupScreen
import com.app.guardian.utils.Config
import com.google.gson.Gson
import org.koin.android.viewmodel.ext.android.viewModel


class LoginActivity : BaseActivity(), View.OnClickListener {
    private val mViewModel: AuthenticationViewModel by viewModel()
    lateinit var mBinding: ActivityLoginBinding
    var is_Email = true
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_login
    }

    override fun initView() {
        mBinding = getBinding()
        setPhoneEmailSelector()
        mBinding.emailphoneSelector.ccp.setCountryForPhoneCode(1)

    }

    private fun setPhoneEmailSelector() {
        mBinding.emailphoneSelector.rgLogin.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbEmail -> {
                    is_Email = true
                    changePhoneEmailState(
                        this,
                        is_Email,
                        mBinding.emailphoneSelector.edtLoginEmail,
                        mBinding.emailphoneSelector.ccp,
                        mBinding.emailphoneSelector.cl1
                    )
                }
                R.id.rbPhone -> {
                    is_Email = false
                    changePhoneEmailState(
                        this,
                        is_Email,
                        mBinding.emailphoneSelector.edtLoginEmail,
                        mBinding.emailphoneSelector.ccp,
                        mBinding.emailphoneSelector.cl1
                    )
                }
            }
        }
    }


    override fun initObserver() {
        mViewModel.getLoginResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            SharedPreferenceManager.putString(AppConstants.BEREAR_TOKEN, data.token)

                            val gson = Gson()
                            val json = gson.toJson(data)
                            SharedPreferenceManager.putString(AppConstants.USER_DETAIL_LOGIN, json)

                            when {
                                SharedPreferenceManager.getString(
                                    AppConstants.USER_ROLE,
                                    AppConstants.APP_ROLE_USER
                                ) == AppConstants.APP_ROLE_USER -> {

                                    if(data.user.is_subscribe ==0) {
                                        startActivity(
                                            Intent(
                                                this@LoginActivity,
                                                SubScriptionPlanScreen::class.java
                                            )
                                        )
                                        overridePendingTransition(R.anim.rightto, R.anim.left)
                                        SharedPreferenceManager.putBoolean(AppConstants.IS_LOGIN, true)
                                    }else{
                                        openDashBoard()
                                        overridePendingTransition(R.anim.rightto, R.anim.left)
                                    }

                                }
                                SharedPreferenceManager.getString(
                                    AppConstants.USER_ROLE,
                                    AppConstants.APP_ROLE_USER
                                ) == AppConstants.APP_ROLE_LAWYER -> {
//                                    displayMessageDialog(
//                                        this@LoginActivity,
//                                        "",
//                                        it.message.toString(),
//                                        false,
//                                        "Ok",
//                                        ""
//                                    )
//                                    displayMessage(this,it.message.toString())
                                    SharedPreferenceManager.putBoolean(AppConstants.IS_LOGIN, true)
                                    openDashBoard()

                                }
                                SharedPreferenceManager.getString(
                                    AppConstants.USER_ROLE,
                                    AppConstants.APP_ROLE_USER
                                ) == AppConstants.APP_ROLE_MEDIATOR -> {
//                                    displayMessageDialog(
//                                        this@LoginActivity,
//                                        "",
//                                        it.message.toString(),
//                                        false,
//                                        "Ok",
//                                        ""
//                                    )
//                                    displayMessage(this,it.message.toString())
                                    SharedPreferenceManager.putBoolean(AppConstants.IS_LOGIN, true)
                                    openDashBoard()
                                }
                            }
                            displayMessage(this,it.message.toString())

                        } else {
                            displayMessage(this, it.message.toString())
                        }
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            displayMessage(this, getString(R.string.text_error_network))

                        Config.CUSTOM_ERROR ->
                            errorObj.customMessage
                                ?.let { }
                    }
                }
            }
        }
    }

    private fun openDashBoard(){
        startActivity(
            Intent(
                this@LoginActivity,
                HomeActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        )
        finish()
        overridePendingTransition(R.anim.rightto, R.anim.left)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.leftto, R.anim.right)
    }

    override fun handleListener() {
        mBinding.txtForgotPassword.setOnClickListener(this)
        mBinding.btnSigIn.setOnClickListener(this)
        mBinding.headderLogin.ivBack.setOnClickListener(this)
        mBinding.txtDoNotHaveAccount.setOnClickListener(this)
        mBinding.noInternetLogin.btnTryAgain.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.txtDoNotHaveAccount -> {
                finish()
                startActivity(
                    Intent(
                        this@LoginActivity,
                        SignupScreen::class.java
                    )
                )
                overridePendingTransition(R.anim.rightto, R.anim.left)
            }
            R.id.btnSigIn -> {
                validations()
            }
            R.id.ivBack -> {
                onBackPressed()
                overridePendingTransition(R.anim.leftto, R.anim.right)
            }
            R.id.txtForgotPassword -> {
                startActivity(
                    Intent(
                        this@LoginActivity,
                        ForgotPasswordActivity::class.java
                    )
                )
                overridePendingTransition(R.anim.rightto, R.anim.left)
            }
            R.id.btnTryAgain -> {
                if (is_Email) {
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

    private fun validations() {
        IntegratorImpl.isValidLogin(is_Email,
            mBinding.emailphoneSelector.edtLoginEmail.text?.trim().toString(),
            mBinding.editTextLoginPass.text?.trim().toString(),
            object : ValidationView.LoginView {
                override fun empty_email() {
                    ReusedMethod.displayMessageDialog(
                        this@LoginActivity,
                        "",
                        resources.getString(R.string.empty_email),
                        false,
                        "Ok",
                        ""
                    )
                    mBinding.emailphoneSelector.cl1.background = ContextCompat.getDrawable(
                        this@LoginActivity,
                        R.drawable.normal_rounded_light_blue_red_borders
                    )
                }

                override fun emailValidation() {
                    ReusedMethod.displayMessageDialog(
                        this@LoginActivity,
                        "",
                        resources.getString(R.string.valid_email),
                        false,
                        "Ok",
                        ""
                    )
                    mBinding.emailphoneSelector.cl1.background = ContextCompat.getDrawable(
                        this@LoginActivity,
                        R.drawable.normal_rounded_light_blue_red_borders
                    )
                }

                override fun empty_passwordValidation() {
                    ReusedMethod.displayMessageDialog(
                        this@LoginActivity,
                        "",
                        resources.getString(R.string.empty_pass),
                        false,
                        "Ok",
                        ""
                    )
                }

                override fun empty_number() {
                    ReusedMethod.displayMessageDialog(
                        this@LoginActivity,
                        "",
                        resources.getString(R.string.empty_number),
                        false,
                        "Ok",
                        ""
                    )
                    mBinding.emailphoneSelector.cl1.background = ContextCompat.getDrawable(
                        this@LoginActivity,
                        R.drawable.normal_rounded_light_blue_red_borders
                    )
                }

                override fun numberValidation() {
                    mBinding.emailphoneSelector.cl1.background = ContextCompat.getDrawable(
                        this@LoginActivity,
                        R.drawable.normal_rounded_light_blue_red_borders
                    )
                    ReusedMethod.displayMessageDialog(
                        this@LoginActivity,
                        "",
                        resources.getString(R.string.valid_number),
                        false,
                        "Ok",
                        ""
                    )
                }

                override fun passwordMinValidation() {
                    ReusedMethod.displayMessageDialog(
                        this@LoginActivity,
                        "",
                        resources.getString(R.string.valid_pass),
                        false,
                        "Ok",
                        ""
                    )
                    ShowRedBorders(this@LoginActivity, mBinding.editTextLoginPass)
                }

                override fun passwordSpecialValidation() {
                    ReusedMethod.displayMessageDialog(
                        this@LoginActivity,
                        "",
                        resources.getString(R.string.valid_pass),
                        false,
                        "Ok",
                        ""
                    )
                    ShowRedBorders(this@LoginActivity, mBinding.editTextLoginPass)
                }

                override fun success() {
                    mBinding.emailphoneSelector.cl1.background = ContextCompat.getDrawable(
                        this@LoginActivity,
                        R.drawable.normal_rounded_light_blue
                    )
                    ShowNoBorders(this@LoginActivity, mBinding.editTextLoginPass)
                    if (is_Email) {
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
            mViewModel.Login(
                isNetworkConnected(this), this@LoginActivity, is_Email,mBinding.emailphoneSelector.ccp.selectedCountryCode.toString(),
                phone_email,
                mBinding.editTextLoginPass.text?.trim().toString(),
            "DEVICETOKEN@123")
        } else {
            mBinding.nsLogin.gone()
            mBinding.noDataLogin.gone()
            mBinding.noInternetLogin.llNointernet.visible()
        }
    }

}