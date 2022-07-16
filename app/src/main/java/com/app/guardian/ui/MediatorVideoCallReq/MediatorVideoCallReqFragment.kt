package com.app.guardian.ui.MediatorVideoCallReq

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentMediatorVideoCallReqBinding
import com.app.guardian.model.GetVideoCallRequestResp.VideoCallRequestListResp
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.MediatorVideoCallReq.adapter.MediatorVideoCallReqAdapter
import com.app.guardian.ui.videocalljoin.VideoCallJoinActivity
import com.app.guardian.utils.Config
import com.google.android.material.textview.MaterialTextView
import org.json.JSONObject
import org.koin.android.viewmodel.ext.android.viewModel

class MediatorVideoCallReqFragment : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentMediatorVideoCallReqBinding
    private val mViewModel: CommonScreensViewModel by viewModel()
    val array = ArrayList<VideoCallRequestListResp>()
    var mediatorVideoCallReqAdapter: MediatorVideoCallReqAdapter? = null
    var broadcaseRecvier: BroadcastReceiver? = null
    var type = ""
    override fun getInflateResource(): Int {
        return R.layout.fragment_mediator_video_call_req
    }

    override fun initView() {
        broadcaseRecvier = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                CallVieoCallReqListAPI(
                    mBinding.searchConnectedHistory.edtLoginEmail.text?.trim().toString()
                )
            }
        }
        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(
            resources.getString(R.string.lawyer_video_req),
            isHeaderVisible = true,
            isBackButtonVisible = true
        )
        mBinding = getBinding()

        mBinding.searchConnectedHistory.lySearchFilter.gone()
        mBinding.searchConnectedHistory.edtLoginEmail.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                CallVieoCallReqListAPI(
                    mBinding.searchConnectedHistory.edtLoginEmail.text?.trim().toString()
                )
                return@OnEditorActionListener true
            }
            false
        })

        mBinding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            mBinding.searchConnectedHistory.edtLoginEmail.text?.clear()
            if (checkedId == R.id.rb1) {
                CallVieoCallReqListAPI("")
            } else {
                CallVieoCallReqListAPI("")
            }

        }
    }

    override fun onResume() {
        super.onResume()
        mBinding.noDataMediatorVideoCallReq.gone()
        mBinding.noInternetMediatorVideoCallReq.llNointernet.gone()
        setAdapter()
        mBinding.rb1.isChecked = true
        CallVieoCallReqListAPI("")
    }

    private fun setAdapter() {
        mediatorVideoCallReqAdapter = MediatorVideoCallReqAdapter(requireContext(),
            object : MediatorVideoCallReqAdapter.onItemClicklisteners {
                override fun onItemClick(position: Int?, userRole: String, userId: Int, id: Int) {
                    if (type == AppConstants.REQ_GET) {
                        AcceptRejectDialog(userRole, userId, id)
                    }

                }

            })
        mBinding.rvReqList.adapter = mediatorVideoCallReqAdapter
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.noInternetMediatorVideoCallReq.btnTryAgain.setOnClickListener(this)
        mBinding.searchConnectedHistory.llsearch.setOnClickListener(this)
    }

    override fun initObserver() {
        mViewModel.getVideoCallRequestListResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            array.clear()
                            array.addAll(data)
                            mBinding.cl1.visible()
                            mBinding.noDataMediatorVideoCallReq.gone()
                            mBinding.noInternetMediatorVideoCallReq.llNointernet.gone()
                            if (data.isEmpty()) {
                                mBinding.cl1.gone()
                                mBinding.noDataMediatorVideoCallReq.visible()
                                mBinding.noInternetMediatorVideoCallReq.llNointernet.gone()
                            }
                            mediatorVideoCallReqAdapter?.updateAll(data)
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
                                ?.let { ReusedMethod.displayMessage(requireActivity(), it) }
                    }
                }
            }
        }
        mViewModel.getAcceptCallByMediatorResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            if (!data.room_id.isNullOrEmpty() || !data.url.isNullOrEmpty()) {
                                joinMeeting(data.url)
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
        mViewModel.getRejectCallByMediatorResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        ReusedMethod.displayMessage(requireActivity(), it.message.toString())

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
                                ?.let {
                                    ReusedMethod.displayMessage(
                                        requireActivity(),
                                        it
                                    )
                                }
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnTryAgain -> {
                onResume()
            }
            R.id.llsearch -> {
                CallVieoCallReqListAPI(
                    mBinding.searchConnectedHistory.edtLoginEmail.text?.trim().toString()
                )
            }
        }
    }

    private fun CallVieoCallReqListAPI(search: String) {
        type =
            if (mBinding.rb1.isChecked) {
                AppConstants.REQ_GET
            } else {
                AppConstants.REQ_SEND
            }
        if (ReusedMethod.isNetworkConnected(requireContext())) {

            mViewModel.GetVideoCallRequestMediatorList(
                true,
                requireActivity() as BaseActivity,
                search,
                type
            )


        } else {
            mBinding.noInternetMediatorVideoCallReq.llNointernet.visible()
            mBinding.noDataMediatorVideoCallReq.gone()
            mBinding.cl1.gone()
        }
    }

    fun AcceptRejectDialog(userRole: String, userId: Int, id: Int) {
        val dialog =
            ReusedMethod.setUpDialog(requireActivity(), R.layout.dialog_layout, true)
        val tvTitle: TextView = dialog.findViewById(R.id.tvTitle)
        val tvMessage: TextView = dialog.findViewById(R.id.tvMessage)
        val tvNegative: MaterialTextView = dialog.findViewById(R.id.tvNegative)
        val tvPositive: MaterialTextView = dialog.findViewById(R.id.tvPositive)
        tvMessage.gone()
        tvNegative.isAllCaps = false
        tvPositive.isAllCaps = false
        tvTitle.text = "Do you want to accept this request?"
        tvPositive.setOnClickListener {
            dialog.dismiss()
            callAcceptReqApi(userRole, userId, id)

        }
        tvNegative.setOnClickListener {
            dialog.dismiss()
            callRejectReqApi(id)
        }
        dialog.show()
    }

    private fun callRejectReqApi(id: Int) {
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            mViewModel.sendRejectCallByMediatorResp(
                true,
                requireActivity() as BaseActivity,
                id.toString(),
            )
        } else {
            ReusedMethod.displayMessage(
                requireActivity(),
                resources.getString(R.string.text_error_network)
            )
        }
    }

    private fun callAcceptReqApi(userRole: String, userId: Int, id: Int) {
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            mViewModel.sendAcceptCallByMediatorResp(
                true,
                requireActivity() as BaseActivity,
                id.toString(),
                userId.toString(),
                userRole
            )
        } else {
            ReusedMethod.displayMessage(
                requireActivity(),
                resources.getString(R.string.text_error_network)
            )
        }
    }

    private fun joinMeeting(url: String?) {
        AndroidNetworking.post(url)
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
                        url
                    )
                    intent.putExtra(AppConstants.EXTRA_ROOM_ID, meetingId)
                    startActivity(intent)
                    requireActivity().finish()

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
