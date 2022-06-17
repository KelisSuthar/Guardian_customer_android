package com.app.guardian.ui.Login

import android.annotation.SuppressLint
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.view.View.OnFocusChangeListener
import androidx.core.content.ContextCompat
import com.app.guardian.R
import com.app.guardian.common.*
import com.app.guardian.common.ReusedMethod.Companion.ShowNoBorders
import com.app.guardian.common.ReusedMethod.Companion.ShowRedBorders
import com.app.guardian.common.ReusedMethod.Companion.changePhoneEmailState
import com.app.guardian.common.ReusedMethod.Companion.displayMessage
import com.app.guardian.common.ReusedMethod.Companion.isNetworkConnected
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.ActivityLoginBinding
import com.app.guardian.model.Login.LoginResp
import com.app.guardian.model.viewModels.AuthenticationViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.SelectRole.SelectRoleScreen
import com.app.guardian.ui.SubscriptionPlan.SubScriptionPlanScreen
import com.app.guardian.ui.forgot.ForgotPasswordActivity
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import org.koin.android.viewmodel.ext.android.viewModel


class LoginActivity : BaseActivity(), View.OnClickListener {
    private val mViewModel: AuthenticationViewModel by viewModel()
    lateinit var mBinding: ActivityLoginBinding
    var DEVICE_TOKEN = SharedPreferenceManager.getString(ApiConstant.EXTRAS_DEVICETOKEN, "")

