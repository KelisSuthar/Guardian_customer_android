package com.app.guardian.ui.Login

import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import com.app.guardian.R
import com.app.guardian.common.IntegratorImpl
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.ReusedMethod.Companion.ShowNoBorders
import com.app.guardian.common.ReusedMethod.Companion.ShowRedBorders
import com.app.guardian.common.ReusedMethod.Companion.displayMessageDialog
import com.app.guardian.common.ReusedMethod.Companion.isNetworkConnected
import com.app.guardian.common.ValidationView
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.ActivityLoginBinding
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.forgot.ForgotPasswordActivity


class LoginActivity : BaseActivity(), View.OnClickListener {
    lateinit var mBinding: ActivityLoginBinding
    var is_Email = false
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_login
    }

    override fun initView() {
        mBinding = getBinding()
        mBinding.headderLogin.ivBack.gone()
        setPhoneEmailSelector()



    }

    private fun setPhoneEmailSelector() {
        mBinding.emailphoneSelector.rgLogin.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {
                R.id.rbEmail -> {
                    is_Email = true
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
                    is_Email = false
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

    override fun initObserver() {
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.leftto, R.anim.right)
    }

    override fun handleListener() {
        mBinding.txtForgotPassword.setOnClickListener(this)
        mBinding.btnSigIn.setOnClickListener(this)
        mBinding.headderLogin.ivBack.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
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
        }
    }

    private fun validations() {
        IntegratorImpl.isValidLogin(is_Email,
            mBinding.emailphoneSelector.edtLoginEmail.text?.trim().toString(),
            mBinding.editTextLoginPass.text?.trim().toString(),
            object : ValidationView.LoginView {
                override fun emailValidation() {
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
                }

                override fun passwordMinValidation() {
                    ShowRedBorders(this@LoginActivity, mBinding.editTextLoginPass)
                }

                override fun passwordSpecialValidation() {
                    ShowRedBorders(this@LoginActivity, mBinding.editTextLoginPass)
                }

                override fun success() {
                    mBinding.emailphoneSelector.cl1.background = ContextCompat.getDrawable(
                        this@LoginActivity,
                        R.drawable.normal_rounded_light_blue
                    )
                    ShowNoBorders(this@LoginActivity, mBinding.editTextLoginPass)
                    callApi()
                }

            })
    }

    private fun callApi() {
        if (isNetworkConnected(this)) {
            displayMessageDialog(this, "", "Successful", false, "Ok", "")
        } else {
            mBinding.nsLogin.gone()
            mBinding.noDataLogin.gone()
            mBinding.noInternetLogin.visible()
        }
    }

}