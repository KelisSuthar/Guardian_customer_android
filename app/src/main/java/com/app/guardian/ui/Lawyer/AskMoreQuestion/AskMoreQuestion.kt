package com.app.guardian.ui.Lawyer.AskMoreQuestion

import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.databinding.FragmentAskMoreQuestionBinding
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity

class AskMoreQuestion : BaseFragment() {

    lateinit var mBinding: FragmentAskMoreQuestionBinding
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

        mBinding.btnAskQuestionSubmit.setOnClickListener {
            ReusedMethod.displayMessage(
                requireActivity(),
                requireActivity().resources.getString(R.string.come_soon)
            )

        }
    }

    override fun postInit() {
    }

    override fun handleListener() {
    }

    override fun initObserver() {
    }

}