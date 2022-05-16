package com.app.guardian.ui.User.ScheduleVirtualWitness

import android.view.View
import com.app.guardian.R
import com.app.guardian.databinding.FragmentScheduleVirtualWitnessBinding
import com.app.guardian.shareddata.base.BaseFragment


class ScheduleVirtualWitnessFragment : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentScheduleVirtualWitnessBinding
    override fun getInflateResource(): Int {
        return R.layout.fragment_schedule_virtual_witness
    }

    override fun initView() {
        mBinding = getBinding()
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.cvScheduleDateTime.setOnClickListener(this)
        mBinding.cvLocationWhereCallWillTakePlace.setOnClickListener(this)
        mBinding.cvScheduleMultipleCalls.setOnClickListener(this)
        mBinding.cvContactSupport.setOnClickListener(this)
    }

    override fun initObserver() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cvScheduleDateTime -> {

            }
            R.id.cvLocationWhereCallWillTakePlace -> {

            }
            R.id.cvScheduleMultipleCalls -> {

            }
            R.id.cvContactSupport -> {

            }
        }
    }


}