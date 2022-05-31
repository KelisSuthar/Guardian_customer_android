package com.app.guardian.ui.ContactedHistory

import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentContectedHistoryBinding
import com.app.guardian.model.connectedhistory.ConnectedHistoryResp
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.ContactedHistory.adapter.ConnectedHistoryAdapter
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ContectedHistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContectedHistoryFragment : BaseFragment(), View.OnClickListener {
    private val mViewModel: CommonScreensViewModel by viewModel()
    lateinit var mBinding: FragmentContectedHistoryBinding
    var connectedHistoryAdapter: ConnectedHistoryAdapter? = null
    var array = ArrayList<ConnectedHistoryResp>()
    var type = ""
    override fun getInflateResource(): Int {
        return R.layout.fragment_contected_history
    }

    override fun initView() {
        mBinding = getBinding()


        mBinding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.rb1) {
                callApi("")

            } else {
                callApi("")
            }

        }
        (activity as HomeActivity).bottomTabVisibility(true)
        (activity as HomeActivity).headerTextVisible(requireActivity().resources.getString(R.string.contacted_history),true,false)

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun postInit() {

    }

    override fun onResume() {
        super.onResume()


        callApi("")


    }

    private fun setAdapter() {
        mBinding.rvContectedHistory.adapter = null
        connectedHistoryAdapter = ConnectedHistoryAdapter(
            requireActivity(),
            type,
            array,
            object : ConnectedHistoryAdapter.onItemClicklisteners {
                override fun onCallClick(position: Int) {

                }

                override fun onChatClick(position: Int?) {

                }

                override fun onItemClick(position: Int?) {

                }

            })
        mBinding.rvContectedHistory.adapter = connectedHistoryAdapter
    }


    private fun callApi(search: String) {
        mBinding.noDataConnectedHistory.gone()
        mBinding.noInternetConnectedhistory.llNointernet.gone()
        mBinding.cl1.visible()
        val getCurrentRole =
            SharedPreferenceManager.getString(AppConstants.USER_ROLE, AppConstants.USER_ROLE)
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            when (getCurrentRole) {
                AppConstants.APP_ROLE_USER -> {
                    type = if (mBinding.rb1.isChecked) {
                        AppConstants.APP_ROLE_LAWYER
                    } else {
                        AppConstants.APP_ROLE_MEDIATOR
                    }
                    setAdapter()
                    mViewModel.getUserConnectedHistory(true, context as BaseActivity, search, type)
                }
                AppConstants.APP_ROLE_LAWYER -> {
                    type = if (mBinding.rb1.isChecked) {
                        AppConstants.APP_ROLE_USER
                    } else {
                        AppConstants.APP_ROLE_MEDIATOR
                    }
                    setAdapter()
                    mViewModel.getLawyerConnectedHistory(
                        true,
                        context as BaseActivity,
                        search,
                        type
                    )
                }
                AppConstants.APP_ROLE_MEDIATOR -> {
                    mBinding.radioGroup.gone()
                    type =AppConstants.APP_ROLE_MEDIATOR
                    setAdapter()
                    mViewModel.getMediatorConnectedHistory(true, context as BaseActivity, search)
                }
            }
        } else {
            mBinding.noDataConnectedHistory.gone()
            mBinding.noInternetConnectedhistory.llNointernet.visible()
            mBinding.cl1.gone()
        }
    }

    override fun handleListener() {

        mBinding.noInternetConnectedhistory.btnTryAgain.setOnClickListener(this)
        mBinding.searchConnectedHistory.llsearch.setOnClickListener(this)
    }

    override fun initObserver() {
        mViewModel.getConenctedHistoryResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            if(!data.isNullOrEmpty()){
                                array.clear()
                                array.addAll(data)
                                connectedHistoryAdapter?.notifyDataSetChanged()
                                if(array.isNullOrEmpty()){
                                    mBinding.noDataConnectedHistory.visible()
                                    mBinding.noInternetConnectedhistory.llNointernet.gone()
                                    mBinding.cl1.gone()
                                }
                            }else{
                                mBinding.noDataConnectedHistory.visible()
                                mBinding.noInternetConnectedhistory.llNointernet.gone()
                                mBinding.cl1.gone()

                            }

                        } else {
                            mBinding.noDataConnectedHistory.visible()
                            mBinding.noInternetConnectedhistory.llNointernet.gone()
                            mBinding.cl1.gone()
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
                if(TextUtils.isEmpty(mBinding.searchConnectedHistory.edtLoginEmail.text.toString())) {
                    callApi(mBinding.searchConnectedHistory.edtLoginEmail.text.toString())
                }
            }
        }
    }

//    private fun changeLayout() {
//        mBinding.rb2.isChecked = mBinding.rb1.isChecked
//    }

}