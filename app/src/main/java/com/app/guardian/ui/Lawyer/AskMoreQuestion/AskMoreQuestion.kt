package com.app.guardian.ui.Lawyer.AskMoreQuestion

import android.os.Handler
import android.view.View
import com.app.guardian.R
import com.app.guardian.common.IntegratorImpl
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.ValidationView
import com.app.guardian.databinding.FragmentAskMoreQuestionBinding
import com.app.guardian.model.viewModels.LawyerViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.Login.LoginActivity
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel

class AskMoreQuestion : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentAskMoreQuestionBinding
    private val mViewModel: LawyerViewModel by viewModel()
    private var strUserLoginEamil: String? = null
    override fun getInflateResource(): Int {
        return R.layout.fragment_ask_more_question
    }

    override fun initView() {
        mBinding = getBinding()
        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.ask_a_question),
            isHeaderVisible = true,
            isBackButtonVisible = true
        )
        strUserLoginEamil = SharedPreferenceManager.getUser()!!.email

        mBinding.txtUserLoginEmail.text = strUserLoginEamil
    }

    override fun postInit() {
    }

    override fun handleListener() {
        mBinding.btnAskQuestionSubmit.setOnClickListener(this)
    }

    override fun initObserver() {
        mViewModel.getAskModequeResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            Handler().postDelayed({
                                requireActivity().onBackPressed()
                            }, 2000)
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
            R.id.btnAskQuestionSubmit -> {
                validations()

            }
        }
    }

    private fun validations() {
        IntegratorImpl.isValidAskQ(
            mBinding.edtAskMoreQuestionTitle.text?.trim().toString(),
            mBinding.edtAskMoreQuestionDesc.text?.trim().toString(),
            object : ValidationView.askModeQuestions {
                override fun emptyTitle() {
                    ReusedMethod.displayMessageDialog(
                        requireActivity(),
                        "",
                        resources.getString(R.string.empty_title),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowRedBorders(requireContext(), mBinding.edtAskMoreQuestionTitle)
                }

                override fun length_Title() {
                    ReusedMethod.displayMessageDialog(
                        requireActivity(),
                        "",
                        resources.getString(R.string.error_title),
                        false,
                        "Cancel",
                        ""
                    )
                    ReusedMethod.ShowRedBorders(requireContext(), mBinding.edtAskMoreQuestionTitle)
                }

                override fun emptyTDesc() {
                    ReusedMethod.displayMessageDialog(
                        requireActivity(),
                        "",
                        resources.getString(R.string.empty_desc),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowNoBorders(requireContext(), mBinding.edtAskMoreQuestionTitle)
                    ReusedMethod.ShowRedBorders(requireContext(), mBinding.edtAskMoreQuestionDesc)
                }

                override fun length_desc() {
                    ReusedMethod.displayMessageDialog(
                        requireActivity(),
                        "",
                        resources.getString(R.string.error_description),
                        false,
                        "Cancel",
                        ""
                    )
                    ReusedMethod.ShowNoBorders(requireContext(), mBinding.edtAskMoreQuestionTitle)
                    ReusedMethod.ShowRedBorders(requireContext(), mBinding.edtAskMoreQuestionDesc)
                }

                override fun success() {
                    callApi()
                    ReusedMethod.ShowNoBorders(requireContext(), mBinding.edtAskMoreQuestionTitle)
                    ReusedMethod.ShowNoBorders(requireContext(), mBinding.edtAskMoreQuestionDesc)
                }

            }
        )
    }

    private fun callApi() {
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            mViewModel.askModeQuestions(
                true,
                requireActivity() as BaseActivity,
                mBinding.edtAskMoreQuestionTitle.text?.trim().toString(),
                mBinding.edtAskMoreQuestionDesc.text?.trim().toString()
            )
        } else {
            ReusedMethod.displayMessage(requireActivity(), resources.getString(R.string.come_soon))
        }
    }

}