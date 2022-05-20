package com.app.guardian.ui.SeekLegalAdvice

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentSeekLegalAdviceListBinding
import com.app.guardian.model.SeekLegalAdviceResp.SeekLegalAdviceResp
import com.app.guardian.model.viewModels.LawyerViewModel
import com.app.guardian.model.viewModels.UserViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.LawyerList.LawyerListFragment
import com.app.guardian.ui.SeekLegalAdvice.AddEditSeekLegalAdv.AddSeekLegalAdvice
import com.app.guardian.ui.SeekLegalAdvice.adapter.SeekLegalAdviceAdapter
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel

class SeekLegalAdviceListFragment(is_icon_show:Boolean,seekLegalIdParams: Int) : BaseFragment(), View.OnClickListener {

    private lateinit var mBinding: FragmentSeekLegalAdviceListBinding
    private var seekLegalAdviceAdapter: SeekLegalAdviceAdapter? = null
    private val mViewModel: UserViewModel by viewModel()
    var seek_legal_adv_delete_id = 0
    private val mViewModel_lawyer: LawyerViewModel by viewModel()
    var array = ArrayList<SeekLegalAdviceResp>()
    var isIconShow = is_icon_show

    private var seekLegalId = seekLegalIdParams

    override fun getInflateResource(): Int {
        return R.layout.fragment_seek_legal_advice_list
    }

    override fun initView() {
        mBinding = getBinding()
//        arguments?.let {
//            seekLegalId = it.getInt("SeekID")
//        }
        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(requireActivity().resources.getString(R.string.seek_legal_advice),true,true)

    }

    private fun setAdapter() {
        when {
            SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                AppConstants.APP_ROLE_USER
            ) == AppConstants.APP_ROLE_USER -> {

                isIconShow = false
            }

            SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                AppConstants.APP_ROLE_USER
            ) == AppConstants.APP_ROLE_MEDIATOR -> {
                isIconShow = false
            }
        }
        seekLegalAdviceAdapter = SeekLegalAdviceAdapter(
            context as Activity,
            isIconShow,
            array,
            object : SeekLegalAdviceAdapter.onClickListeners {
                override fun onEditClick(position: Int) {
                    ReplaceFragment.replaceFragment(
                        requireActivity(),
                        AddSeekLegalAdvice(
                            true, array[position].id!!,
                            array[position].title.toString(),
                            array[position].description.toString()
                        ),
                        true,
                        LawyerListFragment::class.java.name,
                        LawyerListFragment::class.java.name
                    );
                }

                override fun onDeleteClick(position: Int) {
                    seek_legal_adv_delete_id = array[position].id!!
                    callDeleteAdviceApi(array[position].id)
                }

                override fun onItemClick(position: Int) {
                    ReusedMethod. showKnowRightDialog(requireContext(),
                        array[position].title.toString(),"","",array[position].description)
                }

            })
        mBinding.rcySeekLegalAdviceList.adapter = seekLegalAdviceAdapter
    }


    override fun onResume() {
        super.onResume()
        callAPI()
        setAdapter()
        mBinding.rcySeekLegalAdviceList.visible()
        mBinding.noSeekLegal.gone()
        mBinding.noInternetSeekLegal.llNointernet.gone()
    }

    override fun postInit() {

    }


    override fun initObserver() {
        mViewModel.seekLegalAdvice().observe(this, Observer { response ->
            response.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse.let {
                    it?.data?.let { data ->
                        array.clear()
                        array.addAll(data)
                        if (array.size != 0) {
                            seekLegalAdviceAdapter?.notifyDataSetChanged()
                        } else {
                            mBinding.rcySeekLegalAdviceList.gone()
                            mBinding.noSeekLegal.visible()
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

        //Delete Seek legal advice
        mViewModel_lawyer.getCommonResp().observe(this, Observer { response ->
            response.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse.let {
                    it?.data?.let { data ->

                        if (it.status) {
                            array.removeAt(seek_legal_adv_delete_id)
                            if (array.size == 0) {
                                mBinding.rcySeekLegalAdviceList.gone()
                                mBinding.noSeekLegal.visible()
                            }
                            seekLegalAdviceAdapter?.notifyDataSetChanged()
                            ReusedMethod.displayMessage(requireActivity(), it.message.toString())
                        } else {
                            ReusedMethod.displayMessage(requireActivity(), it.message.toString())
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


//    companion object {
//
//        @JvmStatic
//        fun newInstance(seekLegalIdParams: Int) =
//            SeekLegalAdviceListFragment(seekLegalIdParams).apply {
//                arguments = Bundle().apply {
//                    putInt("SeekID", seekLegalIdParams)
//                }
//            }
//    }

    override fun handleListener() {
        mBinding.llAddAdv.setOnClickListener(this)
        mBinding.ivBack.setOnClickListener(this)
        mBinding.noInternetSeekLegal.btnTryAgain.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.llAddAdv -> {
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    AddSeekLegalAdvice(false, 0, "", ""),
                    true,
                    LawyerListFragment::class.java.name,
                    LawyerListFragment::class.java.name
                )
                requireActivity().overridePendingTransition(R.anim.rightto, R.anim.left)
            }
            R.id.ivBack -> {

            }
            R.id.btnTryAgain -> {
                onResume()
            }
        }
    }

    private fun callAPI() {
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            mViewModel.getSeekLegalAdvice(true, context as BaseActivity, seekLegalId!!)
        } else {
            mBinding.rcySeekLegalAdviceList.gone()
            mBinding.noSeekLegal.gone()
            mBinding.noInternetSeekLegal.llNointernet.visible()
        }
    }

    private fun callDeleteAdviceApi(id: Int?) {
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            mViewModel_lawyer.deleteSeekLegalAdvice(true, context as BaseActivity, id)
        } else {
            mBinding.rcySeekLegalAdviceList.gone()
            mBinding.noSeekLegal.gone()
            mBinding.noInternetSeekLegal.llNointernet.visible()
        }
    }

}