    var is_Email = true
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_login
    }

    override fun initView() {
        mBinding = getBinding()
        setPhoneEmailSelector()
        mBinding.emailphoneSelector.ccp.setCountryForPhoneCode(1)
        mBinding.headderLogin.ivBack.gone()

        setFocus()
    }

    private fun setFocus() {
        mBinding.emailphoneSelector.edtLoginEmail.onFocusChangeListener =
            OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.emailphoneSelector.edtLoginEmail.text?.trim().toString()
                if (!hasFocus) {
                    if (is_Email) {
                        if (SmartUtils.emailValidator(value) && !TextUtils.isEmpty(value)) {
                            mBinding.emailphoneSelector.cl1.background = ContextCompat.getDrawable(
                                this@LoginActivity,
                                R.drawable.normal_rounded_light_blue
                            )
                        }
                    } else {
                        if (!TextUtils.isEmpty(value) && value.length == 10) {
                            mBinding.emailphoneSelector.cl1.background = ContextCompat.getDrawable(
                                this@LoginActivity,
                                R.drawable.normal_rounded_light_blue
                            )
                        }
                    }
                }
            }
        mBinding.editTextLoginPass.onFocusChangeListener =
            OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.editTextLoginPass.text?.trim().toString()
                if (!hasFocus) {

                    if (value.length >= 8 && !TextUtils.isEmpty(value) && SmartUtils.checkSpecialPasswordValidation(
                            value
                        )
                    ) {
                        ShowNoBorders(this@LoginActivity, mBinding.editTextLoginPass)
                    }
                }
            }
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


    @SuppressLint("LogNotTimber")
    override fun initObserver() {
        mViewModel.getLoginResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
//                        if (it.status) {
//                            if (data.user.user_role == SharedPreferenceManager.getString(
//                                    AppConstants.USER_ROLE,
//                                    AppConstants.APP_ROLE_USER
//                                )
//                            ) {
//                                showLoadingIndicator(true)
//                                checkLogin(data)
//                                SharedPreferenceManager.putString(
//                                    AppConstants.BEREAR_TOKEN,
//                                    data.token
//                                )
//                                displayMessage(this, it.message.toString())
//                                val gson = Gson()
//                                val json = gson.toJson(data)
//                                SharedPreferenceManager.putString(
//                                    AppConstants.USER_DETAIL_LOGIN,
//                                    json
//                                )
//                                displayMessage(this, it.message.toString())
//                            } else {
//                                displayMessageDialog(
//                                    this@LoginActivity,
//                                    "",
//                                    resources.getString(R.string.valid_role_selections),
//                                    false,
//                                    "OK",
//                                    ""
//                                )
//                            }
//                        } else {
//                            Log.e(
//                                "network_message",
//                                "Display network error form login message : " + it.message
//                            )
//
//                            displayMessage(this, it.message.toString())
//                        }

                        showLoadingIndicator(true)
                        checkLogin(data)
                        SharedPreferenceManager.putString(
                            AppConstants.BEREAR_TOKEN,
                            data.token
                        )
                        displayMessage(this, it.message.toString())
                        val gson = Gson()
                        val json = gson.toJson(data)
                        SharedPreferenceManager.putString(
                            AppConstants.USER_DETAIL_LOGIN,
                            json
                        )
                        val getUserRole = SharedPreferenceManager.getUser()!!.user.user_role
                        var setConstantRole =""
                        if(getUserRole.equals(AppConstants.APP_ROLE_USER))
                            setConstantRole=AppConstants.APP_ROLE_USER
                        else if(getUserRole.equals(AppConstants.APP_ROLE_LAWYER))
                            setConstantRole=AppConstants.APP_ROLE_LAWYER
                        else if(getUserRole.equals(AppConstants.APP_ROLE_MEDIATOR))
                            setConstantRole=AppConstants.APP_ROLE_MEDIATOR

                        SharedPreferenceManager.putString(
                            AppConstants.USER_ROLE,
                            setConstantRole
                        )
                        displayMessage(this, it.message.toString())
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            displayMessage(this, getString(R.string.text_error_network))
                        Config.CUSTOM_ERROR ->
                            errorObj.customMessage
                                ?.let {
                                    displayMessage(this, it)
                                }
                        else -> {

                        }
                    }
                }
            }
        }
    }

    private fun checkLogin(data: LoginResp) {
//        auth!!.signInWithEmailAndPassword(
//            data.user.email.toString(), mBinding.editTextLoginPass.text?.trim()
//                .toString()
//        )
//            .addOnCompleteListener(this) {
//                if (it.isSuccessful) {
        showLoadingIndicator(true)
        when {
            SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                AppConstants.APP_ROLE_USER
            ) == AppConstants.APP_ROLE_USER -> {

                if (data.user.is_subscribe == 0) {
                    startActivity(
                        Intent(
                            this@LoginActivity,
                            SubScriptionPlanScreen::class.java
                        )
                    )
                    overridePendingTransition(R.anim.rightto, R.anim.left)

                } else {
                    SharedPreferenceManager.putBoolean(AppConstants.IS_LOGIN, true)
                    SharedPreferenceManager.putBoolean(AppConstants.IS_SUBSCRIBE, true)
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
                SharedPreferenceManager.putBoolean(AppConstants.IS_SUBSCRIBE, true)
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
                SharedPreferenceManager.putBoolean(AppConstants.IS_SUBSCRIBE, true)
                openDashBoard()
            }
        }

//                } else {
//                    showLoadingIndicator(false)
//                }

//            }
    }

    private fun openDashBoard() {
        startActivity(
            Intent(
                this@LoginActivity,
                HomeActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        )
        finish()
        overridePendingTransition(R.anim.rightto, R.anim.left)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
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
                startActivity(
                    Intent(
                        this@LoginActivity,
                        SelectRoleScreen::class.java
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
                    mBinding.emailphoneSelector.cl1.background = ContextCompat.getDrawable(
                        this@LoginActivity,
                        R.drawable.normal_rounded_light_blue
                    )
                    ShowRedBorders(this@LoginActivity, mBinding.editTextLoginPass)

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
                    mBinding.emailphoneSelector.cl1.background = ContextCompat.getDrawable(
                        this@LoginActivity,
                        R.drawable.normal_rounded_light_blue
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
                    mBinding.emailphoneSelector.cl1.background = ContextCompat.getDrawable(
                        this@LoginActivity,
                        R.drawable.normal_rounded_light_blue
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
                        callApi(
                            mBinding.emailphoneSelector.edtLoginEmail.text?.trim().toString()
                        )
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
                isNetworkConnected(this),
                this@LoginActivity,
                is_Email,
                mBinding.emailphoneSelector.ccp.selectedCountryCode.toString(),
                phone_email,
                mBinding.editTextLoginPass.text?.trim().toString(),
                DEVICE_TOKEN.toString()
            )
        } else {
            mBinding.nsLogin.gone()
            mBinding.noDataLogin.gone()
            mBinding.noInternetLogin.llNointernet.visible()
        }
    }

}