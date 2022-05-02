package com.app.guardian.ui.Splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.databinding.ActivitySplashScreenBinding
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.OTP.OTPScreen
import com.app.guardian.ui.ResetPassword.ResetPasswordActivity
import com.app.guardian.ui.SelectRole.SelectRoleScreen
import com.google.android.gms.actions.ReserveIntents

class SplashScreen : BaseActivity(),View.OnClickListener {
    lateinit var mBinding: ActivitySplashScreenBinding
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorTransparent, 0)
        return  R.layout.activity_splash_screen
    }

    override fun initView() {
        mBinding = getBinding()
        Handler().postDelayed({
            startActivity(
                Intent(
                    this@SplashScreen,
                    ResetPasswordActivity::class.java
                )
            )

        }, 1500)
    }

    override fun initObserver() {

    }

    override fun handleListener() {

    }


    override fun onClick(p0: View?) {

    }
}