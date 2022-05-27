package com.app.guardian.ui.notification

import com.app.guardian.R
import com.app.guardian.databinding.FragmentNotificationListBinding
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity

class NotificationListFragment : BaseFragment() {
    lateinit var mBinding: FragmentNotificationListBinding

    override fun getInflateResource(): Int {
        return R.layout.fragment_notification_list
    }

    override fun initView() {
        mBinding = getBinding()
        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.notification),
            true,
            true
        )
    }

    override fun postInit() {
    }

    override fun handleListener() {
    }

    override fun initObserver() {
    }

}