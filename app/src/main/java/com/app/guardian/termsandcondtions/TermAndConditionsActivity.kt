package com.app.guardian.termsandcondtions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil.getBinding
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.databinding.ActivityAboutUsBinding
import com.app.guardian.databinding.ActivityTermAndConditionsBinding
import com.app.guardian.shareddata.base.BaseActivity

class TermAndConditionsActivity : BaseActivity(), View.OnClickListener {
    lateinit var mBinding: ActivityTermAndConditionsBinding
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_term_and_conditions
    }

    override fun initView() {
        mBinding = getBinding()
        mBinding.headderTerms.tvHeaderText.text = resources.getString(R.string.terms_amp_conditions)
    }

    override fun initObserver() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.leftto, R.anim.right)
    }

    override fun handleListener() {
        mBinding.headderTerms.ivBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
                overridePendingTransition(R.anim.leftto, R.anim.right)
            }
        }
    }
}