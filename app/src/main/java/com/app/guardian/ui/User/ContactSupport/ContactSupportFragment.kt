package com.app.guardian.ui.User.ContactSupport

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.databinding.FragmentContactSupportBinding
import com.app.guardian.databinding.FragmentRecordPoliceInteractionBinding
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.SupportGroups.SupportGroupList
import com.app.guardian.ui.User.RecordPolice.RecordPoliceInteractionFragment
import com.google.firebase.database.core.Context


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
        mBinding.txtSupportGroups.setOnClickListener(this)
        mBinding.rlFindCounsellors.setOnClickListener(this)
        mBinding.rbFindCounsellors.setOnClickListener(this)
        mBinding.rlSupportGroups.setOnClickListener(this)
        mBinding.rbSupportGroups.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        changeLayout(SharedPreferenceManager.getInt(AppConstants.EXTRA_SH_CONTACT_SUPPORT,0))
    }

    private fun changeLayout(i: Int) {
        SharedPreferenceManager.putInt(AppConstants.EXTRA_SH_CONTACT_SUPPORT, i)
        when (i) {
            0 -> {
                mBinding.rlSupportGroups.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.txtSupportGroups.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.txt_dark
                    )
                )
                mBinding.rlFindCounsellors.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rbSupportGroups.isChecked = false
                mBinding.rbFindCounsellors.isChecked = false
            }
            1 -> {
                mBinding.rlSupportGroups.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.txtSupportGroups.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                mBinding.rlFindCounsellors.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rbSupportGroups.isChecked = true
                mBinding.rbFindCounsellors.isChecked = false
            }
            2 -> {
                mBinding.rlSupportGroups.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.txtSupportGroups.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.txt_dark
                    )
                )
                mBinding.rlFindCounsellors.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.rbSupportGroups.isChecked = false
                mBinding.rbFindCounsellors.isChecked = true
            }
        }
    }

    override fun initObserver() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cvFindCounsellors -> {
                changeLayout(2)
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    SupportGroupList(resources.getString(R.string.find_a_counsellors_2)),
                    true,
                    ContactSupportFragment::class.java.name,
                    ContactSupportFragment::class.java.name
                )
            }

            R.id.rlFindCounsellors -> {
                changeLayout(2)
                mBinding.cvFindCounsellors.performClick()
            }
            R.id.rbFindCounsellors -> {
                changeLayout(2)
                mBinding.cvFindCounsellors.performClick()
            }
            R.id.cvSupportGroups -> {
                changeLayout(1)
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    SupportGroupList(resources.getString(R.string.support_groups)),
                    true,
                    ContactSupportFragment::class.java.name,
                    ContactSupportFragment::class.java.name
                )
            }
            R.id.rlSupportGroups -> {
                changeLayout(1)
                mBinding.cvSupportGroups.performClick()
            }
            R.id.rbSupportGroups -> {
                changeLayout(1)
                mBinding.cvSupportGroups.performClick()
            }
            R.id.txtSupportGroups -> {
                changeLayout(1)
                mBinding.cvSupportGroups.performClick()
            }
        }
    }

}