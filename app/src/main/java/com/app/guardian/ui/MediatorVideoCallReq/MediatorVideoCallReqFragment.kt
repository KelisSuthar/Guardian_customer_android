package com.app.guardian.ui.MediatorVideoCallReq

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
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
import com.app.guardian.ui.LawyerVideoCallReq.adapter.LawyerVideoCallReqAdapter
import com.app.guardian.ui.MediatorVideoCallReq.adapter.MediatorVideoCallReqAdapter
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel

class MediatorVideoCallReqFragment : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentMediatorVideoCallReqBinding
    private val mViewModel: CommonScreensViewModel by viewModel()
    val array = ArrayList<VideoCallRequestListResp>()
    var mediatorVideoCallReqAdapter: MediatorVideoCallReqAdapter? = null
    override fun getInflateResource(): Int {
        return R.layout.fragment_mediator_video_call_req
    }

    override fun initView() {
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
                override fun onItemClick(position: Int?) {
//                    ReplaceFragment.replaceFragment(
//                        requireActivity(),
//                        LawyerProfileFragment.newInstance(selectedLawyerListId!!, isDialLawyerOpen),
//                        true,
//                        LawyerListFragment::class.java.name,
//                        LawyerListFragment::class.java.name
//                    )
//                    startActivity(
//                        Intent(context, CreateOrJoinActivity::class.java)
//                            .putExtra(
//                                AppConstants.EXTRA_CALLING_HISTORY_ID,
//                                array[position!!].id.toString()
//                            )
//                            .putExtra(
//                                AppConstants.EXTRA_TO_ID,
//                                array[position!!].from_id.toString()
//                            )
//                            .putExtra(
//                                AppConstants.EXTRA_TO_ROLE,
//                                array[position!!].from_role.toString()
//                            )
//                            .putExtra(
//                                AppConstants.EXTRA_NAME,
//                                array[position!!].user_detail.full_name.toString()
//                            )
//
//                    )
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

    private fun CallVieoCallReqListAPI(search: String) {
        var type =
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
}
