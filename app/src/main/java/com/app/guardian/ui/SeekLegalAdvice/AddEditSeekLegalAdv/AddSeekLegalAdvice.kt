package com.app.guardian.ui.SeekLegalAdvice.AddEditSeekLegalAdv

import android.app.Activity
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.app.guardian.R
import com.app.guardian.common.IntegratorImpl
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.ValidationView
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentAddSeekLegalAdviceBinding
import com.app.guardian.model.viewModels.LawyerViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddSeekLegalAdvice.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddSeekLegalAdvice(isEdit: Boolean, id: Int,title:String,desc:String) : BaseFragment(), View.OnClickListener {
    private val mViewModel: LawyerViewModel by viewModel()
    lateinit var mBinding: FragmentAddSeekLegalAdviceBinding
    var is_edit = isEdit
    var seek_legal_adv_id = id

    var edit_title = title
    var edit_desc = desc
    override fun getInflateResource(): Int {
        return R.layout.fragment_add_seek_legal_advice
    }

    override fun initView() {
        mBinding = getBinding()
        mBinding.headerAddSeekLegal.tvHeaderText.text = "Seek Legal Advice"
if(is_edit){
    mBinding.edtTitle.setText(edit_title)
    mBinding.edtDesc.setText(edit_desc)
}
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.btnSubmit.setOnClickListener(this)
        mBinding.noInternetAddSeekLegal.btnTryAgain.setOnClickListener(this)
        mBinding.headerAddSeekLegal.ivBack.setOnClickListener(this)
    }

    override fun initObserver() {
        mViewModel.getCommonResp().observe(this, Observer { response ->
            response.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse.let {
                    it?.data?.let { data ->

                        if (it.status) {
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


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {

            }
            R.id.btnTryAgain -> {

                callAddEditAdviceApi()
            }
            R.id.btnSubmit -> {
                validation()
            }
        }
    }

    private fun validation() {
        IntegratorImpl.isValidAddSeekAdv(
            mBinding.edtTitle.text?.trim().toString(),
            mBinding.edtDesc.text?.trim().toString(),
            object : ValidationView.AddSeekLegalAdvice {
                override fun emptyTitle() {
                    ReusedMethod.displayMessageDialog(
                        requireActivity(),
                        "",
                        resources.getString(R.string.empty_title),
                        false,
                        "Cancel",
                        ""
                    )
                    ReusedMethod.ShowRedBorders(requireContext(), mBinding.edtTitle)
                }


                override fun emptyTDesc() {
                    ReusedMethod.displayMessageDialog(
                        requireActivity(),
                        "",
                        resources.getString(R.string.empty_desc),
                        false,
                        "Cancel",
                        ""
                    )
                    ReusedMethod.ShowRedBorders(requireContext(), mBinding.edtDesc)
                }

                override fun success() {
                    callAddEditAdviceApi()
                    ReusedMethod.ShowNoBorders(requireContext(), mBinding.edtDesc)
                    ReusedMethod.ShowNoBorders(requireContext(), mBinding.edtTitle)
                }

            })
    }

    private fun callAddEditAdviceApi() {
        mBinding.noInternetAddSeekLegal.llNointernet.gone()
        mBinding.noAddSeekLegal.gone()
        mBinding.ns.visible()
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            if (is_edit) {
                mViewModel.updateSeekLegalAdvice(
                    true,
                    context as BaseActivity,
                    mBinding.edtTitle.text?.trim().toString(),
                    mBinding.edtDesc.text?.trim().toString(),
                    seek_legal_adv_id
                )
            } else {
                mViewModel.addSeekLegalAdvice(
                    true,
                    context as BaseActivity,
                    mBinding.edtTitle.text?.trim().toString(),
                    mBinding.edtDesc.text?.trim().toString(),
                )
            }

        } else {
            mBinding.noInternetAddSeekLegal.llNointernet.visible()
            mBinding.noAddSeekLegal.gone()
            mBinding.ns.gone()
        }
    }

}