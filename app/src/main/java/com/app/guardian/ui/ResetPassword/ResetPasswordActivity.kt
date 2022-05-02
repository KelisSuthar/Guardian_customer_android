package com.app.guardian.ui.ResetPassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.gone
import com.app.guardian.databinding.ActivityResetPasswordBinding
import com.app.guardian.databinding.ActivitySelectRoleScreenBinding
import com.app.guardian.shareddata.base.BaseActivity

class ResetPasswordActivity : BaseActivity(), View.OnClickListener {
    lateinit var mBinding:ActivityResetPasswordBinding

    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return  R.layout.activity_reset_password
    }

    override fun initView() {
        mBinding = getBinding()
        mBinding.lyHeaderResetPassword.tvHeaderText.text = resources.getString(R.string.reset_password)
        mBinding.lyHeaderResetPassword.ivBack.gone()
    }

    override fun initObserver() {
    }

    override fun handleListener() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
    }

    override fun onClick(p0: View?) {
    }
}