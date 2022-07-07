package com.app.guardian.ui.notification

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentNotificationListBinding
import com.app.guardian.model.Notification.NotificationResp
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.model.viewModels.UserViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.Lawyer.AddBaner.AddBannerFragment
import com.app.guardian.ui.LawyerVideoCallReq.LawyerVideoCallReqFragment
import com.app.guardian.ui.MediatorVideoCallReq.MediatorVideoCallReqFragment
import com.app.guardian.ui.SubscriptionPlan.SubScriptionPlanScreen
import com.app.guardian.ui.User.settings.SettingsFragment
import com.app.guardian.ui.VideoCallReq.VideoCallReqFragment
import com.app.guardian.ui.chatting.ChattingFragment
import com.app.guardian.ui.notification.adapter.NotificationListAdapter
import com.app.guardian.ui.videocalljoin.VideoCallJoinActivity
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import org.koin.android.viewmodel.ext.android.viewModel


class NotificationListFragment : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentNotificationListBinding
    private val mViewModel: UserViewModel by viewModel()
    private val commonViewModel: CommonScreensViewModel by viewModel()

    var notificationListAdapter: NotificationListAdapter? = null
    var broadcaseRecvier: BroadcastReceiver? = null
    var array = ArrayList<NotificationResp>()
    var deleteId = -1
    var meeting_Id = ""


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
        SharedPreferenceManager.putInt(
            AppConstants.NOTIFICATION_BAGE,
            0
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
                        AppConstants.EXTRA_VIDEOCALLREQ_PAYLOAD -> {
                            ReusedMethod.displayMessage(requireActivity(),resources.getString(R.string.come_soon))
//                            val jsonObject =
//                                JSONObject(array[position].data_obj)
//                            if (jsonObject.has("room_id")) {
//                                meeting_Id = jsonObject.getString("room_id")
//                                callCheckSubscriptionApi()
//                            } else {
//                                if (SharedPreferenceManager.getLoginUserRole() == AppConstants.APP_ROLE_USER || SharedPreferenceManager.getLoginUserRole() == AppConstants.APP_ROLE_MEDIATOR) {
//                                    ReplaceFragment.replaceFragment(
//                                        requireActivity(),
//                                        LawyerVideoCallReqFragment(),
//                                        true,
//                                        SettingsFragment::class.java.name,
//                                        SettingsFragment::class.java.name
//                                    )
//                                } else {
//                                    ReplaceFragment.replaceFragment(
//                                        requireActivity(),
//                                        VideoCallReqFragment(),
//                                        true,
//                                        SettingsFragment::class.java.name,
//                                        SettingsFragment::class.java.name
//                                    )
//                                }
//                            }


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
        //CHECK SUBSCRIPTION RESP
        commonViewModel.getcheckSubResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            if (data.is_subscribe == 0) {
                                startActivity(
                                    Intent(
                                        requireActivity(),
                                        SubScriptionPlanScreen::class.java
                                    )
                                )
                                requireActivity().overridePendingTransition(
                                    R.anim.rightto,
                                    R.anim.left
                                )
                            } else {
                                joinMeeting(meeting_Id)
                            }

                        } else {
                            ReusedMethod.displayMessage(requireActivity(), it.message.toString())
                        }
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            ReusedMethod.displayMessage(
                                requireActivity(),
                                getString(R.string.text_error_network)
                            )

                        Config.CUSTOM_ERROR ->
                            errorObj.customMessage
                                ?.let {}
                    }
                }
            }
        }

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

                        ReusedMethod.displayMessage(requireActivity(), it.message.toString())
                        Log.i("THIS_APP", "DELETE_ID:       " + deleteId)
                        Log.i("THIS_APP", "DELETE_ID:       " + array)
                        Log.i("THIS_APP", "DELETE_ID:       " + array)
                        array.removeAt(deleteId)
                        notificationListAdapter?.notifyDataSetChanged()
                        if (array.isNullOrEmpty()) {
                            mBinding.rcyNotification.gone()
                            mBinding.noDataNotification.visible()
                            mBinding.noInternetNotification.llNointernet.gone()
                        } else {
                            mBinding.rcyNotification.visible()
                            mBinding.noDataNotification.gone()
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
        mBinding.rcyNotification.visible()
        mBinding.noDataNotification.gone()
        mBinding.noInternetNotification.llNointernet.gone()
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
        CANCEL.text = "Cancel"
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

    private fun callCheckSubscriptionApi() {
        if (ReusedMethod.isNetworkConnected(requireActivity())) {
            commonViewModel.checkSubscritpion(true, context as BaseActivity)
        } else {
            ReusedMethod.displayMessage(requireActivity(), getString(R.string.text_error_network))
        }
    }

    private fun joinMeeting(meeting_Id: String) {
        AndroidNetworking.post("https://api.videosdk.live/v1/meetings/$meeting_Id")
            .addHeaders("Authorization", resources.getString(R.string.video_call_auth))
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    val meetingId = response.getString("meetingId")
                    Log.e("VIDEO_CALL", "JOIN_MEATING_RESP:    $response")
                    val intent =
                        Intent(requireContext(), VideoCallJoinActivity::class.java)
                    intent.putExtra("token", resources.getString(R.string.video_call_auth))
                    intent.putExtra("meetingId", meetingId)
                    intent.putExtra(
                        AppConstants.EXTRA_NAME,
                        SharedPreferenceManager.getUser()?.full_name
                    )
                    intent.putExtra(
                        AppConstants.IS_JOIN,
                        true
                    )
                    intent.putExtra(
                        AppConstants.EXTRA_URL,
                        "https://api.videosdk.live/v1/meetings/$meetingId"
                    )
                    intent.putExtra(AppConstants.EXTRA_ROOM_ID, meetingId)
                    startActivity(intent)

                }

                override fun onError(anError: ANError) {
                    anError.printStackTrace()
                    ReusedMethod.displayMessage(
                        requireActivity(),
                        anError.message.toString()
                    )
                    Log.e("VIDEO_CALL", "JOIN_MEATING_ERRRO:    " + anError.errorBody)

                }
            })
    }
}