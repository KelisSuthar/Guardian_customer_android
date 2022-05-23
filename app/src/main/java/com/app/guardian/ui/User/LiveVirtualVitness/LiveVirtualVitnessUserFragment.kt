package com.app.guardian.ui.User.LiveVirtualVitness

import android.view.View
import com.app.guardian.R
import com.app.guardian.databinding.FragmentLiveVirtualVitnessUserBinding
import com.app.guardian.shareddata.base.BaseFragment


class LiveVirtualVitnessUserFragment : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentLiveVirtualVitnessUserBinding
    override fun getInflateResource(): Int {
        return R.layout.fragment_live_virtual_vitness_user
    }

    override fun initView() {
        mBinding = getBinding()
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.cvAccessYourRecording.setOnClickListener(this)
        mBinding.cvDrivingOffenceList.setOnClickListener(this)
        mBinding.cvDialLawyer.setOnClickListener(this)
    }

    override fun initObserver() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cvAccessYourRecording -> {

            }
            R.id.cvDrivingOffenceList -> {

            }
            R.id.cvDialLawyer -> {

            }
        }
    }

}