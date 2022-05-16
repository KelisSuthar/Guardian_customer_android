package com.app.guardian.ui.User.LiveVirtualVitness

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.guardian.R
import com.app.guardian.databinding.FragmentLiveVirtualVitnessMediatorBinding
import com.app.guardian.databinding.FragmentRecordPoliceInteractionBinding
import com.app.guardian.shareddata.base.BaseFragment


class LiveVirtualVitnessMediatorFragment : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentLiveVirtualVitnessMediatorBinding
    override fun getInflateResource(): Int {
        return R.layout.fragment_live_virtual_vitness_mediator
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