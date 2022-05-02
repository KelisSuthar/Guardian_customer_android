package com.app.guardian.ui.SelectRole

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.gone
import com.app.guardian.databinding.ActivitySelectRoleScreenBinding
import com.app.guardian.shareddata.base.BaseActivity

class SelectRoleScreen : BaseActivity(),View.OnClickListener {
    lateinit var mBinding:ActivitySelectRoleScreenBinding
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return  R.layout.activity_select_role_screen
    }

    override fun initView() {
        mBinding = getBinding()
        mBinding.headder.tvHeaderText.text = resources.getString(R.string.select_user_role)
        mBinding.headder.ivBack.gone()
    }

    override fun initObserver() {

    }

    override fun handleListener() {

    }

    override fun onClick(p0: View?) {
        when(p0?.id)
        {

        }
    }

}