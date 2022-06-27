package com.app.guardian.ui.VideoCallRequest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.guardian.R
import com.app.guardian.databinding.FragmentVideoCallingBinding
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity

class VideoCallingFragment : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentVideoCallingBinding
    override fun getInflateResource(): Int {
        return R.layout.fragment_video_calling
    }

    override fun initView() {
        mBinding = getBinding()
        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.vide_call_req),
            isHeaderVisible = true,
            isBackButtonVisible = false
        )
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.btnCreateMeeting.setOnClickListener(this)
    }

    override fun initObserver() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnCreateMeeting -> {

            }
        }
    }


}