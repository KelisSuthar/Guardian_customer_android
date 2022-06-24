package com.app.guardian.ui.VideoCallRequest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.guardian.R
import com.app.guardian.databinding.FragmentVideoCallingBinding
import com.app.guardian.shareddata.base.BaseFragment

class VideoCallingFragment : BaseFragment(),View.OnClickListener {
    lateinit var mBinding:FragmentVideoCallingBinding
    override fun getInflateResource(): Int {
        return R.layout.fragment_video_calling
    }

    override fun initView() {
        mBinding = getBinding()
    }

    override fun postInit() {

    }

    override fun handleListener() {

    }

    override fun initObserver() {

    }

    override fun onClick(v: View?) {

    }


}