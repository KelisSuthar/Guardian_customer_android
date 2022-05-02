package com.app.guardian.ui.OTP

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.databinding.ActivityOtpscreenBinding
import com.app.guardian.shareddata.base.BaseActivity

class OTPScreen : BaseActivity(), View.OnClickListener {
    lateinit var mBinding: ActivityOtpscreenBinding
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_otpscreen
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