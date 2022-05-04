package com.app.guardian.ui.signup

import android.view.View
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.shareddata.base.BaseActivity

class SignupScreen : BaseActivity(), View.OnClickListener {
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_signup_screen
    }

    override fun initView() {

    }

    override fun initObserver() {

    }

    override fun handleListener() {

    }

    override fun onClick(p0: View?) {
        when(p0?.id){

        }
    }

}