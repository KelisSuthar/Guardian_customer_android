package com.app.guardian.ui.JoinMetting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.guardian.R
import com.app.guardian.databinding.FragmentJoinMeetingBinding
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity


class JoinMeetingFragment : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentJoinMeetingBinding
    override fun getInflateResource(): Int {
        return R.layout.fragment_join_meeting
    }

    override fun initView() {
        mBinding = getBinding()
        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.join_meeting),
            isHeaderVisible = true,
            isBackButtonVisible = false
        )
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.txtSendReqCode.setOnClickListener(this)
    }

    override fun initObserver() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.txtSendReqCode -> {

            }
        }
    }

}