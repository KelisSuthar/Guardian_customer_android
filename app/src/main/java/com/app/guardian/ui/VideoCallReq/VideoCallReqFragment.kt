package com.app.guardian.ui.VideoCallReq

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentSettingsBinding
import com.app.guardian.databinding.FragmentVideoCallReqBinding
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity

class VideoCallReqFragment : BaseFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentVideoCallReqBinding
    override fun getInflateResource(): Int {
        return R.layout.fragment_video_call_req
    }

    override fun initView() {
        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(
            resources.getString(R.string.video_req),
            isHeaderVisible = true,
            isBackButtonVisible = false
        )
        mBinding = getBinding()

        mBinding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.rb1) {
                CallVieoCallReqListAPI()
            } else {
                CallVieoCallReqListAPI()
            }

        }
    }
    override fun onResume() {
        super.onResume()
        mBinding.noDataVideoCallReq.gone()
        mBinding.noInternetVideoCallReq.llNointernet.gone()
        CallVieoCallReqListAPI()
    }
    private fun CallVieoCallReqListAPI() {
        if (ReusedMethod.isNetworkConnected(requireContext())) {

        } else {
            mBinding.noInternetVideoCallReq.llNointernet.visible()
            mBinding.noDataVideoCallReq.gone()
            mBinding.cl1.gone()
        }
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