package com.app.guardian.ui.VideoCallReq

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentVideoCallReqBinding
import com.app.guardian.model.GetVideoCallRequestResp.VideoCallRequestListResp
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.VideoCallReq.adapter.VideoCallReqAdapter
import com.app.guardian.ui.createorjoin.CreateOrJoinActivity
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel

class VideoCallReqFragment : BaseFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentVideoCallReqBinding
    private val mViewModel: CommonScreensViewModel by viewModel()
    var videoCallReqAdapter: VideoCallReqAdapter? = null
    var type = ""
    val array = ArrayList<VideoCallRequestListResp>()
    var broadcaseRecvier: BroadcastReceiver? = null
    override fun getInflateResource(): Int {
        return R.layout.fragment_video_call_req
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
            resources.getString(R.string.video_req),
            isHeaderVisible = true,
            isBackButtonVisible = true
        )
        mBinding = getBinding()
        mBinding.searchConnectedHistory.lySearchFilter.gone()
        mBinding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            mBinding.searchConnectedHistory.edtLoginEmail.text?.clear()
            if (checkedId == R.id.rb1) {
                CallVieoCallReqListAPI("")
            } else {
                CallVieoCallReqListAPI("")
            }

        }
        mBinding.searchConnectedHistory.edtLoginEmail.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                CallVieoCallReqListAPI(
                    mBinding.searchConnectedHistory.edtLoginEmail.text?.trim().toString()
                )
                return@OnEditorActionListener true
            }
            false
        })
        if (SharedPreferenceManager.getLoginUserRole() == AppConstants.APP_ROLE_LAWYER) {
            mBinding.rb1.text = "Mediator"
            mBinding.rb2.text = "User"
        }
    }

    override fun onResume() {
        super.onResume()
        mBinding.noDataVideoCallReq.gone()
        mBinding.noInternetVideoCallReq.llNointernet.gone()
        setAdapter()
        CallVieoCallReqListAPI("")
    }

    private fun setAdapter() {
        videoCallReqAdapter = VideoCallReqAdapter(requireContext(),
            object : VideoCallReqAdapter.onItemClicklisteners {
                override fun onItemClick(position: Int?) {
//                    ReplaceFragment.replaceFragment(
//                        requireActivity(),
//                        LawyerProfileFragment.newInstance(selectedLawyerListId!!, isDialLawyerOpen),
//                        true,
//                        LawyerListFragment::class.java.name,
//                        LawyerListFragment::class.java.name
//                    )
                }

                override fun onVideoCallClick(position: Int?) {
                    startActivity(
                        Intent(context, CreateOrJoinActivity::class.java)
                            .putExtra(
                                AppConstants.EXTRA_CALLING_HISTORY_ID,
                                array[position!!].id.toString()
                            )
                            .putExtra(
                                AppConstants.EXTRA_TO_ID,
                                array[position!!].user_id.toString()
                            )
                            .putExtra(
                                AppConstants.EXTRA_TO_ROLE,
                                array[position!!].user_role
                            )
                            .putExtra(
                                AppConstants.EXTRA_NAME,
                                array[position!!].full_name
                            )
                    )
//                    requireActivity().finish()
                }
            })
        mBinding.rvVideoCallReq.adapter = videoCallReqAdapter
    }

    private fun CallVieoCallReqListAPI(search: String) {
        type = if (mBinding.rb1.isChecked) {
            "mediator"
        } else {
            "user"
        }
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            mViewModel.GetVideoCallRequestLawyerList(
                true,
                requireActivity() as BaseActivity,
                search,
                type
            )
        } else {
            mBinding.noInternetVideoCallReq.llNointernet.visible()
            mBinding.noDataVideoCallReq.gone()
            mBinding.cl1.gone()
        }
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.noInternetVideoCallReq.btnTryAgain.setOnClickListener(this)
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
                            mBinding.noDataVideoCallReq.gone()
                            mBinding.noInternetVideoCallReq.llNointernet.gone()
                            if (data.isEmpty()) {
                                mBinding.cl1.gone()
                                mBinding.noDataVideoCallReq.visible()
                                mBinding.noInternetVideoCallReq.llNointernet.gone()
                            }
                            videoCallReqAdapter?.updateAll(data)
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

}