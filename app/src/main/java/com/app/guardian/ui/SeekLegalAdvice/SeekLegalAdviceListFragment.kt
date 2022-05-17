package com.app.guardian.ui.SeekLegalAdvice

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentSeekLegalAdviceListBinding
import com.app.guardian.model.SeekLegalAdviceResp.SeekLegalAdviceResp
import com.app.guardian.model.viewModels.UserViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.SeekLegalAdvice.adapter.SeekLegalAdviceAdapter
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel

class SeekLegalAdviceListFragment(seekLegalIdParams: Int) : BaseFragment() {

    private  lateinit var mBinding: FragmentSeekLegalAdviceListBinding
    private var seekLegalAdviceAdapter: SeekLegalAdviceAdapter? = null
    private val mViewModel: UserViewModel by viewModel()
    var array = ArrayList<SeekLegalAdviceResp>()

    private var seekLegalId: Int? = null

    override fun getInflateResource(): Int {
        return  R.layout.fragment_seek_legal_advice_list
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            seekLegalId = it.getInt("SeekID")
        }
    }

    override fun initView() {
        mBinding = getBinding()
    }

    private fun setAdapter() {
        seekLegalAdviceAdapter = SeekLegalAdviceAdapter(
            context as Activity,
            array
        )
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

    override fun handleListener() {

    }

    override fun initObserver() {
        mViewModel.seekLegalAdvice().observe(this, Observer { response ->
            response.let {
                requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse.let {
                    it?.data?.let { data ->
                        array.clear()
                        array.addAll(data)
                        if (array.size !=0){
                            seekLegalAdviceAdapter?.notifyDataSetChanged()
                        }else{
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
    }

    private fun callAPI() {
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            mViewModel.getSeekLegalAdvice(true, context as BaseActivity,seekLegalId!!)
        } else {
            mBinding.rcySeekLegalAdviceList.gone()
            mBinding.noSeekLegal.gone()
            mBinding.noInternetSeekLegal.llNointernet.visible()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(seekLegalIdParams: Int) =
            SeekLegalAdviceListFragment(seekLegalIdParams).apply {
                arguments = Bundle().apply {
                    putInt("SeekID", seekLegalIdParams)
                }
            }
    }
}