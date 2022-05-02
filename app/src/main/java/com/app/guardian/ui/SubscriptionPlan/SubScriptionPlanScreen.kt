package com.app.guardian.ui.SubscriptionPlan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.databinding.ActivitySubScriptionPlanScreenBinding
import com.app.guardian.shareddata.base.BaseActivity

class SubScriptionPlanScreen : BaseActivity(),View.OnClickListener {
    lateinit var mBinding:ActivitySubScriptionPlanScreenBinding
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_sub_scription_plan_screen
    }

    override fun initView() {
    mBinding = getBinding()
        setAdapter()
    }

    private fun setAdapter() {
        mBinding.recyclerView.adapter = null
    }

    override fun initObserver() {

    }

    override fun handleListener() {

    }

    override fun onClick(p0: View?) {

    }

}