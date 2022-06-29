package com.app.guardian.ui.LawyerVideoCallReq

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentLawyerVideoCallReqBinding
import com.app.guardian.databinding.FragmentVideoCallingBinding
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity


class LawyerVideoCallReqFragment : BaseFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentLawyerVideoCallReqBinding
    override fun getInflateResource(): Int {
        return R.layout.fragment_lawyer_video_call_req
    }

    override fun initView() {
        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(
            resources.getString(R.string.lawyer_video_req),
            isHeaderVisible = true,
            isBackButtonVisible = false
        )
        mBinding = getBinding()
    }

    override fun onResume() {
        super.onResume()
        mBinding.noDataLawyerVideoCallReq.gone()
        mBinding.noInternetLawyerVideoCallReq.llNointernet.gone()
        CallVieoCallReqListAPI()
    }

    private fun CallVieoCallReqListAPI() {
        if (ReusedMethod.isNetworkConnected(requireContext())) {

        } else {
            mBinding.noInternetLawyerVideoCallReq.llNointernet.visible()
            mBinding.noDataLawyerVideoCallReq.gone()
            mBinding.cl1.gone()
        }
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.noInternetLawyerVideoCallReq.btnTryAgain.setOnClickListener(this)
    }

    override fun initObserver() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnTryAgain -> {
                onResume()
            }
        }
    }

}