package com.app.guardian.ui.User.ContactSupport

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.guardian.R
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.databinding.FragmentContactSupportBinding
import com.app.guardian.databinding.FragmentRecordPoliceInteractionBinding
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.SupportGroups.SupportGroupList
import com.app.guardian.ui.User.RecordPolice.RecordPoliceInteractionFragment


class ContactSupportFragment : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentContactSupportBinding
    override fun getInflateResource(): Int {
        return R.layout.fragment_contact_support
    }

    override fun initView() {
        mBinding = getBinding()
        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.contact_support),
            true,
            true
        )
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.cvSupportGroups.setOnClickListener(this)
        mBinding.cvFindCounsellors.setOnClickListener(this)
        mBinding.rlFindCounsellors.setOnClickListener(this)
        mBinding.rbFindCounsellors.setOnClickListener(this)
        mBinding.rlSupportGroups.setOnClickListener(this)
        mBinding.rbSupportGroups.setOnClickListener(this)
    }

    override fun initObserver() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cvFindCounsellors -> {
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    SupportGroupList(),
                    true,
                    ContactSupportFragment::class.java.name,
                    ContactSupportFragment::class.java.name
                )
            }
            R.id.cvSupportGroups -> {
                mBinding.cvFindCounsellors.performClick()
            }
            R.id.rlFindCounsellors->{
                mBinding.cvFindCounsellors.performClick()
            }
            R.id.rbFindCounsellors->{
                mBinding.cvFindCounsellors.performClick()
            }
            R.id.rlSupportGroups->{
                mBinding.cvFindCounsellors.performClick()
            }
            R.id.rbSupportGroups->{
                mBinding.cvFindCounsellors.performClick()
            }
        }
    }

}