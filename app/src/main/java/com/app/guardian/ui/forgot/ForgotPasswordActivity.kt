package com.app.guardian.ui.forgot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.databinding.ActivityForgotPasswordBinding
import com.app.guardian.databinding.ActivityOtpscreenBinding
import com.app.guardian.shareddata.base.BaseActivity

class ForgotPasswordActivity : BaseActivity(), View.OnClickListener {
    lateinit var mBinding: ActivityForgotPasswordBinding
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_forgot_password
    }

    override fun initView() {
        mBinding = getBinding()
    }

    override fun initObserver() {

    }

    override fun handleListener() {

    }

    override fun onClick(p0: View?) {

    }
}