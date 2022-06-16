package com.app.guardian.ui.notification

import android.app.Activity
import android.util.Log
import android.view.View
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentNotificationListBinding
import com.app.guardian.model.Notification.NotificationResp
import com.app.guardian.model.viewModels.UserViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.notification.adapter.NotificationListAdapter
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel

class NotificationListFragment : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentNotificationListBinding
    private val mViewModel: UserViewModel by viewModel()
    var notificationListAdapter: NotificationListAdapter? = null
    var array = ArrayList<NotificationResp>()

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

    override fun onResume() {
        super.onResume()

        callAPI()
        setAdapter()
        mBinding.rcyNotification.visible()
        mBinding.noDataNotification.gone()
        mBinding.noInternetNotification.llNointernet.gone()
    }

    private fun setAdapter() {
        mBinding.rcyNotification.adapter = null
        notificationListAdapter = NotificationListAdapter(context as Activity, array,
            object : NotificationListAdapter.onItemClicklisteners {
                override fun onDelectclick(selectedNotificationId: Int?) {
                    Log.i("selectedNotificationId:", selectedNotificationId.toString())
                    ReusedMethod.displayMessage(
                        context as Activity,
                        (context as Activity).resources.getString(R.string.come_soon)
                    )
                }
            })
        mBinding.rcyNotification.adapter = notificationListAdapter
    }

    override fun postInit() {
    }

    override fun handleListener() {
        mBinding.noInternetNotification.btnTryAgain.setOnClickListener(this)
    }

    override fun initObserver() {
        mViewModel.getNotificationResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data.let { data ->
                        if (it.status) {
                            array.clear()
                            if (data != null) {
                                array.addAll(data)
                                if (array.isNullOrEmpty()) {
                                    mBinding.rcyNotification.gone()
                                    mBinding.noDataNotification.visible()
                                    mBinding.noInternetNotification.llNointernet.gone()
                                }
                            }
                            notificationListAdapter?.notifyDataSetChanged()
                        } else {
                            mBinding.rcyNotification.gone()
                            mBinding.noDataNotification.visible()
                            mBinding.noInternetNotification.llNointernet.gone()
                        }
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            ReusedMethod.displayMessage(
                                context as Activity,
                                getString(R.string.text_error_network)
                            )

                        Config.CUSTOM_ERROR ->
                            errorObj.customMessage
                                ?.let {
                                    if (errorObj.code == ApiConstant.API_401) {
                                        ReusedMethod.displayMessage(requireActivity(), it)
                                        (activity as HomeActivity).unAuthorizedNavigation()
                                    } else {
                                        ReusedMethod.displayMessage(context as Activity, it)
                                    }
                                }
                    }
                }
            }
        }

    }

    private fun callAPI() {
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            mViewModel.getNotification(true, context as BaseActivity)
        } else {
            mBinding.rcyNotification.gone()
            mBinding.noDataNotification.gone()
            mBinding.noInternetNotification.llNointernet.visible()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnTryAgain -> {
                onResume()
            }
        }
    }
}