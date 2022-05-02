package com.app.guardian.ui.OTP

import `in`.aabhasjindal.otptextview.OTPListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.databinding.ActivityOtpscreenBinding
import com.app.guardian.shareddata.base.BaseActivity

class OTPScreen : BaseActivity(), View.OnClickListener {
    lateinit var mBinding: ActivityOtpscreenBinding
    var OTP = ""
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_otpscreen
    }

    override fun initView() {
        mBinding = getBinding()
        checkInputData()
    }

    private fun checkInputData() {
        mBinding.otpTextView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
            }

            override fun onOTPComplete(otp: String) {
                OTP = otp
                mBinding.otpTextView.resetState()
            }
        }
    }

    override fun initObserver() {

    }

    override fun handleListener() {
        mBinding.btnVerifyOTP.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnVerifyOTP -> {
                if(OTP == ""){

                }else if(OTP.length <4 ){

                }else{
//                    successs
                }
            }
        }
    }
}