package com.app.guardian.ui.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.databinding.ActivityForgotPasswordBinding
import com.app.guardian.databinding.ActivityLoginBinding
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.forgot.ForgotPasswordActivity

class LoginActivity : BaseActivity(), View.OnClickListener {
    lateinit var mBinding: ActivityLoginBinding
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_login

    }

    override fun initView() {
        mBinding = getBinding()
    }

    override fun initObserver() {

    }

    override fun handleListener() {
    mBinding.txtForgotPassword.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.txtForgotPassword ->{
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

}