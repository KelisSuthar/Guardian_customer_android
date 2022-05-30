package com.app.guardian.ui.User.ScheduleVirtualWitness

import android.view.View
import com.app.guardian.R
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.databinding.FragmentScheduleVirtualWitnessBinding
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.User.ContactSupport.ContactSupportFragment


class ScheduleVirtualWitnessFragment : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentScheduleVirtualWitnessBinding
    override fun getInflateResource(): Int {
        return R.layout.fragment_schedule_virtual_witness
    }

    override fun initView() {
        mBinding = getBinding()
        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.schedule_virtual_witness),
            true,
            true
        )
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.cvScheduleDateTime.setOnClickListener(this)
        mBinding.cvLocationWhereCallWillTakePlace.setOnClickListener(this)
        mBinding.cvScheduleMultipleCalls.setOnClickListener(this)
        mBinding.cvContactSupport.setOnClickListener(this)

        mBinding.rlContactSupport.setOnClickListener(this)
        mBinding.rlScheduleMultipleCalls.setOnClickListener(this)
        mBinding.rlLocationWhereCallWillTakePlace.setOnClickListener(this)
        mBinding.rlScheduleDateTime.setOnClickListener(this)
        mBinding.rbContactSupport.setOnClickListener(this)
        mBinding.rbScheduleDateTime.setOnClickListener(this)
        mBinding.rbLocationWhereCallWillTakePlace.setOnClickListener(this)
        mBinding.rbScheduleMultipleCalls.setOnClickListener(this)
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
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    ContactSupportFragment(),
                    true,
                    ScheduleVirtualWitnessFragment::class.java.name,
                    ScheduleVirtualWitnessFragment::class.java.name
                );
            }
            R.id.rlContactSupport->{
                mBinding.cvContactSupport.performClick()
            }
            R.id.rlScheduleMultipleCalls->{}
            R.id.rlLocationWhereCallWillTakePlace->{}
            R.id.rlScheduleDateTime->{}
            R.id.rbContactSupport->{
                mBinding.cvContactSupport.performClick()
            }
            R.id.rbScheduleDateTime->{}
            R.id.rbLocationWhereCallWillTakePlace->{}
            R.id.rbScheduleMultipleCalls->{}
        }
    }


}