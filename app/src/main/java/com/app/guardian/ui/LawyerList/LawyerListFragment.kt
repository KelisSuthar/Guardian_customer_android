package com.app.guardian.ui.LawyerList

import android.app.Activity
import android.view.View
import androidx.lifecycle.Observer
import com.app.guardian.R
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentLawyerListBinding
import com.app.guardian.model.LawyerLsit.LawyerListResp
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.model.viewModels.UserViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.Lawyer.adapter.LawyerListAdapter
import com.app.guardian.ui.LawyerList.LawyerListFragment.Companion.newInstance
import com.app.guardian.ui.LawyerProfile.LawyerProfileFragment
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel


class LawyerListFragment : BaseFragment() {

    private lateinit var mBinding: FragmentLawyerListBinding
    private val mViewModel: UserViewModel by viewModel()
    var lawyerListAdapter: LawyerListAdapter? = null
    var array = ArrayList<LawyerListResp>()

    private var rootView: View? = null
    override fun getInflateResource(): Int {
        return R.layout.fragment_lawyer_list
    }

    override fun initView() {
        mBinding = getBinding()
        (activity as HomeActivity).bottomTabVisibility(true)
        (activity as HomeActivity).headerTextVisible(requireActivity().resources.getString(R.string.lawyer_list),true,false)

    }

    private fun setAdapter() {
        lawyerListAdapter = LawyerListAdapter(
            context as Activity,
            array,
            object : LawyerListAdapter.onItemClicklisteners {
                override fun onSubclick(selectedLawyerListId: Int?) {
                    ReplaceFragment.replaceFragment(requireActivity(),
                        LawyerProfileFragment.newInstance(selectedLawyerListId!!),true,LawyerListFragment::class.java.name,LawyerListFragment::class.java.name);
                  //  callLawyerProfileDetails(array[position].id)
                }

            }
        )
        mBinding.rvLawyerList.adapter = lawyerListAdapter
    }

    override fun postInit() {
    }

    override fun handleListener() {

    }

    override fun initObserver() {
        mViewModel.getLawyerList().observe(this, Observer { response ->
            response.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->

                            array.clear()
                            array.addAll(data)
                        if(array.size !=0){
                            lawyerListAdapter?.notifyDataSetChanged()
                        }else{
                            mBinding.rvLawyerList.gone()
                            mBinding.noLawyer.visible()
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
                                ?.let { ReusedMethod.displayMessage(context as Activity, it) }
                    }
                }
            }

        })
    }

    override fun onResume() {
        super.onResume()
        callAPI()
        setAdapter()
        mBinding.rvLawyerList.visible()
        mBinding.lyLawyerListFilter.lySearch.visible()
        mBinding.lyLawyerListFilter.lySearchFilter.visible()
      //  mBinding.lyLawyerListFilter.edtLoginEmail.gone()
        mBinding.noLawyer.gone()
        mBinding.noInternetLawyer.llNointernet.gone()
    }


    private fun callAPI() {
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            mViewModel.lawyerList(true, context as BaseActivity,"","","")
        } else {
            mBinding.rvLawyerList.gone()
            mBinding.noLawyer.gone()
            mBinding.noInternetLawyer.llNointernet.visible()
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LawyerListFragment().apply {

            }
    }
}