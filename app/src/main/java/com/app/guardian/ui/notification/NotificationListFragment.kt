package com.app.guardian.ui.notification

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentNotificationListBinding
import com.app.guardian.model.Notification.NotificationResp
import com.app.guardian.model.viewModels.UserViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.chatting.ChattingFragment
import com.app.guardian.ui.notification.adapter.NotificationListAdapter
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import com.google.android.material.textview.MaterialTextView
import org.koin.android.viewmodel.ext.android.viewModel

class NotificationListFragment : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentNotificationListBinding
    private val mViewModel: UserViewModel by viewModel()
    var notificationListAdapter: NotificationListAdapter? = null
    var broadcaseRecvier: BroadcastReceiver? = null
    var array = ArrayList<NotificationResp>()
    var deleteId = -1

    override fun getInflateResource(): Int {
        return R.layout.fragment_notification_list
    }

    override fun initView() {
        broadcaseRecvier = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                callAPI()
            }
        }
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
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            broadcaseRecvier!!,
            IntentFilter(
                AppConstants.BROADCAST_REC_INTENT
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(broadcaseRecvier!!)
    }

    private fun setAdapter() {
        mBinding.rcyNotification.adapter = null
        notificationListAdapter = NotificationListAdapter(context as Activity, array,
            object : NotificationListAdapter.onItemClicklisteners {
                override fun onDelectclick(position: Int) {
                    showDialog(position)

                    deleteId = position
                }

                override fun onItemClick(position: Int) {
                    when (array[position].notification_type) {
                        AppConstants.EXTRA_CHAT_MESSAGE_PAYLOAD -> {
                            ReplaceFragment.replaceFragment(
                                requireActivity(),
                                ChattingFragment(array[position].sender_id),
                                true,
                                NotificationListFragment::class.java.name,
                                NotificationListFragment::class.java.name,
                            )
                        }
                    }
                }
            })
        mBinding.rcyNotification.adapter = notificationListAdapter
    }

    private fun callDeleteAPI(id: Int?) {
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            mViewModel.deleteNotification(true, context as BaseActivity, id!!)
        } else {
            mBinding.rcyNotification.gone()
            mBinding.noDataNotification.gone()
            mBinding.noInternetNotification.llNointernet.visible()
        }
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
//DELETE NOTIFICATION
        mViewModel.getDeleteNotificationResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data.let { data ->
                        if (it.status) {
                            ReusedMethod.displayMessage(requireActivity(), it.message.toString())
                            array.removeAt(deleteId)
                            notificationListAdapter?.notifyDataSetChanged()
                        } else {
                            ReusedMethod.displayMessage(requireActivity(), it.message.toString())
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
//DELETE NOTIFICATION
        mViewModel.getDeleteNotificationResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data.let { data ->
                        if (it.status) {
                            ReusedMethod.displayMessage(requireActivity(), it.message.toString())
                            array.removeAt(deleteId)
                            notificationListAdapter?.notifyDataSetChanged()
                            if (array.isNullOrEmpty()) {
                                mBinding.rcyNotification.gone()
                                mBinding.noDataNotification.visible()
                                mBinding.noInternetNotification.llNointernet.gone()
                            }
                        } else {
                            ReusedMethod.displayMessage(requireActivity(), it.message.toString())
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

    private fun showDialog(position: Int?) {
        val dialog = ReusedMethod.setUpDialog(requireContext(), R.layout.dialog_layout, false)
        val OK = dialog.findViewById<MaterialTextView>(R.id.tvPositive)
        val TITLE = dialog.findViewById<TextView>(R.id.tvTitle)
        val MESSAGE = dialog.findViewById<TextView>(R.id.tvMessage)
        val CANCEL = dialog.findViewById<MaterialTextView>(R.id.tvNegative)
        TITLE.text = "Are you sure you want to delete this data ?"
        MESSAGE.gone()
        CANCEL.text = "Close"
        OK.text = "Delete"
        CANCEL.isAllCaps = false
        OK.isAllCaps = false
        CANCEL.setOnClickListener {
            dialog.dismiss()
        }
        OK.setOnClickListener {
            callDeleteAPI(array[position!!].id)
            dialog.dismiss()
        }


        dialog.show()
    }
}