package com.app.guardian.ui.aboutus

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.databinding.ActivityAboutUsBinding
import com.app.guardian.databinding.ActivityResetPasswordBinding
import com.app.guardian.model.viewModels.AuthenticationViewModel
import com.app.guardian.shareddata.base.BaseActivity
import org.koin.android.viewmodel.ext.android.viewModel


class AboutUsActivity : BaseActivity(), View.OnClickListener {
    lateinit var mBinding: ActivityAboutUsBinding
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_about_us
    }

    override fun initView() {
        mBinding = getBinding()
        mBinding.headderAboutUs.tvHeaderText.text  = resources.getString(R.string.about_us)
    }

    override fun initObserver() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.leftto, R.anim.right)
    }

    override fun handleListener() {
        mBinding.headderAboutUs.ivBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ivBack -> {
                onBackPressed()
                overridePendingTransition(R.anim.leftto, R.anim.right)
            }
        }
    }

}