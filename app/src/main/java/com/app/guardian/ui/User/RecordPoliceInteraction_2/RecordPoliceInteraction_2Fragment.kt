package com.app.guardian.ui.User.RecordPoliceInteraction_2

import android.view.View
import com.app.guardian.R
import com.app.guardian.databinding.FragmentRecordPoliceInteraction2Binding
import com.app.guardian.shareddata.base.BaseFragment


class RecordPoliceInteraction_2Fragment : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentRecordPoliceInteraction2Binding

    override fun getInflateResource(): Int {
        return R.layout.fragment_record_police_interaction_2
    }

    override fun initView() {
        mBinding = getBinding()
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.cvKnowBasicRight.setOnClickListener(this)
        mBinding.cvLiveVirtualWitness.setOnClickListener(this)
    }

    override fun initObserver() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cvKnowBasicRight -> {

            }
            R.id.cvLiveVirtualWitness -> {

            }
        }
    }
}