package com.app.guardian.ui.User.ContactSupport

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.guardian.R
import com.app.guardian.databinding.FragmentContactSupportBinding
import com.app.guardian.databinding.FragmentRecordPoliceInteractionBinding
import com.app.guardian.shareddata.base.BaseFragment


class ContactSupportFragment : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentContactSupportBinding
    override fun getInflateResource(): Int {
        return R.layout.fragment_contact_support
    }

    override fun initView() {
        mBinding = getBinding()
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.cvSupportGroups.setOnClickListener(this)
        mBinding.rlFindCounsellors.setOnClickListener(this)

    }

    override fun initObserver() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rlFindCounsellors -> {

            }
            R.id.cvSupportGroups -> {

            }

        }
    }

}